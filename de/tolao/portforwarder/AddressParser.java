package de.tolao.portforwarder;

import java.net.InetSocketAddress;
import java.net.SocketAddress;


public class AddressParser
{
  public AddressParser() {}
  
  public SocketAddress getAddress(String address)
    throws IllegalArgumentException
  {
    int i = address.lastIndexOf(':');
    if (i <= 0) {
      throw new IllegalArgumentException("address '" + address + "' invalid: must have form 'host:port' (e.g. '192.168.8.15:815')");
    }
    String host = address.substring(0, i);
    String portAsString = address.substring(i + 1, address.length());
    int port = getPort(portAsString);
    
    return new InetSocketAddress(host, port);
  }
  


  public int getPort(String port)
    throws IllegalArgumentException
  {
    int p = -1;
    try {
      p = Integer.parseInt(port);
    } catch (NumberFormatException ex) {
      throw new IllegalArgumentException("port '" + port + "' invalid: must be an integer (e.g. '815')");
    }
    return p;
  }
}
