package code.core;

import code.board.Decal;
import code.board.TileGrid;

public class LocalGame extends Scene {
  public LocalGame() {
    bg = new Decal(1920, 1080, "BG/Menu.png", false);
    gameSetup();
  }
  
  private void gameSetup() {
    map = new TileGrid[Core.DEFAULT_MAP_SIZE][Core.DEFAULT_MAP_SIZE];
    for (int x = 0; x < Core.DEFAULT_MAP_SIZE; x++) {
      for (int y = 0; y < Core.DEFAULT_MAP_SIZE; y++) {
        map[x][y] = new TileGrid();
      }
    }
    mapSX = mapSY = Core.DEFAULT_MAP_SIZE;
  }

  public void reset() {
    gameSetup();
  }
}
