// Copyright (c) 1999-2004 Brian Wellington (bwelling@xbill.org)

package org.xbill.DNS;

import org.xbill.DNS.Tokenizer.Token;

import java.net.*;
import java.io.*;
import java.util.*;

/**
 * Well Known Services - Lists services offered by this host.
 *
 * @author Brian Wellington
 */

public class WKSRecord extends Record {

private static final long serialVersionUID = -9104259763909119805L;

public static class Protocol {
	/**
	 * IP protocol identifiers.  This is basically copied out of RFC 1010.
	 */

	private Protocol() {}

	/** Internet Control Message */
	public static final int ICMP = 1;

	/** Internet Group Management */
	public static final int IGMP = 2;

	/** Gateway-to-Gateway */
	public static final int GGP = 3;

	/** Stream */
	public static final int ST = 5;

	/** Transmission Control */
	public static final int TCP = 6;

	/** UCL */
	public static final int UCL = 7;

	/** Exterior Gateway Protocol */
	public static final int EGP = 8;

	/** any private interior gateway */
	public static final int IGP = 9;

	/** BBN RCC Monitoring */
	public static final int BBN_RCC_MON = 10;

	/** Network Voice Protocol */
	public static final int NVP_II = 11;

	/** PUP */
	public static final int PUP = 12;

	/** ARGUS */
	public static final int ARGUS = 13;

	/** EMCON */
	public static final int EMCON = 14;

	/** Cross Net Debugger */
	public static final int XNET = 15;

	/** Chaos */
	public static final int CHAOS = 16;

	/** User Datagram */
	public static final int UDP = 17;

	/** Multiplexing */
	public static final int MUX = 18;

	/** DCN Measurement Subsystems */
	public static final int DCN_MEAS = 19;

	/** Host Monitoring */
	public static final int HMP = 20;

	/** Packet Radio Measurement */
	public static final int PRM = 21;

	/** XEROX NS IDP */
	public static final int XNS_IDP = 22;

	/** Trunk-1 */
	public static final int TRUNK_1 = 23;

	/** Trunk-2 */
	public static final int TRUNK_2 = 24;

	/** Leaf-1 */
	public static final int LEAF_1 = 25;

	/** Leaf-2 */
	public static final int LEAF_2 = 26;

	/** Reliable Data Protocol */
	public static final int RDP = 27;

	/** Internet Reliable Transaction */
	public static final int IRTP = 28;

	/** ISO Transport Protocol Class 4 */
	public static final int ISO_TP4 = 29;

	/** Bulk Data Transfer Protocol */
	public static final int NETBLT = 30;

	/** MFE Network Services Protocol */
	public static final int MFE_NSP = 31;

	/** MERIT Internodal Protocol */
	public static final int MERIT_INP = 32;

	/** Sequential Exchange Protocol */
	public static final int SEP = 33;

	/** CFTP */
	public static final int CFTP = 62;

	/** SATNET and Backroom EXPAK */
	public static final int SAT_EXPAK = 64;

	/** MIT Subnet Support */
	public static final int MIT_SUBNET = 65;

	/** MIT Remote Virtual Disk Protocol */
	public static final int RVD = 66;

	/** Internet Pluribus Packet Core */
	public static final int IPPC = 67;

	/** SATNET Monitoring */
	public static final int SAT_MON = 69;

	/** Internet Packet Core Utility */
	public static final int IPCV = 71;

	/** Backroom SATNET Monitoring */
	public static final int BR_SAT_MON = 76;

	/** WIDEBAND Monitoring */
	public static final int WB_MON = 78;

	/** WIDEBAND EXPAK */
	public static final int WB_EXPAK = 79;

	private static final Mnemonic protocols = new Mnemonic("IP protocol",
							 Mnemonic.CASE_LOWER);

