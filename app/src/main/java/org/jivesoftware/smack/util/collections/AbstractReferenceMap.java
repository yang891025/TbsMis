// Converted, with some major refactors required. Not as memory-efficient as before, could use additional refactoring.
// Perhaps use four different types of HashEntry classes for max efficiency:
//   normal HashEntry for HARD,HARD
//   HardRefEntry for HARD,(SOFT|WEAK)
//   RefHardEntry for (SOFT|WEAK),HARD
//   RefRefEntry for (SOFT|WEAK),(SOFT|WEAK)
/*
 *  Copyright 2002-2004 The Apache Software Foundation
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
import java.lang.ref.Reference;
import java.lang.ref.ReferenceQueue;
import java.lang.ref.SoftReference;
import java.lang.ref.WeakReference;
import java.util.*;

/**
 * An abstract implementation of a hash-based map that allows the entries to
 * be removed by the garbage collector.
 * <p/>
 * This class implements all the features necessary for a subclass reference
 * hash-based map. Key-value entries are stored in instances of the
 * <code>ReferenceEntry</code> class which can be overridden and replaced.
 * The iterators can similarly be replaced, without the need to replace the KeySet,
 * EntrySet and Values view classes.
 * <p/>
 * Overridable methods are provided to change the default hashing behaviour, and
 * to change how entries are added to and removed from the map. Hopefully, all you
 * need for unusual subclasses is here.
 * <p/>
 * When you construct an <code>AbstractReferenceMap</code>, you can specify what
 * kind of references are used to store the map's keys and values.
 * If non-hard references are used, then the garbage collector can remove
 * mappings if a key or value becomes unreachable, or if the JVM's memory is
 * running low. For information on how the different reference types behave,
 * see {@link Reference}.
 * <p/>
 * Different types of references can be specified for keys and values.
 * The keys can be configured to be weak but the values hard,
 * in which case this class will behave like a
 * <a href="http://java.sun.com/j2se/1.4/docs/api/java/util/WeakHashMap.html">
 * <code>WeakHashMap</code></a>. However, you can also specify hard keys and
 * weak values, or any other combination. The default constructor uses
 * hard keys and soft values, providing a memory-sensitive cache.
 * <p/>
 * This {@link Map} implementation does <i>not</i> allow null elements.
 * Attempting to add a null key or value to the map will raise a
 * <code>NullPointerException</code>.
 * <p/>
 * All the available iterators can be reset back to the start by casting to
 * <code>ResettableIterator</code> and calling <code>reset()</code>.
 * <p/>
 * This implementation is not synchronized.
 * You can use {@link Collections#synchronizedMap} to
 * provide synchronized access to a <code>ReferenceMap</code>.
 *
 * @author Paul Jack
 * @author Matt Hall, John Watkinson, Stephen Colebourne
 * @version $Revision: 1.1 $ $Date: 2005/10/11 17:05:32 $
 * @see Reference
 * @since Commons Collections 3.1 (extracted from ReferenceMap in 3.0)
 */
public abstract class AbstractReferenceMap <K,V> extends AbstractHashedMap<K, V> {

    /**
     * Constant indicating that hard references should be used
     */
    public static final int HARD = 0;

    /**
     * Constant indicating that soft references should be used
     */
    public static final int SOFT = 1;

    /**
     * Constant indicating that weak references should be used
     */
    public static final int WEAK = 2;

    /**
     * The reference type for keys.  Must be HARD, SOFT, WEAK.
     *
     * @serial
     */
    protected int keyType;

    /**
     * The reference type for values.  Must be HARD, SOFT, WEAK.
     *
     * @serial
     */
    protected int valueType;

    /**
     * Should the value be automatically purged when the associated key has been collected?
     */
    protected boolean purgeValues;

    /**
     * ReferenceQueue used to eliminate stale mappings.
     * See purge.
     */
    private transient ReferenceQueue queue;

    //-----------------------------------------------------------------------
    /**
     * Constructor used during deserialization.
     */
    protected AbstractReferenceMap() {
    }

