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

/**
 * Implements the logical OR operation over two or more packet filters. In
 * other words, packets pass this filter if they pass <b>any</b> of the filters.
 *
 * @author Matt Tucker
 */
public class OrFilter implements PacketFilter {

    /**
     * The current number of elements in the filter.
     */
    private int size;

    /**
     * The list of filters.
     */
    private PacketFilter [] filters;

    /**
     * Creates an empty OR filter. Filters should be added using the
     * {@link #addFilter(PacketFilter)} method.
     */
    public OrFilter() {
        this.size = 0;
        this.filters = new PacketFilter[3];
    }

    /**
     * Creates an OR filter using the two specified filters.
     *
     * @param filter1 the first packet filter.
     * @param filter2 the second packet filter.
     */
    public OrFilter(PacketFilter filter1, PacketFilter filter2) {
        if (filter1 == null || filter2 == null) {
            throw new IllegalArgumentException("Parameters cannot be null.");
        }
        this.size = 2;
        this.filters = new PacketFilter[2];
        this.filters[0] = filter1;
        this.filters[1] = filter2;
    }

    /**
     * Adds a filter to the filter list for the OR operation. A packet
     * will pass the filter if any filter in the list accepts it.
     *
     * @param filter a filter to add to the filter list.
     */
    public void addFilter(PacketFilter filter) {
        if (filter == null) {
            throw new IllegalArgumentException("Parameter cannot be null.");
        }
        // If there is no more room left in the filters array, expand it.
        if (this.size == this.filters.length) {
            PacketFilter [] newFilters = new PacketFilter[this.filters.length+2];
            for (int i = 0; i< this.filters.length; i++) {
                newFilters[i] = this.filters[i];
            }
            this.filters = newFilters;
        }
        // Add the new filter to the array.
        this.filters[this.size] = filter;
        this.size++;
    }

    @Override
	public boolean accept(Packet packet) {
        for (int i = 0; i< this.size; i++) {
            if (this.filters[i].accept(packet)) {
                return true;
            }
        }
        return false;
    }

    @Override
	public String toString() {
        return this.filters.toString();
    }
}