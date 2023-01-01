package code.core;

import javax.swing.JPanel;

import java.awt.Graphics;

class CorePanel extends JPanel {
  private static final long serialVersionUID = 1;
  
  public void paintComponent(Graphics gra) {
    Core.paintComponent(gra);
  }
}
