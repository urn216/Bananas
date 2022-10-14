package code.board;

import java.awt.Graphics2D;
import java.awt.geom.Rectangle2D;
import java.awt.geom.Path2D;

import java.awt.Color;
import java.awt.Font;
import java.awt.FontMetrics;

import code.math.Vector2;

public class TilePiece implements Comparable<TilePiece> {
  
  private static final int TILE_SIZE = TileGrid.TILE_SIZE;
  
  public final char letter;
  
  /**
  * Constructs a piece used in the game
  * 
  * @param letter the letter representing this piece
  */
  public TilePiece(char letter) {
    this.letter = letter;
  }
  
  @Override
  public TilePiece clone() {return new TilePiece(letter);}
  
  @Override
  public int compareTo(TilePiece o) {
    return ((Character)this.letter).compareTo(o.letter);
  }
  
  @Override
  public boolean equals(Object o) {
    return o instanceof TilePiece ? this.letter == ((TilePiece)o).letter : false;
  }
  
  /**
  * Draws the tile to the screen centred on the given position
  * 
  * @param g the Graphics2D component to draw to
  * @param position the position on the screen to draw the tile centred onto
  * @param z the scale at which to draw the tile
  * @param selected whether or not the tile should be drawn selected
  * @param isIn whether or not the tile should be drawn pressed down
  */
  public void draw(Graphics2D g, Vector2 position, double z, boolean selected, boolean isIn) {
    drawToScreen(g, position.subtract(0.5*TILE_SIZE*z), TILE_SIZE*z, letter, selected, isIn);
  }
  
  /**
  * Draws the tile as seen through a camera in the world
  * 
  * @param g the Graphics2D component to draw to
  * @param origin the top left corner in the world to draw the tile from
  * @param z the level of zoom of the camera viewing the tile
  * @param conX the x-conversion to translate the world to the view of the camera
  * @param conY the y-conversion to translate the world to the view of the camera
  * @param selected whether or not the tile should be drawn selected
  * @param isIn whether or not the tile should be drawn pressed down
  */
  public void draw(Graphics2D g, Vector2 origin, double z, double conX, double conY, boolean selected, boolean isIn) {
    drawToScreen(g, origin.scale(z).subtract(conX, conY), TILE_SIZE*z, letter, selected, isIn);
  }
  
  /**
   * Static method that performs the actual drawing of tiles. Only to be called through parent draw methods.
   * 
   * @param g the Graphics2D component to draw to
   * @param origin the top left corner on the screen from which to draw the tile
   * @param size the scale at which to draw the tile on the screen
   * @param letter the letter representing the tile we desire to draw
   * @param selected whether or not the tile should be drawn selected
  * @param isIn whether or not the tile should be drawn pressed down
   */
  private static void drawToScreen(Graphics2D g, Vector2 origin, double size, char letter, boolean selected, boolean isIn) {
    Font font = new Font("Copperplate", Font.BOLD, (int) Math.round(0.7*size));
    FontMetrics metrics = g.getFontMetrics(font);
    g.setFont(font);
    
    double incr = size/16;
    
    Color tl = TileTheme.tileHigh;
    Color br = TileTheme.tileShad;
    
    if (isIn) {
      tl = TileTheme.tileShad;
      br = TileTheme.tileHigh;
    }
    
    g.setColor(tl);
    g.fill(new Rectangle2D.Double(origin.x, origin.y, size, size));
    
    g.setColor(br);
    Path2D p = new Path2D.Double();
    p.moveTo(origin.x, origin.y+size);
    p.lineTo(origin.x+size, origin.y+size);
    p.lineTo(origin.x+size, origin.y);
    p.closePath();
    g.fill(p);
    
    g.setColor(selected ? TileTheme.tileSele : TileTheme.tileBody);
    g.fill(new Rectangle2D.Double(origin.x+incr, origin.y+incr, size-incr*2, size-incr*2));
    //implement for green/red borders (spell check)
    // g.fill(new Rectangle2D.Double((origin.x+TILE_INCR*2)*z-conX, (origin.y+TILE_INCR*2)*z-conY, (TILE_SIZE-TILE_INCR*4)*z, (TILE_SIZE-TILE_INCR*4)*z));
    
    g.setColor(TileTheme.tileText);
    g.drawString(""+letter, (int)(origin.x+(size-metrics.charWidth(letter))/2), (int)(origin.y+((size - metrics.getHeight())/2) + metrics.getAscent()));
  }
}

// legacy code
// Font font = new Font("Copperplate", Font.BOLD, (int) Math.round((TILE_SIZE*0.7*z)));
// FontMetrics metrics = g.getFontMetrics(font);
// g.setFont(font);

// Color tl = TileTheme.tileHigh;
// Color br = TileTheme.tileShad;

// if (isIn) {
  //   tl = TileTheme.tileShad;
  //   br = TileTheme.tileHigh;
  // }
  
  // g.setColor(tl);
  // g.fill(new Rectangle2D.Double(origin.x*z-conX, origin.y*z-conY, TILE_SIZE*z, TILE_SIZE*z));
  
  // g.setColor(br);
  // Path2D p = new Path2D.Double();
  // p.moveTo(origin.x*z-conX, (origin.y+TILE_SIZE)*z-conY);
  // p.lineTo((origin.x+TILE_SIZE)*z-conX, (origin.y+TILE_SIZE)*z-conY);
  // p.lineTo((origin.x+TILE_SIZE)*z-conX, origin.y*z-conY);
  // p.closePath();
  // g.fill(p);
  
  // g.setColor(selected ? TileTheme.tileSele : TileTheme.tileBody);
  // g.fill(new Rectangle2D.Double((origin.x+TILE_INCR)*z-conX, (origin.y+TILE_INCR)*z-conY, (TILE_SIZE-TILE_INCR*2)*z, (TILE_SIZE-TILE_INCR*2)*z));
  // //implement for green/red borders (spell check)
  // // g.fill(new Rectangle2D.Double((origin.x+TILE_INCR*2)*z-conX, (origin.y+TILE_INCR*2)*z-conY, (TILE_SIZE-TILE_INCR*4)*z, (TILE_SIZE-TILE_INCR*4)*z));
  
  // g.setColor(TileTheme.tileText);
  // g.drawString(""+letter, (int)((origin.x*z+(TILE_SIZE*z-metrics.charWidth(letter))/2)-conX), (int)((origin.y*z+((TILE_SIZE*z - metrics.getHeight())/2) + metrics.getAscent())-conY));
  