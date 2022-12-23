package code.core.scene;

import code.core.Core;
import code.core.scene.elements.Decal;
import code.core.scene.elements.TileGrid;

public class LocalGame extends Scene {
  public LocalGame() {
    bg = new Decal(1920, 1080, "BG/Menu.png", false);
    gameSetup();
  }
  
  private void gameSetup() {
    map = emptyMap();
    pile = emptyMap();
    mapSX = mapSY = map.length;
  }

  public void reset() {
    gameSetup();
  }

  private static TileGrid[][] emptyMap() {
    TileGrid[][] map = new TileGrid[Core.DEFAULT_MAP_SIZE][Core.DEFAULT_MAP_SIZE];
    for (int x = 0; x < map.length; x++) {
      for (int y = 0; y < map[x].length; y++) {
        map[x][y] = new TileGrid();
      }
    }
    return map;
  }
}
