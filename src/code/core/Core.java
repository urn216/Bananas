package code.core;

import code.board.TileGrid;
import code.board.TilePiece;

import java.awt.event.MouseWheelEvent;
import code.math.IOHelp;
import code.math.Vector2;
import code.ui.UIController;
import code.ui.UICreator;

import code.board.Camera;
import code.board.Server;

//import java.util.*;
//import java.awt.Color;
//import java.awt.Font;
import java.awt.image.BufferedImage;
import java.awt.Insets;
//import java.awt.Toolkit;

import java.awt.event.KeyEvent;
import java.awt.event.KeyAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.WindowEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentAdapter;

import javax.swing.JFrame;
import javax.swing.JPanel;
import java.awt.Graphics;
import java.awt.Graphics2D;

enum State {
  MAINMENU,
  HOST,
  RUN,
  END
}

/**
* Core class for the currently unnamed game
*/
public class Core extends JPanel {
  private static final long serialVersionUID = 1;
  
  public static final Vector2 DEFAULT_SCREEN_SIZE = new Vector2(1920, 1080);
  public static final String BLACKLISTED_CHARS = "/\\.?!*\n";
  
  private static final double TICKS_PER_SECOND = 60;
  private static final double MILLISECONDS_PER_TICK = 1000/TICKS_PER_SECOND;
  
  private JFrame f;
  private boolean maximized = true;
  
  private boolean quit = false;
  
  private int toolBarLeft, toolBarRight, toolBarTop, toolBarBot;
  
  private boolean[] keyDown = new boolean[65536];
  private boolean[] mouseDown = new boolean[4];
  private Vector2 mousePos;
  private Vector2 mousePre;
  
  private final UIController uiCon;
  
  private Scene current;
  
  private Server server = null;
  
  private Camera cam;
  private int screenSizeX;
  private int screenSizeY;
  private int smallScreenX = (int)DEFAULT_SCREEN_SIZE.x;
  private int smallScreenY = (int)DEFAULT_SCREEN_SIZE.y;
  
  // private long pFTime = System.currentTimeMillis();
  // private int fCount = 0;
  
  /** Current game state */
  private State state = State.MAINMENU;
  
  public final Settings globalSettings;
  
  /**
  * Main method. Called on execution. Performs basic startup
  *
  * @param args Ignored for now
  */
  public static void main(String[] args) {
    Core core = new Core();
    core.playGame();
  }
  
  /**
  * Performs initialisation of the program. Only to be run on startup
  */
  public Core() {
    f = new JFrame("Bananas");
    f.getContentPane().add(this);
    f.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
    f.setResizable(true);
    BufferedImage image = IOHelp.readImage("icon.png");
    f.setIconImage(image);
    f.addWindowListener( new WindowAdapter() {
      @Override
      public void windowClosing(WindowEvent e) {
        quit = true;
      }
    });
    f.addComponentListener( new ComponentAdapter() {
      @Override
      public void componentResized(ComponentEvent e) {
        screenSizeX = f.getWidth() - toolBarLeft - toolBarRight;
        screenSizeY = f.getHeight() - toolBarTop - toolBarBot;
        if (cam != null) {cam.setScreenSize(screenSizeX, screenSizeY);}
        // System.out.println(screenSizeX + ", " + screenSizeY);
      }
    });
    f.setExtendedState(JFrame.MAXIMIZED_BOTH);
    f.setUndecorated(true);
    f.setVisible(true);
    f.requestFocus();
    globalSettings = new Settings();
    screenSizeX = f.getWidth();
    screenSizeY = f.getHeight();
    
    uiCon = new UIController(globalSettings);
    
    uiCon.putPane("Main Menu", UICreator.createMain(this, uiCon));
    uiCon.putPane("HUD"      , UICreator.createHUD(this, uiCon) );
    
    toMenu();
    
    initialiseControls();
  }
  
