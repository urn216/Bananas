package code.core;

import javax.swing.JFrame;
import javax.swing.JPanel;

import code.core.scene.elements.Camera;

import code.math.IOHelp;
import code.math.Vector2;

import java.awt.Graphics;
import java.awt.image.BufferedImage;
import java.awt.Insets;
import java.awt.GraphicsDevice;
import java.awt.GraphicsEnvironment;

import java.awt.event.WindowEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentAdapter;

public final class Window {
  
  public static final Vector2 DEFAULT_SCREEN_SIZE = new Vector2(1920, 1080);

  public final JFrame FRAME = new JFrame("Bananas");
  public final JPanel PANEL = new JPanel() {public void paintComponent(Graphics gra) {Core.paintComponent(gra);}};
  
  private int screenSizeX, screenSizeY;
  private int smallScreenX, smallScreenY;
  
  int toolBarLeft, toolBarRight, toolBarTop, toolBarBot;

  Window() {
    FRAME.getContentPane().add(PANEL);
    FRAME.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
    FRAME.setResizable(true);
    BufferedImage image = IOHelp.readImage("icon.png");
    FRAME.setIconImage(image);
    
    GraphicsDevice gd = GraphicsEnvironment.getLocalGraphicsEnvironment().getDefaultScreenDevice();
    smallScreenX = gd.getDisplayMode().getWidth()/2;
    smallScreenY = gd.getDisplayMode().getHeight()/2;
    
    screenSizeX = smallScreenX;
    screenSizeY = smallScreenY;
    
    FRAME.addWindowListener( new WindowAdapter() {
      @Override
      public void windowClosing(WindowEvent e) {
        Core.quitToDesk();
      }
    });
    FRAME.addComponentListener( new ComponentAdapter() {
      @Override
      public void componentResized(ComponentEvent e) {
        screenSizeX = FRAME.getWidth() - toolBarLeft - toolBarRight;
        screenSizeY = FRAME.getHeight() - toolBarTop - toolBarBot;

        Camera cam = Core.getActiveCam();

        if (cam != null) {cam.resetZoom();}
        if (!isFullScreen()) {
          smallScreenX = screenSizeX;
          smallScreenY = screenSizeY;
        }
        // System.out.println(screenSizeX + ", " + screenSizeY);
      }
    });
  }

  /**
   * @return the current width of the screen
   */
  public int screenWidth() {
    return screenSizeX;
  }

  /**
   * @return the current height of the screen
   */
  public int screenHeight() {
    return screenSizeY;
  }
  
  /**
  * A helper method that updates the window insets to match their current state
  */
  private void updateInsets() {
    Insets i = FRAME.getInsets(); //Toolkit.getDefaultToolkit().getScreenInsets(getGraphicsConfiguration())
    // System.out.println(i);
    toolBarLeft = i.left;
    toolBarRight = i.right;
    toolBarTop = i.top;
    toolBarBot = i.bottom;
  }
  
  /**
  * A helper method that toggles the fullscreen state for the window
  */
  public void toggleFullscreen() {
    setFullscreen(!isFullScreen());
  }
  
  /**
  * A helper method to check whether or not the window is maximized
  * 
  * @return true if the window is maximized
  */
  public boolean isFullScreen() {
    return FRAME.getExtendedState() == JFrame.MAXIMIZED_BOTH && FRAME.isUndecorated();
  }
  
  /**
  * A helper method that sets the fullscreen state for the window
  * 
  * @param maximize whether or not the screen should me maximized
  */
  public void setFullscreen(boolean maximize) {
    FRAME.removeNotify();
    FRAME.setVisible(false);
    if (maximize) {
      FRAME.setExtendedState(JFrame.MAXIMIZED_BOTH);
      FRAME.setUndecorated(true);
      FRAME.addNotify();
      updateInsets();
    }
    else {
      FRAME.setExtendedState(JFrame.NORMAL);
      FRAME.setUndecorated(false);
      FRAME.addNotify();
      updateInsets();
      FRAME.setSize(smallScreenX + toolBarLeft + toolBarRight, smallScreenY + toolBarTop + toolBarBot);
    }
    FRAME.setVisible(true);
    FRAME.requestFocus();
    
    screenSizeX = FRAME.getWidth()  - toolBarLeft - toolBarRight;
    screenSizeY = FRAME.getHeight() - toolBarTop  - toolBarBot  ;
  }
}
