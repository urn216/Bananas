package code.core;

import java.awt.MouseInfo;

import java.awt.event.KeyAdapter;
import java.awt.event.MouseAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.awt.event.MouseWheelEvent;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.swing.JFrame;

import code.core.scene.elements.Camera;
import code.core.scene.elements.TileGrid;
import code.core.scene.elements.TilePiece;

import code.math.Vector2;
import code.math.Vector2I;

import code.ui.UIController;
import code.ui.UIState;

/**
 * Handles all user input within the game
 */
abstract class Controls {
  
  public static final double EDGE_SCROLL_BOUNDS = 0.02;

  private static double scrollSens = 10;
  
  public static final boolean[] KEY_DOWN = new boolean[65536];
  public static final boolean[] MOUSE_DOWN = new boolean[Math.max(MouseInfo.getNumberOfButtons(), 3)];
  
  public static Vector2I mousePos = new Vector2I();
  public static Vector2I mousePre = new Vector2I();
  public static Vector2I mouseBnd = new Vector2I();
  
  public static boolean boundingBox = false;
  public static Vector2I boundIndex = new Vector2I();
  public static TileGrid[][] boundTiles = null;
  
  public static final Map<TilePiece, Vector2> selectedTileScreenCoordinates = new HashMap<TilePiece, Vector2>();
  
  /**
  * Starts up all the listeners for the window. Only to be called once on startup.
  */
  public static void initialiseControls(JFrame FRAME) {
    scrollSens = Core.GLOBAL_SETTINGS.getIntSetting("scrollSensitivity");
    
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
        
        if (UIController.getHighlightedInteractable() == null) MOUSE_DOWN[e.getButton()] = true;
        mousePre = mousePos;
        
        //left click
        if (e.getButton() == 1) {
          if (UIController.press()) return;
          
          Vector2I ind = Core.getCurrentScene().convertToIndex(mousePos);
          TileGrid pressed = Core.getCurrentScene().pressTile(ind);
          if (pressed != null && pressed.isPlaced()) {
            if (!pressed.isSelected() && Core.getCurrentScene().hasSelectedTiles()) Core.getCurrentScene().deselectTiles();
            Core.getCurrentScene().selectTile(ind);
          }
          boundingBox = (!Core.getCurrentScene().isSelected(ind) || KEY_DOWN[KeyEvent.VK_SHIFT]);
          mouseBnd = mousePos;
          boundIndex = ind;
          return;
        }
        
        //right click
        if (e.getButton() == 3) {
          Core.getCurrentScene().pressTile(Core.getCurrentScene().convertToIndex(mousePos));
          return;
        }
      }
      
      @Override
      public void mouseReleased(MouseEvent e) {
        updateMousePos(e);
        
        MOUSE_DOWN[e.getButton()] = false;
        
        Core.getCurrentScene().unsetIn();
        
        //left click
        if (e.getButton() == 1) {
          UIController.release();
          
          Vector2I ind = Core.getCurrentScene().convertToIndex(mousePos);
          if (boundingBox) Core.getCurrentScene().selectTiles(Core.getCurrentScene().convertToIndex(mouseBnd), ind);
          
          if (boundTiles != null) {
            Core.getCurrentScene().doMove(boundTiles, ind.subtract(boundIndex));
          }
          
          boundingBox = false;
          boundTiles = null;
          return;
        }
        
        //right click
        if (e.getButton() == 3) {
          Core.getCurrentScene().deselectTiles();
          return;
        }
      }
      
