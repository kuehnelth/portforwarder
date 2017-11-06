package de.tolao.portforwarder;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Container;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.io.IOException;
import javax.swing.AbstractAction;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JRootPane;
import javax.swing.JTextField;




public class Window
  extends JFrame
  implements IFeedback
{
  private static final long serialVersionUID = 1L;
  private Conf conf;
  private TcpConnections tcpConnections;
  private UdpConnections udpConnections;
  private AddressParser addressParser;
  private PortForwarder portForwarder;
  private JTextField tfConnectToAddress;
  private JTextField tfAcceptFromPort;
  private JButton tbStartStop;
  private JLabel lblState;
  private JList<TcpConnection> lTcpConnections;
  private JList<UdpConnection> lUdpConnections;
  private ColorBallIcon redIcon;
  private ColorBallIcon greenIcon;
  private boolean isRunning = false;
  
  public Window() throws IOException {
    super("PortForwarder");
    conf = new Conf();
    
    tcpConnections = new TcpConnections();
    udpConnections = new UdpConnections();
    
    addressParser = new AddressParser();
    portForwarder = new PortForwarder(this, tcpConnections, udpConnections);
    
    Container cp = getContentPane();
    cp.setLayout(new BorderLayout());
    
    JPanel p = new JPanel(new GridLayout(0, 2));
    
    tfConnectToAddress = new JTextField(conf.get("connectToAddress"), 20);
    tfConnectToAddress.addFocusListener(new SelectAllFocusListener(tfConnectToAddress));
    tfAcceptFromPort = new JTextField(conf.get("acceptFromPort"), 20);
    tfAcceptFromPort.addFocusListener(new SelectAllFocusListener(tfAcceptFromPort));
    
    redIcon = new ColorBallIcon(Color.RED, 16);
    greenIcon = new ColorBallIcon(Color.GREEN, 16);
    
    lblState = new JLabel(redIcon);
    
    tbStartStop = new JButton(new AbstractAction("start")
    {
      private static final long serialVersionUID = 1L;
      
      public void actionPerformed(ActionEvent e) {
        if (!isRunning) {
          try {
            portForwarder.start(addressParser.getAddress(tfConnectToAddress.getText()), addressParser.getPort(tfAcceptFromPort.getText()));
            setRunning(true);
          } catch (Exception ex) {
            showException(ex);
          }
        } else {
          try {
            portForwarder.stop();
            setRunning(false);
          } catch (Exception ex) {
            showException(ex);
          }
          
        }
      }
    });
    p.add(new JLabel("connect to host:port"));
    p.add(tfConnectToAddress);
    
    p.add(new JLabel("accept on port"));
    p.add(tfAcceptFromPort);
    
    p.add(lblState);
    p.add(tbStartStop);
    
    cp.add(p, "North");
    
    JPanel p2 = new JPanel(new GridLayout(0, 2));
    lTcpConnections = new JList();
    lTcpConnections.setModel(tcpConnections);
    lUdpConnections = new JList();
    lUdpConnections.setModel(udpConnections);
    
    p2.add(lTcpConnections);
    p2.add(lUdpConnections);
    
    cp.add(p2, "Center");
    
    getRootPane().setDefaultButton(tbStartStop);
    pack();
  }
  
  public void showException(Exception ex)
  {
    setRunning(false);
    ex.printStackTrace();
    JOptionPane.showMessageDialog(null, ex.getMessage(), "Exception", 2);
  }
  
  protected void setRunning(boolean running) {
    isRunning = running;
    tbStartStop.setText(running ? "stop" : "start");
    lblState.setIcon(running ? greenIcon : redIcon);
  }
}
