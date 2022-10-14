package code.board;

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
  
  TilePiece piece = null;
  
  private boolean isIn;
  
  private TileGrid[][] nb;
  
  public boolean onScreen(Camera cam, int x, int y) {
    double z = cam.getZoom();
    double conX = cam.conX();
    double conY = cam.conY();
    Vector2I screenSize = cam.getScreenSize();
    
    if((x    *TILE_SIZE)*z-conX < screenSize.x
    && (y    *TILE_SIZE)*z-conY < screenSize.y
    && ((x+1)*TILE_SIZE)*z-conX >= 0
    && ((y+1)*TILE_SIZE)*z-conY >= 0) {return true;}
    return false;
  }
  
  public void place(TilePiece piece) {this.piece = piece;}
  
  public TilePiece unPlace() {TilePiece p = piece; piece = null; return p;}
  
  public boolean isPlaced() {return piece != null;}
  
  public void setIn() {isIn = true;}
  
  public void unsetIn() {isIn = false;}
  
  public boolean isIn() {return isIn;}
  
  public void getNeighbours(TileGrid[][] map, int x, int y, int mapSX, int mapSY) {
    nb = new TileGrid[2][2];
    if (x != 0) nb[0][0] = map[x-1][y];
    if (x != mapSX-1) nb[0][1] = map[x+1][y];
    if (y != 0) nb[1][0] = map[x][y-1];
    if (y != mapSY-1) nb[1][1] = map[x][y+1];
  }
  
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
  public void draw(Graphics2D g, Camera cam, int x, int y, boolean selected) {
    double z = cam.getZoom();
    double conX = cam.conX();
    double conY = cam.conY();
    
    //draw the piece if present
    if (piece != null) {
      piece.draw(g, new Vector2(x*TILE_SIZE, y*TILE_SIZE), z, conX, conY, selected, isIn);
      return;
    }
    
    //otherwise draw the grid section
    Rectangle2D s = new Rectangle2D.Double(x*TILE_SIZE*z-conX, y*TILE_SIZE*z-conY, TILE_SIZE*z, TILE_SIZE*z);
    if (selected) {g.setColor(TileTheme.squaSele); g.fill(s);}
    if (isIn)     {g.setColor(TileTheme.squaHigh); g.fill(s);}
    g.setColor(TileTheme.squaOutl);
    g.draw(s);
  }
}

// Vector2 test = new Vector2(Math.abs(opos.x-tpos.x)-(other.getWidth()+this.width)/2, Math.abs(opos.y-tpos.y)-(other.getHeight()+this.height)/2);
// if (test.x<=0&&test.y<=0) {return test;}