	static {
        Protocol.protocols.setMaximum(0xFF);
        Protocol.protocols.setNumericAllowed(true);

        Protocol.protocols.add(Protocol.ICMP, "icmp");
        Protocol.protocols.add(Protocol.IGMP, "igmp");
        Protocol.protocols.add(Protocol.GGP, "ggp");
        Protocol.protocols.add(Protocol.ST, "st");
        Protocol.protocols.add(Protocol.TCP, "tcp");
        Protocol.protocols.add(Protocol.UCL, "ucl");
        Protocol.protocols.add(Protocol.EGP, "egp");
        Protocol.protocols.add(Protocol.IGP, "igp");
        Protocol.protocols.add(Protocol.BBN_RCC_MON, "bbn-rcc-mon");
        Protocol.protocols.add(Protocol.NVP_II, "nvp-ii");
        Protocol.protocols.add(Protocol.PUP, "pup");
        Protocol.protocols.add(Protocol.ARGUS, "argus");
        Protocol.protocols.add(Protocol.EMCON, "emcon");
        Protocol.protocols.add(Protocol.XNET, "xnet");
        Protocol.protocols.add(Protocol.CHAOS, "chaos");
        Protocol.protocols.add(Protocol.UDP, "udp");
        Protocol.protocols.add(Protocol.MUX, "mux");
        Protocol.protocols.add(Protocol.DCN_MEAS, "dcn-meas");
        Protocol.protocols.add(Protocol.HMP, "hmp");
        Protocol.protocols.add(Protocol.PRM, "prm");
        Protocol.protocols.add(Protocol.XNS_IDP, "xns-idp");
        Protocol.protocols.add(Protocol.TRUNK_1, "trunk-1");
        Protocol.protocols.add(Protocol.TRUNK_2, "trunk-2");
        Protocol.protocols.add(Protocol.LEAF_1, "leaf-1");
        Protocol.protocols.add(Protocol.LEAF_2, "leaf-2");
        Protocol.protocols.add(Protocol.RDP, "rdp");
        Protocol.protocols.add(Protocol.IRTP, "irtp");
        Protocol.protocols.add(Protocol.ISO_TP4, "iso-tp4");
        Protocol.protocols.add(Protocol.NETBLT, "netblt");
        Protocol.protocols.add(Protocol.MFE_NSP, "mfe-nsp");
        Protocol.protocols.add(Protocol.MERIT_INP, "merit-inp");
        Protocol.protocols.add(Protocol.SEP, "sep");
        Protocol.protocols.add(Protocol.CFTP, "cftp");
        Protocol.protocols.add(Protocol.SAT_EXPAK, "sat-expak");
        Protocol.protocols.add(Protocol.MIT_SUBNET, "mit-subnet");
        Protocol.protocols.add(Protocol.RVD, "rvd");
        Protocol.protocols.add(Protocol.IPPC, "ippc");
        Protocol.protocols.add(Protocol.SAT_MON, "sat-mon");
        Protocol.protocols.add(Protocol.IPCV, "ipcv");
        Protocol.protocols.add(Protocol.BR_SAT_MON, "br-sat-mon");
        Protocol.protocols.add(Protocol.WB_MON, "wb-mon");
        Protocol.protocols.add(Protocol.WB_EXPAK, "wb-expak");
	}

	/**
	 * Converts an IP protocol value into its textual representation
	 */
	public static String
	string(int type) {
		return Protocol.protocols.getText(type);
	}

	/**
	 * Converts a textual representation of an IP protocol into its
	 * numeric code.  Integers in the range 0..255 are also accepted.
	 * @param s The textual representation of the protocol
	 * @return The protocol code, or -1 on error.
	 */
	public static int
	value(String s) {
		return Protocol.protocols.getValue(s);
	}
}

public static class Service {
	/**
	 * TCP/UDP services.  This is basically copied out of RFC 1010,
	 * with MIT-ML-DEV removed, as it is not unique, and the description
	 * of SWIFT-RVF fixed.
	 */

	private Service() {}

	/** Remote Job Entry */
	public static final int RJE = 5;

	/** Echo */
	public static final int ECHO = 7;

	/** Discard */
	public static final int DISCARD = 9;

	/** Active Users */
	public static final int USERS = 11;

	/** Daytime */
	public static final int DAYTIME = 13;

	/** Quote of the Day */
	public static final int QUOTE = 17;

	/** Character Generator */
	public static final int CHARGEN = 19;

	/** File Transfer [Default Data] */
	public static final int FTP_DATA = 20;

	/** File Transfer [Control] */
	public static final int FTP = 21;

	/** Telnet */
	public static final int TELNET = 23;

	/** Simple Mail Transfer */
	public static final int SMTP = 25;

	/** NSW User System FE */
	public static final int NSW_FE = 27;

	/** MSG ICP */
	public static final int MSG_ICP = 29;

	/** MSG Authentication */
	public static final int MSG_AUTH = 31;

	/** Display Support Protocol */
	public static final int DSP = 33;

	/** Time */
	public static final int TIME = 37;

	/** Resource Location Protocol */
	public static final int RLP = 39;

	/** Graphics */
	public static final int GRAPHICS = 41;

	/** Host Name Server */
	public static final int NAMESERVER = 42;

	/** Who Is */
	public static final int NICNAME = 43;

	/** MPM FLAGS Protocol */
	public static final int MPM_FLAGS = 44;

	/** Message Processing Module [recv] */
	public static final int MPM = 45;

