package code.ui.interactables;

import code.ui.UIInteractable;
import code.ui.UIAction;
import code.ui.UIActionSetter;

import java.awt.Color;

import java.awt.Graphics2D;
import java.awt.geom.Rectangle2D;

/**
* Class for making functional text fields
*/
public class UITextfield extends UIInteractable {
  private char[] text;
  private int ind[];
  private int line = 0;
  private int totind = 0;
  private final int numLines;

  private final UIAction enterAction;

  /**
  * Constructor for Text Fields
  */
  public UITextfield(int maxLength, int numLines, UIActionSetter<UITextfield> select, UIAction enter) {
    assert (numLines > 0);
    this.name = "UI_Text_Field";
    this.text = new char[maxLength+1];
    this.numLines = numLines;
    this.ind = new int[numLines];
    this.primeAction = () -> select.set(this);
    this.enterAction = enter;
  }

  public void enterAct() {if (enterAction!=null) enterAction.perform();}

  public String getText() {
    return new String(text, 0, totind);
  }

  public String[] getTextLines() {
    String[] res = new String[numLines];
    int j = 0;
    for (int i = 0; i < numLines; i++) {
      res[i] = "";
      char c = '\u0000';
      while (c!='\n' && j < text.length) {
        res[i]+=c;
        c = text[j++];
      }
    }
    return res;
  }

  public boolean checkValid(String check) {
    if (text[0]=='\u0000') return false;
    char[] checker = check.toCharArray();
    for (char cc : checker) {
      for (char tc : text) {
        if (tc == cc) {return false;}
      }
    }
    return true;
  }

  public void print(char c) {
    if (totind>=text.length-1) return;
    // System.out.print(c);
    text[totind] = c;
    totind++;
    ind[line]++;
  }

  public void backspace() {
    if (totind<=0) return;
    totind--;
    if (text[totind]=='\n') line--;
    ind[line]--;
    text[totind] = '\u0000';
  }

  public void newLine() {
    if (line >= numLines-1) return;
    print('\n');
    // System.out.println(ind[line]);
    line++;
  }

  public void reset() {
    text = new char[text.length];
    ind = new int[numLines];
    line = 0;
    totind = 0;
  }

  @Override
  protected void drawBody(Graphics2D g, int off, Color bodyCol, Color textCol) {
    g.setColor(bodyCol);
    g.fill(new Rectangle2D.Double(x, y, width, height));
    g.setColor(textCol);
    g.draw(new Rectangle2D.Double(x, y, width, height));

    String[] s = getTextLines();
    for (int i = 0; i < s.length; i++) {
      g.drawString(s[i], x+width/32, y+((height - metrics.getHeight())/2) + metrics.getAscent() + i*metrics.getAscent());
    }
  }
}