    /**
     * Constructs a new empty map with the specified reference types,
     * load factor and initial capacity.
     *
     * @param keyType     the type of reference to use for keys;
     *                    must be {@link #SOFT} or {@link #WEAK}
     * @param valueType   the type of reference to use for values;
     *                    must be {@link #SOFT} or {@link #WEAK}
     * @param capacity    the initial capacity for the map
     * @param loadFactor  the load factor for the map
     * @param purgeValues should the value be automatically purged when the
     *                    key is garbage collected
     */
    protected AbstractReferenceMap(int keyType, int valueType, int capacity, float loadFactor, boolean purgeValues) {
        super(capacity, loadFactor);
        AbstractReferenceMap.verify("keyType", keyType);
        AbstractReferenceMap.verify("valueType", valueType);
        this.keyType = keyType;
        this.valueType = valueType;
        this.purgeValues = purgeValues;
    }

    /**
     * Initialise this subclass during construction, cloning or deserialization.
     */
    @Override
	protected void init() {
        this.queue = new ReferenceQueue();
    }

    //-----------------------------------------------------------------------
    /**
     * Checks the type int is a valid value.
     *
     * @param name the name for error messages
     * @param type the type value to check
     * @throws IllegalArgumentException if the value if invalid
     */
    private static void verify(String name, int type) {
        if (type < AbstractReferenceMap.HARD || type > AbstractReferenceMap.WEAK) {
            throw new IllegalArgumentException(name + " must be HARD, SOFT, WEAK.");
        }
    }

    //-----------------------------------------------------------------------
    /**
     * Gets the size of the map.
     *
     * @return the size
     */
    @Override
	public int size() {
        this.purgeBeforeRead();
        return super.size();
    }

    /**
     * Checks whether the map is currently empty.
     *
     * @return true if the map is currently size zero
     */
    @Override
	public boolean isEmpty() {
        this.purgeBeforeRead();
        return super.isEmpty();
    }

    /**
     * Checks whether the map contains the specified key.
     *
     * @param key the key to search for
     * @return true if the map contains the key
     */
    @Override
	public boolean containsKey(Object key) {
        this.purgeBeforeRead();
        Map.Entry entry = this.getEntry(key);
        if (entry == null) {
            return false;
        }
        return entry.getValue() != null;
    }

    /**
     * Checks whether the map contains the specified value.
     *
     * @param value the value to search for
     * @return true if the map contains the value
     */
    @Override
	public boolean containsValue(Object value) {
        this.purgeBeforeRead();
        if (value == null) {
            return false;
        }
        return super.containsValue(value);
    }

    /**
     * Gets the value mapped to the key specified.
     *
     * @param key the key
     * @return the mapped value, null if no match
     */
    @Override
	public V get(Object key) {
        this.purgeBeforeRead();
        Map.Entry<K, V> entry = this.getEntry(key);
        if (entry == null) {
            return null;
        }
        return entry.getValue();
    }


    /**
     * Puts a key-value mapping into this map.
     * Neither the key nor the value may be null.
     *
     * @param key   the key to add, must not be null
     * @param value the value to add, must not be null
     * @return the value previously mapped to this key, null if none
     * @throws NullPointerException if either the key or value is null
     */
    @Override
	public V put(K key, V value) {
        if (key == null) {
            throw new NullPointerException("null keys not allowed");
        }
        if (value == null) {
            throw new NullPointerException("null values not allowed");
        }

        this.purgeBeforeWrite();
        return super.put(key, value);
    }

    /**
     * Removes the specified mapping from this map.
     *
     * @param key the mapping to remove
     * @return the value mapped to the removed key, null if key not in map
     */
    @Override
	public V remove(Object key) {
        if (key == null) {
            return null;
        }
        this.purgeBeforeWrite();
        return super.remove(key);
    }

    /**
     * Clears this map.
     */
    @Override
	public void clear() {
        super.clear();
        while (this.queue.poll() != null) {
        } // drain the queue
    }

    //-----------------------------------------------------------------------
    /**
     * Gets a MapIterator over the reference map.
     * The iterator only returns valid key/value pairs.
     *
     * @return a map iterator
     */
    @Override
	public MapIterator<K, V> mapIterator() {
        return new AbstractReferenceMap.ReferenceMapIterator<K, V>(this);
    }

