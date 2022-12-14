package code.ui.components.interactables;

import code.ui.UIActionSetter;
import code.ui.UIAction;
import code.ui.UIController;
import code.ui.components.UIInteractable;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.geom.Rectangle2D;

/**
 * Class for making functional text fields
 */
public class UITextfield extends UIInteractable {
  protected char[] textChars;
  protected int ind[];
  protected int line = 0;
  protected int totind = 0;
  protected final int numLines;

  private final UIAction enterAction;
  private final UIAction clearAction;

  /**
   * Constructor for Text Fields.
   * 
   * @param maxLength the maximum number of characters allowed in the field.
   * @param numLines the maximum number of lines this field may utilise. Must be at least 1.
   * @param enter the destination for the text in this field when enter is pressed. Leave null for new line functionality.
   * @param ui the UIController for this field.
   */
  public UITextfield(String defaultText, int maxLength, int numLines, UIActionSetter<String> enter) {
    assert (numLines > 0);
    this.text = defaultText;
    this.textChars = new char[maxLength+1];
    this.numLines = numLines;
    this.ind = new int[numLines];
    this.primeAction = () -> UIController.setActiveTextfield(this);
    this.clearAction = () -> UIController.setActiveTextfield(null);
    this.enterAction = enter != null ? () -> enter.set(getText()) : this::newLine;
  }

  /**
   * Performs the enter functionality.
   */
  public void enterAct() {enterAction.perform();}

  public String getText() {
    return new String(textChars, 0, totind);
  }

  public String[] getTextLines() {
    if (totind == 0) {
      fontStyle = Font.ITALIC;
      return new String[] {this.text};
    }

    fontStyle = Font.BOLD;
    String[] res = new String[numLines];
    int j = 0;
    for (int i = 0; i < numLines; i++) {
      res[i] = "";
      char c = '\u0000';
      while (c!='\n' && j < textChars.length) {
        res[i]+=c;
        c = textChars[j++];
      }
    }
    return res;
  }

  public boolean checkValid(String check) {
    if (textChars[0]=='\u0000') return false;
    char[] checker = check.toCharArray();
    for (char cc : checker) {
      for (char tc : textChars) {
        if (tc == cc) {return false;}
      }
    }
    return true;
  }

  public boolean isValid() {return true;}

  public void print(char c) {
    if (totind>=textChars.length-1) return;
    // System.out.print(c);
    textChars[totind] = c;
    totind++;
    ind[line]++;
  }

  public void backspace() {
    if (totind<=0) return;
    totind--;
    if (textChars[totind]=='\n') line--;
    ind[line]--;
    textChars[totind] = '\u0000';
  }

  public void newLine() {
    if (line >= numLines-1) {
      clearAction.perform();
      return;
    }
    print('\n');
    // System.out.println(ind[line]);
    line++;
  }

  public void reset() {
    textChars = new char[textChars.length];
    ind = new int[numLines];
    line = 0;
    totind = 0;
  }

  @Override
  protected void drawBody(Graphics2D g, int off, Color bodyCol, Color tl, Color br, Color textCol) {
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
