/**
 * $Revision: 1456 $
 * $Date: 2005-06-01 22:04:54 -0700 (Wed, 01 Jun 2005) $
 *
 * Copyright 2003-2005 Jive Software.
 *
 * All rights reserved. Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.jivesoftware.smack.util;

import org.jivesoftware.smack.util.collections.AbstractMapEntry;

import java.util.*;

/**
 * A specialized Map that is size-limited (using an LRU algorithm) and
 * has an optional expiration time for cache items. The Map is thread-safe.<p>
 *
 * The algorithm for cache is as follows: a HashMap is maintained for fast
 * object lookup. Two linked lists are maintained: one keeps objects in the
 * order they are accessed from cache, the other keeps objects in the order
 * they were originally added to cache. When objects are added to cache, they
 * are first wrapped by a CacheObject which maintains the following pieces
 * of information:<ul>
 * <li> A pointer to the node in the linked list that maintains accessed
 * order for the object. Keeping a reference to the node lets us avoid
 * linear scans of the linked list.
 * <li> A pointer to the node in the linked list that maintains the age
 * of the object in cache. Keeping a reference to the node lets us avoid
 * linear scans of the linked list.</ul>
 * <p/>
 * To get an object from cache, a hash lookup is performed to get a reference
 * to the CacheObject that wraps the real object we are looking for.
 * The object is subsequently moved to the front of the accessed linked list
 * and any necessary cache cleanups are performed. Cache deletion and expiration
 * is performed as needed.
 *
 * @author Matt Tucker
 */
public class Cache<K, V> implements Map<K, V> {

    /**
     * The map the keys and values are stored in.
     */
    protected Map<K, Cache.CacheObject<V>> map;

    /**
     * Linked list to maintain order that cache objects are accessed
     * in, most used to least used.
     */
    protected Cache.LinkedList lastAccessedList;

    /**
     * Linked list to maintain time that cache objects were initially added
     * to the cache, most recently added to oldest added.
     */
    protected Cache.LinkedList ageList;

    /**
     * Maximum number of items the cache will hold.
     */
    protected int maxCacheSize;

    /**
     * Maximum length of time objects can exist in cache before expiring.
     */
    protected long maxLifetime;

    /**
     * Maintain the number of cache hits and misses. A cache hit occurs every
     * time the get method is called and the cache contains the requested
     * object. A cache miss represents the opposite occurence.<p>
     *
     * Keeping track of cache hits and misses lets one measure how efficient
     * the cache is; the higher the percentage of hits, the more efficient.
     */
    protected long cacheHits, cacheMisses;

    /**
     * Create a new cache and specify the maximum size of for the cache in
     * bytes, and the maximum lifetime of objects.
     *
     * @param maxSize the maximum number of objects the cache will hold. -1
     *      means the cache has no max size.
     * @param maxLifetime the maximum amount of time (in ms) objects can exist in
     *      cache before being deleted. -1 means objects never expire.
     */
    public Cache(int maxSize, long maxLifetime) {
        if (maxSize == 0) {
            throw new IllegalArgumentException("Max cache size cannot be 0.");
        }
        maxCacheSize = maxSize;
        this.maxLifetime = maxLifetime;

        // Our primary data structure is a hash map. The default capacity of 11
        // is too small in almost all cases, so we set it bigger.
        this.map = new HashMap<K, Cache.CacheObject<V>>(103);

        this.lastAccessedList = new Cache.LinkedList();
        this.ageList = new Cache.LinkedList();
    }

    @Override
	public synchronized V put(K key, V value) {
        V oldValue = null;
        // Delete an old entry if it exists.
        if (this.map.containsKey(key)) {
            oldValue = this.remove(key, true);
        }

        Cache.CacheObject<V> cacheObject = new Cache.CacheObject<V>(value);
        this.map.put(key, cacheObject);
        // Make an entry into the cache order list.
        // Store the cache order list entry so that we can get back to it
        // during later lookups.
        cacheObject.lastAccessedListNode = this.lastAccessedList.addFirst(key);
        // Add the object to the age list
        Cache.LinkedListNode ageNode = this.ageList.addFirst(key);
        ageNode.timestamp = System.currentTimeMillis();
        cacheObject.ageListNode = ageNode;

        // If cache is too full, remove least used cache entries until it is not too full.
        this.cullCache();

        return oldValue;
    }