    /**
     * Returns a set view of this map's entries.
     * An iterator returned entry is valid until <code>next()</code> is called again.
     * The <code>setValue()</code> method on the <code>toArray</code> entries has no effect.
     *
     * @return a set view of this map's entries
     */
    @Override
	public Set<Map.Entry<K, V>> entrySet() {
        if (this.entrySet == null) {
            this.entrySet = new AbstractReferenceMap.ReferenceEntrySet<K, V>(this);
        }
        return this.entrySet;
    }

    /**
     * Returns a set view of this map's keys.
     *
     * @return a set view of this map's keys
     */
    @Override
	public Set<K> keySet() {
        if (this.keySet == null) {
            this.keySet = new AbstractReferenceMap.ReferenceKeySet<K, V>(this);
        }
        return this.keySet;
    }

    /**
     * Returns a collection view of this map's values.
     *
     * @return a set view of this map's values
     */
    @Override
	public Collection<V> values() {
        if (this.values == null) {
            this.values = new AbstractReferenceMap.ReferenceValues<K, V>(this);
        }
        return this.values;
    }

    //-----------------------------------------------------------------------
    /**
     * Purges stale mappings from this map before read operations.
     * <p/>
     * This implementation calls {@link #purge()} to maintain a consistent state.
     */
    protected void purgeBeforeRead() {
        this.purge();
    }

    /**
     * Purges stale mappings from this map before write operations.
     * <p/>
     * This implementation calls {@link #purge()} to maintain a consistent state.
     */
    protected void purgeBeforeWrite() {
        this.purge();
    }

    /**
     * Purges stale mappings from this map.
     * <p/>
     * Note that this method is not synchronized!  Special
     * care must be taken if, for instance, you want stale
     * mappings to be removed on a periodic basis by some
     * background thread.
     */
    protected void purge() {
        Reference ref = this.queue.poll();
        while (ref != null) {
            this.purge(ref);
            ref = this.queue.poll();
        }
    }

    /**
     * Purges the specified reference.
     *
     * @param ref the reference to purge
     */
    protected void purge(Reference ref) {
        // The hashCode of the reference is the hashCode of the
        // mapping key, even if the reference refers to the
        // mapping value...
        int hash = ref.hashCode();
        int index = this.hashIndex(hash, this.data.length);
        AbstractHashedMap.HashEntry<K, V> previous = null;
        AbstractHashedMap.HashEntry<K, V> entry = this.data[index];
        while (entry != null) {
            if (((AbstractReferenceMap.ReferenceEntry<K, V>) entry).purge(ref)) {
                if (previous == null) {
                    this.data[index] = entry.next;
                } else {
                    previous.next = entry.next;
                }
                size--;
                return;
            }
            previous = entry;
            entry = entry.next;
        }

    }

    //-----------------------------------------------------------------------
    /**
     * Gets the entry mapped to the key specified.
     *
     * @param key the key
     * @return the entry, null if no match
     */
    @Override
	protected AbstractHashedMap.HashEntry<K, V> getEntry(Object key) {
        if (key == null) {
            return null;
        } else {
            return super.getEntry(key);
        }
    }

    /**
     * Gets the hash code for a MapEntry.
     * Subclasses can override this, for example to use the identityHashCode.
     *
     * @param key   the key to get a hash code for, may be null
     * @param value the value to get a hash code for, may be null
     * @return the hash code, as per the MapEntry specification
     */
    protected int hashEntry(Object key, Object value) {
        return (key == null ? 0 : key.hashCode()) ^ (value == null ? 0 : value.hashCode());
    }

    /**
     * Compares two keys, in internal converted form, to see if they are equal.
     * <p/>
     * This implementation converts the key from the entry to a real reference
     * before comparison.
     *
     * @param key1 the first key to compare passed in from outside
     * @param key2 the second key extracted from the entry via <code>entry.key</code>
     * @return true if equal
     */
    @Override
	protected boolean isEqualKey(Object key1, Object key2) {
        //if ((key1 == null) && (key2 != null) || (key1 != null) || (key2 == null)) {
        //    return false;
        //}
        // GenericsNote: Conversion from reference handled by getKey() which replaced all .key references
        //key2 = (keyType > HARD ? ((Reference) key2).get() : key2);
        return key1 == key2 || key1.equals(key2);
    }

