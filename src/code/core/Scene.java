package code.core;

// import code.math.IOHelp;
// import code.math.MathHelp;
import code.math.Vector2;
import code.math.Vector2I;

import code.board.Camera;
import code.board.Decal;
import code.board.TileGrid;
import code.board.TilePiece;

// import java.util.*;
import java.awt.Graphics2D;
import java.util.ArrayList;
import java.util.List;

/**
* Scene class
*/
public class Scene
{
  //TODO break this into multiple classes; menu; lobby; and game
  protected int mapSX;
  protected int mapSY;
  protected TileGrid[][] map;
  
  protected Decal bg;
  
  protected final List<TileGrid> selectedTiles = new ArrayList<TileGrid>();

  protected final List<TilePiece> inventory = new ArrayList<TilePiece>();

  protected TilePiece held = new TilePiece('A');
  
  /**
  * Constructor for Scenes
  */
  public Scene() {
    bg = new Decal(1920, 1080, "BG/Menu.png", false);
    gameSetup();
  }
  
  private void gameSetup() {
    map = GenerateRandom.generate();
    mapSX = map.length;
    mapSY = map[0].length;
  }
  
  public void reset() {
    gameSetup();
  }
  
  public int[] getStats() {
    int[] stats = {};
    return stats;
  }
  
  public int getMapSX() {return mapSX;}
  
  public int getMapSY() {return mapSY;}
  
  public TileGrid getTile(Vector2I p) {return map[p.x][p.y];}

  public TilePiece getHeldTile() {return held;}
  
  public List<TileGrid> getSelectedTiles() {return selectedTiles;}

  public boolean hasSelectedTiles() {return !selectedTiles.isEmpty();}
  
  public Vector2 createPoint(int x, int y) {return new Vector2((x-mapSX/2)*TileGrid.TILE_SIZE, (y-mapSY/2)*TileGrid.TILE_SIZE);}
  
  private boolean validate(Vector2I p) {
    if (p.x < 0 || p.x >= mapSX
    ||  p.y < 0 || p.y >= mapSY) return false;
    return true;
  }
  
  public void pressTile(Vector2I p) {
    if (!validate(p)) return;
    TileGrid t = getTile(p);
    t.setIn();
  }
  
  public void selectTile(Vector2I p) {
    deselectTiles();
    if (validate(p) && getTile(p).isIn()) selectedTiles.add(getTile(p));
    unsetIn();
  }
  
  public void deselectTiles() {
    selectedTiles.clear();
  }
  
  public boolean placeTile(Vector2I p, TilePiece piece) {
    if (validate(p)) {
      TileGrid t = getTile(p);
      if (t.isIn()) {
        t.place(piece);
      }
    }
    unsetIn();
    return true;
  }
  
  public boolean removeTile(Vector2I p) {
    if (validate(p)) {
      TileGrid t = getTile(p);
      if (t.isIn() && selectedTiles.contains(t)) {
        inventory.add(t.unPlace());
      }
    }
    unsetIn();
    return true;
  }
  
  private void unsetIn() {
    for (int i = 0; i < mapSX; i++) {
      for (int j = 0; j < mapSY; j++) {
        map[i][j].unsetIn();
      }
    }
  }
  
  public Vector2I convertToIndex(Vector2 pos, Camera cam) {
    return new Vector2I ((int)(((pos.x+cam.conX())/(cam.getZoom()*TileGrid.TILE_SIZE))+mapSX/2), (int)(((pos.y+cam.conY())/(cam.getZoom()*TileGrid.TILE_SIZE))+mapSX/2));
  }
  
  public void draw(Graphics2D g, Camera cam, boolean revealAll) {
    bg.draw(g, cam);
    
    for (int i = 0; i < mapSX; i++) {
      for (int j = 0; j < mapSY; j++) {
        TileGrid t = map[i][j];
        if (t.onScreen(cam, i-mapSX/2, j-mapSY/2)) t.draw(g, cam, i-mapSX/2, j-mapSY/2, selectedTiles.contains(t));
      }
    }
  }
}
