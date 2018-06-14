package org.jivesoftware.smack;

import java.util.List;

import org.jivesoftware.smack.packet.RosterPacket;
import org.jivesoftware.smack.packet.RosterPacket.Item;

/**
 * This is an interface for persistent roster storage needed to implement XEP-0237
 * @author Till Klocke
 *
 */

public interface RosterStorage {
	
	/**
	 * This method returns a List object with all RosterEntries contained in this store.
	 * @return List object with all entries in local roster storage
	 */
    List<Item> getEntries();
	/**
	 * This method returns the RosterEntry which belongs to a specific user.
	 * @param bareJid The bare JID of the RosterEntry
	 * @return The RosterEntry which belongs to that user
	 */
    Item getEntry(String bareJid);
	/**
	 * Returns the number of entries in this roster store
	 * @return the number of entries
	 */
    int getEntryCount();
	/**
	 * This methos returns the version number as specified by the "ver" attribute
	 * of the local store. Should return an emtpy string if store is empty.
	 * @return local roster version
	 */
    String getRosterVersion();
	/**
	 * This method stores a new RosterEntry in this store or overrides an existing one.
	 * If ver is null an IllegalArgumentException should be thrown.
	 * @param entry the entry to save
	 * @param ver the version this roster push contained
	 */
    void addEntry(Item item, String ver);
	/**
	 * Removes an entry from the persistent storage
	 * @param bareJid The bare JID of the entry to be removed
	 */
    void removeEntry(String bareJid);
	/**
	 * Update an entry which has been modified locally
	 * @param entry the entry to be updated
	 */
    void updateLocalEntry(Item item);
}
