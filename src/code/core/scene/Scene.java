package code.core.scene;

import code.core.scene.elements.Camera;
import code.core.scene.elements.Decal;
import code.core.scene.elements.TileGrid;
import code.core.scene.elements.TilePiece;
// import code.math.IOHelp;
// import code.math.MathHelp;
import code.math.Vector2;
import code.math.Vector2I;

// import java.util.*;
import java.awt.Graphics2D;
import java.util.ArrayList;
import java.util.List;

/**
* Scene class
*/
public abstract class Scene
{
  protected final int mapSX;
  protected final int mapSY;
  protected final TileGrid[][] map;
  protected final TileGrid[][] pile;
  
  protected final Decal bg;
  
  protected final List<TileGrid> selectedTiles = new ArrayList<TileGrid>();
  
  /**
   * @return the main menu scene singleton
   */
  public static final Menu mainMenu() {return Menu.MENU;}

  /**
   * @return a new game scene, with a clear board of standard size for the connected server to populate
   */
  public static final Scene localGame() {return new LocalGame();}

  protected Scene(int mapSX, int mapSY, TileGrid[][] map, TileGrid[][] pile, Decal bg) {
    this.mapSX = mapSX;
    this.mapSY = mapSY;
    this.map = map;
    this.pile = pile;
    this.bg = bg;
  }
  
  /**
   * Resets the scene to a fresh, unaltered state
   */
  public abstract void reset();
  
  /**
   * @return the width of the player's board
   */
  public int getMapSX() {return mapSX;}
  
  /**
   * @return the height of the player's board
   */
  public int getMapSY() {return mapSY;}
  
  /**
   * Gets a tile at a given index within the scene.
   * 
   * @param p the index to get the tile from
   * @return the {@code TileGrid} at the given index
   */
  private TileGrid getTile(Vector2I p) {return p.y >= mapSY ? map[p.x][p.y-mapSY] : pile[p.x][p.y];}
  
  /**
   * @return an unmodifiable list containing the currently selected tiles
   */
  public List<TileGrid> getSelectedTiles() {return List.copyOf(selectedTiles);}
  
  /**
   * @return true if there are tiles in the current selection
   */
  public boolean hasSelectedTiles() {return !selectedTiles.isEmpty();}
  
  // public Vector2 createPoint(int x, int y) {return new Vector2((x-mapSX/2)*TileGrid.TILE_SIZE, (y-mapSY/2)*TileGrid.TILE_SIZE);}
  
  /**
   * assesses whether or not a given index is a valid location within the scene
   * 
   * @param p the index to check
   * @return true if the index is within the bounds of either the central pile or the player's personal board
   */
  private boolean validate(Vector2I p) {
    if (p.x < 0 || p.x >= mapSX
    ||  p.y < 0 || p.y >= mapSY*2) return false;
    return true;
  }
  
  /**
   * Checks to see if the tile at a given index is selected.
   * 
   * @param p the index to check
   * @return true if the inspected tile is selected
   */
  public boolean isSelected(Vector2I p) {
    if (validate(p)) {
      TileGrid t = getTile(p);
      if (t.isPlaced() && selectedTiles.contains(t)) {
        return true;
      }
    }
    return false;
  }
  
  /**
   * Presses a tile in.
   * 
   * @param p the index to press
   */
  public void pressTile(Vector2I p) {
    if (!validate(p)) return;
    TileGrid t = getTile(p);
    t.setIn();
  }
  
  /**
   * Selects a single tile at a given index.
   * 
   * @param p the index to select
   * 
   * @return true if a {@code TilePiece} was present at the selected location
   */
  public boolean selectTile(Vector2I p) {
    deselectTiles();
    if (validate(p) && getTile(p).isIn()) selectedTiles.add(getTile(p));
    unsetIn();
    return hasSelectedTiles();
  }
  
