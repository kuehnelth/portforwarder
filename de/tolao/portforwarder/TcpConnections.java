package de.tolao.portforwarder;

import java.nio.channels.Channel;

public class TcpConnections extends javax.swing.DefaultListModel<TcpConnection> {
    private static final long serialVersionUID = 1L;

    public TcpConnections() {}

    public int indexByTcpAcceptedClient(Channel tcpAcceptedClient) {
        for (int i = 0; i < size(); i++)
            if (((TcpConnection)get(i)).getTcpAcceptedClient() == tcpAcceptedClient)
                return i;
        return -1;
    }

    public int indexByTcpConnectionToAddress(Channel tcpConnectionToAddress) {
        for (int i = 0; i < size(); i++)
            if (((TcpConnection)get(i)).getTcpConnectionToAddress() == tcpConnectionToAddress)
                return i;
        return -1;
    }
}