    /**
     * Creates a ReferenceEntry instead of a HashEntry.
     *
     * @param next     the next entry in sequence
     * @param hashCode the hash code to use
     * @param key      the key to store
     * @param value    the value to store
     * @return the newly created entry
     */
    @Override
	public AbstractHashedMap.HashEntry<K, V> createEntry(AbstractHashedMap.HashEntry<K, V> next, int hashCode, K key, V value) {
        return new AbstractReferenceMap.ReferenceEntry<K, V>(this, (AbstractReferenceMap.ReferenceEntry<K, V>) next, hashCode, key, value);
    }

    /**
     * Creates an entry set iterator.
     *
     * @return the entrySet iterator
     */
    @Override
	protected Iterator<Map.Entry<K, V>> createEntrySetIterator() {
        return new AbstractReferenceMap.ReferenceEntrySetIterator<K, V>(this);
    }

    /**
     * Creates an key set iterator.
     *
     * @return the keySet iterator
     */
    @Override
	protected Iterator<K> createKeySetIterator() {
        return new AbstractReferenceMap.ReferenceKeySetIterator<K, V>(this);
    }

    /**
     * Creates an values iterator.
     *
     * @return the values iterator
     */
    @Override
	protected Iterator<V> createValuesIterator() {
        return new AbstractReferenceMap.ReferenceValuesIterator<K, V>(this);
    }

    //-----------------------------------------------------------------------
    /**
     * EntrySet implementation.
     */
    static class ReferenceEntrySet <K,V> extends AbstractHashedMap.EntrySet<K, V> {

        protected ReferenceEntrySet(AbstractHashedMap<K, V> parent) {
            super(parent);
        }

        @Override
		public Object[] toArray() {
            return this.toArray(new Object[0]);
        }

        @Override
		public <T> T[] toArray(T[] arr) {
            // special implementation to handle disappearing entries
            ArrayList<Map.Entry<K, V>> list = new ArrayList<Map.Entry<K, V>>();
            Iterator<Map.Entry<K, V>> iterator = this.iterator();
            while (iterator.hasNext()) {
                Map.Entry<K, V> e = iterator.next();
                list.add(new DefaultMapEntry<K, V>(e.getKey(), e.getValue()));
            }
            return list.toArray(arr);
        }
    }

    //-----------------------------------------------------------------------
    /**
     * KeySet implementation.
     */
    static class ReferenceKeySet <K,V> extends AbstractHashedMap.KeySet<K, V> {

        protected ReferenceKeySet(AbstractHashedMap<K, V> parent) {
            super(parent);
        }

        @Override
		public Object[] toArray() {
            return this.toArray(new Object[0]);
        }

        @Override
		public <T> T[] toArray(T[] arr) {
            // special implementation to handle disappearing keys
            List<K> list = new ArrayList<K>(this.parent.size());
            for (Iterator<K> it = this.iterator(); it.hasNext();) {
                list.add(it.next());
            }
            return list.toArray(arr);
        }
    }

    //-----------------------------------------------------------------------
    /**
     * Values implementation.
     */
    static class ReferenceValues <K,V> extends AbstractHashedMap.Values<K, V> {

        protected ReferenceValues(AbstractHashedMap<K, V> parent) {
            super(parent);
        }

        @Override
		public Object[] toArray() {
            return this.toArray(new Object[0]);
        }

        @Override
		public <T> T[] toArray(T[] arr) {
            // special implementation to handle disappearing values
            List<V> list = new ArrayList<V>(this.parent.size());
            for (Iterator<V> it = this.iterator(); it.hasNext();) {
                list.add(it.next());
            }
            return list.toArray(arr);
        }
    }

    //-----------------------------------------------------------------------
    /**
     * A MapEntry implementation for the map.
     * <p/>
     * If getKey() or getValue() returns null, it means
     * the mapping is stale and should be removed.
     *
     * @since Commons Collections 3.1
     */
    protected static class ReferenceEntry <K,V> extends AbstractHashedMap.HashEntry<K, V> {
        /**
         * The parent map
         */
        protected final AbstractReferenceMap<K, V> parent;

        protected Reference<K> refKey;
        protected Reference<V> refValue;