  /**
   * Selects all the tiles between two indices. Ordering not important.
   * 
   * @param a the first index
   * @param b the second index
   */
  public void selectTiles(Vector2I a, Vector2I b) {
    deselectTiles();
    Vector2I tL = new Vector2I(Math.min(a.x, b.x), Math.min(a.y, b.y));
    Vector2I bR = new Vector2I(Math.max(a.x, b.x), Math.max(a.y, b.y));
    for (int y = Math.max(0, tL.y); y <= bR.y && y < mapSY*2; y++) {
      for (int x = Math.max(0, tL.x); x <= bR.x && x < mapSX; x++) {
        TileGrid t = getTile(new Vector2I(x, y));
        if (t.isPlaced()) selectedTiles.add(t);
      }
    }
    unsetIn();
  }
  
  /**
   * Deselects all the tiles in the current selection.
   */
  public void deselectTiles() {
    selectedTiles.clear();
  }
  
  /**
   * Places a {@code TilePiece} into the scene at a given index in a board, 
   * with a choice between placing into the pile or the player's personal board.
   * 
   * @param p The index within the chosen board to place the piece
   * @param piece The piece to place
   * @param pile <ul> 
   *        <li>True - to place into the central pile </li>
   *        <li>False - to place into the player's personal board </li>
   */
  public void placeTile(Vector2I p, TilePiece piece, boolean pile) {
    placeTile(pile ? p : p.add(0, mapSY), piece);
  }
  
  /**
   * Places a {@code TilePiece} into the scene at a given index.
   * 
   * @param p The index within the scene to place the piece
   * @param piece The piece to place
   */
  public void placeTile(Vector2I p, TilePiece piece) {
    if (validate(p)) getTile(p).place(piece);
  }
  
  // public void removeTile(Vector2I p) {
  //   if (validate(p)) getTile(p).unPlace();
  // }
  
  /**
   * Resets all the tiles in the scene to their default 'out' state.
   */
  public void unsetIn() {
    for (int i = 0; i < mapSX; i++) {
      for (int j = 0; j < mapSY; j++) {
        map[i][j].unsetIn();
        pile[i][j].unsetIn();
      }
    }
  }
  
  /**
   * Converts a pixel on the screen to an index within this scene,
   * to be used to access a tile in either this player's board or the central pile.
   * 
   * @param pos the position on the screen to convert
   * @param cam the {@code Camera} to translate the position to an index through
   * 
   * @return a usable index within the scene
   */
  public Vector2I convertToIndex(Vector2 pos, Camera cam) {
    Vector2I res = new Vector2I ((int)(((pos.x+cam.conX())/(cam.getZoom()*TileGrid.TILE_SIZE))+mapSX/2), (int)(((pos.y+cam.conY())/(cam.getZoom()*TileGrid.TILE_SIZE))+mapSX/2));
    if (res.y >= mapSY) res = res.subtract(0, 1);
    return res;
  }
  
  /**
   * Draws the scene and all its contents to a {@code Graphics2D} object.
   * 
   * @param g the {@code Graphics2D} object to draw to
   * @param cam the {@code Camera} to view the scene through
   */
  public void draw(Graphics2D g, Camera cam) {
    bg.draw(g);
    
    for (int i = 0; i < mapSX; i++) {
      for (int j = 0; j < mapSY; j++) {
        TileGrid t = pile[i][j];
        if (TileGrid.onScreen(cam, i-mapSX/2, j-mapSY/2)) t.draw(g, cam, i-mapSX/2, j-mapSY/2, selectedTiles.contains(t));
      }
    }
    
    for (int i = 0; i < mapSX; i++) {
      for (int j = 0; j < mapSY; j++) {
        TileGrid t = map[i][j];
        if (TileGrid.onScreen(cam, i-mapSX/2, j+1+mapSY/2)) t.draw(g, cam, i-mapSX/2, j+1+mapSY/2, selectedTiles.contains(t));
      }
    }
  }
}
