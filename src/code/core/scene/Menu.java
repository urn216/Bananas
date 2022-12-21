package code.core.scene;

import code.board.Decal;
import code.board.TileGrid;

public class Menu extends Scene {
  public static final Menu MENU = new Menu();

  private Menu() {
    bg = new Decal(1920, 1080, "BG/Menu.png", false);
    map = new TileGrid[0][0];
    mapSX = mapSY = 0;
  }

  public void reset() {}
}