        /**
         * Creates a new entry object for the ReferenceMap.
         *
         * @param parent   the parent map
         * @param next     the next entry in the hash bucket
         * @param hashCode the hash code of the key
         * @param key      the key
         * @param value    the value
         */
        public ReferenceEntry(AbstractReferenceMap<K, V> parent, AbstractReferenceMap.ReferenceEntry<K, V> next, int hashCode, K key, V value) {
            super(next, hashCode, null, null);
            this.parent = parent;
            if (parent.keyType != AbstractReferenceMap.HARD) {
                this.refKey = this.toReference(parent.keyType, key, hashCode);
            } else {
                setKey(key);
            }
            if (parent.valueType != AbstractReferenceMap.HARD) {
                this.refValue = this.toReference(parent.valueType, value, hashCode); // the key hashCode is passed in deliberately
            } else {
                setValue(value);
            }
        }

        /**
         * Gets the key from the entry.
         * This method dereferences weak and soft keys and thus may return null.
         *
         * @return the key, which may be null if it was garbage collected
         */
        @Override
		public K getKey() {
            return this.parent.keyType > AbstractReferenceMap.HARD ? this.refKey.get() : super.getKey();
        }

        /**
         * Gets the value from the entry.
         * This method dereferences weak and soft value and thus may return null.
         *
         * @return the value, which may be null if it was garbage collected
         */
        @Override
		public V getValue() {
            return this.parent.valueType > AbstractReferenceMap.HARD ? this.refValue.get() : super.getValue();
        }

        /**
         * Sets the value of the entry.
         *
         * @param obj the object to store
         * @return the previous value
         */
        @Override
		public V setValue(V obj) {
            V old = this.getValue();
            if (this.parent.valueType > AbstractReferenceMap.HARD) {
                this.refValue.clear();
                this.refValue = this.toReference(this.parent.valueType, obj, this.hashCode);
            } else {
                super.setValue(obj);
            }
            return old;
        }

        /**
         * Compares this map entry to another.
         * <p/>
         * This implementation uses <code>isEqualKey</code> and
         * <code>isEqualValue</code> on the main map for comparison.
         *
         * @param obj the other map entry to compare to
         * @return true if equal, false if not
         */
        @Override
		public boolean equals(Object obj) {
            if (obj == this) {
                return true;
            }
            if (obj instanceof Map.Entry == false) {
                return false;
            }

            Map.Entry entry = (Map.Entry) obj;
            Object entryKey = entry.getKey();  // convert to hard reference
            Object entryValue = entry.getValue();  // convert to hard reference
            if (entryKey == null || entryValue == null) {
                return false;
            }
            // compare using map methods, aiding identity subclass
            // note that key is direct access and value is via method
            return this.parent.isEqualKey(entryKey, this.getKey()) && this.parent.isEqualValue(entryValue, this.getValue());
        }

        /**
         * Gets the hashcode of the entry using temporary hard references.
         * <p/>
         * This implementation uses <code>hashEntry</code> on the main map.
         *
         * @return the hashcode of the entry
         */
        @Override
		public int hashCode() {
            return this.parent.hashEntry(this.getKey(), this.getValue());
        }

        /**
         * Constructs a reference of the given type to the given referent.
         * The reference is registered with the queue for later purging.
         *
         * @param type     HARD, SOFT or WEAK
         * @param referent the object to refer to
         * @param hash     the hash code of the <i>key</i> of the mapping;
         *                 this number might be different from referent.hashCode() if
         *                 the referent represents a value and not a key
         */
        protected <T> Reference<T> toReference(int type, T referent, int hash) {
            switch (type) {
                case AbstractReferenceMap.SOFT:
                    return new AbstractReferenceMap.SoftRef<T>(hash, referent, this.parent.queue);
                case AbstractReferenceMap.WEAK:
                    return new AbstractReferenceMap.WeakRef<T>(hash, referent, this.parent.queue);
                default:
                    throw new Error("Attempt to create hard reference in ReferenceMap!");
            }
        }

