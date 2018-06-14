/**
 * $Revision$
 * $Date$
 *
 * Copyright 2006-2007 Jive Software.
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

import java.util.*;
import java.util.Map.Entry;

/**
 * A Privacy IQ Packet, is used by the {@link org.jivesoftware.smack.PrivacyListManager}
 * and {@link org.jivesoftware.smack.provider.PrivacyProvider} to allow and block
 * communications from other users. It contains the appropriate structure to suit
 * user-defined privacy lists. Different configured Privacy packages are used in the
 * server & manager communication in order to:
 * <ul>
 * <li>Retrieving one's privacy lists. 
 * <li>Adding, removing, and editing one's privacy lists. 
 * <li>Setting, changing, or declining active lists. 
 * <li>Setting, changing, or declining the default list (i.e., the list that is active by default). 
 * </ul>
 * Privacy Items can handle different kind of blocking communications based on JID, group, 
 * subscription type or globally {@link PrivacyItem}
 * 
 * @author Francisco Vives
 */
public class Privacy extends IQ {
	/** declineActiveList is true when the user declines the use of the active list **/
	private boolean declineActiveList;
	/** activeName is the name associated with the active list set for the session **/
	private String activeName;
	/** declineDefaultList is true when the user declines the use of the default list **/
	private boolean declineDefaultList;
	/** defaultName is the name of the default list that applies to the user as a whole **/
	private String defaultName;
	/** itemLists holds the set of privacy items classified in lists. It is a map where the 
	 * key is the name of the list and the value a collection with privacy items. **/
	private final Map<String, List<PrivacyItem>> itemLists = new HashMap<String, List<PrivacyItem>>();

    /**
     * Set or update a privacy list with privacy items.
     *
     * @param listName the name of the new privacy list.
     * @param listItem the {@link PrivacyItem} that rules the list.
     * @return the privacy List.
     */
    public List setPrivacyList(String listName, List<PrivacyItem> listItem) {
        // Add new list to the itemLists
        getItemLists().put(listName, listItem);
        return listItem;
    }

    /**
     * Set the active list based on the default list.
     *
     * @return the active List.
     */
    public List<PrivacyItem> setActivePrivacyList() {
        setActiveName(getDefaultName());
        return getItemLists().get(getActiveName());
    }

    /**
     * Deletes an existing privacy list. If the privacy list being deleted was the default list 
     * then the user will end up with no default list. Therefore, the user will have to set a new 
     * default list.
     *
     * @param listName the name of the list being deleted.
     */
    public void deletePrivacyList(String listName) {
        // Remove the list from the cache
        getItemLists().remove(listName);

        // Check if deleted list was the default list
        if (getDefaultName() != null && listName.equals(getDefaultName())) {
            setDefaultName(null);
        }
    }

    /**
     * Returns the active privacy list or <tt>null</tt> if none was found.
     *
     * @return list with {@link PrivacyItem} or <tt>null</tt> if none was found.
     */
    public List<PrivacyItem> getActivePrivacyList() {
        // Check if we have the default list
        if (getActiveName() == null) {
        	return null;
        } else {
        	return getItemLists().get(getActiveName());
        }
    }

    /**
     * Returns the default privacy list or <tt>null</tt> if none was found.
     *
     * @return list with {@link PrivacyItem} or <tt>null</tt> if none was found.
     */
    public List<PrivacyItem> getDefaultPrivacyList() {
        // Check if we have the default list
        if (getDefaultName() == null) {
        	return null;
        } else {
        	return getItemLists().get(getDefaultName());
        }
    }

    /**
     * Returns a specific privacy list.
     *
     * @param listName the name of the list to get.
     * @return a List with {@link PrivacyItem}
     */
    public List<PrivacyItem> getPrivacyList(String listName) {
        return getItemLists().get(listName);
    }

    /**
     * Returns the privacy item in the specified order.
     *
     * @param listName the name of the privacy list.
     * @param order the order of the element.
     * @return a List with {@link PrivacyItem}
     */
    public PrivacyItem getItem(String listName, int order) {
    	Iterator<PrivacyItem> values = this.getPrivacyList(listName).iterator();
    	PrivacyItem itemFound = null;
    	while (itemFound == null && values.hasNext()) {
    		PrivacyItem element = values.next();
			if (element.getOrder() == order) {
				itemFound = element;
			}
		}
    	return itemFound;
    }

    /**
     * Sets a given privacy list as the new user default list.
     *
     * @param newDefault the new default privacy list.
     * @return if the default list was changed.
     */
    public boolean changeDefaultList(String newDefault) {
        if (getItemLists().containsKey(newDefault)) {
            setDefaultName(newDefault);
           return true;
        } else {
        	return false; 
        }
    }

