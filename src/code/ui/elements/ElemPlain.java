package code.ui.elements;

import code.ui.UIInteractable;
import code.ui.UIElement;

import code.math.Vector2;
import code.math.MathHelp;

import java.awt.Graphics2D;
import java.awt.geom.Rectangle2D;

import java.awt.Color;

/**
* Write a description of class Element here.
*
* @author (your name)
* @version (a version number or a date)
*/
public class ElemPlain extends UIElement {

  /**
  * Plain box element
  *
  * @param tL The percent inwards from the top left corner of the screen for the top left corner of this element
  * @param bR The percent inwards from the top left corner of the screen for the bottom right corner of this element
  * @param colours The colour pack to use for this element
  * @param ties Determines which directions should be faded from/towards in transitions
  */
  public ElemPlain(Vector2 tL, Vector2 bR, Color[] colours, boolean[] ties) {
    assert(ties.length==4);
    topLeft = tL;
    botRight = bR;
    bgCol = colours[0];
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
    return "PLAIN";
  }

  @Override
  public void draw(Graphics2D g, double UIscale, int screenSizeX, int screenSizeY, UIInteractable highlighted, double[] playerStats) {
    if (!active && !transIn) {return;}
    double fadeDist = this.fadeDist*screenSizeY;
    Color bg = bgCol;

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

        lurd = fadeLoc(lurd, fadeDist-fadeCount);
      }
    }
    else if (transOut) {
      if (fadeCount >= fadeDist) {transOut = false; active = false; fadeCount = 0; return;}
      else {
        fadeCount = Math.min(fadeDist, MathHelp.lerp(0, fadeDist, (System.currentTimeMillis()-startTimeMillis)/animTimeMillis));

        bg  = fadeCol(bg,  1-fadeCount/fadeDist);

        lurd = fadeLoc(lurd, fadeCount);
      }
    }

    //Draw the element
    g.setColor(bg);
    g.fill(new Rectangle2D.Double(lurd[0].x, lurd[0].y, lurd[1].x-lurd[0].x, lurd[1].y-lurd[0].y));

  }
}
