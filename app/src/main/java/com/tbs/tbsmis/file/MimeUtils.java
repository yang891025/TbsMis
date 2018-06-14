/*
 * Copyright (c) 2010-2011, The MiCode Open Source Community (www.micode.net)
 *
 * This file is part of FileExplorer.
 *
 * FileExplorer is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * FileExplorer is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with SwiFTP.  If not, see <http://www.gnu.org/licenses/>.
 */

package com.tbs.tbsmis.file;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Properties;

/**
 * Utilities for dealing with MIME types.
 * Used to implement java.net.URLConnection and android.webkit.MimeTypeMap.
 */
public final class MimeUtils {
    private static final Map<String, String> mimeTypeToExtensionMap = new HashMap<String, String>();

    private static final Map<String, String> extensionToMimeTypeMap = new HashMap<String, String>();

    static {
        // The following table is based on /etc/mime.types data minus
        // chemical/* MIME types and MIME types that don't map to any
        // file extensions. We also exclude top-level domain names to
        // deal with cases like:
        //
        // mail.google.com/a/google.com
        //
        // and "active" MIME types (due to potential security issues).

        MimeUtils.add("application/andrew-inset", "ez");
        MimeUtils.add("application/dsptype", "tsp");
        MimeUtils.add("application/futuresplash", "spl");
        MimeUtils.add("application/hta", "hta");
        MimeUtils.add("application/mac-binhex40", "hqx");
        MimeUtils.add("application/mac-compactpro", "cpt");
        MimeUtils.add("application/mathematica", "nb");
        MimeUtils.add("application/msaccess", "mdb");
        MimeUtils.add("application/oda", "oda");
        MimeUtils.add("application/ogg", "ogg");
        MimeUtils.add("application/pdf", "pdf");
        MimeUtils.add("application/pgp-keys", "key");
        MimeUtils.add("application/pgp-signature", "pgp");
        MimeUtils.add("application/pics-rules", "prf");
        MimeUtils.add("application/rar", "rar");
        MimeUtils.add("application/rdf+xml", "rdf");
        MimeUtils.add("application/rss+xml", "rss");
        MimeUtils.add("application/zip", "zip");
        MimeUtils.add("application/vnd.android.package-archive", "apk");
        MimeUtils.add("application/vnd.cinderella", "cdy");
        MimeUtils.add("application/vnd.ms-pki.stl", "stl");
        MimeUtils.add("application/vnd.oasis.opendocument.database", "odb");
        MimeUtils.add("application/vnd.oasis.opendocument.formula", "odf");
        MimeUtils.add("application/vnd.oasis.opendocument.graphics", "odg");
        MimeUtils.add("application/vnd.oasis.opendocument.graphics-template", "otg");
        MimeUtils.add("application/vnd.oasis.opendocument.image", "odi");
        MimeUtils.add("application/vnd.oasis.opendocument.spreadsheet", "ods");
        MimeUtils.add("application/vnd.oasis.opendocument.spreadsheet-template", "ots");
        MimeUtils.add("application/vnd.oasis.opendocument.text", "odt");
        MimeUtils.add("application/vnd.oasis.opendocument.text-master", "odm");
        MimeUtils.add("application/vnd.oasis.opendocument.text-template", "ott");
        MimeUtils.add("application/vnd.oasis.opendocument.text-web", "oth");
        MimeUtils.add("application/vnd.google-earth.kml+xml", "kml");
        MimeUtils.add("application/vnd.google-earth.kmz", "kmz");
        MimeUtils.add("application/msword", "doc");
        MimeUtils.add("application/msword", "dot");
        MimeUtils.add("application/vnd.openxmlformats-officedocument.wordprocessingml.document", "docx");
        MimeUtils.add("application/vnd.openxmlformats-officedocument.wordprocessingml.template", "dotx");
        MimeUtils.add("application/vnd.ms-excel", "xls");
        MimeUtils.add("application/vnd.ms-excel", "xlt");
        MimeUtils.add("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet", "xlsx");
        MimeUtils.add("application/vnd.openxmlformats-officedocument.spreadsheetml.template", "xltx");
        MimeUtils.add("application/vnd.ms-powerpoint", "ppt");
        MimeUtils.add("application/vnd.ms-powerpoint", "pot");
        MimeUtils.add("application/vnd.ms-powerpoint", "pps");
        MimeUtils.add("application/vnd.openxmlformats-officedocument.presentationml.presentation", "pptx");
        MimeUtils.add("application/vnd.openxmlformats-officedocument.presentationml.template", "potx");
        MimeUtils.add("application/vnd.openxmlformats-officedocument.presentationml.slideshow", "ppsx");
        MimeUtils.add("application/vnd.rim.cod", "cod");
        MimeUtils.add("application/vnd.smaf", "mmf");
        MimeUtils.add("application/vnd.stardivision.calc", "sdc");
        MimeUtils.add("application/vnd.stardivision.draw", "sda");
        MimeUtils.add("application/vnd.stardivision.impress", "sdd");
        MimeUtils.add("application/vnd.stardivision.impress", "sdp");
        MimeUtils.add("application/vnd.stardivision.math", "smf");
        MimeUtils.add("application/vnd.stardivision.writer", "sdw");
        MimeUtils.add("application/vnd.stardivision.writer", "vor");
        MimeUtils.add("application/vnd.stardivision.writer-global", "sgl");
        MimeUtils.add("application/vnd.sun.xml.calc", "sxc");
        MimeUtils.add("application/vnd.sun.xml.calc.template", "stc");
        MimeUtils.add("application/vnd.sun.xml.draw", "sxd");
        MimeUtils.add("application/vnd.sun.xml.draw.template", "std");
        MimeUtils.add("application/vnd.sun.xml.impress", "sxi");
        MimeUtils.add("application/vnd.sun.xml.impress.template", "sti");
        MimeUtils.add("application/vnd.sun.xml.math", "sxm");
        MimeUtils.add("application/vnd.sun.xml.writer", "sxw");
        MimeUtils.add("application/vnd.sun.xml.writer.global", "sxg");
        MimeUtils.add("application/vnd.sun.xml.writer.template", "stw");
        MimeUtils.add("application/vnd.visio", "vsd");
        MimeUtils.add("application/x-abiword", "abw");
        MimeUtils.add("application/x-apple-diskimage", "dmg");
        MimeUtils.add("application/x-bcpio", "bcpio");
        MimeUtils.add("application/x-bittorrent", "torrent");
        MimeUtils.add("application/x-cdf", "cdf");
        MimeUtils.add("application/x-cdlink", "vcd");
        MimeUtils.add("application/x-chess-pgn", "pgn");
        MimeUtils.add("application/x-cpio", "cpio");
        MimeUtils.add("application/x-debian-package", "deb");
        MimeUtils.add("application/x-debian-package", "udeb");
        MimeUtils.add("application/x-director", "dcr");
        MimeUtils.add("application/x-director", "dir");
        MimeUtils.add("application/x-director", "dxr");
        MimeUtils.add("application/x-dms", "dms");
        MimeUtils.add("application/x-doom", "wad");
        MimeUtils.add("application/x-dvi", "dvi");
        MimeUtils.add("application/x-flac", "flac");
        MimeUtils.add("application/x-font", "pfa");
        MimeUtils.add("application/x-font", "pfb");
        MimeUtils.add("application/x-font", "gsf");
        MimeUtils.add("application/x-font", "pcf");
        MimeUtils.add("application/x-font", "pcf.Z");
        MimeUtils.add("application/x-freemind", "mm");
        MimeUtils.add("application/x-futuresplash", "spl");
        MimeUtils.add("application/x-gnumeric", "gnumeric");
        MimeUtils.add("application/x-go-sgf", "sgf");
        MimeUtils.add("application/x-graphing-calculator", "gcf");
        MimeUtils.add("application/x-gtar", "gtar");
        MimeUtils.add("application/x-gtar", "tgz");
        MimeUtils.add("application/x-gtar", "taz");
        MimeUtils.add("application/x-hdf", "hdf");
        MimeUtils.add("application/x-ica", "ica");
        MimeUtils.add("application/x-internet-signup", "ins");
        MimeUtils.add("application/x-internet-signup", "isp");
        MimeUtils.add("application/x-iphone", "iii");
        MimeUtils.add("application/x-iso9660-image", "iso");
        MimeUtils.add("application/x-jmol", "jmz");
        MimeUtils.add("application/x-kchart", "chrt");
        MimeUtils.add("application/x-killustrator", "kil");
        MimeUtils.add("application/x-koan", "skp");
        MimeUtils.add("application/x-koan", "skd");
        MimeUtils.add("application/x-koan", "skt");
        MimeUtils.add("application/x-koan", "skm");
        MimeUtils.add("application/x-kpresenter", "kpr");
        MimeUtils.add("application/x-kpresenter", "kpt");
        MimeUtils.add("application/x-kspread", "ksp");
        MimeUtils.add("application/x-kword", "kwd");
        MimeUtils.add("application/x-kword", "kwt");
        MimeUtils.add("application/x-latex", "latex");
        MimeUtils.add("application/x-lha", "lha");
        MimeUtils.add("application/x-lzh", "lzh");
        MimeUtils.add("application/x-lzx", "lzx");
        MimeUtils.add("application/x-maker", "frm");
        MimeUtils.add("application/x-maker", "maker");
        MimeUtils.add("application/x-maker", "frame");
        MimeUtils.add("application/x-maker", "fb");
        MimeUtils.add("application/x-maker", "book");
        MimeUtils.add("application/x-maker", "fbdoc");
        MimeUtils.add("application/x-mif", "mif");
        MimeUtils.add("application/x-ms-wmd", "wmd");
        MimeUtils.add("application/x-ms-wmz", "wmz");
        MimeUtils.add("application/x-msi", "msi");
        MimeUtils.add("application/x-ns-proxy-autoconfig", "pac");
        MimeUtils.add("application/x-nwc", "nwc");
        MimeUtils.add("application/x-object", "o");
        MimeUtils.add("application/x-oz-application", "oza");
        MimeUtils.add("application/x-pkcs12", "p12");
        MimeUtils.add("application/x-pkcs7-certreqresp", "p7r");
        MimeUtils.add("application/x-pkcs7-crl", "crl");
        MimeUtils.add("application/x-quicktimeplayer", "qtl");
        MimeUtils.add("application/x-shar", "shar");
        MimeUtils.add("application/x-shockwave-flash", "swf");
        MimeUtils.add("application/x-stuffit", "sit");
        MimeUtils.add("application/x-sv4cpio", "sv4cpio");
        MimeUtils.add("application/x-sv4crc", "sv4crc");
        MimeUtils.add("application/x-tar", "tar");
        MimeUtils.add("application/x-texinfo", "texinfo");
        MimeUtils.add("application/x-texinfo", "texi");
        MimeUtils.add("application/x-troff", "t");
        MimeUtils.add("application/x-troff", "roff");
        MimeUtils.add("application/x-troff-man", "man");
        MimeUtils.add("application/x-ustar", "ustar");
        MimeUtils.add("application/x-wais-source", "src");
        MimeUtils.add("application/x-wingz", "wz");
        MimeUtils.add("application/x-webarchive", "webarchive");
        MimeUtils.add("application/x-webarchive-xml", "webarchivexml");
        MimeUtils.add("application/x-x509-ca-cert", "crt");
        MimeUtils.add("application/x-x509-user-cert", "crt");
        MimeUtils.add("application/x-xcf", "xcf");
        MimeUtils.add("application/x-xfig", "fig");
        MimeUtils.add("application/xhtml+xml", "xhtml");
        MimeUtils.add("audio/*", "3gpp");
        MimeUtils.add("audio/amr", "amr");
        MimeUtils.add("audio/basic", "snd");
        MimeUtils.add("audio/*", "mid");
        MimeUtils.add("audio/midi", "midi");
        MimeUtils.add("audio/midi", "kar");
        MimeUtils.add("audio/midi", "xmf");
        MimeUtils.add("audio/mobile-xmf", "mxmf");
        MimeUtils.add("audio/*", "mpga");
        MimeUtils.add("audio/*", "mpega");
        MimeUtils.add("audio/*", "mp2");
        MimeUtils.add("audio/*", "mp3");
        MimeUtils.add("audio/*", "m4a");
        MimeUtils.add("audio/*", "m3u");
        MimeUtils.add("audio/prs.sid", "sid");
        MimeUtils.add("audio/x-aiff", "aif");
        MimeUtils.add("audio/x-aiff", "aiff");
        MimeUtils.add("audio/x-aiff", "aifc");
        MimeUtils.add("audio/x-gsm", "gsm");
        MimeUtils.add("audio/x-mpegurl", "m3u");
        MimeUtils.add("audio/*", "wma");
        MimeUtils.add("audio/*", "wax");
        MimeUtils.add("audio/x-pn-realaudio", "ra");
        MimeUtils.add("audio/x-pn-realaudio", "rm");
        MimeUtils.add("audio/x-pn-realaudio", "ram");
        MimeUtils.add("audio/x-realaudio", "ra");
        MimeUtils.add("audio/x-scpls", "pls");
        MimeUtils.add("audio/x-sd2", "sd2");
        MimeUtils.add("audio/*", "wav");
        MimeUtils.add("image/bmp", "bmp");
        MimeUtils.add("audio/x-qcp", "qcp");
        MimeUtils.add("image/*", "gif");
        MimeUtils.add("image/*", "cur");
        MimeUtils.add("image/*", "ico");
        MimeUtils.add("image/ief", "ief");
        MimeUtils.add("image/*", "jpeg");
        MimeUtils.add("image/*", "jpg");
        MimeUtils.add("image/*", "jpe");
        MimeUtils.add("image/pcx", "pcx");
        MimeUtils.add("image/*", "png");
        MimeUtils.add("image/svg+xml", "svg");
        MimeUtils.add("image/svg+xml", "svgz");
        MimeUtils.add("image/tiff", "tiff");
        MimeUtils.add("image/tiff", "tif");
        MimeUtils.add("image/vnd.djvu", "djvu");
        MimeUtils.add("image/vnd.djvu", "djv");
        MimeUtils.add("image/vnd.wap.wbmp", "wbmp");
        MimeUtils.add("image/x-cmu-raster", "ras");
        MimeUtils.add("image/x-coreldraw", "cdr");
        MimeUtils.add("image/x-coreldrawpattern", "pat");
        MimeUtils.add("image/x-coreldrawtemplate", "cdt");
        MimeUtils.add("image/x-corelphotopaint", "cpt");
        MimeUtils.add("image/x-icon", "ico");
        MimeUtils.add("image/x-jg", "art");
        MimeUtils.add("image/x-jng", "jng");
        MimeUtils.add("image/x-ms-bmp", "bmp");
        MimeUtils.add("image/x-photoshop", "psd");
        MimeUtils.add("image/x-portable-anymap", "pnm");
        MimeUtils.add("image/x-portable-bitmap", "pbm");
        MimeUtils.add("image/x-portable-graymap", "pgm");
        MimeUtils.add("image/x-portable-pixmap", "ppm");
        MimeUtils.add("image/x-rgb", "rgb");
        MimeUtils.add("image/x-xbitmap", "xbm");
        MimeUtils.add("image/x-xpixmap", "xpm");
        MimeUtils.add("image/x-xwindowdump", "xwd");
        MimeUtils.add("model/iges", "igs");
        MimeUtils.add("model/iges", "iges");
        MimeUtils.add("model/mesh", "msh");
        MimeUtils.add("model/mesh", "mesh");
        MimeUtils.add("model/mesh", "silo");
        MimeUtils.add("text/calendar", "ics");
        MimeUtils.add("text/calendar", "icz");
        MimeUtils.add("text/comma-separated-values", "csv");
        MimeUtils.add("text/css", "css");
        MimeUtils.add("text/html", "htm");
        MimeUtils.add("text/html", "html");
        MimeUtils.add("text/h323", "323");
        MimeUtils.add("text/iuls", "uls");
        MimeUtils.add("text/mathml", "mml");
        // add ".txt" first so it will be the default for ExtensionFromMimeType
        MimeUtils.add("text/plain", "txt");
        MimeUtils.add("text/plain", "asc");
        MimeUtils.add("text/plain", "text");
        MimeUtils.add("text/plain", "diff");
        MimeUtils.add("text/plain", "po");     // reserve "pot" for vnd.ms-powerpoint
        MimeUtils.add("text/richtext", "rtx");
        MimeUtils.add("text/rtf", "rtf");
        MimeUtils.add("text/texmacs", "ts");
        MimeUtils.add("text/text", "phps");
        MimeUtils.add("text/tab-separated-values", "tsv");
        MimeUtils.add("text/xml", "xml");
        MimeUtils.add("text/x-bibtex", "bib");
        MimeUtils.add("text/x-boo", "boo");
        MimeUtils.add("text/x-c++hdr", "h++");
        MimeUtils.add("text/x-c++hdr", "hpp");
        MimeUtils.add("text/x-c++hdr", "hxx");
        MimeUtils.add("text/x-c++hdr", "hh");
        MimeUtils.add("text/x-c++src", "c++");
        MimeUtils.add("text/x-c++src", "cpp");
        MimeUtils.add("text/x-c++src", "cxx");
        MimeUtils.add("text/x-chdr", "h");
        MimeUtils.add("text/x-component", "htc");
        MimeUtils.add("text/x-csh", "csh");
        MimeUtils.add("text/x-csrc", "c");
        MimeUtils.add("text/x-dsrc", "d");
        MimeUtils.add("text/x-haskell", "hs");
        MimeUtils.add("text/x-java", "java");
        MimeUtils.add("text/x-literate-haskell", "lhs");
        MimeUtils.add("text/x-moc", "moc");
        MimeUtils.add("text/x-pascal", "p");
        MimeUtils.add("text/x-pascal", "pas");
        MimeUtils.add("text/x-pcs-gcd", "gcd");
        MimeUtils.add("text/x-setext", "etx");
        MimeUtils.add("text/x-tcl", "tcl");
        MimeUtils.add("text/x-tex", "tex");
        MimeUtils.add("text/x-tex", "ltx");
        MimeUtils.add("text/x-tex", "sty");
        MimeUtils.add("text/x-tex", "cls");
        MimeUtils.add("text/x-vcalendar", "vcs");
        MimeUtils.add("text/x-vcard", "vcf");
        MimeUtils.add("video/3gpp", "3gpp");
        MimeUtils.add("video/*", "3gp");
        MimeUtils.add("video/3gpp", "3g2");
        MimeUtils.add("video/dl", "dl");
        MimeUtils.add("video/dv", "dif");
        MimeUtils.add("video/dv", "dv");
        MimeUtils.add("video/fli", "fli");
        MimeUtils.add("video/m4v", "m4v");
        MimeUtils.add("video/mpeg", "mpeg");
        MimeUtils.add("video/mpeg", "mpg");
        MimeUtils.add("video/mpeg", "mpe");
        MimeUtils.add("video/*", "mp4");
        MimeUtils.add("video/mpeg", "vob");
        MimeUtils.add("video/quicktime", "qt");
        MimeUtils.add("video/*", "mov");
        MimeUtils.add("video/vnd.mpegurl", "mxu");
        MimeUtils.add("video/webm", "webm");
        MimeUtils.add("video/x-la-asf", "lsf");
        MimeUtils.add("video/x-la-asf", "lsx");
        MimeUtils.add("video/x-mng", "mng");
        MimeUtils.add("video/x-ms-asf", "asf");
        MimeUtils.add("video/x-ms-asf", "asx");
        MimeUtils.add("video/*", "wm");
        MimeUtils.add("video/*", "wmv");
        MimeUtils.add("video/*", "wmx");
        MimeUtils.add("video/*", "wvx");
        MimeUtils.add("video/*", "avi");
        MimeUtils.add("video/*", "movie");
        MimeUtils.add("video/*", "flv");
        MimeUtils.add("video/*", "flt");
        MimeUtils.add("x-conference/x-cooltalk", "ice");
        MimeUtils.add("x-epoc/x-sisx-app", "sisx");
        MimeUtils.applyOverrides();
    }

