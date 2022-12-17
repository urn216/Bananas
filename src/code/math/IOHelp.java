package code.math;

import code.core.Core;
import code.core.Scene;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.*;
import java.nio.file.*;

import java.util.List;
import java.util.ArrayList;
import java.util.Arrays;

/**
* Non initializable class with helper methods and fields for use in Input/Output operations
*/
public abstract class IOHelp {
  
  //-------------------------------------------------------------------------------------
  //                              FILE TRANSFER HELPERS
  //-------------------------------------------------------------------------------------
  
  public static final void saveToFile(String filename, String content) {
    try {
      File f = new File(filename);
      f.createNewFile();
      PrintStream out = new PrintStream(f);
      out.print(content);
      out.close();
    } catch(IOException e){System.err.println("Saving failed " + e);}
  }
  
  public static final void copyContents(File source, Path dest) {
    try {
      Files.copy(source.toPath(), dest, StandardCopyOption.valueOf("REPLACE_EXISTING"));
    } catch(IOException e){System.err.println("Copying failed " + e);}
    if (source.isDirectory()) {
      for (File fi : source.listFiles()) {
        IOHelp.copyContents(fi, dest.resolve(fi.toPath().getFileName()));
      }
    }
  }
  
  public static final void copyContents(InputStream source, Path dest) {
    try {
      Files.copy(source, dest, StandardCopyOption.valueOf("REPLACE_EXISTING"));
    } catch(IOException e){System.err.println("Copying failed " + e);}
  }
  
  /**
  * Creates an empty file directory at the given location.
  *
  * Deletes everything within the directory if something exists in its place. Be careful with this power.
  *
  * @param filename the directory to create.
  *
  * @return true if creation was a success; false if something went wrong
  */
  public static final boolean createDir(String filename) {
    File fi = new File(filename);
    if (fi.exists()) {
      for (File f : fi.listFiles()) {
        if (!delete(f)) return false;
      }
      return true;
    }
    else {return fi.mkdirs();}
  }
  
  public static final boolean exists(String filename) {return new File(filename).exists();}
  
  public static boolean delete(File f) {
    if (f.isDirectory()) {
      for (File fi : f.listFiles()) {
        IOHelp.delete(fi);
      }
    }
    return f.delete();
  }
  
  public static final List<String> readAllLines(String filename, boolean inJar) {
    try {
      if (!inJar) return Files.readAllLines(Paths.get(filename));

      BufferedReader file = new BufferedReader(new InputStreamReader(Scene.class.getResourceAsStream(filename)));
      List<String> allLines = new ArrayList<String>();
      String line;
      while ((line = file.readLine()) != null) {
        allLines.add(line);
      }
      return allLines;
    } catch(IOException e){System.err.println("Reading failed: " + e);}

    return new ArrayList<String>();
  }
  
  /**
  * @param filename The path of the texture file desired
  *
  * @return a buffered image of the desired texture
  */
  public static final BufferedImage readImage(String filename) {
    try {
      return ImageIO.read(Core.class.getResourceAsStream("/data/textures/" + filename));
    }catch(IOException e){System.err.println("Failed to find Texture at " + filename);}
    return null;
  }
  
  /**
  * @param filename The path of the texture file desired
  *
  * @return a square texture in RGBA array format
  */
  public static final int[] readImageInt(String filename) {
    BufferedImage img = IOHelp.readImage(filename);
    
    return img.getRGB(0, 0, img.getHeight(), img.getHeight(), null, 0, img.getWidth());
  }
  
  //-------------------------------------------------------------------------------------
  //                            NETWORK TRANSFER HELPERS
  //-------------------------------------------------------------------------------------
  
  /**
  * The maximum number of characters that will be read in a single message when sent over a network
  */
  public static final int MAX_MESSAGE_LENGTH = 1024;
  
  /**
  * An end-of-message marker to be tacked onto the end of any set of data transmitted over a network
  */
  public static final byte END = 0b0000000;
  
  /**
  * A header indicating that a text message follows
  */
  public static final byte MSG = 0b0000001;
  /**
  * A header indicating that a tile movement follows
  */
  public static final byte MVE = 0b0000010;
  /**
  * A header indicating that a tile placement follows
  */
  public static final byte SET = 0b0000011;
  /**
  * A single-byte message which indicates the match has begun
  */
  public static final byte BGN = 0b0000100;
  /**
  * A single-byte message which indicates this user is ready
  */
  public static final byte RDY = 0b0000101;
  /**
  * A header indicating a username follows
  */
  public static final byte USR_SND = 0b0000110;
  /**
  * A single-byte message which indicates a username request
  */
  public static final byte USR_REQ = 0b0000110;
  
  /**
  * A single-byte message which disconnects a client due to the lobby being full
  */
  public static final byte EXIT_LOBBY_FULL = 0b1000001;
  /**
  * A single-byte message which disconnects a client due to the game already running
  */
  public static final byte EXIT_GAME_IN_PROGRESS = 0b1000010;
  /**
  * A single-byte message which disconnects a client due to the server being closed
  */
  public static final byte EXIT_SERVER_CLOSED = 0b1000011;
  /**
  * A single-byte message which disconnects a client due to the client being kicked
  */
  public static final byte EXIT_KICKED = 0b1000100;
  /**
  * A single-byte message which disconnects a client due to the client having disconnected themselves
  */
  public static final byte EXIT_DISCONNECTED = 0b1000101;
  
