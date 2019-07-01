package de.tolao.portforwarder;

import java.io.IOException;
import java.io.PrintStream;
import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.Channel;
import java.nio.channels.DatagramChannel;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.Iterator;
import java.util.Set;

public class PortForwarder
{
    private volatile IFeedback feedback;
    private volatile Thread thread = null;
    private volatile boolean stop = false;
    private volatile TcpConnections tcpConnections;
    private volatile UdpConnections udpConnections;

    public PortForwarder(IFeedback feedback, TcpConnections tcpConnections, UdpConnections udpConnections) {
        this.feedback = feedback;
        this.tcpConnections = tcpConnections;
        this.udpConnections = udpConnections;
    }

    public void start(final SocketAddress connectToAddress, final int acceptFromPort) {
        if (thread != null) {
            throw new IllegalStateException("start() was called in running state");
        }
        stop = false;

        thread = new Thread(new Runnable()
        {
            public void run() {
                try {
                    work(connectToAddress, acceptFromPort);
                } catch (IOException ex) {
                    javax.swing.SwingUtilities.invokeLater(new Runnable()
                    {
                        public void run() {
                            feedback.showException(ex);
                            thread = null;
                        }
                    });
                }
            }
        }, "portForwarderThread");

        thread.start();
    }

    public void stop() throws InterruptedException {
        if (thread == null) {
            throw new IllegalStateException("stop() was called in non-running state");
        }
        stop = true;
        thread.interrupt();
        try {
            thread.join();
        } finally {
            thread = null;
        }
    }

    public void work(SocketAddress connectToAddress, int acceptFromPort) throws IOException {
        System.out.println("PortForwarder," + connectToAddress + "," + acceptFromPort);

        tcpConnections.clear();
        udpConnections.clear();

        ByteBuffer buf = ByteBuffer.allocate(8192);

        Selector selector = null;
        ServerSocketChannel tcpAcceptor = null;
        DatagramChannel udpAcceptor = null;
        int i;
        int i;
        try {
            selector = Selector.open();

            tcpAcceptor = ServerSocketChannel.open();
            tcpAcceptor.socket().bind(new InetSocketAddress(acceptFromPort));
            tcpAcceptor.configureBlocking(false);
            tcpAcceptor.register(selector, 16);


            udpAcceptor = DatagramChannel.open();
            udpAcceptor.socket().bind(new InetSocketAddress(acceptFromPort));
            udpAcceptor.configureBlocking(false);
            udpAcceptor.register(selector, 1);
            Iterator<SelectionKey> i;
            for (; !stop; i.hasNext()) {
                selector.select();

                Set<SelectionKey> keys = selector.selectedKeys();

                i = keys.iterator();
                continue;
                SelectionKey key = (SelectionKey)i.next();
                i.remove();

                if (key.isValid()) {
                    Channel c = key.channel();
                    int idx = -1;

                    if ((key.isAcceptable()) && (c == tcpAcceptor)) {
                        SocketChannel tcpAcceptedClient = tcpAcceptor.accept();

                        if (tcpAcceptedClient != null) {
                            boolean ok = false;
                            SocketChannel tcpConnectionToAddress = null;
                            try {
                                tcpAcceptedClient.configureBlocking(false);
                                tcpAcceptedClient.register(selector, 1);

                                tcpConnectionToAddress = SocketChannel.open(connectToAddress);
                                tcpConnectionToAddress.configureBlocking(false);
                                tcpConnectionToAddress.register(selector, 1);

                                tcpConnections.addElement(new TcpConnection(tcpAcceptedClient, tcpConnectionToAddress));
                                ok = true;
                            } finally {
                                if ((!ok) && (tcpAcceptedClient != null))
                                    tcpAcceptedClient.close();
                                if ((!ok) && (tcpConnectionToAddress != null))
                                    tcpConnectionToAddress.close();
                            }
                        }
                    } else if ((key.isReadable()) && ((idx = tcpConnections.indexByTcpAcceptedClient(c)) >= 0)) {
                        TcpConnection u = (TcpConnection)tcpConnections.get(idx);

                        buf.clear();

                        int r = -1;
                        try {
                            r = u.getTcpAcceptedClient().read(buf);
                        }
                        catch (IOException localIOException) {}


                        if (r == -1) {
                            System.out.println("client has closed TCP.");
                            u.close();
                            tcpConnections.remove(idx);
                        } else {
                            buf.flip();
                            u.getTcpConnectionToAddress().write(buf);
                        }
                    } else if ((key.isReadable()) && ((idx = tcpConnections.indexByTcpConnectionToAddress(c)) >= 0)) {
                        TcpConnection u = (TcpConnection)tcpConnections.get(idx);

                        buf.clear();

                        int r = -1;
                        try {
                            r = u.getTcpConnectionToAddress().read(buf);
                        }
                        catch (IOException localIOException1) {}


                        if (r == -1) {
                            System.out.println("server has closed TCP.");
                            u.close();
                            tcpConnections.remove(idx);
                        } else {
                            buf.flip();
                            u.getTcpAcceptedClient().write(buf);
                        }
                    } else if ((key.isReadable()) && (c == udpAcceptor)) {
                        buf.clear();
                        SocketAddress udpClientAddress = udpAcceptor.receive(buf);
                        if (udpClientAddress != null) {
                            buf.flip();

                            if ((idx = udpConnections.indexByUdpClientAddress(udpClientAddress)) == -1) {
                                udpConnections.cleanup();
                                DatagramChannel udpConnectionToAddress = DatagramChannel.open();
                                boolean ok = false;
                                try {
                                    udpConnectionToAddress.configureBlocking(false);
                                    udpConnectionToAddress.register(selector, 1);

                                    idx = udpConnections.size();
                                    udpConnections.addElement(new UdpConnection(udpClientAddress, udpConnectionToAddress));
                                    ok = true;
                                } finally {
                                    if ((!ok) && (udpConnectionToAddress != null)) {
                                        udpConnectionToAddress.close();
                                    }
                                }
                            }
                            UdpConnection u = (UdpConnection)udpConnections.get(idx);
                            u.getUdpConnectionToAddress().send(buf, connectToAddress);
                            u.markUsed();
                        }
                    } else if ((key.isReadable()) && ((idx = udpConnections.indexByUdpConnectionToAddress(c)) >= 0)) {
                        buf.clear();
                        UdpConnection u = (UdpConnection)udpConnections.get(idx);
                        u.getUdpConnectionToAddress().receive(buf);
                        buf.flip();
                        udpAcceptor.send(buf, u.getUdpClientAddress());
                        u.markUsed();
                    }

                }
            }
        }
        finally
        {
            for (int i = 0; i < udpConnections.size(); i++)
                ((UdpConnection)udpConnections.get(i)).close();
            udpConnections.clear();

            for (int i = 0; i < tcpConnections.size(); i++)
                ((TcpConnection)tcpConnections.get(i)).close();
            tcpConnections.clear();

            if (udpAcceptor != null) {
                udpAcceptor.close();
            }
            if (tcpAcceptor != null) {
                tcpAcceptor.close();
            }
            if (selector != null) {
                selector.close();
            }
        }
        System.out.println("thread returning.");
    }
}
