// GenericsNote: Converted -- However, null keys will now be represented in the internal structures, a big change.
/*
 *  Copyright 2003-2004 The Apache Software Foundation
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */
package org.jivesoftware.smack.util.collections;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.*;
import java.util.Map.Entry;

/**
 * An abstract implementation of a hash-based map which provides numerous points for
 * subclasses to override.
 * <p/>
 * This class implements all the features necessary for a subclass hash-based map.
 * Key-value entries are stored in instances of the <code>HashEntry</code> class,
 * which can be overridden and replaced. The iterators can similarly be replaced,
 * without the need to replace the KeySet, EntrySet and Values view classes.
 * <p/>
 * Overridable methods are provided to change the default hashing behaviour, and
 * to change how entries are added to and removed from the map. Hopefully, all you
 * need for unusual subclasses is here.
 * <p/>
 * NOTE: From Commons Collections 3.1 this class extends AbstractMap.
 * This is to provide backwards compatibility for ReferenceMap between v3.0 and v3.1.
 * This extends clause will be removed in v4.0.
 *
 * @author java util HashMap
 * @author Matt Hall, John Watkinson, Stephen Colebourne
 * @version $Revision: 1.1 $ $Date: 2005/10/11 17:05:32 $
 * @since Commons Collections 3.0
 */
public class AbstractHashedMap <K,V> extends AbstractMap<K, V> implements IterableMap<K, V> {

    protected static final String NO_NEXT_ENTRY = "No next() entry in the iteration";
    protected static final String NO_PREVIOUS_ENTRY = "No previous() entry in the iteration";
    protected static final String REMOVE_INVALID = "remove() can only be called once after next()";
    protected static final String GETKEY_INVALID = "getKey() can only be called after next() and before remove()";
    protected static final String GETVALUE_INVALID = "getValue() can only be called after next() and before remove()";
    protected static final String SETVALUE_INVALID = "setValue() can only be called after next() and before remove()";

    /**
     * The default capacity to use
     */
    protected static final int DEFAULT_CAPACITY = 16;
    /**
     * The default threshold to use
     */
    protected static final int DEFAULT_THRESHOLD = 12;
    /**
     * The default load factor to use
     */
    protected static final float DEFAULT_LOAD_FACTOR = 0.75f;
    /**
     * The maximum capacity allowed
     */
    protected static final int MAXIMUM_CAPACITY = 1 << 30;
    /**
     * An object for masking null
     */
    protected static final Object NULL = new Object();

    /**
     * Load factor, normally 0.75
     */
    protected transient float loadFactor;
    /**
     * The size of the map
     */
    protected transient int size;
    /**
     * Map entries
     */
    protected transient AbstractHashedMap.HashEntry<K, V>[] data;
    /**
     * Size at which to rehash
     */
    protected transient int threshold;
    /**
     * Modification count for iterators
     */
    protected transient int modCount;
    /**
     * Entry set
     */
    protected transient AbstractHashedMap.EntrySet<K, V> entrySet;
    /**
     * Key set
     */
    protected transient AbstractHashedMap.KeySet<K, V> keySet;
    /**
     * Values
     */
    protected transient AbstractHashedMap.Values<K, V> values;

    /**
     * Constructor only used in deserialization, do not use otherwise.
     */
    protected AbstractHashedMap() {
    }

    /**
     * Constructor which performs no validation on the passed in parameters.
     *
     * @param initialCapacity the initial capacity, must be a power of two
     * @param loadFactor      the load factor, must be &gt; 0.0f and generally &lt; 1.0f
     * @param threshold       the threshold, must be sensible
     */
    protected AbstractHashedMap(int initialCapacity, float loadFactor, int threshold) {
        this.loadFactor = loadFactor;
        data = new AbstractHashedMap.HashEntry[initialCapacity];
        this.threshold = threshold;
        this.init();
    }

    /**
     * Constructs a new, empty map with the specified initial capacity and
     * default load factor.
     *
     * @param initialCapacity the initial capacity
     * @throws IllegalArgumentException if the initial capacity is less than one
     */
    protected AbstractHashedMap(int initialCapacity) {
        this(initialCapacity, AbstractHashedMap.DEFAULT_LOAD_FACTOR);
    }

    /**
     * Constructs a new, empty map with the specified initial capacity and
     * load factor.
     *
     * @param initialCapacity the initial capacity
     * @param loadFactor      the load factor
     * @throws IllegalArgumentException if the initial capacity is less than one
     * @throws IllegalArgumentException if the load factor is less than or equal to zero
     */
    protected AbstractHashedMap(int initialCapacity, float loadFactor) {
        if (initialCapacity < 1) {
            throw new IllegalArgumentException("Initial capacity must be greater than 0");
        }
        if (loadFactor <= 0.0f || Float.isNaN(loadFactor)) {
            throw new IllegalArgumentException("Load factor must be greater than 0");
        }
        this.loadFactor = loadFactor;
        threshold = this.calculateThreshold(initialCapacity, loadFactor);
        initialCapacity = this.calculateNewCapacity(initialCapacity);
        data = new AbstractHashedMap.HashEntry[initialCapacity];
        this.init();
    }