  /**
  * Switches the current scene to a new one via the new scene's name
  *
  * @param name The name of the scene to switch to
  */
  public void toMenu() {
    server = null;
    current = new Scene(true);
    cam = new Camera(new Vector2(), new Vector2(), 1, screenSizeX, screenSizeY);
    
    state = State.MAINMENU;
    uiCon.setCurrent("Main Menu");
  }
  
  public void hostGame() {
    server = new Server();
    current = new Scene(false);
    cam = new Camera(new Vector2(), new Vector2(), 1, screenSizeX, screenSizeY);
    
    state = State.RUN;
    uiCon.setCurrent("HUD");
  }

  public void joinGame() {
    server = null;
    current = new Scene(false);
    cam = new Camera(new Vector2(), new Vector2(), 1, screenSizeX, screenSizeY);
    
    state = State.RUN;
    uiCon.setCurrent("HUD");
  }
  
  /**
  * A helper method that updates the window insets to match their current state
  */
  private void updateInsets() {
    Insets i = f.getInsets(); //Toolkit.getDefaultToolkit().getScreenInsets(getGraphicsConfiguration())
    // System.out.println(i);
    toolBarLeft = i.left;
    toolBarRight = i.right;
    toolBarTop = i.top;
    toolBarBot = i.bottom;
  }
  
  /**
  * A helper method that toggles fullscreen for the window
  */
  public void doFull() {
    f.removeNotify();
    if (maximized) {
      f.setExtendedState(JFrame.NORMAL);
      f.setUndecorated(false);
      f.addNotify();
      updateInsets();
      f.setSize(smallScreenX + toolBarLeft + toolBarRight, smallScreenY + toolBarTop + toolBarBot);
    }
    else {
      smallScreenX = screenSizeX;
      smallScreenY = screenSizeY;
      f.setVisible(false);
      f.setExtendedState(JFrame.MAXIMIZED_BOTH);
      f.setUndecorated(true);
      f.setVisible(true);
      updateInsets();
      f.addNotify();
    }
    f.requestFocus();
    maximized = !maximized;
  }
  
  /**
  * Returns the scene to the main menu state
  */
  public void quitToMenu() {
    toMenu();
  }
  
  /**
  * Sets the flag to quit the game at the nearest convenience
  */
  public void quitToDesk() {
    quit = true;
  }
  
  /**
  * Main loop. Should always be running. Runs the rest of the game engine
  */
  private void playGame() {
    while (true) {
      long tickTime = System.currentTimeMillis();
      
      switch (state) {
        case MAINMENU:
        break;
        case HOST:
        server.broadcast("Hello");
        case RUN:
        case END:
        if (mouseDown[2] || mouseDown[3]) {
          cam.setOffset(cam.getOffset().add(mousePos.subtract(mousePre)));
          mousePre = mousePos.copy();
        }
        break;
      }
      
      repaint();
      if (quit) {
        System.exit(0);
      }
      tickTime = System.currentTimeMillis() - tickTime;
      try {
        Thread.sleep(Math.max((long)(MILLISECONDS_PER_TICK - tickTime), 0));
      } catch(InterruptedException e){System.out.println(e); System.exit(0);}
    }
  }
  
  @Override
  public void paintComponent(Graphics gra) {
    Graphics2D g = (Graphics2D) gra;
    
    switch (state) {
      case MAINMENU:
      if (current != null) {current.draw(g, cam, false);}
      uiCon.draw(g, screenSizeX, screenSizeY);
      break;
      case HOST:
      case RUN:
      case END:
      current.draw(g, cam, false);
      if (mouseDown[1]) current.getHeldTile().draw(g, mousePos, cam.getZoom(), false, false);
      // uiCon.draw(g, screenSizeX, screenSizeY, current.getStats());
      uiCon.draw(g, screenSizeX, screenSizeY);
      break;
    }
  }
  
