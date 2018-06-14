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

package org.jivesoftware.smack.filter;

import org.jivesoftware.smack.packet.Packet;
import org.jivesoftware.smack.util.StringUtils;

/**
 * Filter for packets where the "from" field exactly matches a specified JID. If the specified
 * address is a bare JID then the filter will match any address whose bare JID matches the
 * specified JID. But if the specified address is a full JID then the filter will only match
 * if the sender of the packet matches the specified resource.
 *
 * @author Gaston Dombiak
 */
public class FromMatchesFilter implements PacketFilter {

    private final String address;
    /**
     * Flag that indicates if the checking will be done against bare JID addresses or full JIDs.
     */
    private boolean matchBareJID;

    /**
     * Creates a "from" filter using the "from" field part. If the specified address is a bare JID
     * then the filter will match any address whose bare JID matches the specified JID. But if the
     * specified address is a full JID then the filter will only match if the sender of the packet
     * matches the specified resource.
     *
     * @param address the from field value the packet must match. Could be a full or bare JID.
     */
    public FromMatchesFilter(String address) {
        if (address == null) {
            throw new IllegalArgumentException("Parameter cannot be null.");
        }
        this.address = address.toLowerCase();
        this.matchBareJID = "".equals(StringUtils.parseResource(address));
    }

    @Override
	public boolean accept(Packet packet) {
        if (packet.getFrom() == null) {
            return false;
        }
        else if (this.matchBareJID) {
            // Check if the bare JID of the sender of the packet matches the specified JID
            return packet.getFrom().toLowerCase().startsWith(this.address);
        }
        else {
            // Check if the full JID of the sender of the packet matches the specified JID
            return this.address.equals(packet.getFrom().toLowerCase());
        }
    }

    @Override
	public String toString() {
        return "FromMatchesFilter: " + this.address;
    }
}