    /**
     * Constructor copying elements from another map.
     *
     * @param map the map to copy
     * @throws NullPointerException if the map is null
     */
    protected AbstractHashedMap(Map<? extends K, ? extends V> map) {
        this(Math.max(2 * map.size(), AbstractHashedMap.DEFAULT_CAPACITY), AbstractHashedMap.DEFAULT_LOAD_FACTOR);
        this.putAll(map);
    }

    /**
     * Initialise subclasses during construction, cloning or deserialization.
     */
    protected void init() {
    }

    //-----------------------------------------------------------------------
    /**
     * Gets the value mapped to the key specified.
     *
     * @param key the key
     * @return the mapped value, null if no match
     */
    @Override
	public V get(Object key) {
        int hashCode = this.hash(key == null ? AbstractHashedMap.NULL : key);
        AbstractHashedMap.HashEntry<K, V> entry = this.data[this.hashIndex(hashCode, this.data.length)]; // no local for hash index
        while (entry != null) {
            if (entry.hashCode == hashCode && this.isEqualKey(key, entry.key)) {
                return entry.getValue();
            }
            entry = entry.next;
        }
        return null;
    }

    /**
     * Gets the size of the map.
     *
     * @return the size
     */
    @Override
	public int size() {
        return this.size;
    }

    /**
     * Checks whether the map is currently empty.
     *
     * @return true if the map is currently size zero
     */
    @Override
	public boolean isEmpty() {
        return this.size == 0;
    }

    //-----------------------------------------------------------------------
    /**
     * Checks whether the map contains the specified key.
     *
     * @param key the key to search for
     * @return true if the map contains the key
     */
    @Override
	public boolean containsKey(Object key) {
        int hashCode = this.hash(key == null ? AbstractHashedMap.NULL : key);
        AbstractHashedMap.HashEntry entry = this.data[this.hashIndex(hashCode, this.data.length)]; // no local for hash index
        while (entry != null) {
            if (entry.hashCode == hashCode && this.isEqualKey(key, entry.getKey())) {
                return true;
            }
            entry = entry.next;
        }
        return false;
    }

    /**
     * Checks whether the map contains the specified value.
     *
     * @param value the value to search for
     * @return true if the map contains the value
     */
    @Override
	public boolean containsValue(Object value) {
        if (value == null) {
            for (int i = 0, isize = this.data.length; i < isize; i++) {
                AbstractHashedMap.HashEntry entry = this.data[i];
                while (entry != null) {
                    if (entry.getValue() == null) {
                        return true;
                    }
                    entry = entry.next;
                }
            }
        } else {
            for (int i = 0, isize = this.data.length; i < isize; i++) {
                AbstractHashedMap.HashEntry entry = this.data[i];
                while (entry != null) {
                    if (this.isEqualValue(value, entry.getValue())) {
                        return true;
                    }
                    entry = entry.next;
                }
            }
        }
        return false;
    }

    //-----------------------------------------------------------------------
    /**
     * Puts a key-value mapping into this map.
     *
     * @param key   the key to add
     * @param value the value to add
     * @return the value previously mapped to this key, null if none
     */
    @Override
	public V put(K key, V value) {
        int hashCode = this.hash(key == null ? AbstractHashedMap.NULL : key);
        int index = this.hashIndex(hashCode, this.data.length);
        AbstractHashedMap.HashEntry<K, V> entry = this.data[index];
        while (entry != null) {
            if (entry.hashCode == hashCode && this.isEqualKey(key, entry.getKey())) {
                V oldValue = entry.getValue();
                this.updateEntry(entry, value);
                return oldValue;
            }
            entry = entry.next;
        }
        this.addMapping(index, hashCode, key, value);
        return null;
    }

    /**
     * Puts all the values from the specified map into this map.
     * <p/>
     * This implementation iterates around the specified map and
     * uses {@link #put(Object, Object)}.
     *
     * @param map the map to add
     * @throws NullPointerException if the map is null
     */
    @Override
	public void putAll(Map<? extends K, ? extends V> map) {
        int mapSize = map.size();
        if (mapSize == 0) {
            return;
        }
        int newSize = (int) ((this.size + mapSize) / this.loadFactor + 1);
        this.ensureCapacity(this.calculateNewCapacity(newSize));
        // Have to cast here because of compiler inference problems.
        for (Iterator it = map.entrySet().iterator(); it.hasNext();) {
            Entry<? extends K, ? extends V> entry = (Entry<? extends K, ? extends V>) it.next();
            this.put(entry.getKey(), entry.getValue());
        }
    }