    @Override
	public synchronized V get(Object key) {
        // First, clear all entries that have been in cache longer than the
        // maximum defined age.
        this.deleteExpiredEntries();

        Cache.CacheObject<V> cacheObject = this.map.get(key);
        if (cacheObject == null) {
            // The object didn't exist in cache, so increment cache misses.
            this.cacheMisses++;
            return null;
        }
        // Remove the object from it's current place in the cache order list,
        // and re-insert it at the front of the list.
        cacheObject.lastAccessedListNode.remove();
        this.lastAccessedList.addFirst(cacheObject.lastAccessedListNode);

        // The object exists in cache, so increment cache hits. Also, increment
        // the object's read count.
        this.cacheHits++;
        cacheObject.readCount++;

        return cacheObject.object;
    }

    @Override
	public synchronized V remove(Object key) {
        return this.remove(key, false);
    }

    /*
     * Remove operation with a flag so we can tell coherence if the remove was
     * caused by cache internal processing such as eviction or loading
     */
    public synchronized V remove(Object key, boolean internal) {
        //noinspection SuspiciousMethodCalls
        Cache.CacheObject<V> cacheObject = this.map.remove(key);
        // If the object is not in cache, stop trying to remove it.
        if (cacheObject == null) {
            return null;
        }
        // Remove from the cache order list
        cacheObject.lastAccessedListNode.remove();
        cacheObject.ageListNode.remove();
        // Remove references to linked list nodes
        cacheObject.ageListNode = null;
        cacheObject.lastAccessedListNode = null;

        return cacheObject.object;
    }

    @Override
	public synchronized void clear() {
        Object[] keys = this.map.keySet().toArray();
        for (Object key : keys) {
            this.remove(key);
        }

        // Now, reset all containers.
        this.map.clear();
        this.lastAccessedList.clear();
        this.ageList.clear();

        this.cacheHits = 0;
        this.cacheMisses = 0;
    }

    @Override
	public synchronized int size() {
        // First, clear all entries that have been in cache longer than the
        // maximum defined age.
        this.deleteExpiredEntries();

        return this.map.size();
    }

    @Override
	public synchronized boolean isEmpty() {
        // First, clear all entries that have been in cache longer than the
        // maximum defined age.
        this.deleteExpiredEntries();

        return this.map.isEmpty();
    }

    @Override
	public synchronized Collection<V> values() {
        // First, clear all entries that have been in cache longer than the
        // maximum defined age.
        this.deleteExpiredEntries();

        return Collections.unmodifiableCollection(new AbstractCollection<V>() {
            Collection<Cache.CacheObject<V>> values = Cache.this.map.values();
            @Override
			public Iterator<V> iterator() {
                return new Iterator<V>() {
                    Iterator<Cache.CacheObject<V>> it = values.iterator();

                    @Override
					public boolean hasNext() {
                        return this.it.hasNext();
                    }

                    @Override
					public V next() {
                        return this.it.next().object;
                    }

                    @Override
					public void remove() {
                        this.it.remove();
                    }
                };
            }

            @Override
			public int size() {
                return this.values.size();
            }
        });
    }

    @Override
	public synchronized boolean containsKey(Object key) {
        // First, clear all entries that have been in cache longer than the
        // maximum defined age.
        this.deleteExpiredEntries();

        return this.map.containsKey(key);
    }

