package code.core;

import code.board.TileGrid;
// import code.board.TilePiece;

import java.awt.event.MouseWheelEvent;
import code.math.IOHelp;
import code.math.Vector2;
import code.math.Vector2I;
import code.ui.UIController;
import code.ui.UICreator;
import code.board.Camera;
import code.board.Decal;
import code.board.Server;

import java.awt.image.BufferedImage;
import java.awt.Insets;
import java.awt.GraphicsDevice;
import java.awt.GraphicsEnvironment;

import java.awt.event.KeyEvent;
import java.awt.event.KeyAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.WindowEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentAdapter;

import javax.swing.JFrame;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;

enum State {
  MAINMENU,
  HOST,
  RUN,
  END, 
  SPLASH
}

/**
* Core class for the currently unnamed game
*/
public abstract class Core {
  
  public static final Vector2 DEFAULT_SCREEN_SIZE = new Vector2(1920, 1080);
  public static final String BLACKLISTED_CHARS = "/\\.?!*\n";
  
  private static final double TICKS_PER_SECOND = 60;
  private static final double MILLISECONDS_PER_TICK = 1000/TICKS_PER_SECOND;
  
  private static final long START_TIME = System.currentTimeMillis();
  private static final int SPLASH_TIME = 1000;
  
  public static final int DEFAULT_MAP_SIZE = 32;
  
  private static final JFrame FRAME = new JFrame("Bananas");
  private static final CorePanel PANEL = new CorePanel();
  private static boolean maximized = true;
  
  private static final Decal splash;
  
  private static boolean quit = false;
  
  private static int toolBarLeft, toolBarRight, toolBarTop, toolBarBot;
  
  private static boolean[] keyDown = new boolean[65536];
  private static boolean[] mouseDown = new boolean[4];
  private static Vector2 mousePos = new Vector2();
  private static Vector2 mousePre = new Vector2();
  private static Vector2 mouseBnd = new Vector2();
  
  private static boolean bounding = false;
  
  private static Scene current;
  
  private static Camera cam;
  private static int screenSizeX;
  private static int screenSizeY;
  private static int smallScreenX;
  private static int smallScreenY;
  
  // private long pFTime = System.currentTimeMillis();
  // private int fCount = 0;
  
  /** Current game state */
  private static State state = State.SPLASH;
  
  public static final Settings globalSettings;
  
  /**
  * Main method. Called on execution. Performs basic startup
  *
  * @param args Ignored for now
  */
  public static void main(String[] args) {
    Core.playGame();
  }

  static {
    FRAME.getContentPane().add(PANEL);
    FRAME.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
    FRAME.setResizable(true);
    BufferedImage image = IOHelp.readImage("icon.png");
    FRAME.setIconImage(image);
    FRAME.setExtendedState(JFrame.MAXIMIZED_BOTH);
    FRAME.setUndecorated(true);
    
    GraphicsDevice gd = GraphicsEnvironment.getLocalGraphicsEnvironment().getDefaultScreenDevice();
    smallScreenX = gd.getDisplayMode().getWidth()/2;
    smallScreenY = gd.getDisplayMode().getHeight()/2;
    splash = new Decal(smallScreenX, smallScreenY, "splash.png", false);
    FRAME.setBackground(new Color(173, 173, 173));
    
    FRAME.addWindowListener( new WindowAdapter() {
      @Override
      public void windowClosing(WindowEvent e) {
        quit = true;
      }
    });
    FRAME.addComponentListener( new ComponentAdapter() {
      @Override
      public void componentResized(ComponentEvent e) {
        screenSizeX = FRAME.getWidth() - toolBarLeft - toolBarRight;
        screenSizeY = FRAME.getHeight() - toolBarTop - toolBarBot;
        if (cam != null) {cam.setScreenSize(screenSizeX, screenSizeY);}
        // System.out.println(screenSizeX + ", " + screenSizeY);
      }
    });
    
    FRAME.setVisible(true);
    FRAME.requestFocus();
    globalSettings = new Settings();
    screenSizeX = FRAME.getWidth();
    screenSizeY = FRAME.getHeight();
    
    UIController.putPane("Main Menu", UICreator.createMain());
    UIController.putPane("HUD"      , UICreator.createHUD ());
  }
  
  /**
  * A helper method that updates the window insets to match their current state
  */
  private static void updateInsets() {
    Insets i = FRAME.getInsets(); //Toolkit.getDefaultToolkit().getScreenInsets(getGraphicsConfiguration())
    // System.out.println(i);
    toolBarLeft = i.left;
    toolBarRight = i.right;
    toolBarTop = i.top;
    toolBarBot = i.bottom;
  }
  