    /**
     * Removes the specified mapping from this map.
     *
     * @param key the mapping to remove
     * @return the value mapped to the removed key, null if key not in map
     */
    @Override
	public V remove(Object key) {
        int hashCode = this.hash(key == null ? AbstractHashedMap.NULL : key);
        int index = this.hashIndex(hashCode, this.data.length);
        AbstractHashedMap.HashEntry<K, V> entry = this.data[index];
        AbstractHashedMap.HashEntry<K, V> previous = null;
        while (entry != null) {
            if (entry.hashCode == hashCode && this.isEqualKey(key, entry.getKey())) {
                V oldValue = entry.getValue();
                this.removeMapping(entry, index, previous);
                return oldValue;
            }
            previous = entry;
            entry = entry.next;
        }
        return null;
    }

    /**
     * Clears the map, resetting the size to zero and nullifying references
     * to avoid garbage collection issues.
     */
    @Override
	public void clear() {
        this.modCount++;
        AbstractHashedMap.HashEntry[] data = this.data;
        for (int i = data.length - 1; i >= 0; i--) {
            data[i] = null;
        }
        this.size = 0;
    }

    /**
     * Gets the hash code for the key specified.
     * This implementation uses the additional hashing routine from JDK1.4.
     * Subclasses can override this to return alternate hash codes.
     *
     * @param key the key to get a hash code for
     * @return the hash code
     */
    protected int hash(Object key) {
        // same as JDK 1.4
        int h = key.hashCode();
        h += ~(h << 9);
        h ^= h >>> 14;
        h += h << 4;
        h ^= h >>> 10;
        return h;
    }

    /**
     * Compares two keys, in internal converted form, to see if they are equal.
     * This implementation uses the equals method.
     * Subclasses can override this to match differently.
     *
     * @param key1 the first key to compare passed in from outside
     * @param key2 the second key extracted from the entry via <code>entry.key</code>
     * @return true if equal
     */
    protected boolean isEqualKey(Object key1, Object key2) {
        return key1 == key2 || key1 != null && key1.equals(key2);
    }

    /**
     * Compares two values, in external form, to see if they are equal.
     * This implementation uses the equals method and assumes neither value is null.
     * Subclasses can override this to match differently.
     *
     * @param value1 the first value to compare passed in from outside
     * @param value2 the second value extracted from the entry via <code>getValue()</code>
     * @return true if equal
     */
    protected boolean isEqualValue(Object value1, Object value2) {
        return value1 == value2 || value1.equals(value2);
    }

    /**
     * Gets the index into the data storage for the hashCode specified.
     * This implementation uses the least significant bits of the hashCode.
     * Subclasses can override this to return alternate bucketing.
     *
     * @param hashCode the hash code to use
     * @param dataSize the size of the data to pick a bucket from
     * @return the bucket index
     */
    protected int hashIndex(int hashCode, int dataSize) {
        return hashCode & dataSize - 1;
    }

    //-----------------------------------------------------------------------
    /**
     * Gets the entry mapped to the key specified.
     * <p/>
     * This method exists for subclasses that may need to perform a multi-step
     * process accessing the entry. The public methods in this class don't use this
     * method to gain a small performance boost.
     *
     * @param key the key
     * @return the entry, null if no match
     */
    protected AbstractHashedMap.HashEntry<K, V> getEntry(Object key) {
        int hashCode = this.hash(key == null ? AbstractHashedMap.NULL : key);
        AbstractHashedMap.HashEntry<K, V> entry = this.data[this.hashIndex(hashCode, this.data.length)]; // no local for hash index
        while (entry != null) {
            if (entry.hashCode == hashCode && this.isEqualKey(key, entry.getKey())) {
                return entry;
            }
            entry = entry.next;
        }
        return null;
    }

    //-----------------------------------------------------------------------
    /**
     * Updates an existing key-value mapping to change the value.
     * <p/>
     * This implementation calls <code>setValue()</code> on the entry.
     * Subclasses could override to handle changes to the map.
     *
     * @param entry    the entry to update
     * @param newValue the new value to store
     */
    protected void updateEntry(AbstractHashedMap.HashEntry<K, V> entry, V newValue) {
        entry.setValue(newValue);
    }

    /**
     * Reuses an existing key-value mapping, storing completely new data.
     * <p/>
     * This implementation sets all the data fields on the entry.
     * Subclasses could populate additional entry fields.
     *
     * @param entry     the entry to update, not null
     * @param hashIndex the index in the data array
     * @param hashCode  the hash code of the key to add
     * @param key       the key to add
     * @param value     the value to add
     */
    protected void reuseEntry(AbstractHashedMap.HashEntry<K, V> entry, int hashIndex, int hashCode, K key, V value) {
        entry.next = this.data[hashIndex];
        entry.hashCode = hashCode;
        entry.key = key;
        entry.value = value;
    }

    //-----------------------------------------------------------------------
    /**
     * Adds a new key-value mapping into this map.
     * <p/>
     * This implementation calls <code>createEntry()</code>, <code>addEntry()</code>
     * and <code>checkCapacity()</code>.
     * It also handles changes to <code>modCount</code> and <code>size</code>.
     * Subclasses could override to fully control adds to the map.
     *
     * @param hashIndex the index into the data array to store at
     * @param hashCode  the hash code of the key to add
     * @param key       the key to add
     * @param value     the value to add
     */
    protected void addMapping(int hashIndex, int hashCode, K key, V value) {
        this.modCount++;
        AbstractHashedMap.HashEntry<K, V> entry = this.createEntry(this.data[hashIndex], hashCode, key, value);
        this.addEntry(entry, hashIndex);
        this.size++;
        this.checkCapacity();
    }

