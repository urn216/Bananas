package code.core.scene;

import code.core.Core;

import code.core.scene.elements.Camera;
import code.core.scene.elements.Decal;
import code.core.scene.elements.TileGrid;

import code.math.Vector2I;

import java.awt.Graphics2D;

class LocalGame extends Scene {
  public LocalGame() {
    super(
      Core.DEFAULT_MAP_SIZE, 
      Core.DEFAULT_MAP_SIZE, 
      emptyMaps(8), 
      emptyMap(true), 
      new Decal(1920, 1080, "BG/Menu.png", false)
    );
  }

  @Override
  public void reset() {
    for (TileGrid[][] map : maps) clearMap(map, mapSY);
    clearMap(pile, 0);
  }

  private static void clearMap(TileGrid[][] map, int yOffset) {
    for (int x = 0; x < map.length; x++) {
      for (int y = 0; y < map[x].length; y++) {
        map[x][y] = new TileGrid(x, y+yOffset);
      }
    }
    for (int x = 0; x < map.length; x++) {
      for (int y = 0; y < map[x].length; y++) {
        map[x][y].findNeighbours(map, yOffset);
      }
    }
  }

  private static TileGrid[][] emptyMap(boolean pile) {
    TileGrid[][] map = new TileGrid[Core.DEFAULT_MAP_SIZE][Core.DEFAULT_MAP_SIZE];
    clearMap(map, pile ? 0 : Core.DEFAULT_MAP_SIZE);
    return map;
  }

  private static TileGrid[][][] emptyMaps(int num) {
    TileGrid[][][] maps = new TileGrid[num][][];
    for (int i = 0; i < num; i++) maps[i] = emptyMap(false);
    return maps;
  }

  public void draw(Graphics2D g, Camera cam) {
    bg.draw(g);
    
    for (int i = 0; i < mapSX; i++) {
      for (int j = 0; j < mapSY; j++) {
        TileGrid t = pile[i][j];
        if (TileGrid.onScreen(cam, i-mapSX/2, j-mapSY/2)) t.draw(g, cam, i-mapSX/2, j-mapSY/2, t == pressedTile);
      }
    }

    for (int p = 0; p < numPlayers; p++) {
      Vector2I offset = playerPositions[p];
      TileGrid[][] map = maps[p];

      for (int i = 0; i < mapSX; i++) {
        for (int j = 0; j < mapSY; j++) {
          TileGrid t = map[i][j];
          if (TileGrid.onScreen(cam, i+offset.x, j+offset.y)) t.draw(g, cam, i+offset.x, j+offset.y, t == pressedTile);
        }
      }
    }
  }
}
