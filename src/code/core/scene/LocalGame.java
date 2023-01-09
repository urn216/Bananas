package code.core.scene;

import code.core.Core;
import code.core.scene.elements.Decal;
import code.core.scene.elements.TileGrid;

class LocalGame extends Scene {
  public LocalGame() {
    super(
      Core.DEFAULT_MAP_SIZE, 
      Core.DEFAULT_MAP_SIZE, 
      emptyMap(false), 
      emptyMap(true), 
      new Decal(1920, 1080, "BG/Menu.png", false)
    );
  }

  @Override
  public void reset() {
    clearMap(map, mapSY);
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
}