  /**
  * A helper method that toggles fullscreen for the window
  */
  public static void doFull() {
    FRAME.removeNotify();
    if (maximized) {
      FRAME.setExtendedState(JFrame.NORMAL);
      FRAME.setUndecorated(false);
      FRAME.addNotify();
      updateInsets();
      FRAME.setSize(smallScreenX + toolBarLeft + toolBarRight, smallScreenY + toolBarTop + toolBarBot);
    }
    else {
      smallScreenX = screenSizeX;
      smallScreenY = screenSizeY;
      FRAME.setVisible(false);
      FRAME.setExtendedState(JFrame.MAXIMIZED_BOTH);
      FRAME.setUndecorated(true);
      FRAME.setVisible(true);
      updateInsets();
      FRAME.addNotify();
    }
    FRAME.requestFocus();
    maximized = !maximized;
  }
  
  /**
  * Switches the current scene to the main menu
  */
  public static void toMenu() {
    Client.disconnect();
    Server.shutdown();
    current = Menu.MENU;
    cam = new Camera(new Vector2(), new Vector2(), 1, screenSizeX, screenSizeY);
    
    state = State.MAINMENU;
    UIController.setCurrent("Main Menu");
  }
  
  /**
   * Creates a new server under a given port number and connects the client to it
   * 
   * @param port the port number to host the server through
   */
  public static void hostGame(int port) {
    Client.disconnect();
    Server.shutdown();
    
    Server.startup(port);
    Client.connect("localhost", port);
    if (!Client.isConnected()) return;
    
    current = new LocalGame();
    cam = new Camera(new Vector2(), new Vector2(), 1, screenSizeX, screenSizeY);
    
    state = State.HOST;
    UIController.setCurrent("HUD");
  }
  
  /**
  * Connects the client to an existing server
  * 
  * @param ip the ip address to connect to
  * @param port the port number to connect to
  */
  public static void joinGame(String ip, int port) {
    Client.disconnect();
    Server.shutdown();
    
    Client.connect(ip, port);
    if (!Client.isConnected()) return;

    current = new LocalGame();
    cam = new Camera(new Vector2(), new Vector2(), 1, screenSizeX, screenSizeY);
    
    state = State.RUN;
    UIController.setCurrent("HUD");
  }
  
  /**
  * Sets the flag to quit the game at the nearest convenience
  */
  public static void quitToDesk() {
    quit = true;
  }
  
  /**
  * Main loop. Should always be running. Runs the rest of the game engine
  */
  private static void playGame() {
    while (true) {
      long tickTime = System.currentTimeMillis();
      
      switch (state) {
        case SPLASH:
        if (tickTime-START_TIME >= SPLASH_TIME) {
          initialiseControls();
          toMenu();
        }
        break;
        
        case MAINMENU:
        break;
        
        case HOST:
        case RUN:
        case END:
        if (mouseDown[2] || mouseDown[3]) {
          cam.setOffset(cam.getOffset().add(mousePos.subtract(mousePre)));
          mousePre = mousePos;
        }
        break;
      }
      
      if (quit) {
        Client.disconnect();
        Server.shutdown();
        System.exit(0);
      }
      PANEL.repaint();
      tickTime = System.currentTimeMillis() - tickTime;
      try {
        Thread.sleep(Math.max((long)(MILLISECONDS_PER_TICK - tickTime), 0));
      } catch(InterruptedException e){System.out.println(e); System.exit(0);}
    }
  }
  
  public static void paintComponent(Graphics gra) {
    Graphics2D g = (Graphics2D) gra.create();
    
    switch (state) {
      case SPLASH:
      splash.draw(g);
      break;
      
      case HOST:
      case RUN:
      current.draw(g, cam);
      if (mouseDown[1]) {
        if (bounding) UIController.drawBoundingBox(g, mouseBnd, mousePos);
        else {
          for (TileGrid p : current.getSelectedTiles()) {
            if (p.getTilePiece()!=null) p.getTilePiece().draw(g, mousePos, cam.getZoom(), false, false);
          }
        }
      }
      // uiCon.draw(g, screenSizeX, screenSizeY, current.getStats());
      UIController.draw(g, screenSizeX, screenSizeY);
      break;
      
      case MAINMENU:
      case END:
      current.draw(g, cam);
      UIController.draw(g, screenSizeX, screenSizeY);
      break;
    }
    
    g.dispose();
  }
  
