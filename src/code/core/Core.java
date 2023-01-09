package code.core;

import code.core.scene.Scene;
import code.core.scene.elements.Camera;
import code.core.scene.elements.Decal;
import code.core.scene.elements.TileGrid;
import code.core.scene.elements.TilePiece;
import code.error.ConnectionException;

import java.awt.event.MouseWheelEvent;

import code.math.IOHelp;
import code.math.Vector2;
import code.math.Vector2I;

import code.server.Server;

import code.ui.UIController;
import code.ui.UIState;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
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
  
  public static final double EDGE_SCROLL_BOUNDS = 0.05;
  
  private static final double TICKS_PER_SECOND = 60;
  private static final double MILLISECONDS_PER_TICK = 1000/TICKS_PER_SECOND;
  
  private static final long START_TIME = System.currentTimeMillis();
  private static final int SPLASH_TIME = 1000;
  
  public static final int DEFAULT_MAP_SIZE = 32;
  
  private static final JFrame FRAME = new JFrame("Bananas");
  private static final CorePanel PANEL = new CorePanel();
  
  private static final Decal splash;
  
  private static boolean quit = false;
  
  private static int toolBarLeft, toolBarRight, toolBarTop, toolBarBot;
  
  private static boolean[] keyDown = new boolean[65536];
  private static boolean[] mouseDown = new boolean[4];
  private static Vector2I mousePos = new Vector2I();
  private static Vector2I mousePre = new Vector2I();
  private static Vector2I mouseBnd = new Vector2I();
  
  private static boolean boundingBox = false;
  private static Vector2I boundIndex = new Vector2I();
  private static TileGrid[][] boundTiles = null;

  private static final Map<TilePiece, Vector2> selectedTileScreenCoordinates = new HashMap<TilePiece, Vector2>();
  
  private static Scene currentScene;
  
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

    globalSettings = new Settings();
    
    splash = new Decal(screenSizeX/2, screenSizeY/2, "splash.png", false);
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
    setFullscreen(FRAME.getExtendedState() != JFrame.MAXIMIZED_BOTH);
  }

  /**
   * A helper method to check whether or not the window is maximized
   * 
   * @return true if the window is maximized
   */
  public static boolean isFullScreen() {
    return FRAME.getExtendedState() == JFrame.MAXIMIZED_BOTH;
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

    screenSizeX = FRAME.getWidth() - toolBarLeft - toolBarRight;
    screenSizeY = FRAME.getHeight() - toolBarTop - toolBarBot;
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
          initialiseControls();
          toMenu();
        }
        break;
        
        case MAINMENU:
        break;
        
        case HOST:
        case RUN:
        leftMouseAction();
        case END:
        cameraMovement();
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
      splash.draw(g);
      break;
      
      case HOST:
      case RUN:
      currentScene.draw(g, cam);
      if (mouseDown[1]) {
        if (boundingBox) UIController.drawBoundingBox(g, mouseBnd, mousePos);
        else if (boundTiles != null) {
          for (Entry<TilePiece, Vector2> pair : selectedTileScreenCoordinates.entrySet()) {
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
  
  /**
  * Starts up all the listeners for the window. Only to be called once on startup.
  */
  private static void initialiseControls() {
    
    //Mouse Controls
    FRAME.addMouseMotionListener(new MouseAdapter() {
      @Override
      public void mouseMoved(MouseEvent e) {
        updateMousePos(e);
      }
      
      @Override
      public void mouseDragged(MouseEvent e) {
        updateMousePos(e);
      }
    });
    FRAME.addMouseListener(new MouseAdapter() {
      @Override
      public void mousePressed(MouseEvent e) {
        updateMousePos(e);
        
        if (UIController.getHighlighted() == null) mouseDown[e.getButton()] = true;
        mousePre = mousePos;
        
        //left click
        if (e.getButton() == 1) {
          if (UIController.press()) return;
          
          Vector2I ind = currentScene.convertToIndex(mousePos, cam);
          TileGrid pressed = currentScene.pressTile(ind);
          if (pressed != null && pressed.isPlaced()) {
            if (!pressed.isSelected() && currentScene.hasSelectedTiles()) currentScene.deselectTiles();
            currentScene.selectTile(ind);
          }
          boundingBox = (!currentScene.isSelected(ind) || keyDown[KeyEvent.VK_SHIFT]);
          mouseBnd = mousePos;
          boundIndex = ind;
          return;
        }
        
        //right click
        if (e.getButton() == 3) {
          currentScene.pressTile(currentScene.convertToIndex(mousePos, cam));
          return;
        }
      }
      
      @Override
      public void mouseReleased(MouseEvent e) {
        updateMousePos(e);
        
        mouseDown[e.getButton()] = false;
          
        currentScene.unsetIn();
        
        //left click
        if (e.getButton() == 1) {
          UIController.release();
          
          Vector2I ind = currentScene.convertToIndex(mousePos, cam);
          if (boundingBox) currentScene.selectTiles(currentScene.convertToIndex(mouseBnd, cam), ind);

          if (boundTiles != null) {
            currentScene.doMove(boundTiles, ind.subtract(boundIndex));
          }
          
          boundingBox = false;
          boundTiles = null;
          return;
        }
        
        //right click
        if (e.getButton() == 3) {
          currentScene.deselectTiles();
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
          toggleFullscreen();
          return;
        }
        if (keyCode == KeyEvent.VK_ESCAPE) {
          UIController.release();
          UIController.back();
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
    
    /**
     * Updates the program's understanding of the location of the mouse cursor after a supplied {@code MouseEvent}.
     * 
     * @param e the {@code MouseEvent} to determine the cursor's current position from
     */
    private static void updateMousePos(MouseEvent e) {
      int x = e.getX() - toolBarLeft;
      int y = e.getY() - toolBarTop;
      mousePos = new Vector2I(x, y);
      
      UIController.cursorMove(mousePos);
    }

    /**
     * Performs dragging of tiles around the board
     */
    private static void leftMouseAction() {
      if (!mouseDown[1]) return;

      currentScene.pressTile(currentScene.convertToIndex(mousePos, cam));

      if (boundingBox) return;
      
      if (boundTiles == null) {
        if (currentScene.convertToIndex(mousePos, cam).equals(boundIndex)) return;
        boundTiles = calculateOffsetGrid();
        selectedTileScreenCoordinates.clear();
      }

      for (TileGrid t : currentScene.getSelectedTiles()) {
        selectedTileScreenCoordinates.put(t.getTilePiece(), mousePos.add((t.x-boundIndex.x)*TileGrid.TILE_SIZE*cam.getZoom(), (t.y-boundIndex.y)*TileGrid.TILE_SIZE*cam.getZoom()));
      }
    }

    private static TileGrid[][] calculateOffsetGrid() {
      int minX = Integer.MAX_VALUE, minY = Integer.MAX_VALUE, maxX = Integer.MIN_VALUE, maxY = Integer.MIN_VALUE;
      List<TileGrid> selectedTiles = currentScene.getSelectedTiles();
      for (TileGrid t : selectedTiles) {
        minX = Math.min(minX, t.x); maxX = Math.max(maxX, t.x);
        minY = Math.min(minY, t.y); maxY = Math.max(maxY, t.y);
      }
      TileGrid[][] res = new TileGrid[maxX+1-minX][maxY+1-minY];
      for (TileGrid t : selectedTiles) {
        res[t.x-minX][t.y-minY] = t;
      }
      return res;
    }
  
    /**
     * moves the camera around the scene
     */
    private static void cameraMovement() {
      if (!UIController.isMode(UIState.DEFAULT)) return;
      if (mouseDown[2] || mouseDown[3]) {
        cam.addOffset(mousePos.subtract(mousePre));
        mousePre = mousePos;
        return;
      }
      if (mousePos.x < EDGE_SCROLL_BOUNDS*screenSizeX              ) cam.addOffset(new Vector2(20*cam.getZoom(), 0) );
      if (mousePos.y < EDGE_SCROLL_BOUNDS*screenSizeY              ) cam.addOffset(new Vector2(0, 20*cam.getZoom()) );
      if (mousePos.x > screenSizeX - EDGE_SCROLL_BOUNDS*screenSizeX) cam.addOffset(new Vector2(-20*cam.getZoom(), 0));
      if (mousePos.y > screenSizeY - EDGE_SCROLL_BOUNDS*screenSizeY) cam.addOffset(new Vector2(0, -20*cam.getZoom()));
    }
  }
  