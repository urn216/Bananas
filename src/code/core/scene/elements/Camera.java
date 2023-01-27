package code.core.scene.elements;

import code.core.Core;
import code.core.Window;

import code.math.MathHelp;
import code.math.Vector2;

/**
* A camera allows a player to see what's happening in the game.
*/
public class Camera
{
  private static final double CLOSE_MAGNITUDE = 0.125;

  private static final int    OFFSET_BOUNDS = Core.DEFAULT_MAP_SIZE*TileGrid.TILE_SIZE*3/2 + TileGrid.TILE_SIZE;
  private static final double ZOOM_BOUND_U  = 3;
  private static final double ZOOM_BOUND_L  = 0.25;

  private double defaultZoom;
  private double zoom;
  private Vector2 position;
  private Vector2 offset;
  private Vector2 target;

  /**
  * @Camera
  *
  * Constructs a camera with an x position, a y position, a default zoom level, and the current resolution of the game window.
  */
  public Camera(Vector2 worldPos, Vector2 offset, double z)
  {
    this.position = worldPos;
    this.offset = offset;
    this.defaultZoom = z;
    this.zoom = Core.WINDOW.screenHeight()/Window.DEFAULT_SCREEN_SIZE.y*z;
  }

  public Vector2 getOffset() {return offset;}

  public void setOffset(Vector2 offset) {
    double bounds = OFFSET_BOUNDS*zoom;
    this.offset = new Vector2(MathHelp.clamp(offset.x, -bounds, bounds), MathHelp.clamp(offset.y, -bounds, bounds));
  }

  public void addOffset(Vector2 offset) {
    double bounds = OFFSET_BOUNDS*zoom;
    this.offset = new Vector2(MathHelp.clamp(offset.x+this.offset.x, -bounds, bounds), MathHelp.clamp(offset.y+this.offset.y, -bounds, bounds));
  }

  // public Vector2 getSize() {return new Vector2(screenSizeX/(zoom*2), screenSizeY/(zoom*2));}

  public Vector2 getTarget() {return target;}

  public double getZoom() {return zoom;}

  public double getDZoom() {return (Core.WINDOW.screenHeight()/Window.DEFAULT_SCREEN_SIZE.y)*defaultZoom;}

  public void resetZoom() {this.zoom = getDZoom();}

  public void setTarget(Vector2 t){
    target = t;
  }

  public void setZoom(double z) {
    offset = offset.scale(z/zoom);
    this.zoom = z;
  }

  public void setZoom(double z, Vector2 cursorOffset) {
    double screenScale = Core.WINDOW.screenHeight()/Window.DEFAULT_SCREEN_SIZE.y;
    z = MathHelp.clamp(z, screenScale*ZOOM_BOUND_L, screenScale*ZOOM_BOUND_U);
    offset = offset.scale(z/zoom).add(cursorOffset.scale(1-z/zoom));
    this.zoom = z;
    setOffset(offset);
  }

  public void follow() {
    if (target != null) {
      Vector2 dist = new Vector2(target.subtract(position));
      if (dist.magsquare() >= 0.1)
        position = position.add(dist.scale(CLOSE_MAGNITUDE));
    }
  }

  public double conX() {
    return position.x*zoom-Core.WINDOW.screenWidth()/2-offset.x;
  }

  public double conY() {
    return position.y*zoom-Core.WINDOW.screenHeight()/2-offset.y;
  }

  public Vector2 getPos() {
    return position;
  }

  /**
   * Checks to see if an object is currently visible within the bounds of a camera
   * 
   * @param leftWorldBound  the left-most extent of the object within world-space
   * @param upperWorldBound the top-most extent of the object within world-space
   * @param rightWorldBound the right-most extent of the object within world-space
   * @param lowerWorldBound the bottom-most extent of the object within world-space
   * 
   * @return {@code true} if the given bounds lie within the frame of the camera
   */
  public boolean canSee(double leftWorldBound, double upperWorldBound, double rightWorldBound, double lowerWorldBound) {
    double conX = conX();
    double conY = conY();

    if (leftWorldBound*zoom-conX < Core.WINDOW.screenWidth ()
    && upperWorldBound*zoom-conY < Core.WINDOW.screenHeight()
    && rightWorldBound*zoom-conX >= 0
    && lowerWorldBound*zoom-conY >= 0) {return true;}
    return false;
  }
}
