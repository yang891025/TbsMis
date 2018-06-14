// Copyright (c) 2003-2004 Brian Wellington (bwelling@xbill.org)

package org.xbill.DNS;

import java.io.*;
import java.util.*;

/**
 * A helper class for constructing dynamic DNS (DDNS) update messages.
 *
 * @author Brian Wellington
 */

public class Update extends Message {

private final Name origin;
private final int dclass;

/**
 * Creates an update message.
 * @param zone The name of the zone being updated.
 * @param dclass The class of the zone being updated.
 */
public
Update(Name zone, int dclass) {
    if (!zone.isAbsolute())
		throw new RelativeNameException(zone);
	DClass.check(dclass);
    this.getHeader().setOpcode(Opcode.UPDATE);
	Record soa = Record.newRecord(zone, Type.SOA, DClass.IN);
    this.addRecord(soa, Section.QUESTION);
    origin = zone;
	this.dclass = dclass;
}

/**
 * Creates an update message.  The class is assumed to be IN.
 * @param zone The name of the zone being updated.
 */
public
Update(Name zone) {
	this(zone, DClass.IN);
}

private void
newPrereq(Record rec) {
    this.addRecord(rec, Section.PREREQ);
}

private void
newUpdate(Record rec) {
    this.addRecord(rec, Section.UPDATE);
}

/**
 * Inserts a prerequisite that the specified name exists; that is, there
 * exist records with the given name in the zone.
 */
public void
present(Name name) {
    this.newPrereq(Record.newRecord(name, Type.ANY, DClass.ANY, 0));
}

/**
 * Inserts a prerequisite that the specified rrset exists; that is, there
 * exist records with the given name and type in the zone.
 */
public void
present(Name name, int type) {
    this.newPrereq(Record.newRecord(name, type, DClass.ANY, 0));
}

/**
 * Parses a record from the string, and inserts a prerequisite that the
 * record exists.  Due to the way value-dependent prequisites work, the
 * condition that must be met is that the set of all records with the same 
 * and type in the update message must be identical to the set of all records
 * with that name and type on the server.
 * @throws IOException The record could not be parsed.
 */
public void
present(Name name, int type, String record) throws IOException {
    this.newPrereq(Record.fromString(name, type, this.dclass, 0, record, this.origin));
}

/**
 * Parses a record from the tokenizer, and inserts a prerequisite that the
 * record exists.  Due to the way value-dependent prequisites work, the
 * condition that must be met is that the set of all records with the same 
 * and type in the update message must be identical to the set of all records
 * with that name and type on the server.
 * @throws IOException The record could not be parsed.
 */
public void
present(Name name, int type, Tokenizer tokenizer) throws IOException {
    this.newPrereq(Record.fromString(name, type, this.dclass, 0, tokenizer, this.origin));
}

/**
 * Inserts a prerequisite that the specified record exists.  Due to the way
 * value-dependent prequisites work, the condition that must be met is that
 * the set of all records with the same and type in the update message must
 * be identical to the set of all records with that name and type on the server.
 */
public void
present(Record record) {
    this.newPrereq(record);
}

/**
 * Inserts a prerequisite that the specified name does not exist; that is,
 * there are no records with the given name in the zone.
 */
public void
absent(Name name) {
    this.newPrereq(Record.newRecord(name, Type.ANY, DClass.NONE, 0));
}

/**
 * Inserts a prerequisite that the specified rrset does not exist; that is,
 * there are no records with the given name and type in the zone.
 */
public void
absent(Name name, int type) {
    this.newPrereq(Record.newRecord(name, type, DClass.NONE, 0));
}

/**
 * Parses a record from the string, and indicates that the record
 * should be inserted into the zone.
 * @throws IOException The record could not be parsed.
 */
public void
add(Name name, int type, long ttl, String record) throws IOException {
    this.newUpdate(Record.fromString(name, type, this.dclass, ttl, record, this.origin));
}

/**
 * Parses a record from the tokenizer, and indicates that the record
 * should be inserted into the zone.
 * @throws IOException The record could not be parsed.
 */
public void
add(Name name, int type, long ttl, Tokenizer tokenizer) throws IOException {
    this.newUpdate(Record.fromString(name, type, this.dclass, ttl, tokenizer,
            this.origin));
}

/**
 * Indicates that the record should be inserted into the zone.
 */
public void
add(Record record) {
    this.newUpdate(record);
}

/**
 * Indicates that the records should be inserted into the zone.
 */
public void
add(Record [] records) {
	for (int i = 0; i < records.length; i++)
        this.add(records[i]);
}

/**
 * Indicates that all of the records in the rrset should be inserted into the
 * zone.
 */
public void
add(RRset rrset) {
	for (Iterator it = rrset.rrs(); it.hasNext(); )
        this.add((Record) it.next());
}

/**
 * Indicates that all records with the given name should be deleted from
 * the zone.
 */
public void
delete(Name name) {
    this.newUpdate(Record.newRecord(name, Type.ANY, DClass.ANY, 0));
}

/**
 * Indicates that all records with the given name and type should be deleted
 * from the zone.
 */
public void
delete(Name name, int type) {
    this.newUpdate(Record.newRecord(name, type, DClass.ANY, 0));
}

/**
 * Parses a record from the string, and indicates that the record
 * should be deleted from the zone.
 * @throws IOException The record could not be parsed.
 */
public void
delete(Name name, int type, String record) throws IOException {
    this.newUpdate(Record.fromString(name, type, DClass.NONE, 0, record,
            this.origin));
}

/**
 * Parses a record from the tokenizer, and indicates that the record
 * should be deleted from the zone.
 * @throws IOException The record could not be parsed.
 */
public void
delete(Name name, int type, Tokenizer tokenizer) throws IOException {
    this.newUpdate(Record.fromString(name, type, DClass.NONE, 0, tokenizer,
            this.origin));
}

/**
 * Indicates that the specified record should be deleted from the zone.
 */
public void
delete(Record record) {
    this.newUpdate(record.withDClass(DClass.NONE, 0));
}

/**
 * Indicates that the records should be deleted from the zone.
 */
public void
delete(Record [] records) {
	for (int i = 0; i < records.length; i++)
        this.delete(records[i]);
}

/**
 * Indicates that all of the records in the rrset should be deleted from the
 * zone.
 */
public void
delete(RRset rrset) {
	for (Iterator it = rrset.rrs(); it.hasNext(); )
        this.delete((Record) it.next());
}

/**
 * Parses a record from the string, and indicates that the record
 * should be inserted into the zone replacing any other records with the
 * same name and type.
 * @throws IOException The record could not be parsed.
 */
public void
replace(Name name, int type, long ttl, String record) throws IOException {
    this.delete(name, type);
    this.add(name, type, ttl, record);
}

/**
 * Parses a record from the tokenizer, and indicates that the record
 * should be inserted into the zone replacing any other records with the
 * same name and type.
 * @throws IOException The record could not be parsed.
 */
public void
replace(Name name, int type, long ttl, Tokenizer tokenizer) throws IOException
{
    this.delete(name, type);
    this.add(name, type, ttl, tokenizer);
}

/**
 * Indicates that the record should be inserted into the zone replacing any
 * other records with the same name and type.
 */
public void
replace(Record record) {
    this.delete(record.getName(), record.getType());
    this.add(record);
}

/**
 * Indicates that the records should be inserted into the zone replacing any
 * other records with the same name and type as each one.
 */
public void
replace(Record [] records) {
	for (int i = 0; i < records.length; i++)
        this.replace(records[i]);
}

/**
 * Indicates that all of the records in the rrset should be inserted into the
 * zone replacing any other records with the same name and type.
 */
public void
replace(RRset rrset) {
    this.delete(rrset.getName(), rrset.getType());
	for (Iterator it = rrset.rrs(); it.hasNext(); )
        this.add((Record) it.next());
}

}
