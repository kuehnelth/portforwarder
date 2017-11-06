package de.tolao.portforwarder;

import java.io.IOException;

public class Main
{
  public Main() {}
  
  public static void main(String[] args) throws IOException {
    javax.swing.SwingUtilities.invokeLater(new Runnable()
    {
      public void run() {
        try {
          Window w = new Window();
          w.setDefaultCloseOperation(3);
          w.setVisible(true);
        } catch (IOException e) {
          e.printStackTrace();
          System.exit(1);
        }
      }
    });
  }
}
