package code.core.scene;

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
public abstract class Scene
{
  protected int mapSX;
  protected int mapSY;
  protected TileGrid[][] map;
  protected TileGrid[][] pile;
  
  protected Decal bg;
  
  protected final List<TileGrid> selectedTiles = new ArrayList<TileGrid>();
  
  public abstract void reset();
  
  public int[] getStats() {
    int[] stats = {};
    return stats;
  }
  
  public int getMapSX() {return mapSX;}
  
  public int getMapSY() {return mapSY;}
  
  private TileGrid getTile(Vector2I p) {return p.y >= mapSY ? map[p.x][p.y-mapSY] : pile[p.x][p.y];}
  
  public List<TileGrid> getSelectedTiles() {return selectedTiles;}
  
  public boolean hasSelectedTiles() {return !selectedTiles.isEmpty();}
  
  public Vector2 createPoint(int x, int y) {return new Vector2((x-mapSX/2)*TileGrid.TILE_SIZE, (y-mapSY/2)*TileGrid.TILE_SIZE);}
  
  private boolean validate(Vector2I p) {
    if (p.x < 0 || p.x >= mapSX
    ||  p.y < 0 || p.y >= mapSY*2) return false;
    return true;
  }
  
  public boolean isSelected(Vector2I p) {
    if (validate(p)) {
      TileGrid t = getTile(p);
      if (t.isPlaced() && (t.isIn() || selectedTiles.contains(t))) {
        return true;
      }
    }
    return false;
  }
  
  public void pressTile(Vector2I p) {
    if (!validate(p)) return;
    TileGrid t = getTile(p);
    t.setIn();
  }
  
  public boolean selectTile(Vector2I p) {
    deselectTiles();
    if (validate(p) && getTile(p).isIn()) selectedTiles.add(getTile(p));
    unsetIn();
    return hasSelectedTiles();
  }
  
  public void selectTiles(Vector2I a, Vector2I b) {
    deselectTiles();
    if (validate(a) && validate(b)) {
      Vector2I tL = new Vector2I(Math.min(a.x, b.x), Math.min(a.y, b.y));
      Vector2I bR = new Vector2I(Math.max(a.x, b.x), Math.max(a.y, b.y));
      for (int y = tL.y; y <= bR.y; y++) {
        for (int x = tL.x; x <= bR.x; x++) {
          TileGrid t = getTile(new Vector2I(x, y));
          if (t.isPlaced()) selectedTiles.add(t);
        }
      }
    }
    unsetIn();
  }
  
  public void deselectTiles() {
    selectedTiles.clear();
  }

  public boolean placeTile(Vector2I p, TilePiece piece, boolean pile) {
    return placeTile(pile ? p : p.add(0, mapSY), piece);
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
        t.unPlace();
      }
    }
    unsetIn();
    return true;
  }
  
  public void unsetIn() {
    for (int i = 0; i < mapSX; i++) {
      for (int j = 0; j < mapSY; j++) {
        map[i][j].unsetIn();
        pile[i][j].unsetIn();
      }
    }
  }
  
  public Vector2I convertToIndex(Vector2 pos, Camera cam) {
    Vector2I res = new Vector2I ((int)(((pos.x+cam.conX())/(cam.getZoom()*TileGrid.TILE_SIZE))+mapSX/2), (int)(((pos.y+cam.conY())/(cam.getZoom()*TileGrid.TILE_SIZE))+mapSX/2));
    if (res.y >= mapSY) res = res.subtract(0, 1);
    return res;
  }
  
  public void draw(Graphics2D g, Camera cam) {
    bg.draw(g);

    for (int i = 0; i < mapSX; i++) {
      for (int j = 0; j < mapSY; j++) {
        TileGrid t = pile[i][j];
        if (t.onScreen(cam, i-mapSX/2, j-mapSY/2)) t.draw(g, cam, i-mapSX/2, j-mapSY/2, selectedTiles.contains(t));
      }
    }
    
    for (int i = 0; i < mapSX; i++) {
      for (int j = 0; j < mapSY; j++) {
        TileGrid t = map[i][j];
        if (t.onScreen(cam, i-mapSX/2, j+1+mapSY/2)) t.draw(g, cam, i-mapSX/2, j+1+mapSY/2, selectedTiles.contains(t));
      }
    }
  }
}
