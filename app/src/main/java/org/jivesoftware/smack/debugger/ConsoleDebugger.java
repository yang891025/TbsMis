package org.jivesoftware.smack.debugger;

import org.jivesoftware.smack.ConnectionListener;
import org.jivesoftware.smack.PacketListener;
import org.jivesoftware.smack.Connection;
import org.jivesoftware.smack.packet.Packet;
import org.jivesoftware.smack.util.*;

import java.io.Reader;
import java.io.Writer;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Very simple debugger that prints to the console (stdout) the sent and received stanzas. Use
 * this debugger with caution since printing to the console is an expensive operation that may
 * even block the thread since only one thread may print at a time.<p>
 * <p/>
 * It is possible to not only print the raw sent and received stanzas but also the interpreted
 * packets by Smack. By default interpreted packets won't be printed. To enable this feature
 * just change the <tt>printInterpreted</tt> static variable to <tt>true</tt>.
 *
 * @author Gaston Dombiak
 */
public class ConsoleDebugger implements SmackDebugger {

    public static boolean printInterpreted;
    private final SimpleDateFormat dateFormatter = new SimpleDateFormat("HH:mm:ss aaa");

    private Connection connection;

    private PacketListener listener;
    private ConnectionListener connListener;

    private Writer writer;
    private Reader reader;
    private ReaderListener readerListener;
    private WriterListener writerListener;

    public ConsoleDebugger(Connection connection, Writer writer, Reader reader) {
        this.connection = connection;
        this.writer = writer;
        this.reader = reader;
        this.createDebug();
    }

    /**
     * Creates the listeners that will print in the console when new activity is detected.
     */
    private void createDebug() {
        // Create a special Reader that wraps the main Reader and logs data to the GUI.
        ObservableReader debugReader = new ObservableReader(this.reader);
        this.readerListener = new ReaderListener() {
            @Override
			public void read(String str) {
                System.out.println(
                        ConsoleDebugger.this.dateFormatter.format(new Date()) + " RCV  (" + ConsoleDebugger.this.connection.hashCode() +
                        "): " +
                        str);
            }
        };
        debugReader.addReaderListener(this.readerListener);

        // Create a special Writer that wraps the main Writer and logs data to the GUI.
        ObservableWriter debugWriter = new ObservableWriter(this.writer);
        this.writerListener = new WriterListener() {
            @Override
			public void write(String str) {
                System.out.println(
                        ConsoleDebugger.this.dateFormatter.format(new Date()) + " SENT (" + ConsoleDebugger.this.connection.hashCode() +
                        "): " +
                        str);
            }
        };
        debugWriter.addWriterListener(this.writerListener);

        // Assign the reader/writer objects to use the debug versions. The packet reader
        // and writer will use the debug versions when they are created.
        this.reader = debugReader;
        this.writer = debugWriter;

        // Create a thread that will listen for all incoming packets and write them to
        // the GUI. This is what we call "interpreted" packet data, since it's the packet
        // data as Smack sees it and not as it's coming in as raw XML.
        this.listener = new PacketListener() {
            @Override
			public void processPacket(Packet packet) {
                if (ConsoleDebugger.printInterpreted) {
                    System.out.println(
                            ConsoleDebugger.this.dateFormatter.format(new Date()) + " RCV PKT (" +
                                    ConsoleDebugger.this.connection.hashCode() +
                            "): " +
                            packet.toXML());
                }
            }
        };

        this.connListener = new ConnectionListener() {
            @Override
			public void connectionClosed() {
                System.out.println(
                        ConsoleDebugger.this.dateFormatter.format(new Date()) + " Connection closed (" +
                                ConsoleDebugger.this.connection.hashCode() +
                        ")");
            }

            @Override
			public void connectionClosedOnError(Exception e) {
                System.out.println(
                        ConsoleDebugger.this.dateFormatter.format(new Date()) +
                        " Connection closed due to an exception (" +
                                ConsoleDebugger.this.connection.hashCode() +
                        ")");
                e.printStackTrace();
            }
            @Override
			public void reconnectionFailed(Exception e) {
                System.out.println(
                        ConsoleDebugger.this.dateFormatter.format(new Date()) +
                        " Reconnection failed due to an exception (" +
                                ConsoleDebugger.this.connection.hashCode() +
                        ")");
                e.printStackTrace();
            }
            @Override
			public void reconnectionSuccessful() {
                System.out.println(
                        ConsoleDebugger.this.dateFormatter.format(new Date()) + " Connection reconnected (" +
                                ConsoleDebugger.this.connection.hashCode() +
                        ")");
            }
            @Override
			public void reconnectingIn(int seconds) {
                System.out.println(
                        ConsoleDebugger.this.dateFormatter.format(new Date()) + " Connection (" +
                                ConsoleDebugger.this.connection.hashCode() +
                        ") will reconnect in " + seconds);
            }
        };
    }

    @Override
	public Reader newConnectionReader(Reader newReader) {
        ((ObservableReader) this.reader).removeReaderListener(this.readerListener);
        ObservableReader debugReader = new ObservableReader(newReader);
        debugReader.addReaderListener(this.readerListener);
        this.reader = debugReader;
        return this.reader;
    }

    @Override
	public Writer newConnectionWriter(Writer newWriter) {
        ((ObservableWriter) this.writer).removeWriterListener(this.writerListener);
        ObservableWriter debugWriter = new ObservableWriter(newWriter);
        debugWriter.addWriterListener(this.writerListener);
        this.writer = debugWriter;
        return this.writer;
    }

    @Override
	public void userHasLogged(String user) {
        boolean isAnonymous = "".equals(StringUtils.parseName(user));
        String title =
                "User logged (" + this.connection.hashCode() + "): "
                + (isAnonymous ? "" : StringUtils.parseBareAddress(user))
                + "@"
                + this.connection.getServiceName()
                + ":"
                + this.connection.getPort();
        title += "/" + StringUtils.parseResource(user);
        System.out.println(title);
        // Add the connection listener to the connection so that the debugger can be notified
        // whenever the connection is closed.
        this.connection.addConnectionListener(this.connListener);
    }

    @Override
	public Reader getReader() {
        return this.reader;
    }

    @Override
	public Writer getWriter() {
        return this.writer;
    }

    @Override
	public PacketListener getReaderListener() {
        return this.listener;
    }

    @Override
	public PacketListener getWriterListener() {
        return null;
    }
}