	/** MPM [default send] */
	public static final int MPM_SND = 46;

	/** NI FTP */
	public static final int NI_FTP = 47;

	/** Login Host Protocol */
	public static final int LOGIN = 49;

	/** IMP Logical Address Maintenance */
	public static final int LA_MAINT = 51;

	/** Domain Name Server */
	public static final int DOMAIN = 53;

	/** ISI Graphics Language */
	public static final int ISI_GL = 55;

	/** NI MAIL */
	public static final int NI_MAIL = 61;

	/** VIA Systems - FTP */
	public static final int VIA_FTP = 63;

	/** TACACS-Database Service */
	public static final int TACACS_DS = 65;

	/** Bootstrap Protocol Server */
	public static final int BOOTPS = 67;

	/** Bootstrap Protocol Client */
	public static final int BOOTPC = 68;

	/** Trivial File Transfer */
	public static final int TFTP = 69;

	/** Remote Job Service */
	public static final int NETRJS_1 = 71;

	/** Remote Job Service */
	public static final int NETRJS_2 = 72;

	/** Remote Job Service */
	public static final int NETRJS_3 = 73;

	/** Remote Job Service */
	public static final int NETRJS_4 = 74;

	/** Finger */
	public static final int FINGER = 79;

	/** HOSTS2 Name Server */
	public static final int HOSTS2_NS = 81;

	/** SU/MIT Telnet Gateway */
	public static final int SU_MIT_TG = 89;

	/** MIT Dover Spooler */
	public static final int MIT_DOV = 91;

	/** Device Control Protocol */
	public static final int DCP = 93;

	/** SUPDUP */
	public static final int SUPDUP = 95;

	/** Swift Remote Virtual File Protocol */
	public static final int SWIFT_RVF = 97;

	/** TAC News */
	public static final int TACNEWS = 98;

	/** Metagram Relay */
	public static final int METAGRAM = 99;

	/** NIC Host Name Server */
	public static final int HOSTNAME = 101;

	/** ISO-TSAP */
	public static final int ISO_TSAP = 102;

	/** X400 */
	public static final int X400 = 103;

	/** X400-SND */
	public static final int X400_SND = 104;

	/** Mailbox Name Nameserver */
	public static final int CSNET_NS = 105;

	/** Remote Telnet Service */
	public static final int RTELNET = 107;

	/** Post Office Protocol - Version 2 */
	public static final int POP_2 = 109;

	/** SUN Remote Procedure Call */
	public static final int SUNRPC = 111;

	/** Authentication Service */
	public static final int AUTH = 113;

	/** Simple File Transfer Protocol */
	public static final int SFTP = 115;

	/** UUCP Path Service */
	public static final int UUCP_PATH = 117;

	/** Network News Transfer Protocol */
	public static final int NNTP = 119;

	/** HYDRA Expedited Remote Procedure */
	public static final int ERPC = 121;

	/** Network Time Protocol */
	public static final int NTP = 123;

	/** Locus PC-Interface Net Map Server */
	public static final int LOCUS_MAP = 125;

	/** Locus PC-Interface Conn Server */
	public static final int LOCUS_CON = 127;

	/** Password Generator Protocol */
	public static final int PWDGEN = 129;

	/** CISCO FNATIVE */
	public static final int CISCO_FNA = 130;

	/** CISCO TNATIVE */
	public static final int CISCO_TNA = 131;

	/** CISCO SYSMAINT */
	public static final int CISCO_SYS = 132;

	/** Statistics Service */
	public static final int STATSRV = 133;

	/** INGRES-NET Service */
	public static final int INGRES_NET = 134;

	/** Location Service */
	public static final int LOC_SRV = 135;

	/** PROFILE Naming System */
	public static final int PROFILE = 136;

	/** NETBIOS Name Service */
	public static final int NETBIOS_NS = 137;

	/** NETBIOS Datagram Service */
	public static final int NETBIOS_DGM = 138;

	/** NETBIOS Session Service */
	public static final int NETBIOS_SSN = 139;

	/** EMFIS Data Service */
	public static final int EMFIS_DATA = 140;

	/** EMFIS Control Service */
	public static final int EMFIS_CNTL = 141;

	/** Britton-Lee IDM */
	public static final int BL_IDM = 142;

	/** Survey Measurement */
	public static final int SUR_MEAS = 243;

	/** LINK */
	public static final int LINK = 245;

	private static final Mnemonic services = new Mnemonic("TCP/UDP service",
							Mnemonic.CASE_LOWER);

