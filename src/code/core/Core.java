package code.core;

import code.core.scene.Scene;
import code.core.scene.elements.Camera;
import code.core.scene.elements.Decal;
import code.core.scene.elements.TilePiece;
import code.error.ConnectionException;

import code.math.IOHelp;
import code.math.Vector2;

import code.server.Server;

import code.ui.UIController;

import java.util.Map.Entry;

import java.awt.image.BufferedImage;
import java.awt.Insets;
import java.awt.GraphicsDevice;
import java.awt.GraphicsEnvironment;

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
  
  public static final Settings GLOBAL_SETTINGS;
  
  public static final String BLACKLISTED_CHARS = "/\\.?!*\n";
  
  public static final int DEFAULT_MAP_SIZE = 32;
  
  private static final double TICKS_PER_SECOND = 60;
  private static final double MILLISECONDS_PER_TICK = 1000/TICKS_PER_SECOND;
  
  private static final long START_TIME = System.currentTimeMillis();
  private static final int SPLASH_TIME = 1000;
  
  private static final JFrame FRAME = new JFrame("Bananas");
  private static final CorePanel PANEL = new CorePanel();
  
  private static final Decal SPLASH;
  
  private static boolean quit = false;
  
  private static Scene currentScene;
  
  private static Camera cam;
  private static int screenSizeX;
  private static int screenSizeY;
  private static int smallScreenX;
  private static int smallScreenY;
  
  static int toolBarLeft, toolBarRight, toolBarTop, toolBarBot;
  
  /** Current game state */
  private static State state = State.SPLASH;
  
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
    
    GraphicsDevice gd = GraphicsEnvironment.getLocalGraphicsEnvironment().getDefaultScreenDevice();
    smallScreenX = gd.getDisplayMode().getWidth()/2;
    smallScreenY = gd.getDisplayMode().getHeight()/2;
    
    screenSizeX = smallScreenX;
    screenSizeY = smallScreenY;
    
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
        if (!isFullScreen()) {
          smallScreenX = screenSizeX;
          smallScreenY = screenSizeY;
        }
        // System.out.println(screenSizeX + ", " + screenSizeY);
      }
    });
    
    GLOBAL_SETTINGS = new Settings();
    
    SPLASH = new Decal(screenSizeX/2, screenSizeY/2, "splash.png", false);
    FRAME.setBackground(new Color(173, 173, 173));
    
    UIController.putPane("Main Menu", UICreator.createMain());
    UIController.putPane("HUD"      , UICreator.createHUD ());
  }
  
  /**
  * @return the currently active scene
  */
  public static Scene getCurrentScene() {
    return currentScene;
  }
  
  /**
  * @return the currently active camera
  */
  public static Camera getCam() {
    return cam;
  }

  /**
   * @return the current width of the screen
   */
  public static int screenWidth() {
    return screenSizeX;
  }

  /**
   * @return the current height of the screen
   */
  public static int screenHeight() {
    return screenSizeY;
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
  * A helper method that toggles the fullscreen state for the window
  */
  public static void toggleFullscreen() {
    setFullscreen(!isFullScreen());
  }
  
  /**
  * A helper method to check whether or not the window is maximized
  * 
  * @return true if the window is maximized
  */
  public static boolean isFullScreen() {
    return FRAME.getExtendedState() == JFrame.MAXIMIZED_BOTH && FRAME.isUndecorated();
  }
  
  /**
  * A helper method that sets the fullscreen state for the window
  * 
  * @param maximize whether or not the screen should me maximized
  */
  public static void setFullscreen(boolean maximize) {
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
  
  /**
  * Switches the current scene to the main menu
  */
  public static void toMenu() {
    Client.disconnect();
    Server.shutdown();
    currentScene = Scene.mainMenu();
    cam = new Camera(new Vector2(), new Vector2(), 1, screenSizeX, screenSizeY);
    
    state = State.MAINMENU;
    UIController.setCurrent("Main Menu");
  }
  
  /**
  * Creates a new server under a given port number and connects the client to it
  * 
  * @param port the port number to host the server through
  * 
  * @return true if the connection was successfully established
  */
  public static void hostGame(int port) throws ConnectionException {
    Client.disconnect();
    Server.shutdown();
    
    Server.startup(port);
    Client.connect("localhost", port);
    if (Server.isRunning() && Client.isConnected()) return;
    
    Client.disconnect();
    Server.shutdown();
    throw new ConnectionException();
  }
  
  /**
  * Connects the client to an existing server
  * 
  * @param ip the ip address to connect to
  * @param port the port number to connect to
  * 
  * @return true if the connection was successfully established
  */
  public static void joinGame(String ip, int port) throws ConnectionException {
    Client.disconnect();
    Server.shutdown();
    
    Client.connect(ip, port);
    if (Client.isConnected()) return;
    
    Client.disconnect();
    Server.shutdown();
    throw new ConnectionException();
  }
  
  /**
  * Begins a new match
  */
  public static void beginMatch() {
    currentScene = Scene.localGame();
    cam = new Camera(new Vector2(), new Vector2(), 1, screenSizeX, screenSizeY);
    
    state = Server.isRunning() ? State.HOST : State.RUN;
    UIController.setCurrent("HUD");
  }
  
  /**
  * Sets a flag to close the program at the nearest convenience
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
          Controls.initialiseControls(FRAME);
          toMenu();
        }
        break;
        
        case MAINMENU:
        break;
        
        case HOST:
        case RUN:
        Controls.leftMouseAction();
        case END:
        Controls.cameraMovement();
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
  
  /**
  * Paints the contents of the program to the given {@code Graphics} object.
  * 
  * @param gra the supplied {@code Graphics} object
  */
  public static void paintComponent(Graphics gra) {
    Graphics2D g = (Graphics2D) gra.create();
    
    switch (state) {
      case SPLASH:
      SPLASH.draw(g);
      break;
      
      case HOST:
      case RUN:
      currentScene.draw(g, cam);
      if (Controls.MOUSE_DOWN[1]) {
        if (Controls.boundingBox) UIController.drawBoundingBox(g, Controls.mouseBnd, Controls.mousePos);
        else if (Controls.boundTiles != null) {
          for (Entry<TilePiece, Vector2> pair : Controls.selectedTileScreenCoordinates.entrySet()) {
            pair.getKey().draw(g, pair.getValue(), cam.getZoom(), false, false);
          }
        }
      }
      UIController.draw(g, screenSizeX, screenSizeY);
      break;
      
      case MAINMENU:
      case END:
      currentScene.draw(g, cam);
      UIController.draw(g, screenSizeX, screenSizeY);
      break;
    }
    
    g.dispose();
  }
}