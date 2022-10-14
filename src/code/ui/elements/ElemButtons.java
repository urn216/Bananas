package code.ui.elements;

import code.ui.interactables.UISlider;
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
public class ElemButtons extends UIElement {

  private double buffer;
  private double buttonHeight;

  private Color accCol;
  private Color buthovCol;
  private Color butoutCol;
  private Color butinCol;
  private Color butlockCol;

  protected UIInteractable[] clickables;

  /**
  * Vertical Button box element
  *
  * @param tL The percent inwards from the top left corner of the screen for the top left corner of this element
  * @param bR The percent inwards from the top left corner of the screen for the bottom right corner of this element
  * @param buffer The amount of buffer space between buttons
  * @param colours The colour pack to use for this element
  * @param bNames an array containing all the buttons to have in the column
  * @param actions an array containing all the actions for the buttons to perform
  * @param ties Determines which directions should be faded from/towards in transitions
  */
  public ElemButtons(Vector2 tL, Vector2 bR, double buttonHeight, double buffer, Color[] colours, UIInteractable[] clickables, boolean[] ties) {
    assert(ties.length==4);
    topLeft = tL;
    botRight = bR;
    this.buffer = buffer;
    this.buttonHeight = buttonHeight;
    bgCol = colours[0];
    accCol = colours[2];
    buthovCol = colours[6];
    butoutCol = colours[3];
    butinCol = colours[4];
    butlockCol = colours[5];
    this.clickables = clickables;
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
    return "BUTTONS";
  }

  @Override
  public void transOut() {
    for (UIInteractable b : clickables) b.setOut();
    if (!transOut) startTimeMillis = System.currentTimeMillis();
    transOut = active;
  }

  @Override
  public UIInteractable getClickable(double x, double y) {
    for (UIInteractable c : clickables) {
      if (c.touching(x, y)) return c;
    }
    return null;
  }

  @Override
  public void resetClickables() {
    for (UIInteractable c : clickables) {
      c.setOut();
    }
  }

  @Override
  public void draw(Graphics2D g, double UIscale, int screenSizeX, int screenSizeY, UIInteractable highlighted, double[] playerStats) {
    if (!active && !transIn) {return;}
    double fadeDist = this.fadeDist*screenSizeY;
    Color bg = bgCol;
    Color acc = accCol;
    Color out = butoutCol;
    Color hov = buthovCol;

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
        out = fadeCol(out, fadeCount/fadeDist);
        hov = fadeCol(hov, fadeCount/fadeDist);

        lurd = fadeLoc(lurd, fadeDist-fadeCount);
      }
    }
    else if (transOut) {
      if (fadeCount >= fadeDist) {transOut = false; active = false; fadeCount = 0; return;}
      else {
        fadeCount = Math.min(fadeDist, MathHelp.lerp(0, fadeDist, (System.currentTimeMillis()-startTimeMillis)/animTimeMillis));

        bg  = fadeCol(bg,  1-fadeCount/fadeDist);
        acc = fadeCol(acc, 1-fadeCount/fadeDist);
        out = fadeCol(out, 1-fadeCount/fadeDist);
        hov = fadeCol(hov, 1-fadeCount/fadeDist);

        lurd = fadeLoc(lurd, fadeCount);
      }
    }

    //Draw the element
    g.setColor(bg);
    g.fill(new Rectangle2D.Double(lurd[0].x, lurd[0].y, lurd[1].x-lurd[0].x, lurd[1].y-lurd[0].y));
    float buff = (float) (buffer * UIscale);

    float x = (float) lurd[0].x + buff;
    float y = (float) lurd[0].y + buff;
    float width = (float) lurd[1].x - buff - x;
    float height = (float) (buttonHeight * UIscale);
    // float height = (float) (lurd[1].y-y)/(clickables.length)-buff;

    for (UIInteractable i : clickables) {
      i.draw(g, x, y, width, height, out, i == highlighted ? hov : acc, acc, butinCol, butlockCol);
      y += buff + height;
      if (i instanceof UISlider) y += height;
    }
  }
}
