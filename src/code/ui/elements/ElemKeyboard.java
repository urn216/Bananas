package code.ui.elements;

import java.awt.Color;
import java.awt.Graphics2D;

import code.math.Vector2;
import code.ui.components.UIInteractable;

public class ElemKeyboard extends UIElement {

  public ElemKeyboard(Vector2 topLeft, Vector2 botRight, boolean[] ties) {
    super(topLeft, botRight, ties);
  }

  @Override
  protected void draw(Graphics2D g, int screenSizeY, Vector2 tL, Vector2 bR, Color[] c, UIInteractable highlighted) {
    // TODO Auto-generated method stub
    
  }
}
