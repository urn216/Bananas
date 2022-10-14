package code.ui.elements;

import code.ui.UIInteractable;
import code.ui.UIElement;

import code.math.Vector2;
import code.math.MathHelp;

import java.awt.Graphics2D;
import java.awt.geom.Rectangle2D;

import java.awt.Color;
import java.awt.Font;
import java.awt.FontMetrics;

/**
* Write a description of class Element here.
*
* @author (your name)
* @version (a version number or a date)
*/
public class ElemInfo extends UIElement {

  private double buffer;

  private String title;

  private String[] text = {"Health: ", "Pos X: ", "Pos Y: ", "Vel X: ", "Vel Y: ", "FPS: "};
  private int[] indices;

  private int fontStyle;
  private int fontSize;

  private Color accCol;

  /**
  * Text box element
  *
  * @param tL The percent inwards from the top left corner of the screen for the top left corner of this element
  * @param bR The percent inwards from the top left corner of the screen for the bottom right corner of this element
  * @param buff The amount of buffer space between buttons
  * @param names The text that should appear on each row of this element
  * @param ind The information indices to use for each row of this element
  * @param style The Font style the text should adopt
  * @param size The Font size the text should adopt
  * @param colours The colour pack to use for this element
  * @param ties Determines which directions should be faded from/towards in transitions
  */
  public ElemInfo(Vector2 tL, Vector2 bR, double buff, int[] ind, int style, int size, Color[] colours, boolean[] ties) {
    assert(ties.length==4);
    topLeft = tL;
    botRight = bR;
    buffer = buff;
    this.indices = ind;
    this.fontStyle = style;
    this.fontSize = size;
    bgCol = colours[0];
    accCol = colours[2];
    indices = ind;
    if (ties[0] || ties[1]) {
      fadeUp    = ties[0] ? 1 : -1;
      fadeDown  = ties[1] ? -1 : 1;
    }
    if (ties[2] || ties[3]) {
      fadeLeft  = ties[2] ? 1 : -1;
      fadeRight = ties[3] ? -1 : 1;
    }
  }

  @Override
  public String getType() {
    return "INFO";
  }

  @Override
  public void draw(Graphics2D g, double UIscale, int screenSizeX, int screenSizeY, UIInteractable highlighted, double[] playerStats) {
    if (!active && !transIn) {return;}
    double fadeDist = this.fadeDist*screenSizeY;
    Color bg = bgCol;
    Color acc = accCol;

    Vector2[] lurd = {
      new Vector2(topLeft.x*screenSizeX, topLeft.y*screenSizeY),
      new Vector2(botRight.x*screenSizeX, botRight.y*screenSizeY)
    };

    //Transition if necessary
    if (transIn) {
      if (fadeCount >= fadeDist) {transIn = false; active = true; fadeCount = 0;}
      else {
        fadeCount = Math.min(fadeDist, MathHelp.lerp(0, fadeDist, (System.currentTimeMillis()-startTimeMillis)/animTimeMillis));

        bg  = fadeCol(bg,  fadeCount/fadeDist);
        acc = fadeCol(acc, fadeCount/fadeDist);

        lurd = fadeLoc(lurd, fadeDist-fadeCount);
      }
    }
    else if (transOut) {
      if (fadeCount >= fadeDist) {transOut = false; active = false; fadeCount = 0; return;}
      else {
        fadeCount = Math.min(fadeDist, MathHelp.lerp(0, fadeDist, (System.currentTimeMillis()-startTimeMillis)/animTimeMillis));

        bg  = fadeCol(bg,  1-fadeCount/fadeDist);
        acc = fadeCol(acc, 1-fadeCount/fadeDist);

        lurd = fadeLoc(lurd, fadeCount);
      }
    }

    //Draw the element
    g.setColor(bg);
    g.fill(new Rectangle2D.Double(lurd[0].x, lurd[0].y, lurd[1].x-lurd[0].x, lurd[1].y-lurd[0].y));
    Font font = new Font("Copperplate", fontStyle, (int) Math.round(fontSize*UIscale));
    FontMetrics metrics = g.getFontMetrics(font);
    float buff = (float) (buffer*UIscale);

    g.setFont(font);
    g.setColor(acc);
    float tp = (float) (lurd[0].y + buff);
    float ht = (float) ((lurd[1].y-tp)/(indices.length)-buff);
    for (int i=0; i<indices.length; i++) {
      int ind = indices[i];
      title = text[ind]+String.format("%.2f", playerStats[ind]);
      g.drawString(title, (float) (lurd[0].x + (lurd[1].x-lurd[0].x-metrics.stringWidth(title))/2), (float) (tp + (buff+ht)*i + ((ht - metrics.getHeight())/2) + metrics.getAscent()));
    }
  }
}