    @Override
	public void putAll(Map<? extends K, ? extends V> map) {
        for (Map.Entry<? extends K, ? extends V> entry : map.entrySet()) {
            V value = entry.getValue();
            // If the map is another DefaultCache instance than the
            // entry values will be CacheObject instances that need
            // to be converted to the normal object form.
            if (value instanceof Cache.CacheObject) {
                //noinspection unchecked
                value = ((Cache.CacheObject<V>) value).object;
            }
            this.put(entry.getKey(), value);
        }
    }

    @Override
	public synchronized boolean containsValue(Object value) {
        // First, clear all entries that have been in cache longer than the
        // maximum defined age.
        this.deleteExpiredEntries();

        //noinspection unchecked
        Cache.CacheObject<V> cacheObject = new Cache.CacheObject<V>((V) value);

        return this.map.containsValue(cacheObject);
    }

    @Override
	public synchronized Set<Map.Entry<K, V>> entrySet() {
        // Warning -- this method returns CacheObject instances and not Objects
        // in the same form they were put into cache.

        // First, clear all entries that have been in cache longer than the
        // maximum defined age.
        this.deleteExpiredEntries();

        return new AbstractSet<Map.Entry<K, V>>() {
            private final Set<Map.Entry<K, Cache.CacheObject<V>>> set = Cache.this.map.entrySet();

            @Override
			public Iterator<Map.Entry<K, V>> iterator() {
                return new Iterator<Map.Entry<K, V>>() {
                    private final Iterator<Map.Entry<K, Cache.CacheObject<V>>> it = set.iterator();
                    @Override
					public boolean hasNext() {
                        return this.it.hasNext();
                    }

                    @Override
					public Map.Entry<K, V> next() {
                        Map.Entry<K, Cache.CacheObject<V>> entry = this.it.next();
                        return new AbstractMapEntry<K, V>(entry.getKey(), entry.getValue().object) {
                            @Override
                            public V setValue(V value) {
                                throw new UnsupportedOperationException("Cannot set");
                            }
                        };
                    }

                    @Override
					public void remove() {
                        this.it.remove();
                    }
                };

            }

            @Override
			public int size() {
                return this.set.size();
            }
        };
    }

    @Override
	public synchronized Set<K> keySet() {
        // First, clear all entries that have been in cache longer than the
        // maximum defined age.
        this.deleteExpiredEntries();

        return Collections.unmodifiableSet(this.map.keySet());
    }

    public long getCacheHits() {
        return this.cacheHits;
    }

    public long getCacheMisses() {
        return this.cacheMisses;
    }

    public int getMaxCacheSize() {
        return this.maxCacheSize;
    }

    public synchronized void setMaxCacheSize(int maxCacheSize) {
        this.maxCacheSize = maxCacheSize;
        // It's possible that the new max size is smaller than our current cache
        // size. If so, we need to delete infrequently used items.
        this.cullCache();
    }

    public long getMaxLifetime() {
        return this.maxLifetime;
    }

    public void setMaxLifetime(long maxLifetime) {
        this.maxLifetime = maxLifetime;
    }

    /**
     * Clears all entries out of cache where the entries are older than the
     * maximum defined age.
     */
    protected synchronized void deleteExpiredEntries() {
        // Check if expiration is turned on.
        if (this.maxLifetime <= 0) {
            return;
        }

        // Remove all old entries. To do this, we remove objects from the end
        // of the linked list until they are no longer too old. We get to avoid
        // any hash lookups or looking at any more objects than is strictly
        // neccessary.
        Cache.LinkedListNode node = this.ageList.getLast();
        // If there are no entries in the age list, return.
        if (node == null) {
            return;
        }

        // Determine the expireTime, which is the moment in time that elements
        // should expire from cache. Then, we can do an easy check to see
        // if the expire time is greater than the expire time.
        long expireTime = System.currentTimeMillis() - this.maxLifetime;

        while (expireTime > node.timestamp) {
            if (this.remove(node.object, true) == null) {
                System.err.println("Error attempting to remove(" + node.object +
                ") - cacheObject not found in cache!");
                // remove from the ageList
                node.remove();
            }

            // Get the next node.
            node = this.ageList.getLast();
            // If there are no more entries in the age list, return.
            if (node == null) {
                return;
            }
        }
    }