    /**
     * Remove the list.
     *
     * @param listName name of the list to remove.
     */
     public void deleteList(String listName) {
         getItemLists().remove(listName);
     }

    /**
     * Returns the name associated with the active list set for the session. Communications
     * will be verified against the active list.
     *
     * @return the name of the active list.
     */
	public String getActiveName() {
		return this.activeName;
	}

    /**
     * Sets the name associated with the active list set for the session. Communications
     * will be verified against the active list.
     * 
     * @param activeName is the name of the active list.
     */
	public void setActiveName(String activeName) {
		this.activeName = activeName;
	}

    /**
     * Returns the name of the default list that applies to the user as a whole. Default list is 
     * processed if there is no active list set for the target session/resource to which a stanza 
     * is addressed, or if there are no current sessions for the user.
     * 
     * @return the name of the default list.
     */
	public String getDefaultName() {
		return this.defaultName;
	}

    /**
     * Sets the name of the default list that applies to the user as a whole. Default list is 
     * processed if there is no active list set for the target session/resource to which a stanza 
     * is addressed, or if there are no current sessions for the user.
     * 
     * If there is no default list set, then all Privacy Items are processed.
     * 
     * @param defaultName is the name of the default list.
     */
	public void setDefaultName(String defaultName) {
		this.defaultName = defaultName;
	}

    /**
     * Returns the collection of privacy list that the user holds. A Privacy List contains a set of 
     * rules that define if communication with the list owner is allowed or denied. 
     * Users may have zero, one or more privacy items.
     * 
     * @return a map where the key is the name of the list and the value the 
     * collection of privacy items.
     */
	public Map<String, List<PrivacyItem>> getItemLists() {
		return this.itemLists;
	}

    /** 
     * Returns whether the receiver allows or declines the use of an active list.
     * 
     * @return the decline status of the list.
     */
	public boolean isDeclineActiveList() {
		return this.declineActiveList;
	}

    /** 
     * Sets whether the receiver allows or declines the use of an active list.
     * 
     * @param declineActiveList indicates if the receiver declines the use of an active list.
     */
	public void setDeclineActiveList(boolean declineActiveList) {
		this.declineActiveList = declineActiveList;
	}

    /** 
     * Returns whether the receiver allows or declines the use of a default list.
     * 
     * @return the decline status of the list.
     */
	public boolean isDeclineDefaultList() {
		return this.declineDefaultList;
	}

    /** 
     * Sets whether the receiver allows or declines the use of a default list.
     * 
     * @param declineDefaultList indicates if the receiver declines the use of a default list.
     */
	public void setDeclineDefaultList(boolean declineDefaultList) {
		this.declineDefaultList = declineDefaultList;
	}

	/** 
     * Returns all the list names the user has defined to group restrictions.
     * 
     * @return a Set with Strings containing every list names.
     */
	public Set<String> getPrivacyListNames() {
		return itemLists.keySet();
	}
	
	@Override
	public String getChildElementXML() {
        StringBuilder buf = new StringBuilder();
        buf.append("<query xmlns=\"jabber:iq:privacy\">");
        
        // Add the active tag
        if (isDeclineActiveList()) {
        	buf.append("<active/>");
        } else {
        	if (getActiveName() != null) {
            	buf.append("<active name=\"").append(getActiveName()).append("\"/>");
            }
        }
        // Add the default tag
        if (isDeclineDefaultList()) {
        	buf.append("<default/>");
        } else {
	        if (getDefaultName() != null) {
	        	buf.append("<default name=\"").append(getDefaultName()).append("\"/>");
	        }
        }
        
        // Add the list with their privacy items
        for (Entry<String, List<PrivacyItem>> entry : getItemLists().entrySet()) {
          String listName = entry.getKey();
          List<PrivacyItem> items = entry.getValue();
			// Begin the list tag
			if (items.isEmpty()) {
				buf.append("<list name=\"").append(listName).append("\"/>");
			} else {
				buf.append("<list name=\"").append(listName).append("\">");
			}
	        for (PrivacyItem item : items) {
	        	// Append the item xml representation
	        	buf.append(item.toXML());
	        }
	        // Close the list tag
	        if (!items.isEmpty()) {
				buf.append("</list>");
			}
		}

        // Add packet extensions, if any are defined.
        buf.append(this.getExtensionsXML());
        buf.append("</query>");
        return buf.toString();
    }
    
}