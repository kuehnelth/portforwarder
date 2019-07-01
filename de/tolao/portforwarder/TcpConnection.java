package de.tolao.portforwarder;

import java.nio.channels.SocketChannel;

public class TcpConnection
{
    private SocketChannel tcpAcceptedClient;
    private SocketChannel tcpConnectionToAddress;

    public TcpConnection(SocketChannel tcpAcceptedClient, SocketChannel tcpConnectionToAddress) {
        this.tcpAcceptedClient = tcpAcceptedClient;
        this.tcpConnectionToAddress = tcpConnectionToAddress;
    }

    public SocketChannel getTcpAcceptedClient() {
        return tcpAcceptedClient;
    }

    public SocketChannel getTcpConnectionToAddress() {
        return tcpConnectionToAddress;
    }

    public void close() throws java.io.IOException {
        if (tcpAcceptedClient != null)
            tcpAcceptedClient.close();
        if (tcpConnectionToAddress != null) {
            tcpConnectionToAddress.close();
        }
    }

    public String toString() {
        String s = "N/A";
        try {
            s = tcpAcceptedClient.getRemoteAddress().toString() + " via " + tcpConnectionToAddress.getLocalAddress();
        }
        catch (java.io.IOException localIOException) {}

        return "TCP: " + s;
    }
}