  /**
  * Starts up all the listeners for the window. Only to be called once on startup.
  */
  private static void initialiseControls() {
    
    //Mouse Controls
    FRAME.addMouseMotionListener(new MouseAdapter() {
      @Override
      public void mouseMoved(MouseEvent e) {
        int x = e.getX() - toolBarLeft;
        int y = e.getY() - toolBarTop;
        mousePos = new Vector2(x, y);
        UIController.cursorMove(x, y);
      }
      
      @Override
      public void mouseDragged(MouseEvent e) {
        int x = e.getX() - toolBarLeft;
        int y = e.getY() - toolBarTop;
        mousePos = new Vector2(x, y);
        UIController.cursorMove(x, y);
        UIController.useSlider(x);
      }
    });
    FRAME.addMouseListener(new MouseAdapter() {
      @Override
      public void mousePressed(MouseEvent e) {
        int x = e.getX() - toolBarLeft;
        int y = e.getY() - toolBarTop;
        mousePos = new Vector2(x, y);
        
        if (UIController.getHighlighted() == null) mouseDown[e.getButton()] = true;
        mousePre = mousePos;
        
        //left click
        if (e.getButton() == 1) {
          UIController.cursorMove(x, y);
          if (UIController.press()) return;
          Vector2I ind = current.convertToIndex(mousePos, cam);
          current.pressTile(ind);
          if (!current.isSelected(ind)) {
            mouseBnd = mousePos;
            bounding = true;
            return;
          }
          bounding = false;
          return;
        }
        
        //right click
        if (e.getButton() == 3) {
          current.pressTile(current.convertToIndex(mousePos, cam));
          return;
        }
      }
      
      @Override
      public void mouseReleased(MouseEvent e) {
        int x = e.getX() - toolBarLeft;
        int y = e.getY() - toolBarTop;
        mouseDown[e.getButton()] = false;
        
        //left click
        if (e.getButton() == 1) {
          Vector2I ind = current.convertToIndex(mousePos, cam);
          if (!current.selectTile(ind) && bounding) current.selectTiles(current.convertToIndex(mouseBnd, cam), ind);
          bounding = false;
          UIController.cursorMove(x, y);
          UIController.release();
          return;
        }
        
        //right click
        if (e.getButton() == 3) {
          // current.removeTile(current.convertToIndex(mousePos, cam));
          current.deselectTiles();
          current.unsetIn();
          return;
        }
      }
    });
    
    FRAME.addMouseWheelListener(new MouseAdapter() {
      public void mouseWheelMoved(MouseWheelEvent e) {
        double z = e.getWheelRotation()<0 ? cam.getZoom()*1.1 : cam.getZoom()/1.1;
        cam.setZoom(z, mousePos.subtract(screenSizeX*0.5, screenSizeY*0.5));
      }
    });
    
    //Keyboard Controls
    FRAME.addKeyListener(new KeyAdapter() {
      @Override
      public void keyPressed(KeyEvent e) {
        int keyCode = e.getKeyCode();
        
        if (UIController.getActiveTextfield() != null && !keyDown[KeyEvent.VK_CONTROL]) UIController.typeKey(e);
        
        if(keyDown[keyCode]) return; //Key already in
        keyDown[keyCode] = true;
        
        // System.out.print(keyCode);
        if (keyCode == KeyEvent.VK_F11) {
          doFull();
          return;
        }
        if (keyCode == KeyEvent.VK_ESCAPE) {
          UIController.back(); //TODO check for changes
          return;
        }
        if (keyCode == KeyEvent.VK_ENTER) {
          UIController.press();
          return;
        }
        if (keyCode == KeyEvent.VK_MINUS) {
          cam.setZoom(cam.getZoom()/1.1);
          return;
        }
        if (keyCode == KeyEvent.VK_EQUALS) {
          cam.setZoom(cam.getZoom()*1.1);
          return;
        }
        // if (keyCode >= 65 && keyCode <= 90) {
          //   TileGrid t = current.hasSelectedTiles() ? current.getSelectedTiles().get(0) : null;
          //   if (t != null) t.place(new TilePiece((char)keyCode, false));
          //   return;
          // }
        }
        
        @Override
        public void keyReleased(KeyEvent e){
          int keyCode = e.getKeyCode();
          keyDown[keyCode] = false;
          
          if (keyCode == KeyEvent.VK_ENTER) {
            UIController.release();
          }
        }
      });
    }
  }
  