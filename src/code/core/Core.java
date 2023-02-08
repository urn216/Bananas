package code.core;

import code.core.scene.Scene;
import code.core.scene.elements.Camera;
import code.core.scene.elements.Decal;
import code.core.scene.elements.TilePiece;
import code.error.ConnectionException;

import code.math.Vector2;

import code.server.Server;

import code.ui.UIController;

import java.util.Map.Entry;

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

  public static final Window WINDOW;
  
  public static final Settings GLOBAL_SETTINGS;
  
  public static final String BLACKLISTED_CHARS = "/\\.?!*\n";
  
  public static final int DEFAULT_MAP_SIZE = 32;
  
  private static final double TICKS_PER_SECOND = 60;
  private static final double MILLISECONDS_PER_TICK = 1000/TICKS_PER_SECOND;
  
  private static final long START_TIME = System.currentTimeMillis();
  private static final int SPLASH_TIME = 1000;
  
  private static final Decal SPLASH;
  
  private static boolean quit = false;
  
  private static Scene currentScene;
  
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
    WINDOW = new Window();

    GLOBAL_SETTINGS = new Settings();

    SPLASH = new Decal(WINDOW.screenWidth()/2, WINDOW.screenHeight()/2, "splash.png", false);
    WINDOW.FRAME.setBackground(new Color(173, 173, 173));
    
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
  public static Camera getActiveCam() {
    return currentScene == null ? null : currentScene.getCam();
  }
  
  /**
  * Switches the current scene to the main menu
  */
  public static void toMenu() {
    Client.disconnect();
    Server.shutdown();
    currentScene = Scene.mainMenu();
    
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
    
    int res = Client.connect(ip, port);
    if (Client.isConnected()) return;
    
    Client.disconnect();
    Server.shutdown();
    if (res < 0) throw new ConnectionException();
  }
  
  /**
  * Begins a new match
  */
  public static void beginMatch() {
    currentScene = Scene.localGame();
    
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
          Controls.initialiseControls(WINDOW.FRAME);
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
      WINDOW.PANEL.repaint();
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
    Graphics2D g = (Graphics2D)gra;
    
    switch (state) {
      case SPLASH:
      SPLASH.draw(g);
      break;
      
      case HOST:
      case RUN:
      currentScene.draw(g);
      if (Controls.MOUSE_DOWN[1]) {
        if (Controls.boundingBox) UIController.drawBoundingBox(g, Controls.mouseBnd, Controls.mousePos);
        else if (Controls.boundTiles != null) {
          for (Entry<TilePiece, Vector2> pair : Controls.selectedTileScreenCoordinates.entrySet()) {
            pair.getKey().draw(g, pair.getValue(), currentScene.getCam().getZoom(), false, false);
          }
        }
      }
      UIController.draw(g, WINDOW.screenWidth(), WINDOW.screenHeight());
      break;
      
      case MAINMENU:
      case END:
      currentScene.draw(g);
      UIController.draw(g, WINDOW.screenWidth(), WINDOW.screenHeight());
      break;
    }
  }
}