package code.core.scene;

import code.core.scene.elements.Decal;
import code.core.scene.elements.TileGrid;

public class Menu extends Scene {
  public static final Menu MENU = new Menu();

  private Menu() {
    bg = new Decal(1920, 1080, "BG/Menu.png", false);
    map = new TileGrid[0][0];
    mapSX = mapSY = 0;
  }

  public void reset() {}
}
