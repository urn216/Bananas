package code.core.scene;

import code.core.scene.elements.Camera;
import code.core.scene.elements.Decal;
import code.core.scene.elements.TileGrid;

import java.awt.Graphics2D;

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

  @Override
  public boolean hasSelectedTiles() {return false;}

  @Override
  public void deselectTiles() {}



  @Override
  public void draw(Graphics2D g, Camera cam) {
    bg.draw(g);
  }
}