      @Override
      public void mouseExited(MouseEvent e) {
        mousePos = new Vector2I(Core.WINDOW.screenWidth()/2, Core.WINDOW.screenHeight()/2);
      }
    });
    
    FRAME.addMouseWheelListener(new MouseAdapter() {
      public void mouseWheelMoved(MouseWheelEvent e) {

        Camera cam = Core.getActiveCam();

        if (KEY_DOWN[KeyEvent.VK_CONTROL] || KEY_DOWN[KeyEvent.VK_META]) {
          cam.setZoom(
          e.getWheelRotation()<0 ? cam.getZoom()*((scrollSens*0.02)+1) : cam.getZoom()/((scrollSens*0.02)+1), 
          mousePos.subtract(Core.WINDOW.screenWidth()*0.5, Core.WINDOW.screenHeight()*0.5)
          );
          return;
        }
        
        if(e.isShiftDown()) {
          cam.addOffset(
          new Vector2(-e.getPreciseWheelRotation()*scrollSens*20, 0)
          );
          return;
        }
        
        cam.addOffset(
        new Vector2(0, -e.getPreciseWheelRotation()*scrollSens*20)
        );
      }
    });
    
    //Keyboard Controls
    FRAME.addKeyListener(new KeyAdapter() {
      @Override
      public void keyPressed(KeyEvent e) {
        int keyCode = e.getKeyCode();
        
        if (UIController.getActiveTextfield() != null && !KEY_DOWN[KeyEvent.VK_CONTROL]) UIController.typeKey(e);
        
        if(KEY_DOWN[keyCode]) return; //Key already in
        KEY_DOWN[keyCode] = true;
        
        // System.out.print(keyCode);
        if (keyCode == KeyEvent.VK_F11) {
          Core.WINDOW.toggleFullscreen();
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
          Core.getActiveCam().setZoom(Core.getActiveCam().getZoom()/1.1);
          return;
        }
        if (keyCode == KeyEvent.VK_EQUALS) {
          Core.getActiveCam().setZoom(Core.getActiveCam().getZoom()*1.1);
          return;
        }
      }
      
      @Override
      public void keyReleased(KeyEvent e){
        int keyCode = e.getKeyCode();
        KEY_DOWN[keyCode] = false;
        
        if (keyCode == KeyEvent.VK_ENTER) {
          UIController.release();
        }
      }
    });
  }

  public static void updateScrollSensitivity(int v) {
    scrollSens = v;
  }
  
  /**
  * Updates the program's understanding of the location of the mouse cursor after a supplied {@code MouseEvent}.
  * 
  * @param e the {@code MouseEvent} to determine the cursor's current position from
  */
  public static void updateMousePos(MouseEvent e) {
    int x = e.getX() - Core.WINDOW.toolBarLeft;
    int y = e.getY() - Core.WINDOW.toolBarTop;
    mousePos = new Vector2I(x, y);
    
    UIController.cursorMove(mousePos);
  }
  
  /**
  * Performs dragging of tiles around the board
  */
  public static void leftMouseAction() {
    if (!MOUSE_DOWN[1] || KEY_DOWN[KeyEvent.VK_META]) return;
    
    Core.getCurrentScene().pressTile(Core.getCurrentScene().convertToIndex(mousePos));
    
    if (boundingBox) return;
    
    if (boundTiles == null) {
      if (Core.getCurrentScene().convertToIndex(mousePos).equals(boundIndex)) return;
      boundTiles = calculateOffsetGrid();
      selectedTileScreenCoordinates.clear();
    }

    Camera cam = Core.getActiveCam();
    
    for (TileGrid t : Core.getCurrentScene().getSelectedTiles()) {
      selectedTileScreenCoordinates.put(
      t.getTilePiece(),
      mousePos.add(
      (t.x-boundIndex.x)*TileGrid.TILE_SIZE*cam.getZoom(),
      (t.y-boundIndex.y)*TileGrid.TILE_SIZE*cam.getZoom()
      )
      );
    }
  }
  
  /**
  * moves the camera around the scene
  */
  public static void cameraMovement() {
    if (!UIController.isState(UIState.DEFAULT)) return;

    Camera cam = Core.getActiveCam();

    if (MOUSE_DOWN[2] || MOUSE_DOWN[3] || (MOUSE_DOWN[1] && KEY_DOWN[KeyEvent.VK_META])) {
      cam.addOffset(mousePos.subtract(mousePre));
      mousePre = mousePos;
      return;
    }

    //Left
    if (
    KEY_DOWN[KeyEvent.VK_LEFT] || 
    KEY_DOWN[KeyEvent.VK_A   ] || 
    mousePos.x < EDGE_SCROLL_BOUNDS*Core.WINDOW.screenWidth()
    ) cam.addOffset(new Vector2(10+15*cam.getZoom(), 0) );
    //Up
    if (
    KEY_DOWN[KeyEvent.VK_UP] || 
    KEY_DOWN[KeyEvent.VK_W ] || 
    mousePos.y < EDGE_SCROLL_BOUNDS*Core.WINDOW.screenHeight()
    ) cam.addOffset(new Vector2(0, 10+15*cam.getZoom()) );
    //Right
    if (
    KEY_DOWN[KeyEvent.VK_RIGHT] || 
    KEY_DOWN[KeyEvent.VK_D    ] || 
    mousePos.x > Core.WINDOW.screenWidth() - EDGE_SCROLL_BOUNDS*Core.WINDOW.screenWidth()
    ) cam.addOffset(new Vector2(-10-15*cam.getZoom(), 0));
    //Down
    if (
    KEY_DOWN[KeyEvent.VK_DOWN] || 
    KEY_DOWN[KeyEvent.VK_S   ] || 
    mousePos.y > Core.WINDOW.screenHeight() - EDGE_SCROLL_BOUNDS*Core.WINDOW.screenHeight()
    ) cam.addOffset(new Vector2(0, -10-15*cam.getZoom()));
  }
  
  /**
   * Finds the subset of the current board which is currently selected,
   * and returns a new 2D array of {@code TileGrid}s which are actively selected.
   * 
   * @return a 2D array of {@code TileGrid}s
   */
  private static TileGrid[][] calculateOffsetGrid() {
    int minX = Integer.MAX_VALUE, minY = Integer.MAX_VALUE, maxX = Integer.MIN_VALUE, maxY = Integer.MIN_VALUE;
    List<TileGrid> selectedTiles = Core.getCurrentScene().getSelectedTiles();
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
}
