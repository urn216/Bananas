package code.core.scene.elements;

import code.math.IOHelp;

// import code.core.Scene;

import code.math.Vector2;

import java.awt.Graphics2D;

import java.awt.image.BufferedImage;

/**
* Write a description of class Decal here.
*
* @author (your name)
* @version (a version number or a date)
*/
public class Decal
{
  private boolean camPan;
  private BufferedImage img;
  private Vector2 position;
  private Vector2 origin;
  private int width;
  private int height;

  /**
  * Constructor for Decal objects
  */
  public Decal(double x, double y, String file, boolean pan)
  {
    position = new Vector2(x, y);
    camPan = pan;
    img = IOHelp.readImage(file);
    width = img.getWidth();
    height = img.getHeight();
    origin = new Vector2(x-width/2, y-height/2);
  }

  public void draw(Graphics2D g, Camera cam) {
    if (camPan) {
      double z = cam.getZoom();
      double conX = cam.conX();
      double conY = cam.conY();
      g.drawImage(img, null, (int)(position.x*z-conX-width/2), (int)(position.y*z-conY-height/2));
    }
    else {
      g.drawImage(img, null, (int)(origin.x), (int)(origin.y));
    }
  }

  public void draw(Graphics2D g) {
    g.drawImage(img, null, (int)(origin.x), (int)(origin.y));
  }
}
