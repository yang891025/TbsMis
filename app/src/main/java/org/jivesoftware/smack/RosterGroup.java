/**
 * $RCSfile$
 * $Revision$
 * $Date$
 *
 * Copyright 2003-2007 Jive Software.
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

package org.jivesoftware.smack;

import org.jivesoftware.smack.filter.PacketIDFilter;
import org.jivesoftware.smack.packet.IQ;
import org.jivesoftware.smack.packet.IQ.Type;
import org.jivesoftware.smack.packet.RosterPacket;
import org.jivesoftware.smack.packet.RosterPacket.Item;
import org.jivesoftware.smack.util.StringUtils;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

/**
 * A group of roster entries.
 *
 * @see Roster#getGroup(String)
 * @author Matt Tucker
 */
public class RosterGroup {

    private final String name;
    private final Connection connection;
    private final List<RosterEntry> entries;

    /**
     * Creates a new roster group instance.
     *
     * @param name the name of the group.
     * @param connection the connection the group belongs to.
     */
    RosterGroup(String name, Connection connection) {
        this.name = name;
        this.connection = connection;
        this.entries = new ArrayList<RosterEntry>();
    }

    /**
     * Returns the name of the group.
     *
     * @return the name of the group.
     */
    public String getName() {
        return this.name;
    }

    /**
     * Sets the name of the group. Changing the group's name is like moving all the group entries
     * of the group to a new group specified by the new name. Since this group won't have entries 
     * it will be removed from the roster. This means that all the references to this object will 
     * be invalid and will need to be updated to the new group specified by the new name.
     *
     * @param name the name of the group.
     */
    public void setName(String name) {
        synchronized (this.entries) {
            for (RosterEntry entry : this.entries) {
                RosterPacket packet = new RosterPacket();
                packet.setType(Type.SET);
                Item item = RosterEntry.toRosterItem(entry);
                item.removeGroupName(this.name);
                item.addGroupName(name);
                packet.addRosterItem(item);
                this.connection.sendPacket(packet);
            }
        }
    }

    /**
     * Returns the number of entries in the group.
     *
     * @return the number of entries in the group.
     */
    public int getEntryCount() {
        synchronized (this.entries) {
            return this.entries.size();
        }
    }

    /**
     * Returns an unmodifiable collection of all entries in the group.
     *
     * @return all entries in the group.
     */
    public Collection<RosterEntry> getEntries() {
        synchronized (this.entries) {
            return Collections.unmodifiableList(new ArrayList<RosterEntry>(this.entries));
        }
    }

    /**
     * Returns the roster entry associated with the given XMPP address or
     * <tt>null</tt> if the user is not an entry in the group.
     *
     * @param user the XMPP address of the user (eg "jsmith@example.com").
     * @return the roster entry or <tt>null</tt> if it does not exist in the group.
     */
    public RosterEntry getEntry(String user) {
        if (user == null) {
            return null;
        }
        // Roster entries never include a resource so remove the resource
        // if it's a part of the XMPP address.
        user = StringUtils.parseBareAddress(user);
        String userLowerCase = user.toLowerCase();
        synchronized (this.entries) {
            for (RosterEntry entry : this.entries) {
                if (entry.getUser().equals(userLowerCase)) {
                    return entry;
                }
            }
        }
        return null;
    }

    /**
     * Returns true if the specified entry is part of this group.
     *
     * @param entry a roster entry.
     * @return true if the entry is part of this group.
     */
    public boolean contains(RosterEntry entry) {
        synchronized (this.entries) {
            return this.entries.contains(entry);
        }
    }

    /**
     * Returns true if the specified XMPP address is an entry in this group.
     *
     * @param user the XMPP address of the user.
     * @return true if the XMPP address is an entry in this group.
     */
    public boolean contains(String user) {
        return this.getEntry(user) != null;
    }

    /**
     * Adds a roster entry to this group. If the entry was unfiled then it will be removed from 
     * the unfiled list and will be added to this group.
     * Note that this is an asynchronous call -- Smack must wait for the server
     * to receive the updated roster.
     *
     * @param entry a roster entry.
     * @throws XMPPException if an error occured while trying to add the entry to the group.
     */
    public void addEntry(RosterEntry entry) throws XMPPException {
        PacketCollector collector = null;
        // Only add the entry if it isn't already in the list.
        synchronized (this.entries) {
            if (!this.entries.contains(entry)) {
                RosterPacket packet = new RosterPacket();
                packet.setType(Type.SET);
                Item item = RosterEntry.toRosterItem(entry);
                item.addGroupName(this.getName());
                packet.addRosterItem(item);
                // Wait up to a certain number of seconds for a reply from the server.
                collector = this.connection
                        .createPacketCollector(new PacketIDFilter(packet.getPacketID()));
                this.connection.sendPacket(packet);
            }
        }
        if (collector != null) {
            IQ response = (IQ) collector.nextResult(SmackConfiguration.getPacketReplyTimeout());
            collector.cancel();
            if (response == null) {
                throw new XMPPException("No response from the server.");
            }
            // If the server replied with an error, throw an exception.
            else if (response.getType() == Type.ERROR) {
                throw new XMPPException(response.getError());
            }
        }
    }

    /**
     * Removes a roster entry from this group. If the entry does not belong to any other group 
     * then it will be considered as unfiled, therefore it will be added to the list of unfiled 
     * entries.
     * Note that this is an asynchronous call -- Smack must wait for the server
     * to receive the updated roster.
     *
     * @param entry a roster entry.
     * @throws XMPPException if an error occured while trying to remove the entry from the group. 
     */
    public void removeEntry(RosterEntry entry) throws XMPPException {
        PacketCollector collector = null;
        // Only remove the entry if it's in the entry list.
        // Remove the entry locally, if we wait for RosterPacketListenerprocess>>Packet(Packet)
        // to take place the entry will exist in the group until a packet is received from the 
        // server.
        synchronized (this.entries) {
            if (this.entries.contains(entry)) {
                RosterPacket packet = new RosterPacket();
                packet.setType(Type.SET);
                Item item = RosterEntry.toRosterItem(entry);
                item.removeGroupName(getName());
                packet.addRosterItem(item);
                // Wait up to a certain number of seconds for a reply from the server.
                collector = this.connection
                        .createPacketCollector(new PacketIDFilter(packet.getPacketID()));
                this.connection.sendPacket(packet);
            }
        }
        if (collector != null) {
            IQ response = (IQ) collector.nextResult(SmackConfiguration.getPacketReplyTimeout());
            collector.cancel();
            if (response == null) {
                throw new XMPPException("No response from the server.");
            }
            // If the server replied with an error, throw an exception.
            else if (response.getType() == Type.ERROR) {
                throw new XMPPException(response.getError());
            }
        }
    }

    public void addEntryLocal(RosterEntry entry) {
        // Only add the entry if it isn't already in the list.
        synchronized (this.entries) {
            this.entries.remove(entry);
            this.entries.add(entry);
        }
    }

    void removeEntryLocal(RosterEntry entry) {
         // Only remove the entry if it's in the entry list.
        synchronized (this.entries) {
            if (this.entries.contains(entry)) {
                this.entries.remove(entry);
            }
        }
    }
}