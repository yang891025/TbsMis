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

package org.jivesoftware.smack.packet;

import org.jivesoftware.smack.util.StringUtils;

import java.util.*;
import java.util.concurrent.CopyOnWriteArraySet;

/**
 * Represents XMPP roster packets.
 *
 * @author Matt Tucker
 */
public class RosterPacket extends IQ {

    private final List<RosterPacket.Item> rosterItems = new ArrayList<RosterPacket.Item>();
    /*
     * The ver attribute following XEP-0237
     */
    private String version;

    /**
     * Adds a roster item to the packet.
     *
     * @param item a roster item.
     */
    public void addRosterItem(RosterPacket.Item item) {
        synchronized (this.rosterItems) {
            this.rosterItems.add(item);
        }
    }

    public String getVersion(){
    	return this.version;
    }

    public void setVersion(String version){
    	this.version = version;
    }

    /**
     * Returns the number of roster items in this roster packet.
     *
     * @return the number of roster items.
     */
    public int getRosterItemCount() {
        synchronized (this.rosterItems) {
            return this.rosterItems.size();
        }
    }

    /**
     * Returns an unmodifiable collection for the roster items in the packet.
     *
     * @return an unmodifiable collection for the roster items in the packet.
     */
    public Collection<RosterPacket.Item> getRosterItems() {
        synchronized (this.rosterItems) {
            return Collections.unmodifiableList(new ArrayList<RosterPacket.Item>(this.rosterItems));
        }
    }

    @Override
	public String getChildElementXML() {
        StringBuilder buf = new StringBuilder();
        buf.append("<query xmlns=\"jabber:iq:roster\" ");
        if(this.version !=null){
        	buf.append(" ver=\""+ this.version +"\" ");
        }
        buf.append(">");
        synchronized (this.rosterItems) {
            for (RosterPacket.Item entry : this.rosterItems) {
                buf.append(entry.toXML());
            }
        }
        buf.append("</query>");
        return buf.toString();
    }

    /**
     * A roster item, which consists of a JID, their name, the type of subscription, and
     * the groups the roster item belongs to.
     */
    public static class Item {

        private final String user;
        private String name;
        private RosterPacket.ItemType itemType;
        private RosterPacket.ItemStatus itemStatus;
        private final Set<String> groupNames;

        /**
         * Creates a new roster item.
         *
         * @param user the user.
         * @param name the user's name.
         */
        public Item(String user, String name) {
            this.user = user.toLowerCase();
            this.name = name;
            this.itemType = null;
            this.itemStatus = null;
            this.groupNames = new CopyOnWriteArraySet<String>();
        }

        /**
         * Returns the user.
         *
         * @return the user.
         */
        public String getUser() {
            return this.user;
        }

        /**
         * Returns the user's name.
         *
         * @return the user's name.
         */
        public String getName() {
            return this.name;
        }

        /**
         * Sets the user's name.
         *
         * @param name the user's name.
         */
        public void setName(String name) {
            this.name = name;
        }

        /**
         * Returns the roster item type.
         *
         * @return the roster item type.
         */
        public RosterPacket.ItemType getItemType() {
            return this.itemType;
        }

        /**
         * Sets the roster item type.
         *
         * @param itemType the roster item type.
         */
        public void setItemType(RosterPacket.ItemType itemType) {
            this.itemType = itemType;
        }

        /**
         * Returns the roster item status.
         *
         * @return the roster item status.
         */
        public RosterPacket.ItemStatus getItemStatus() {
            return this.itemStatus;
        }

        /**
         * Sets the roster item status.
         *
         * @param itemStatus the roster item status.
         */
        public void setItemStatus(RosterPacket.ItemStatus itemStatus) {
            this.itemStatus = itemStatus;
        }

        /**
         * Returns an unmodifiable set of the group names that the roster item
         * belongs to.
         *
         * @return an unmodifiable set of the group names.
         */
        public Set<String> getGroupNames() {
            return Collections.unmodifiableSet(this.groupNames);
        }

        /**
         * Adds a group name.
         *
         * @param groupName the group name.
         */
        public void addGroupName(String groupName) {
            this.groupNames.add(groupName);
        }

        /**
         * Removes a group name.
         *
         * @param groupName the group name.
         */
        public void removeGroupName(String groupName) {
            this.groupNames.remove(groupName);
        }

        public String toXML() {
            StringBuilder buf = new StringBuilder();
            buf.append("<item jid=\"").append(this.user).append("\"");
            if (this.name != null) {
                buf.append(" name=\"").append(StringUtils.escapeForXML(this.name)).append("\"");
            }
            if (this.itemType != null) {
                buf.append(" subscription=\"").append(this.itemType).append("\"");
            }
            if (this.itemStatus != null) {
                buf.append(" ask=\"").append(this.itemStatus).append("\"");
            }
            buf.append(">");
            for (String groupName : this.groupNames) {
                buf.append("<group>").append(StringUtils.escapeForXML(groupName)).append("</group>");
            }
            buf.append("</item>");
            return buf.toString();
        }
    }

    /**
     * The subscription status of a roster item. An optional element that indicates
     * the subscription status if a change request is pending.
     */
    public static class ItemStatus {

        /**
         * Request to subcribe.
         */
        public static final RosterPacket.ItemStatus SUBSCRIPTION_PENDING = new RosterPacket.ItemStatus("subscribe");

        /**
         * Request to unsubscribe.
         */
        public static final RosterPacket.ItemStatus UNSUBSCRIPTION_PENDING = new RosterPacket.ItemStatus("unsubscribe");

        public static RosterPacket.ItemStatus fromString(String value) {
            if (value == null) {
                return null;
            }
            value = value.toLowerCase();
            if ("unsubscribe".equals(value)) {
                return RosterPacket.ItemStatus.UNSUBSCRIPTION_PENDING;
            }
            else if ("subscribe".equals(value)) {
                return RosterPacket.ItemStatus.SUBSCRIPTION_PENDING;
            }
            else {
                return null;
            }
        }

        private final String value;

        /**
         * Returns the item status associated with the specified string.
         *
         * @param value the item status.
         */
        private ItemStatus(String value) {
            this.value = value;
        }

        @Override
		public String toString() {
            return this.value;
        }
    }

    public enum ItemType {

        /**
         * The user and subscriber have no interest in each other's presence.
         */
        none,

        /**
         * The user is interested in receiving presence updates from the subscriber.
         */
        to,

        /**
         * The subscriber is interested in receiving presence updates from the user.
         */
        from,

        /**
         * The user and subscriber have a mutual interest in each other's presence.
         */
        both,

        /**
         * The user wishes to stop receiving presence updates from the subscriber.
         */
        remove
    }
}