    /**
     * Creates an entry to store the key-value data.
     * <p/>
     * This implementation creates a new HashEntry instance.
     * Subclasses can override this to return a different storage class,
     * or implement caching.
     *
     * @param next     the next entry in sequence
     * @param hashCode the hash code to use
     * @param key      the key to store
     * @param value    the value to store
     * @return the newly created entry
     */
    protected AbstractHashedMap.HashEntry<K, V> createEntry(AbstractHashedMap.HashEntry<K, V> next, int hashCode, K key, V value) {
        return new AbstractHashedMap.HashEntry<K, V>(next, hashCode, key, value);
    }

    /**
     * Adds an entry into this map.
     * <p/>
     * This implementation adds the entry to the data storage table.
     * Subclasses could override to handle changes to the map.
     *
     * @param entry     the entry to add
     * @param hashIndex the index into the data array to store at
     */
    protected void addEntry(AbstractHashedMap.HashEntry<K, V> entry, int hashIndex) {
        this.data[hashIndex] = entry;
    }

    //-----------------------------------------------------------------------
    /**
     * Removes a mapping from the map.
     * <p/>
     * This implementation calls <code>removeEntry()</code> and <code>destroyEntry()</code>.
     * It also handles changes to <code>modCount</code> and <code>size</code>.
     * Subclasses could override to fully control removals from the map.
     *
     * @param entry     the entry to remove
     * @param hashIndex the index into the data structure
     * @param previous  the previous entry in the chain
     */
    protected void removeMapping(AbstractHashedMap.HashEntry<K, V> entry, int hashIndex, AbstractHashedMap.HashEntry<K, V> previous) {
        this.modCount++;
        this.removeEntry(entry, hashIndex, previous);
        this.size--;
        this.destroyEntry(entry);
    }

    /**
     * Removes an entry from the chain stored in a particular index.
     * <p/>
     * This implementation removes the entry from the data storage table.
     * The size is not updated.
     * Subclasses could override to handle changes to the map.
     *
     * @param entry     the entry to remove
     * @param hashIndex the index into the data structure
     * @param previous  the previous entry in the chain
     */
    protected void removeEntry(AbstractHashedMap.HashEntry<K, V> entry, int hashIndex, AbstractHashedMap.HashEntry<K, V> previous) {
        if (previous == null) {
            this.data[hashIndex] = entry.next;
        } else {
            previous.next = entry.next;
        }
    }

    /**
     * Kills an entry ready for the garbage collector.
     * <p/>
     * This implementation prepares the HashEntry for garbage collection.
     * Subclasses can override this to implement caching (override clear as well).
     *
     * @param entry the entry to destroy
     */
    protected void destroyEntry(AbstractHashedMap.HashEntry<K, V> entry) {
        entry.next = null;
        entry.key = null;
        entry.value = null;
    }

    //-----------------------------------------------------------------------
    /**
     * Checks the capacity of the map and enlarges it if necessary.
     * <p/>
     * This implementation uses the threshold to check if the map needs enlarging
     */
    protected void checkCapacity() {
        if (this.size >= this.threshold) {
            int newCapacity = this.data.length * 2;
            if (newCapacity <= AbstractHashedMap.MAXIMUM_CAPACITY) {
                this.ensureCapacity(newCapacity);
            }
        }
    }

    /**
     * Changes the size of the data structure to the capacity proposed.
     *
     * @param newCapacity the new capacity of the array (a power of two, less or equal to max)
     */
    protected void ensureCapacity(int newCapacity) {
        int oldCapacity = this.data.length;
        if (newCapacity <= oldCapacity) {
            return;
        }
        if (this.size == 0) {
            this.threshold = this.calculateThreshold(newCapacity, this.loadFactor);
            this.data = new AbstractHashedMap.HashEntry[newCapacity];
        } else {
            AbstractHashedMap.HashEntry<K, V> oldEntries[] = this.data;
            AbstractHashedMap.HashEntry<K, V> newEntries[] = new AbstractHashedMap.HashEntry[newCapacity];

            this.modCount++;
            for (int i = oldCapacity - 1; i >= 0; i--) {
                AbstractHashedMap.HashEntry<K, V> entry = oldEntries[i];
                if (entry != null) {
                    oldEntries[i] = null;  // gc
                    do {
                        AbstractHashedMap.HashEntry<K, V> next = entry.next;
                        int index = this.hashIndex(entry.hashCode, newCapacity);
                        entry.next = newEntries[index];
                        newEntries[index] = entry;
                        entry = next;
                    } while (entry != null);
                }
            }
            this.threshold = this.calculateThreshold(newCapacity, this.loadFactor);
            this.data = newEntries;
        }
    }