  /**
  * Starts up all the listeners for the window. Only to be called once on startup.
  */
  private void initialiseControls() {
    
    //Mouse Controls
    f.addMouseMotionListener(new MouseAdapter() {
      @Override
      public void mouseMoved(MouseEvent e) {
        int x = e.getX() - toolBarLeft;
        int y = e.getY() - toolBarTop;
        mousePos = new Vector2(x, y);
        uiCon.cursorMove(x, y);
      }
      
      @Override
      public void mouseDragged(MouseEvent e) {
        int x = e.getX() - toolBarLeft;
        int y = e.getY() - toolBarTop;
        mousePos = new Vector2(x, y);
        uiCon.cursorMove(x, y);
        uiCon.useSlider(x);
      }
    });
    f.addMouseListener(new MouseAdapter() {
      @Override
      public void mousePressed(MouseEvent e) {
        int x = e.getX() - toolBarLeft;
        int y = e.getY() - toolBarTop;
        mousePos = new Vector2(x, y);
        
        if (uiCon.getHighlighted() == null) mouseDown[e.getButton()] = true;
        mousePre = mousePos.copy();
        
        //left click
        if (e.getButton() == 1) {
          uiCon.cursorMove(x, y);
          if (uiCon.press()) return;
          current.pressTile(current.convertToIndex(mousePos, cam));
        }
        
        //right click
        if (e.getButton() == 3) {
          current.pressTile(current.convertToIndex(mousePos, cam));
        }
      }
      
      @Override
      public void mouseReleased(MouseEvent e) {
        int x = e.getX() - toolBarLeft;
        int y = e.getY() - toolBarTop;
        mouseDown[e.getButton()] = false;
        
        //left click
        if (e.getButton() == 1) {
          current.selectTile(current.convertToIndex(mousePos, cam));
          uiCon.cursorMove(x, y);
          uiCon.release();
          return;
        }
        
        //right click
        if (e.getButton() == 3) {
          current.removeTile(current.convertToIndex(mousePos, cam));
          current.deselectTiles();
          return;
        }
      }
    });
    
    f.addMouseWheelListener(new MouseAdapter() {
      public void mouseWheelMoved(MouseWheelEvent e) {
        double z = e.getWheelRotation()<0 ? cam.getZoom()*1.1 : cam.getZoom()/1.1;
        cam.setZoom(z, mousePos.subtract(screenSizeX*0.5, screenSizeY*0.5));
      }
    });
    
    //Keyboard Controls
    f.addKeyListener(new KeyAdapter() {
      @Override
      public void keyPressed(KeyEvent e) {
        int keyCode = e.getKeyCode();
        
        if (uiCon.getActiveTextfield() != null && !keyDown[KeyEvent.VK_CONTROL]) uiCon.typeKey(e);
        
        if(keyDown[keyCode]) return; //Key already in
        keyDown[keyCode] = true;
        
        // System.out.print(keyCode);
        if (keyCode == KeyEvent.VK_F11) {
          doFull();
        }
        else if (keyCode == KeyEvent.VK_ESCAPE) {
          uiCon.back();
        }
        else if (keyCode == KeyEvent.VK_ENTER) {
          uiCon.press();
        }
        else if (keyCode == KeyEvent.VK_MINUS) {
          cam.setZoom(cam.getZoom()/2);
        }
        else if (keyCode == KeyEvent.VK_EQUALS) {
          cam.setZoom(cam.getZoom()*2);
        }
        else if (keyCode >= 65 && keyCode <= 90) {
          TileGrid t = current.hasSelectedTiles() ? current.getSelectedTiles().get(0) : null;
          if (t != null) t.place(new TilePiece((char)keyCode));
        }
      }
      
      @Override
      public void keyReleased(KeyEvent e){
        int keyCode = e.getKeyCode();
        keyDown[keyCode] = false;
        
        if (keyCode == KeyEvent.VK_ENTER) {
          uiCon.release();
        }
      }
    });
  }
}
