package de.tolao.portforwarder;

import java.net.SocketAddress;

public class UdpConnections extends javax.swing.DefaultListModel<UdpConnection>
{
  private static final long serialVersionUID = 1L;
  
  public UdpConnections() {}
  
  public int indexByUdpConnectionToAddress(java.nio.channels.Channel udpConnectionToAddress)
  {
    for (int i = 0; i < size(); i++)
      if (((UdpConnection)get(i)).getUdpConnectionToAddress() == udpConnectionToAddress)
        return i;
    return -1;
  }
  
  public int indexByUdpClientAddress(SocketAddress udpClientAddress) {
    for (int i = 0; i < size(); i++)
      if (((UdpConnection)get(i)).getUdpClientAddress().equals(udpClientAddress))
        return i;
    return -1;
  }
  
  public void cleanup() throws java.io.IOException {
    for (int i = 0; i < size(); i++) {
      UdpConnection u = (UdpConnection)get(i);
      if (u.hasElapsed()) {
        u.close();
        remove(i);
        i--;
      }
    }
  }
}