  private static final byte EXIT_MASK = 0b1000000;
  
  /**
  * Checks to see if a byte is a client disconnect code,
  * indicating that a connection should be terminated.
  * 
  * @param b The byte to analyse
  * @return true if this byte is an exit condition
  */
  public static final boolean isExitCondition(int b) {
    return (b&EXIT_MASK)==EXIT_MASK;
  }
  
  /**
  * Encodes a move representing the swapping of two tiles in a pair of grids
  * 
  * @param x1 the x coord of the first tile
  * @param y1 the y coord of the first tile
  * @param c1 the char representing the first tile
  * @param p1 whether the first tile is in the communal pile
  * @param x2 the x coord of the second tile
  * @param y2 the y coord of the second tile
  * @param c2 the char representing the second tile
  * @param p2 whether the second tile is in the communal pile
  * 
  * @return an encoded byte array containing all the supplied data, ready to be transmitted online
  */
  public static final byte[] encodeFromTo(int x1, int y1, char c1, boolean p1, int x2, int y2, char c2, boolean p2) {
    byte[] from = encodeTilePos(x1, y1, c1, p1);
    byte[] to = encodeTilePos(x2, y2, c2, p2);
    return new byte[]{from[0], from[1], to[0], to[1]};
  }
  
  /**
  * Encodes a move representing the swapping of two tiles in a pair of grids
  * 
  * @param pos1 the x and y coords of the first tile
  * @param c1 the char representing the first tile
  * @param p1 whether the first tile is in the communal pile
  * @param pos2 the x and y coords of the second tile
  * @param c2 the char representing the second tile
  * @param p2 whether the second tile is in the communal pile
  * 
  * @return an encoded byte array containing all the supplied data, ready to be transmitted online
  */
  public static final byte[] encodeFromTo(Vector2I pos1, char c1, boolean p1, Vector2I pos2, char c2, boolean p2) {
    return encodeFromTo(pos1.x, pos1.y, c1, p1, pos2.x, pos2.y, c2, p2);
  }
  
  /**
  * Encodes a tile position in either the communal pile or a player's personal grid
  * 
  * @param x the x coord of the tile
  * @param y the y coord of the tile
  * @param c the char representing the tile
  * @param pile whether the tile is in the communal pile
  * 
  * @return an encoded byte array containing all the supplied data, ready to be transmitted online
  */
  public static final byte[] encodeTilePos(int x, int y, char c, boolean pile) {
    if (c<'A' || c>'Z') c = '[';
    int m = (y*Core.DEFAULT_MAP_SIZE+x)<<6 | (c-65)<<1 | (pile ? 1 : 0);
    
    return new byte[]{(byte)(m>>8), (byte)m};
  }
  
  /**
  * Encodes a tile position in either the communal pile or a player's personal grid
  * 
  * @param pos the x and y coords of the tile
  * @param c the char representing the tile
  * @param pile whether the tile is in the communal pile
  * 
  * @return an encoded byte array containing all the supplied data, ready to be transmitted online
  */
  public static final byte[] encodeTilePos(Vector2I pos, char c, boolean pile) {
    return encodeTilePos(pos.x, pos.y, c, pile);
  }
  
  /**
  * Takes a byte array representing the position of a tile
  * and converts it into the underlying integer,
  * ready to be decoded by parts
  * 
  * @param b the byte array to retrieve a tile's info from
  * @param offset the offset in the array from which to retrieve the info
  * 
  * @return the underlying encoded integer
  */
  public static final int decodeTilePos(byte[] b, int offset) {
    return (b[offset]&0b11111111)<<8 | b[offset+1]&0b11111111;
  }
  
  /**
  * Takes in an encoded integer and extracts the position data from it
  * 
  * @param m the encoded integer
  * 
  * @return a Vector2I with the x and y coordinates of the underlying tile
  */
  public static final Vector2I extractPos(int m) {
    int p = m>>>6;
    return new Vector2I(p%Core.DEFAULT_MAP_SIZE, p/Core.DEFAULT_MAP_SIZE);
  }
  
  /**
  * Takes in an encoded integer and extracts the letter data from it
  * 
  * @param m the encoded integer
  * 
  * @return a char representing the underlying tile
  */
  public static final char extractLetter(int m) {
    return (char)(((m>>>1)&0b11111)+65);
  }
  
  /**
  * Takes in an encoded integer and extracts the grid of origin from it
  * 
  * @param m the encoded integer
  * 
  * @return a boolean representing whether or not the underlying tile is in the communal pile
  */
  public static final boolean extractPile(int m) {
    return (m&1)==1;
  }
  
  /**
  * Testing suite for byte conversion
  * 
  * @param args Unused
  */
  public static void main(String[] args) {
    byte[] test = encodeTilePos(29, 11, 'Z', false);
    System.out.println("encoded bytes: " + Arrays.toString(test));
    int m = decodeTilePos(test, 0);
    System.out.println("decoded int: " + m);
    Vector2I pos = extractPos(m);
    char c = extractLetter(m);
    boolean pile = extractPile(m);
    System.out.println(pos + ", " + c + ", " + pile);
  }
}