    /**
     * Calculates the new capacity of the map.
     * This implementation normalizes the capacity to a power of two.
     *
     * @param proposedCapacity the proposed capacity
     * @return the normalized new capacity
     */
    protected int calculateNewCapacity(int proposedCapacity) {
        int newCapacity = 1;
        if (proposedCapacity > AbstractHashedMap.MAXIMUM_CAPACITY) {
            newCapacity = AbstractHashedMap.MAXIMUM_CAPACITY;
        } else {
            while (newCapacity < proposedCapacity) {
                newCapacity <<= 1;  // multiply by two
            }
            if (newCapacity > AbstractHashedMap.MAXIMUM_CAPACITY) {
                newCapacity = AbstractHashedMap.MAXIMUM_CAPACITY;
            }
        }
        return newCapacity;
    }

    /**
     * Calculates the new threshold of the map, where it will be resized.
     * This implementation uses the load factor.
     *
     * @param newCapacity the new capacity
     * @param factor      the load factor
     * @return the new resize threshold
     */
    protected int calculateThreshold(int newCapacity, float factor) {
        return (int) (newCapacity * factor);
    }

    //-----------------------------------------------------------------------
    /**
     * Gets the <code>next</code> field from a <code>HashEntry</code>.
     * Used in subclasses that have no visibility of the field.
     *
     * @param entry the entry to query, must not be null
     * @return the <code>next</code> field of the entry
     * @throws NullPointerException if the entry is null
     * @since Commons Collections 3.1
     */
    protected AbstractHashedMap.HashEntry<K, V> entryNext(AbstractHashedMap.HashEntry<K, V> entry) {
        return entry.next;
    }

    /**
     * Gets the <code>hashCode</code> field from a <code>HashEntry</code>.
     * Used in subclasses that have no visibility of the field.
     *
     * @param entry the entry to query, must not be null
     * @return the <code>hashCode</code> field of the entry
     * @throws NullPointerException if the entry is null
     * @since Commons Collections 3.1
     */
    protected int entryHashCode(AbstractHashedMap.HashEntry<K, V> entry) {
        return entry.hashCode;
    }

    /**
     * Gets the <code>key</code> field from a <code>HashEntry</code>.
     * Used in subclasses that have no visibility of the field.
     *
     * @param entry the entry to query, must not be null
     * @return the <code>key</code> field of the entry
     * @throws NullPointerException if the entry is null
     * @since Commons Collections 3.1
     */
    protected K entryKey(AbstractHashedMap.HashEntry<K, V> entry) {
        return entry.key;
    }

    /**
     * Gets the <code>value</code> field from a <code>HashEntry</code>.
     * Used in subclasses that have no visibility of the field.
     *
     * @param entry the entry to query, must not be null
     * @return the <code>value</code> field of the entry
     * @throws NullPointerException if the entry is null
     * @since Commons Collections 3.1
     */
    protected V entryValue(AbstractHashedMap.HashEntry<K, V> entry) {
        return entry.value;
    }

    //-----------------------------------------------------------------------
    /**
     * Gets an iterator over the map.
     * Changes made to the iterator affect this map.
     * <p/>
     * A MapIterator returns the keys in the map. It also provides convenient
     * methods to get the key and value, and set the value.
     * It avoids the need to create an entrySet/keySet/values object.
     * It also avoids creating the Map.Entry object.
     *
     * @return the map iterator
     */
    @Override
	public MapIterator<K, V> mapIterator() {
        if (this.size == 0) {
            return EmptyMapIterator.INSTANCE;
        }
        return new AbstractHashedMap.HashMapIterator<K, V>(this);
    }

    /**
     * MapIterator implementation.
     */
    protected static class HashMapIterator <K,V> extends AbstractHashedMap.HashIterator<K, V> implements MapIterator<K, V> {

        protected HashMapIterator(AbstractHashedMap<K, V> parent) {
            super(parent);
        }

        @Override
		public K next() {
            return nextEntry().getKey();
        }

        @Override
		public K getKey() {
            AbstractHashedMap.HashEntry<K, V> current = this.currentEntry();
            if (current == null) {
                throw new IllegalStateException(GETKEY_INVALID);
            }
            return current.getKey();
        }

        @Override
		public V getValue() {
            AbstractHashedMap.HashEntry<K, V> current = this.currentEntry();
            if (current == null) {
                throw new IllegalStateException(GETVALUE_INVALID);
            }
            return current.getValue();
        }

        @Override
		public V setValue(V value) {
            AbstractHashedMap.HashEntry<K, V> current = this.currentEntry();
            if (current == null) {
                throw new IllegalStateException(SETVALUE_INVALID);
            }
            return current.setValue(value);
        }
    }

    //-----------------------------------------------------------------------
    /**
     * Gets the entrySet view of the map.
     * Changes made to the view affect this map.
     * To simply iterate through the entries, use {@link #mapIterator()}.
     *
     * @return the entrySet view
     */
    @Override
	public Set<Entry<K, V>> entrySet() {
        if (this.entrySet == null) {
            this.entrySet = new AbstractHashedMap.EntrySet<K, V>(this);
        }
        return this.entrySet;
    }

