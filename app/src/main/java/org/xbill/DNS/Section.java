// Copyright (c) 1999-2004 Brian Wellington (bwelling@xbill.org)

package org.xbill.DNS;

/**
 * Constants and functions relating to DNS message sections
 *
 * @author Brian Wellington
 */

public final class Section {

/** The question (first) section */
public static final int QUESTION	= 0;

/** The answer (second) section */
public static final int ANSWER		= 1;

/** The authority (third) section */
public static final int AUTHORITY	= 2;

/** The additional (fourth) section */
public static final int ADDITIONAL	= 3;

/* Aliases for dynamic update */
/** The zone (first) section of a dynamic update message */
public static final int ZONE		= 0;

/** The prerequisite (second) section of a dynamic update message */
public static final int PREREQ		= 1;

/** The update (third) section of a dynamic update message */
public static final int UPDATE		= 2;

private static final Mnemonic sections = new Mnemonic("Message Section",
						Mnemonic.CASE_LOWER);
private static final String [] longSections = new String[4];
private static final String [] updateSections = new String[4];

static {
    Section.sections.setMaximum(3);
    Section.sections.setNumericAllowed(true);

    Section.sections.add(Section.QUESTION, "qd");
    Section.sections.add(Section.ANSWER, "an");
    Section.sections.add(Section.AUTHORITY, "au");
    Section.sections.add(Section.ADDITIONAL, "ad");

    Section.longSections[Section.QUESTION]		= "QUESTIONS";
    Section.longSections[Section.ANSWER]		= "ANSWERS";
    Section.longSections[Section.AUTHORITY]		= "AUTHORITY RECORDS";
    Section.longSections[Section.ADDITIONAL]	= "ADDITIONAL RECORDS";

    Section.updateSections[Section.ZONE]		= "ZONE";
    Section.updateSections[Section.PREREQ]		= "PREREQUISITES";
    Section.updateSections[Section.UPDATE]		= "UPDATE RECORDS";
    Section.updateSections[Section.ADDITIONAL]	= "ADDITIONAL RECORDS";
}

private
Section() {}

/** Converts a numeric Section into an abbreviation String */
public static String
string(int i) {
	return Section.sections.getText(i);
}

/** Converts a numeric Section into a full description String */
public static String
longString(int i) {
    Section.sections.check(i);
	return Section.longSections[i];
}

/**
 * Converts a numeric Section into a full description String for an update
 * Message.
 */
public static String
updString(int i) {
    Section.sections.check(i);
	return Section.updateSections[i];
}

/** Converts a String representation of a Section into its numeric value */
public static int
value(String s) {
	return Section.sections.getValue(s);
}

}
