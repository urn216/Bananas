package code.ui.components;

import java.awt.Color;
import java.awt.Font;
import java.awt.FontMetrics;

import java.awt.Graphics2D;

public class UIText extends UIComponent {

  private final double fontScale;
  private final int fontStyle;

  public UIText(String text, double fontScale, int fontStyle) {
    this.text = text;
    this.fontScale = fontScale;
    this.fontStyle = fontStyle;
  }

  @Override
  public void draw(Graphics2D g, Color... colours) {
    Font font = new Font("Copperplate", fontStyle, (int) Math.round((height*fontScale)));
    FontMetrics metrics = g.getFontMetrics(font);
    g.setFont(font);
    g.setColor(colours[0]);

    g.drawString(text, x+(width-metrics.stringWidth(text))/2, y+((height - metrics.getHeight())/2) + metrics.getAscent());
  }
  
}