    /**
     * Creates an entry set iterator.
     * Subclasses can override this to return iterators with different properties.
     *
     * @return the entrySet iterator
     */
    protected Iterator<Entry<K, V>> createEntrySetIterator() {
        if (this.size() == 0) {
            return EmptyIterator.INSTANCE;
        }
        return new AbstractHashedMap.EntrySetIterator<K, V>(this);
    }

    /**
     * EntrySet implementation.
     */
    protected static class EntrySet <K,V> extends AbstractSet<Entry<K, V>> {
        /**
         * The parent map
         */
        protected final AbstractHashedMap<K, V> parent;

        protected EntrySet(AbstractHashedMap<K, V> parent) {
            this.parent = parent;
        }

        @Override
		public int size() {
            return this.parent.size();
        }

        @Override
		public void clear() {
            this.parent.clear();
        }

        public boolean contains(Entry<K, V> entry) {
            Entry<K, V> e = entry;
            Map.Entry<K, V> match = this.parent.getEntry(e.getKey());
            return match != null && match.equals(e);
        }

        @Override
		public boolean remove(Object obj) {
            if (obj instanceof Map.Entry == false) {
                return false;
            }
            if (this.contains(obj) == false) {
                return false;
            }
            Map.Entry<K, V> entry = (Map.Entry<K, V>) obj;
            K key = entry.getKey();
            this.parent.remove(key);
            return true;
        }

        @Override
		public Iterator<Map.Entry<K, V>> iterator() {
            return this.parent.createEntrySetIterator();
        }
    }

    /**
     * EntrySet iterator.
     */
    protected static class EntrySetIterator <K,V> extends AbstractHashedMap.HashIterator<K, V> implements Iterator<Map.Entry<K, V>> {

        protected EntrySetIterator(AbstractHashedMap<K, V> parent) {
            super(parent);
        }

        @Override
		public AbstractHashedMap.HashEntry<K, V> next() {
            return nextEntry();
        }
    }

    //-----------------------------------------------------------------------
    /**
     * Gets the keySet view of the map.
     * Changes made to the view affect this map.
     * To simply iterate through the keys, use {@link #mapIterator()}.
     *
     * @return the keySet view
     */
    @Override
	public Set<K> keySet() {
        if (this.keySet == null) {
            this.keySet = new AbstractHashedMap.KeySet<K, V>(this);
        }
        return this.keySet;
    }

    /**
     * Creates a key set iterator.
     * Subclasses can override this to return iterators with different properties.
     *
     * @return the keySet iterator
     */
    protected Iterator<K> createKeySetIterator() {
        if (this.size() == 0) {
            return EmptyIterator.INSTANCE;
        }
        return new AbstractHashedMap.KeySetIterator<K, V>(this);
    }

    /**
     * KeySet implementation.
     */
    protected static class KeySet <K,V> extends AbstractSet<K> {
        /**
         * The parent map
         */
        protected final AbstractHashedMap<K, V> parent;

        protected KeySet(AbstractHashedMap<K, V> parent) {
            this.parent = parent;
        }

        @Override
		public int size() {
            return this.parent.size();
        }

        @Override
		public void clear() {
            this.parent.clear();
        }

        @Override
		public boolean contains(Object key) {
            return this.parent.containsKey(key);
        }

        @Override
		public boolean remove(Object key) {
            boolean result = this.parent.containsKey(key);
            this.parent.remove(key);
            return result;
        }

        @Override
		public Iterator<K> iterator() {
            return this.parent.createKeySetIterator();
        }
    }

    /**
     * KeySet iterator.
     */
    protected static class KeySetIterator <K,V> extends AbstractHashedMap.HashIterator<K, V> implements Iterator<K> {

        protected KeySetIterator(AbstractHashedMap<K, V> parent) {
            super(parent);
        }

        @Override
		public K next() {
            return nextEntry().getKey();
        }
    }

    //-----------------------------------------------------------------------
    /**
     * Gets the values view of the map.
     * Changes made to the view affect this map.
     * To simply iterate through the values, use {@link #mapIterator()}.
     *
     * @return the values view
     */
    @Override
	public Collection<V> values() {
        if (this.values == null) {
            this.values = new AbstractHashedMap.Values(this);
        }
        return this.values;
    }

    /**
     * Creates a values iterator.
     * Subclasses can override this to return iterators with different properties.
     *
     * @return the values iterator
     */
    protected Iterator<V> createValuesIterator() {
        if (this.size() == 0) {
            return EmptyIterator.INSTANCE;
        }
        return new AbstractHashedMap.ValuesIterator<K, V>(this);
    }

    /**
     * Values implementation.
     */
    protected static class Values <K,V> extends AbstractCollection<V> {
        /**
         * The parent map
         */
        protected final AbstractHashedMap<K, V> parent;

        protected Values(AbstractHashedMap<K, V> parent) {
            this.parent = parent;
        }

        @Override
		public int size() {
            return this.parent.size();
        }