    /**
     * Removes the least recently used elements if the cache size is greater than
     * or equal to the maximum allowed size until the cache is at least 10% empty.
     */
    protected synchronized void cullCache() {
        // Check if a max cache size is defined.
        if (this.maxCacheSize < 0) {
            return;
        }

        // See if the cache is too big. If so, clean out cache until it's 10% free.
        if (this.map.size() > this.maxCacheSize) {
            // First, delete any old entries to see how much memory that frees.
            this.deleteExpiredEntries();
            // Next, delete the least recently used elements until 10% of the cache
            // has been freed.
            int desiredSize = (int) (this.maxCacheSize * .90);
            for (int i = this.map.size(); i>desiredSize; i--) {
                // Get the key and invoke the remove method on it.
                if (this.remove(this.lastAccessedList.getLast().object, true) == null) {
                    System.err.println("Error attempting to cullCache with remove(" +
                            lastAccessedList.getLast().object + ") - " +
                            "cacheObject not found in cache!");
                    this.lastAccessedList.getLast().remove();
                }
            }
        }
    }

    /**
     * Wrapper for all objects put into cache. It's primary purpose is to maintain
     * references to the linked lists that maintain the creation time of the object
     * and the ordering of the most used objects.
     *
     * This class is optimized for speed rather than strictly correct encapsulation.
     */
    private static class CacheObject<V> {

       /**
        * Underlying object wrapped by the CacheObject.
        */
        public V object;

        /**
         * A reference to the node in the cache order list. We keep the reference
         * here to avoid linear scans of the list. Every time the object is
         * accessed, the node is removed from its current spot in the list and
         * moved to the front.
         */
        public Cache.LinkedListNode lastAccessedListNode;

        /**
         * A reference to the node in the age order list. We keep the reference
         * here to avoid linear scans of the list. The reference is used if the
         * object has to be deleted from the list.
         */
        public Cache.LinkedListNode ageListNode;

        /**
         * A count of the number of times the object has been read from cache.
         */
        public int readCount;

        /**
         * Creates a new cache object wrapper.
         *
         * @param object the underlying Object to wrap.
         */
        public CacheObject(V object) {
            this.object = object;
        }

        @Override
		public boolean equals(Object o) {
            if (this == o) {
                return true;
            }
            if (!(o instanceof Cache.CacheObject)) {
                return false;
            }

            Cache.CacheObject cacheObject = (Cache.CacheObject) o;

            return this.object.equals(cacheObject.object);

        }

        @Override
		public int hashCode() {
            return this.object.hashCode();
        }
    }

    /**
     * Simple LinkedList implementation. The main feature is that list nodes
     * are public, which allows very fast delete operations when one has a
     * reference to the node that is to be deleted.<p>
     */
    private static class LinkedList {

        /**
         * The root of the list keeps a reference to both the first and last
         * elements of the list.
         */
        private final Cache.LinkedListNode head = new Cache.LinkedListNode("head", null, null);

        /**
         * Creates a new linked list.
         */
        public LinkedList() {
            this.head.next = this.head.previous = this.head;
        }

        /**
         * Returns the first linked list node in the list.
         *
         * @return the first element of the list.
         */
        public Cache.LinkedListNode getFirst() {
            Cache.LinkedListNode node = this.head.next;
            if (node == this.head) {
                return null;
            }
            return node;
        }

        /**
         * Returns the last linked list node in the list.
         *
         * @return the last element of the list.
         */
        public Cache.LinkedListNode getLast() {
            Cache.LinkedListNode node = this.head.previous;
            if (node == this.head) {
                return null;
            }
            return node;
        }

