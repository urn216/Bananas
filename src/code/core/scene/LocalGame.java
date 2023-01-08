package code.core.scene;

import code.core.Core;
import code.core.scene.elements.Decal;
import code.core.scene.elements.TileGrid;

class LocalGame extends Scene {
  public LocalGame() {
    super(
      Core.DEFAULT_MAP_SIZE, 
      Core.DEFAULT_MAP_SIZE, 
      emptyMap(), 
      emptyMap(), 
      new Decal(1920, 1080, "BG/Menu.png", false)
    );
  }

  @Override
  public void reset() {
    clearMap(map);
    clearMap(pile);
  }

  private static void clearMap(TileGrid[][] map) {
    for (int x = 0; x < map.length; x++) {
      for (int y = 0; y < map[x].length; y++) {
        map[x][y] = new TileGrid(x, y);
      }
    }
    for (int x = 0; x < map.length; x++) {
      for (int y = 0; y < map[x].length; y++) {
        map[x][y].findNeighbours(map);
      }
    }
  }

  private static TileGrid[][] emptyMap() {
    TileGrid[][] map = new TileGrid[Core.DEFAULT_MAP_SIZE][Core.DEFAULT_MAP_SIZE];
    clearMap(map);
    return map;
  }
}
