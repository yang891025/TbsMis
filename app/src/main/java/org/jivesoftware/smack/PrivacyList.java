package org.jivesoftware.smack;

import org.jivesoftware.smack.packet.PrivacyItem;

import java.util.List;

/**
 * A privacy list represents a list of contacts that is a read only class used to represent a set of allowed or blocked communications. 
 * Basically it can:<ul>
 *
 *      <li>Handle many {@link PrivacyItem}.</li>
 *      <li>Answer if it is the default list.</li>
 *      <li>Answer if it is the active list.</li>
 * </ul>
 *
 * {@link PrivacyItem Privacy Items} can handle different kind of blocking communications based on JID, group,
 * subscription type or globally.
 * 
 * @author Francisco Vives
 */
public class PrivacyList {

    /** Holds if it is an active list or not **/
    private final boolean isActiveList;
    /** Holds if it is an default list or not **/
    private final boolean isDefaultList;
    /** Holds the list name used to print **/
    private final String listName;
    /** Holds the list of {@see PrivacyItem} **/
    private final List<PrivacyItem> items;
    
    protected PrivacyList(boolean isActiveList, boolean isDefaultList,
            String listName, List<PrivacyItem> privacyItems) {
        this.isActiveList = isActiveList;
        this.isDefaultList = isDefaultList;
        this.listName = listName;
        items = privacyItems;
    }

    public boolean isActiveList() {
        return this.isActiveList;
    }

    public boolean isDefaultList() {
        return this.isDefaultList;
    }

    public List<PrivacyItem> getItems() {
        return this.items;
    }

    @Override
	public String toString() {
        return this.listName;
    }

}