    private static void add(String mimeType, String extension) {
        //
        // if we have an existing x --> y mapping, we do not want to
        // override it with another mapping x --> ?
        // this is mostly because of the way the mime-type map below
        // is constructed (if a mime type maps to several extensions
        // the first extension is considered the most popular and is
        // added first; we do not want to overwrite it later).
        //
        if (!MimeUtils.mimeTypeToExtensionMap.containsKey(mimeType)) {
            MimeUtils.mimeTypeToExtensionMap.put(mimeType, extension);
        }
        MimeUtils.extensionToMimeTypeMap.put(extension, mimeType);
    }

    private static InputStream getContentTypesPropertiesStream() {
        // User override?
        String userTable = System.getProperty("content.types.user.table");
        if (userTable != null) {
            File f = new File(userTable);
            if (f.exists()) {
                try {
                    return new FileInputStream(f);
                } catch (IOException ignored) {
                }
            }
        }

        // Standard location?
        File f = new File(System.getProperty("java.home"), "lib" + File.separator + "content-types.properties");
        if (f.exists()) {
            try {
                return new FileInputStream(f);
            } catch (IOException ignored) {
            }
        }

        return null;
    }

    /**
     * This isn't what the RI does. The RI doesn't have hard-coded defaults, so supplying your
     * own "content.types.user.table" means you don't get any of the built-ins, and the built-ins
     * come from "$JAVA_HOME/lib/content-types.properties".
     */
    private static void applyOverrides() {
        // Get the appropriate InputStream to read overrides from, if any.
        InputStream stream = MimeUtils.getContentTypesPropertiesStream();
        if (stream == null) {
            return;
        }

        try {
            try {
                // Read the properties file...
                Properties overrides = new Properties();
                overrides.load(stream);
                // And translate its mapping to ours...
                for (Entry<Object, Object> entry : overrides.entrySet()) {
                    String extension = (String) entry.getKey();
                    String mimeType = (String) entry.getValue();
                    MimeUtils.add(mimeType, extension);
                }
            } finally {
                stream.close();
            }
        } catch (IOException ignored) {
        }
    }