        @Override
		public void clear() {
            this.parent.clear();
        }

        @Override
		public boolean contains(Object value) {
            return this.parent.containsValue(value);
        }

        @Override
		public Iterator<V> iterator() {
            return this.parent.createValuesIterator();
        }
    }

    /**
     * Values iterator.
     */
    protected static class ValuesIterator <K,V> extends AbstractHashedMap.HashIterator<K, V> implements Iterator<V> {

        protected ValuesIterator(AbstractHashedMap<K, V> parent) {
            super(parent);
        }

        @Override
		public V next() {
            return nextEntry().getValue();
        }
    }

    //-----------------------------------------------------------------------
    /**
     * HashEntry used to store the data.
     * <p/>
     * If you subclass <code>AbstractHashedMap</code> but not <code>HashEntry</code>
     * then you will not be able to access the protected fields.
     * The <code>entryXxx()</code> methods on <code>AbstractHashedMap</code> exist
     * to provide the necessary access.
     */
    protected static class HashEntry <K,V> implements Map.Entry<K, V>, KeyValue<K, V> {
        /**
         * The next entry in the hash chain
         */
        protected AbstractHashedMap.HashEntry<K, V> next;
        /**
         * The hash code of the key
         */
        protected int hashCode;
        /**
         * The key
         */
        private K key;
        /**
         * The value
         */
        private V value;

        protected HashEntry(AbstractHashedMap.HashEntry<K, V> next, int hashCode, K key, V value) {
            this.next = next;
            this.hashCode = hashCode;
            this.key = key;
            this.value = value;
        }

        @Override
		public K getKey() {
            return this.key;
        }

        public void setKey(K key) {
            this.key = key;
        }

        @Override
		public V getValue() {
            return this.value;
        }

        @Override
		public V setValue(V value) {
            V old = this.value;
            this.value = value;
            return old;
        }

        @Override
		public boolean equals(Object obj) {
            if (obj == this) {
                return true;
            }
            if (obj instanceof Map.Entry == false) {
                return false;
            }
            Map.Entry other = (Map.Entry) obj;
            return (this.getKey() == null ? other.getKey() == null : this.getKey().equals(other.getKey())) && (this.getValue() == null ? other.getValue() == null : this.getValue().equals(other.getValue()));
        }

        @Override
		public int hashCode() {
            return (this.getKey() == null ? 0 : this.getKey().hashCode()) ^ (this.getValue() == null ? 0 : this.getValue().hashCode());
        }

        @Override
		public String toString() {
            return new StringBuilder().append(this.getKey()).append('=').append(this.getValue()).toString();
        }
    }

    /**
     * Base Iterator
     */
    protected abstract static class HashIterator <K,V> {

        /**
         * The parent map
         */
        protected final AbstractHashedMap parent;
        /**
         * The current index into the array of buckets
         */
        protected int hashIndex;
        /**
         * The last returned entry
         */
        protected AbstractHashedMap.HashEntry<K, V> last;
        /**
         * The next entry
         */
        protected AbstractHashedMap.HashEntry<K, V> next;
        /**
         * The modification count expected
         */
        protected int expectedModCount;

        protected HashIterator(AbstractHashedMap<K, V> parent) {
            this.parent = parent;
            AbstractHashedMap.HashEntry<K, V>[] data = parent.data;
            int i = data.length;
            AbstractHashedMap.HashEntry<K, V> next = null;
            while (i > 0 && next == null) {
                next = data[--i];
            }
            this.next = next;
            hashIndex = i;
            expectedModCount = parent.modCount;
        }

        public boolean hasNext() {
            return this.next != null;
        }

        protected AbstractHashedMap.HashEntry<K, V> nextEntry() {
            if (this.parent.modCount != this.expectedModCount) {
                throw new ConcurrentModificationException();
            }
            AbstractHashedMap.HashEntry<K, V> newCurrent = this.next;
            if (newCurrent == null) {
                throw new NoSuchElementException(NO_NEXT_ENTRY);
            }
            AbstractHashedMap.HashEntry<K, V>[] data = this.parent.data;
            int i = this.hashIndex;
            AbstractHashedMap.HashEntry<K, V> n = newCurrent.next;
            while (n == null && i > 0) {
                n = data[--i];
            }
            this.next = n;
            this.hashIndex = i;
            this.last = newCurrent;
            return newCurrent;
        }

        protected AbstractHashedMap.HashEntry<K, V> currentEntry() {
            return this.last;
        }

        public void remove() {
            if (this.last == null) {
                throw new IllegalStateException(REMOVE_INVALID);
            }
            if (this.parent.modCount != this.expectedModCount) {
                throw new ConcurrentModificationException();
            }
            this.parent.remove(this.last.getKey());
            this.last = null;
            this.expectedModCount = this.parent.modCount;
        }

        @Override
		public String toString() {
            if (this.last != null) {
                return "Iterator[" + this.last.getKey() + "=" + this.last.getValue() + "]";
            } else {
                return "Iterator[]";
            }
        }
    }

