package code.core.scene;

import code.core.scene.elements.Decal;
import code.core.scene.elements.TileGrid;

class Menu extends Scene {

  public static final Menu MENU = new Menu();

  private Menu() {
    super(
      0, 
      0, 
      new TileGrid[0][0][0], 
      new TileGrid[0][0], 
      new Decal(1920, 1080, "BG/Menu.png", false)
    );
  }

  @Override
  public void reset() {}
}