	static {
        Service.services.setMaximum(0xFFFF);
        Service.services.setNumericAllowed(true);

        Service.services.add(Service.RJE, "rje");
        Service.services.add(Service.ECHO, "echo");
        Service.services.add(Service.DISCARD, "discard");
        Service.services.add(Service.USERS, "users");
        Service.services.add(Service.DAYTIME, "daytime");
        Service.services.add(Service.QUOTE, "quote");
        Service.services.add(Service.CHARGEN, "chargen");
        Service.services.add(Service.FTP_DATA, "ftp-data");
        Service.services.add(Service.FTP, "ftp");
        Service.services.add(Service.TELNET, "telnet");
        Service.services.add(Service.SMTP, "smtp");
        Service.services.add(Service.NSW_FE, "nsw-fe");
        Service.services.add(Service.MSG_ICP, "msg-icp");
        Service.services.add(Service.MSG_AUTH, "msg-auth");
        Service.services.add(Service.DSP, "dsp");
        Service.services.add(Service.TIME, "time");
        Service.services.add(Service.RLP, "rlp");
        Service.services.add(Service.GRAPHICS, "graphics");
        Service.services.add(Service.NAMESERVER, "nameserver");
        Service.services.add(Service.NICNAME, "nicname");
        Service.services.add(Service.MPM_FLAGS, "mpm-flags");
        Service.services.add(Service.MPM, "mpm");
        Service.services.add(Service.MPM_SND, "mpm-snd");
        Service.services.add(Service.NI_FTP, "ni-ftp");
        Service.services.add(Service.LOGIN, "login");
        Service.services.add(Service.LA_MAINT, "la-maint");
        Service.services.add(Service.DOMAIN, "domain");
        Service.services.add(Service.ISI_GL, "isi-gl");
        Service.services.add(Service.NI_MAIL, "ni-mail");
        Service.services.add(Service.VIA_FTP, "via-ftp");
        Service.services.add(Service.TACACS_DS, "tacacs-ds");
        Service.services.add(Service.BOOTPS, "bootps");
        Service.services.add(Service.BOOTPC, "bootpc");
        Service.services.add(Service.TFTP, "tftp");
        Service.services.add(Service.NETRJS_1, "netrjs-1");
        Service.services.add(Service.NETRJS_2, "netrjs-2");
        Service.services.add(Service.NETRJS_3, "netrjs-3");
        Service.services.add(Service.NETRJS_4, "netrjs-4");
        Service.services.add(Service.FINGER, "finger");
        Service.services.add(Service.HOSTS2_NS, "hosts2-ns");
        Service.services.add(Service.SU_MIT_TG, "su-mit-tg");
        Service.services.add(Service.MIT_DOV, "mit-dov");
        Service.services.add(Service.DCP, "dcp");
        Service.services.add(Service.SUPDUP, "supdup");
        Service.services.add(Service.SWIFT_RVF, "swift-rvf");
        Service.services.add(Service.TACNEWS, "tacnews");
        Service.services.add(Service.METAGRAM, "metagram");
        Service.services.add(Service.HOSTNAME, "hostname");
        Service.services.add(Service.ISO_TSAP, "iso-tsap");
        Service.services.add(Service.X400, "x400");
        Service.services.add(Service.X400_SND, "x400-snd");
        Service.services.add(Service.CSNET_NS, "csnet-ns");
        Service.services.add(Service.RTELNET, "rtelnet");
        Service.services.add(Service.POP_2, "pop-2");
        Service.services.add(Service.SUNRPC, "sunrpc");
        Service.services.add(Service.AUTH, "auth");
        Service.services.add(Service.SFTP, "sftp");
        Service.services.add(Service.UUCP_PATH, "uucp-path");
        Service.services.add(Service.NNTP, "nntp");
        Service.services.add(Service.ERPC, "erpc");
        Service.services.add(Service.NTP, "ntp");
        Service.services.add(Service.LOCUS_MAP, "locus-map");
        Service.services.add(Service.LOCUS_CON, "locus-con");
        Service.services.add(Service.PWDGEN, "pwdgen");
        Service.services.add(Service.CISCO_FNA, "cisco-fna");
        Service.services.add(Service.CISCO_TNA, "cisco-tna");
        Service.services.add(Service.CISCO_SYS, "cisco-sys");
        Service.services.add(Service.STATSRV, "statsrv");
        Service.services.add(Service.INGRES_NET, "ingres-net");
        Service.services.add(Service.LOC_SRV, "loc-srv");
        Service.services.add(Service.PROFILE, "profile");
        Service.services.add(Service.NETBIOS_NS, "netbios-ns");
        Service.services.add(Service.NETBIOS_DGM, "netbios-dgm");
        Service.services.add(Service.NETBIOS_SSN, "netbios-ssn");
        Service.services.add(Service.EMFIS_DATA, "emfis-data");
        Service.services.add(Service.EMFIS_CNTL, "emfis-cntl");
        Service.services.add(Service.BL_IDM, "bl-idm");
        Service.services.add(Service.SUR_MEAS, "sur-meas");
        Service.services.add(Service.LINK, "link");
	}