    private MimeUtils() {
    }

    /**
     * Returns true if the given MIME type has an entry in the map.
     * @param mimeType A MIME type (i.e. text/plain)
     * @return True iff there is a mimeType entry in the map.
     */
    public static boolean hasMimeType(String mimeType) {
        if (mimeType == null || mimeType.isEmpty()) {
            return false;
        }
        return MimeUtils.mimeTypeToExtensionMap.containsKey(mimeType);
    }

    /**
     * Returns the MIME type for the given extension.
     * @param extension A file extension without the leading '.'
     * @return The MIME type for the given extension or null iff there is none.
     */
    public static String guessMimeTypeFromExtension(String extension) {
        if (extension == null || extension.isEmpty()) {
            return null;
        }
        return MimeUtils.extensionToMimeTypeMap.get(extension);
    }

    /**
     * Returns true if the given extension has a registered MIME type.
     * @param extension A file extension without the leading '.'
     * @return True iff there is an extension entry in the map.
     */
    public static boolean hasExtension(String extension) {
        if (extension == null || extension.isEmpty()) {
            return false;
        }
        return MimeUtils.extensionToMimeTypeMap.containsKey(extension);
    }

    /**
     * Returns the registered extension for the given MIME type. Note that some
     * MIME types map to multiple extensions. This call will return the most
     * common extension for the given MIME type.
     * @param mimeType A MIME type (i.e. text/plain)
     * @return The extension for the given MIME type or null iff there is none.
     */
    public static String guessExtensionFromMimeType(String mimeType) {
        if (mimeType == null || mimeType.isEmpty()) {
            return null;
        }
        return MimeUtils.mimeTypeToExtensionMap.get(mimeType);
    }
}