        /**
         * Purges the specified reference
         *
         * @param ref the reference to purge
         * @return true or false
         */
        boolean purge(Reference ref) {
            boolean r = this.parent.keyType > AbstractReferenceMap.HARD && this.refKey == ref;
            r = r || this.parent.valueType > AbstractReferenceMap.HARD && this.refValue == ref;
            if (r) {
                if (this.parent.keyType > AbstractReferenceMap.HARD) {
                    this.refKey.clear();
                }
                if (this.parent.valueType > AbstractReferenceMap.HARD) {
                    this.refValue.clear();
                } else if (this.parent.purgeValues) {
                    this.setValue(null);
                }
            }
            return r;
        }

        /**
         * Gets the next entry in the bucket.
         *
         * @return the next entry in the bucket
         */
        protected AbstractReferenceMap.ReferenceEntry<K, V> next() {
            return (AbstractReferenceMap.ReferenceEntry<K, V>) this.next;
        }
    }

    //-----------------------------------------------------------------------
    /**
     * The EntrySet iterator.
     */
    static class ReferenceIteratorBase <K,V> {
        /**
         * The parent map
         */
        final AbstractReferenceMap<K, V> parent;

        // These fields keep track of where we are in the table.
        int index;
        AbstractReferenceMap.ReferenceEntry<K, V> entry;
        AbstractReferenceMap.ReferenceEntry<K, V> previous;

        // These Object fields provide hard references to the
        // current and next entry; this assures that if hasNext()
        // returns true, next() will actually return a valid element.
        K nextKey;
        V nextValue;
        K currentKey;
        V currentValue;

        int expectedModCount;

        public ReferenceIteratorBase(AbstractReferenceMap<K, V> parent) {
            this.parent = parent;
            this.index = parent.size() != 0 ? parent.data.length : 0;
            // have to do this here!  size() invocation above
            // may have altered the modCount.
            this.expectedModCount = parent.modCount;
        }

        public boolean hasNext() {
            this.checkMod();
            while (this.nextNull()) {
                AbstractReferenceMap.ReferenceEntry<K, V> e = this.entry;
                int i = this.index;
                while (e == null && i > 0) {
                    i--;
                    e = (AbstractReferenceMap.ReferenceEntry<K, V>) this.parent.data[i];
                }
                this.entry = e;
                this.index = i;
                if (e == null) {
                    this.currentKey = null;
                    this.currentValue = null;
                    return false;
                }
                this.nextKey = e.getKey();
                this.nextValue = e.getValue();
                if (this.nextNull()) {
                    this.entry = this.entry.next();
                }
            }
            return true;
        }

        private void checkMod() {
            if (this.parent.modCount != this.expectedModCount) {
                throw new ConcurrentModificationException();
            }
        }

        private boolean nextNull() {
            return this.nextKey == null || this.nextValue == null;
        }

        protected AbstractReferenceMap.ReferenceEntry<K, V> nextEntry() {
            this.checkMod();
            if (this.nextNull() && !this.hasNext()) {
                throw new NoSuchElementException();
            }
            this.previous = this.entry;
            this.entry = this.entry.next();
            this.currentKey = this.nextKey;
            this.currentValue = this.nextValue;
            this.nextKey = null;
            this.nextValue = null;
            return this.previous;
        }

        protected AbstractReferenceMap.ReferenceEntry<K, V> currentEntry() {
            this.checkMod();
            return this.previous;
        }

        public AbstractReferenceMap.ReferenceEntry<K, V> superNext() {
            return this.nextEntry();
        }

        public void remove() {
            this.checkMod();
            if (this.previous == null) {
                throw new IllegalStateException();
            }
            this.parent.remove(this.currentKey);
            this.previous = null;
            this.currentKey = null;
            this.currentValue = null;
            this.expectedModCount = this.parent.modCount;
        }
    }

    /**
     * The EntrySet iterator.
     */
    static class ReferenceEntrySetIterator <K,V> extends AbstractReferenceMap.ReferenceIteratorBase<K, V> implements Iterator<Map.Entry<K, V>> {

        public ReferenceEntrySetIterator(AbstractReferenceMap<K, V> abstractReferenceMap) {
            super(abstractReferenceMap);
        }

        @Override
		public AbstractReferenceMap.ReferenceEntry<K, V> next() {
            return this.superNext();
        }

    }

    /**
     * The keySet iterator.
     */
    static class ReferenceKeySetIterator <K,V> extends AbstractReferenceMap.ReferenceIteratorBase<K, V> implements Iterator<K> {

