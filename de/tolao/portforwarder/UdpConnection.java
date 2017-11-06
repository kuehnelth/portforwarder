package de.tolao.portforwarder;

import java.nio.channels.DatagramChannel;

public class UdpConnection
{
  private java.net.SocketAddress udpClientAddress;
  private DatagramChannel udpConnectionToAddress;
  private long lastUsedAt;
  
  public UdpConnection(java.net.SocketAddress udpClientAddress, DatagramChannel udpConnectionToAddress)
  {
    this.udpClientAddress = udpClientAddress;
    this.udpConnectionToAddress = udpConnectionToAddress;
    lastUsedAt = System.nanoTime();
  }
  
  public java.net.SocketAddress getUdpClientAddress() {
    return udpClientAddress;
  }
  
  public DatagramChannel getUdpConnectionToAddress() {
    return udpConnectionToAddress;
  }
  
  public boolean hasElapsed() {
    return lastUsedAt + 10000000000L < System.nanoTime();
  }
  
  public void markUsed() {
    lastUsedAt = System.nanoTime();
  }
  
  public void close() throws java.io.IOException {
    if (udpConnectionToAddress != null) {
      udpConnectionToAddress.close();
    }
  }
  
  public String toString() {
    String s = "N/A";
    try {
      s = udpClientAddress + " via " + udpConnectionToAddress.getLocalAddress();
    }
    catch (java.io.IOException localIOException) {}
    
    return "UDP: " + s;
  }
}