    //-----------------------------------------------------------------------
    /**
     * Writes the map data to the stream. This method must be overridden if a
     * subclass must be setup before <code>put()</code> is used.
     * <p/>
     * Serialization is not one of the JDK's nicest topics. Normal serialization will
     * initialise the superclass before the subclass. Sometimes however, this isn't
     * what you want, as in this case the <code>put()</code> method on read can be
     * affected by subclass state.
     * <p/>
     * The solution adopted here is to serialize the state data of this class in
     * this protected method. This method must be called by the
     * <code>writeObject()</code> of the first serializable subclass.
     * <p/>
     * Subclasses may override if they have a specific field that must be present
     * on read before this implementation will work. Generally, the read determines
     * what must be serialized here, if anything.
     *
     * @param out the output stream
     */
    protected void doWriteObject(ObjectOutputStream out) throws IOException {
        out.writeFloat(this.loadFactor);
        out.writeInt(this.data.length);
        out.writeInt(this.size);
        for (MapIterator it = this.mapIterator(); it.hasNext();) {
            out.writeObject(it.next());
            out.writeObject(it.getValue());
        }
    }

    /**
     * Reads the map data from the stream. This method must be overridden if a
     * subclass must be setup before <code>put()</code> is used.
     * <p/>
     * Serialization is not one of the JDK's nicest topics. Normal serialization will
     * initialise the superclass before the subclass. Sometimes however, this isn't
     * what you want, as in this case the <code>put()</code> method on read can be
     * affected by subclass state.
     * <p/>
     * The solution adopted here is to deserialize the state data of this class in
     * this protected method. This method must be called by the
     * <code>readObject()</code> of the first serializable subclass.
     * <p/>
     * Subclasses may override if the subclass has a specific field that must be present
     * before <code>put()</code> or <code>calculateThreshold()</code> will work correctly.
     *
     * @param in the input stream
     */
    protected void doReadObject(ObjectInputStream in) throws IOException, ClassNotFoundException {
        this.loadFactor = in.readFloat();
        int capacity = in.readInt();
        int size = in.readInt();
        this.init();
        this.data = new AbstractHashedMap.HashEntry[capacity];
        for (int i = 0; i < size; i++) {
            K key = (K) in.readObject();
            V value = (V) in.readObject();
            this.put(key, value);
        }
        this.threshold = this.calculateThreshold(this.data.length, this.loadFactor);
    }

    //-----------------------------------------------------------------------
    /**
     * Clones the map without cloning the keys or values.
     * <p/>
     * To implement <code>clone()</code>, a subclass must implement the
     * <code>Cloneable</code> interface and make this method public.
     *
     * @return a shallow clone
     */
    @Override
	protected Object clone() {
        try {
            AbstractHashedMap cloned = (AbstractHashedMap) super.clone();
            cloned.data = new AbstractHashedMap.HashEntry[this.data.length];
            cloned.entrySet = null;
            cloned.keySet = null;
            cloned.values = null;
            cloned.modCount = 0;
            cloned.size = 0;
            cloned.init();
            cloned.putAll(this);
            return cloned;

        } catch (CloneNotSupportedException ex) {
            return null;  // should never happen
        }
    }

    /**
     * Compares this map with another.
     *
     * @param obj the object to compare to
     * @return true if equal
     */
    @Override
	public boolean equals(Object obj) {
        if (obj == this) {
            return true;
        }
        if (obj instanceof Map == false) {
            return false;
        }
        Map map = (Map) obj;
        if (map.size() != this.size()) {
            return false;
        }
        MapIterator it = this.mapIterator();
        try {
            while (it.hasNext()) {
                Object key = it.next();
                Object value = it.getValue();
                if (value == null) {
                    if (map.get(key) != null || map.containsKey(key) == false) {
                        return false;
                    }
                } else {
                    if (value.equals(map.get(key)) == false) {
                        return false;
                    }
                }
            }
        } catch (ClassCastException ignored) {
            return false;
        } catch (NullPointerException ignored) {
            return false;
        }
        return true;
    }

    /**
     * Gets the standard Map hashCode.
     *
     * @return the hash code defined in the Map interface
     */
    @Override
	public int hashCode() {
        int total = 0;
        Iterator it = this.createEntrySetIterator();
        while (it.hasNext()) {
            total += it.next().hashCode();
        }
        return total;
    }

    /**
     * Gets the map as a String.
     *
     * @return a string version of the map
     */
    @Override
	public String toString() {
        if (this.size() == 0) {
            return "{}";
        }
        StringBuilder buf = new StringBuilder(32 * this.size());
        buf.append('{');

        MapIterator it = this.mapIterator();
        boolean hasNext = it.hasNext();
        while (hasNext) {
            Object key = it.next();
            Object value = it.getValue();
            buf.append(key == this ? "(this Map)" : key).append('=').append(value == this ? "(this Map)" : value);

            hasNext = it.hasNext();
            if (hasNext) {
                buf.append(',').append(' ');
            }
        }

        buf.append('}');
        return buf.toString();
    }
}