        /**
         * Adds a node to the beginning of the list.
         *
         * @param node the node to add to the beginning of the list.
         * @return the node
         */
        public Cache.LinkedListNode addFirst(Cache.LinkedListNode node) {
            node.next = this.head.next;
            node.previous = this.head;
            node.previous.next = node;
            node.next.previous = node;
            return node;
        }

        /**
         * Adds an object to the beginning of the list by automatically creating a
         * a new node and adding it to the beginning of the list.
         *
         * @param object the object to add to the beginning of the list.
         * @return the node created to wrap the object.
         */
        public Cache.LinkedListNode addFirst(Object object) {
            Cache.LinkedListNode node = new Cache.LinkedListNode(object, this.head.next, this.head);
            node.previous.next = node;
            node.next.previous = node;
            return node;
        }

        /**
         * Adds an object to the end of the list by automatically creating a
         * a new node and adding it to the end of the list.
         *
         * @param object the object to add to the end of the list.
         * @return the node created to wrap the object.
         */
        public Cache.LinkedListNode addLast(Object object) {
            Cache.LinkedListNode node = new Cache.LinkedListNode(object, this.head, this.head.previous);
            node.previous.next = node;
            node.next.previous = node;
            return node;
        }

        /**
         * Erases all elements in the list and re-initializes it.
         */
        public void clear() {
            //Remove all references in the list.
            Cache.LinkedListNode node = this.getLast();
            while (node != null) {
                node.remove();
                node = this.getLast();
            }

            //Re-initialize.
            this.head.next = this.head.previous = this.head;
        }

        /**
         * Returns a String representation of the linked list with a comma
         * delimited list of all the elements in the list.
         *
         * @return a String representation of the LinkedList.
         */
        @Override
		public String toString() {
            Cache.LinkedListNode node = this.head.next;
            StringBuilder buf = new StringBuilder();
            while (node != this.head) {
                buf.append(node).append(", ");
                node = node.next;
            }
            return buf.toString();
        }
    }

    /**
     * Doubly linked node in a LinkedList. Most LinkedList implementations keep the
     * equivalent of this class private. We make it public so that references
     * to each node in the list can be maintained externally.
     *
     * Exposing this class lets us make remove operations very fast. Remove is
     * built into this class and only requires two reference reassignments. If
     * remove existed in the main LinkedList class, a linear scan would have to
     * be performed to find the correct node to delete.
     *
     * The linked list implementation was specifically written for the Jive
     * cache system. While it can be used as a general purpose linked list, for
     * most applications, it is more suitable to use the linked list that is part
     * of the Java Collections package.
     */
    private static class LinkedListNode {

        public Cache.LinkedListNode previous;
        public Cache.LinkedListNode next;
        public Object object;

        /**
         * This class is further customized for the Jive cache system. It
         * maintains a timestamp of when a Cacheable object was first added to
         * cache. Timestamps are stored as long values and represent the number
         * of milliseconds passed since January 1, 1970 00:00:00.000 GMT.<p>
         *
         * The creation timestamp is used in the case that the cache has a
         * maximum lifetime set. In that case, when
         * [current time] - [creation time] > [max lifetime], the object will be
         * deleted from cache.
         */
        public long timestamp;

        /**
         * Constructs a new linked list node.
         *
         * @param object the Object that the node represents.
         * @param next a reference to the next LinkedListNode in the list.
         * @param previous a reference to the previous LinkedListNode in the list.
         */
        public LinkedListNode(Object object, Cache.LinkedListNode next,
                Cache.LinkedListNode previous)
        {
            this.object = object;
            this.next = next;
            this.previous = previous;
        }

        /**
         * Removes this node from the linked list that it is a part of.
         */
        public void remove() {
            this.previous.next = this.next;
            this.next.previous = this.previous;
        }

        /**
         * Returns a String representation of the linked list node by calling the
         * toString method of the node's object.
         *
         * @return a String representation of the LinkedListNode.
         */
        @Override
		public String toString() {
            return this.object.toString();
        }
    }
}