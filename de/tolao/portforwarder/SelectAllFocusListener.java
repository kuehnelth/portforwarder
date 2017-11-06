package de.tolao.portforwarder;

import java.awt.event.FocusEvent;
import javax.swing.JTextField;

public class SelectAllFocusListener implements java.awt.event.FocusListener
{
  private JTextField tf;
  
  public SelectAllFocusListener(JTextField tf)
  {
    this.tf = tf;
  }
  
  public void focusGained(FocusEvent e)
  {
    tf.selectAll();
  }
  
  public void focusLost(FocusEvent e)
  {
    tf.select(0, 0);
  }
}