        ReferenceKeySetIterator(AbstractReferenceMap<K, V> parent) {
            super(parent);
        }

        @Override
		public K next() {
            return this.nextEntry().getKey();
        }
    }

    /**
     * The values iterator.
     */
    static class ReferenceValuesIterator <K,V> extends AbstractReferenceMap.ReferenceIteratorBase<K, V> implements Iterator<V> {

        ReferenceValuesIterator(AbstractReferenceMap<K, V> parent) {
            super(parent);
        }

        @Override
		public V next() {
            return this.nextEntry().getValue();
        }
    }

    /**
     * The MapIterator implementation.
     */
    static class ReferenceMapIterator <K,V> extends AbstractReferenceMap.ReferenceIteratorBase<K, V> implements MapIterator<K, V> {

        protected ReferenceMapIterator(AbstractReferenceMap<K, V> parent) {
            super(parent);
        }

        @Override
		public K next() {
            return this.nextEntry().getKey();
        }

        @Override
		public K getKey() {
            AbstractHashedMap.HashEntry<K, V> current = this.currentEntry();
            if (current == null) {
                throw new IllegalStateException(AbstractHashedMap.GETKEY_INVALID);
            }
            return current.getKey();
        }

        @Override
		public V getValue() {
            AbstractHashedMap.HashEntry<K, V> current = this.currentEntry();
            if (current == null) {
                throw new IllegalStateException(AbstractHashedMap.GETVALUE_INVALID);
            }
            return current.getValue();
        }

        @Override
		public V setValue(V value) {
            AbstractHashedMap.HashEntry<K, V> current = this.currentEntry();
            if (current == null) {
                throw new IllegalStateException(AbstractHashedMap.SETVALUE_INVALID);
            }
            return current.setValue(value);
        }
    }

    //-----------------------------------------------------------------------
    // These two classes store the hashCode of the key of
    // of the mapping, so that after they're dequeued a quick
    // lookup of the bucket in the table can occur.

    /**
     * A soft reference holder.
     */
    static class SoftRef <T> extends SoftReference<T> {
        /**
         * the hashCode of the key (even if the reference points to a value)
         */
        private final int hash;

        public SoftRef(int hash, T r, ReferenceQueue q) {
            super(r, q);
            this.hash = hash;
        }

        @Override
		public int hashCode() {
            return this.hash;
        }
    }

    /**
     * A weak reference holder.
     */
    static class WeakRef <T> extends WeakReference<T> {
        /**
         * the hashCode of the key (even if the reference points to a value)
         */
        private final int hash;

        public WeakRef(int hash, T r, ReferenceQueue q) {
            super(r, q);
            this.hash = hash;
        }

        @Override
		public int hashCode() {
            return this.hash;
        }
    }

    //-----------------------------------------------------------------------
    /**
     * Replaces the superclass method to store the state of this class.
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
    @Override
	protected void doWriteObject(ObjectOutputStream out) throws IOException {
        out.writeInt(this.keyType);
        out.writeInt(this.valueType);
        out.writeBoolean(this.purgeValues);
        out.writeFloat(this.loadFactor);
        out.writeInt(this.data.length);
        for (MapIterator it = this.mapIterator(); it.hasNext();) {
            out.writeObject(it.next());
            out.writeObject(it.getValue());
        }
        out.writeObject(null);  // null terminate map
        // do not call super.doWriteObject() as code there doesn't work for reference map
    }

    /**
     * Replaces the superclassm method to read the state of this class.
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
    @Override
	protected void doReadObject(ObjectInputStream in) throws IOException, ClassNotFoundException {
        keyType = in.readInt();
        valueType = in.readInt();
        purgeValues = in.readBoolean();
        loadFactor = in.readFloat();
        int capacity = in.readInt();
        this.init();
        this.data = new AbstractHashedMap.HashEntry[capacity];
        while (true) {
            K key = (K) in.readObject();
            if (key == null) {
                break;
            }
            V value = (V) in.readObject();
            this.put(key, value);
        }
        this.threshold = this.calculateThreshold(this.data.length, this.loadFactor);
        // do not call super.doReadObject() as code there doesn't work for reference map
    }

}
