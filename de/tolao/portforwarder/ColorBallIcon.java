package de.tolao.portforwarder;

import java.awt.Color;
import java.awt.Component;
import java.awt.Graphics;
import java.awt.Graphics2D;
import javax.swing.Icon;






public class ColorBallIcon
  implements Icon
{
  private Color color;
  private int diameter;
  
  public ColorBallIcon(Color color, int diameter)
  {
    this.color = color;
    this.diameter = diameter;
  }
  
  public void paintIcon(Component c, Graphics g, int x, int y)
  {
    Graphics2D g2d = (Graphics2D)g.create();
    
    g2d.setColor(color);
    g2d.drawOval(x, y, diameter - 1, diameter - 1);
    g2d.fillOval(x, y, diameter - 1, diameter - 1);
    
    g2d.dispose();
  }
  
  public int getIconWidth()
  {
    return diameter;
  }
  
  public int getIconHeight()
  {
    return diameter;
  }
}