	/**
	 * Converts a TCP/UDP service port number into its textual
	 * representation.
	 */
	public static String
	string(int type) {
		return Service.services.getText(type);
	}

	/**
	 * Converts a textual representation of a TCP/UDP service into its
	 * port number.  Integers in the range 0..65535 are also accepted.
	 * @param s The textual representation of the service.
	 * @return The port number, or -1 on error.
	 */
	public static int
	value(String s) {
		return Service.services.getValue(s);
	}
}
private byte [] address;
private int protocol;
private int [] services;

WKSRecord() {}

@Override
Record
getObject() {
	return new WKSRecord();
}

/**
 * Creates a WKS Record from the given data
 * @param address The IP address
 * @param protocol The IP protocol number
 * @param services An array of supported services, represented by port number.
 */
public
WKSRecord(Name name, int dclass, long ttl, InetAddress address, int protocol,
	  int [] services)
{
	super(name, Type.WKS, dclass, ttl);
	if (Address.familyOf(address) != Address.IPv4)
		throw new IllegalArgumentException("invalid IPv4 address");
	this.address = address.getAddress();
	this.protocol = Record.checkU8("protocol", protocol);
	for (int i = 0; i < services.length; i++) {
        Record.checkU16("service", services[i]);
	}
	this.services = new int[services.length];
	System.arraycopy(services, 0, this.services, 0, services.length);
	Arrays.sort(this.services);
}

@Override
void
rrFromWire(DNSInput in) throws IOException {
    this.address = in.readByteArray(4);
    this.protocol = in.readU8();
	byte [] array = in.readByteArray();
	List list = new ArrayList();
	for (int i = 0; i < array.length; i++) {
		for (int j = 0; j < 8; j++) {
			int octet = array[i] & 0xFF;
			if ((octet & 1 << 7 - j) != 0) {
				list.add(new Integer(i * 8 + j));
			}
		}
	}
    this.services = new int[list.size()];
	for (int i = 0; i < list.size(); i++) {
        this.services[i] = ((Integer) list.get(i)).intValue();
	}
}

@Override
void
rdataFromString(Tokenizer st, Name origin) throws IOException {
	String s = st.getString();
    this.address = Address.toByteArray(s, Address.IPv4);
	if (this.address == null)
		throw st.exception("invalid address");

	s = st.getString();
    this.protocol = WKSRecord.Protocol.value(s);
	if (this.protocol < 0) {
		throw st.exception("Invalid IP protocol: " + s);
	}

	List list = new ArrayList();
	while (true) {
		Token t = st.get();
		if (!t.isString())
			break;
		int service = WKSRecord.Service.value(t.value);
		if (service < 0) {
			throw st.exception("Invalid TCP/UDP service: " +
					   t.value);
		}
		list.add(new Integer(service));
	}
	st.unget();
    this.services = new int[list.size()];
	for (int i = 0; i < list.size(); i++) {
        this.services[i] = ((Integer) list.get(i)).intValue();
	}
}

/**
 * Converts rdata to a String
 */
@Override
String
rrToString() {
	StringBuffer sb = new StringBuffer();
	sb.append(Address.toDottedQuad(this.address));
	sb.append(" ");
	sb.append(this.protocol);
	for (int i = 0; i < this.services.length; i++) {
		sb.append(" " + this.services[i]);
	}
	return sb.toString();
}

/**
 * Returns the IP address.
 */
public InetAddress
getAddress() {
	try {
		return InetAddress.getByAddress(this.address);
	} catch (UnknownHostException e) {
		return null;
	}
}

/**
 * Returns the IP protocol.
 */
public int
getProtocol() {
	return this.protocol;
}

/**
 * Returns the services provided by the host on the specified address.
 */
public int []
getServices() {
	return this.services;
}

@Override
void
rrToWire(DNSOutput out, Compression c, boolean canonical) {
	out.writeByteArray(this.address);
	out.writeU8(this.protocol);
	int highestPort = this.services[this.services.length - 1];
	byte [] array = new byte[highestPort / 8 + 1];
	for (int i = 0; i < this.services.length; i++) {
		int port = this.services[i];
		array[port / 8] |= 1 << 7 - port % 8;
	}
	out.writeByteArray(array);
}

}
