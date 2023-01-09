package code.core.scene.elements;

// import code.math.MathHelp;
import code.math.Vector2;
import code.math.Vector2I;

// import java.util.*;
import java.awt.Graphics2D;
import java.awt.geom.Rectangle2D;

/**
* A slot in the board for tiles to be placed
*/
public class TileGrid
{
  public static final int TILE_SIZE = 64;

  public final int x;

  public final int y;
  
  private TilePiece piece = null;
  
  private boolean isSelected = false;
  
  private final TileGrid[][] neighbouringTiles; //TODO spellchecker

  private TileGrid() {
    neighbouringTiles = null;
    x = y = -1;
  }

  public TileGrid(int x, int y) {
    neighbouringTiles = new TileGrid[][] {{new TileGrid(), new TileGrid()}, {new TileGrid(), new TileGrid()}};
    this.x = x;
    this.y = y;
  }

  /**
   * @return the piece placed in this grid element, or null if one is not present
   */
  public TilePiece getTilePiece() {
    return this.piece;
  }

  /**
   * @return the position of this {@code TileGrid} as a {@code Vector2I}
   */
  public Vector2I getPos() {
    return new Vector2I(x, y);
  }
  
  /**
   * places a given {@code TilePiece} into this grid element
   * 
   * @param piece the {@code TilePiece} to add to this spot in the grid
   */
  public void place(TilePiece piece) {
    this.piece = piece;
  }
  
  /**
   * sets the placed {@code TilePiece} to null
   * 
   * @return the removed {@code TilePiece}, or null if one was not present to begin with
   */
  public TilePiece unPlace() {
    TilePiece p = piece; piece = null; return p;
  }
  
  /**
   * @return true if this grid element currently has a tile placed within it
   */
  public boolean isPlaced() {
    return piece != null;
  }
  
  /**
   * sets the selected state of this grid element to {@code true}
   */
  public void select() {
    isSelected = true;
  }
  
  /**
   * resets the selected state of this grid element to {@code false}
   */
  public void deselect() {
    isSelected = false;
  }
  
  /**
   * @return true if this grid element is currently selected
   */
  public boolean isSelected() {return isSelected;}

  /**
   * Finds the neighbouring nodes to this tile,
   * assuming it is located at this {@code TileGrid}'s {@code x} and {@code y} coordinates.
   * 
   * @param map the total map of tiles this node is located in
   */
  public void findNeighbours(TileGrid[][] map, int yOffset) {
    if (x != 0)                       neighbouringTiles[0][0] = map[x-1][y-yOffset  ];
    if (x != map.length-1)            neighbouringTiles[0][1] = map[x+1][y-yOffset  ];
    if (y != 0+yOffset)               neighbouringTiles[1][0] = map[x]  [y-yOffset-1];
    if (y != map[0].length+yOffset-1) neighbouringTiles[1][1] = map[x]  [y-yOffset+1];
  } 
  
  @Override
  public boolean equals(Object other) {
    return this==other;
  }
  
  /**
   * Draws this section of the grid, whether it be a placed tile or an empty grid section
   * 
   * @param g the graphics object to draw to
   * @param cam the camera through which this TileGrid is being observed
   * @param x the x co-ordinate of this tile in the grid
   * @param y the y co-ordinate of this tile in the grid
   * @param selected whether or not this tile has been selected
   */
  public void draw(Graphics2D g, Camera cam, int x, int y, boolean isIn) {
    double z = cam.getZoom();
    double conX = cam.conX();
    double conY = cam.conY();
    
    //draw the piece if present
    if (piece != null) {
      piece.draw(g, new Vector2(x*TILE_SIZE, y*TILE_SIZE), z, conX, conY, isSelected, isIn);
      return;
    }
    
    //otherwise draw the grid section
    Rectangle2D s = new Rectangle2D.Double(x*TILE_SIZE*z-conX, y*TILE_SIZE*z-conY, TILE_SIZE*z, TILE_SIZE*z);
    if (isSelected) {g.setColor(TileTheme.squaSele); g.fill(s);}
    if (isIn      ) {g.setColor(TileTheme.squaHigh); g.fill(s);}
    g.setColor(TileTheme.squaOutl);
    g.draw(s);
  }

  /**
   * Checks to see if a grid element is currently visible within the bounds of a camera
   * 
   * @param cam the camera to try to view the grid element through
   * @param x the x coordinate of the element to view
   * @param y the y coordinate of the element to view
   * 
   * @return true if the chosen grid element is visible to the camera
   */
  public static boolean onScreen(Camera cam, int x, int y) {
    double z = cam.getZoom();
    double conX = cam.conX();
    double conY = cam.conY();
    Vector2I screenSize = cam.getScreenSize();
    
    if(( x   *TILE_SIZE)*z-conX < screenSize.x
    && ( y   *TILE_SIZE)*z-conY < screenSize.y
    && ((x+1)*TILE_SIZE)*z-conX >= 0
    && ((y+1)*TILE_SIZE)*z-conY >= 0) {return true;}
    return false;
  }
}

// Vector2 test = new Vector2(Math.abs(opos.x-tpos.x)-(other.getWidth()+this.width)/2, Math.abs(opos.y-tpos.y)-(other.getHeight()+this.height)/2);
// if (test.x<=0&&test.y<=0) {return test;}
