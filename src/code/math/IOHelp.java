package code.math;

import code.core.Core;
import code.core.Scene;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.*;
import java.nio.file.*;

import java.util.List;
import java.util.ArrayList;

/**
* class helping do file stuff
*/
public abstract class IOHelp {
  public static void saveToFile(String filename, String content) {
    try {
      File f = new File(filename);
      f.createNewFile();
      PrintStream out = new PrintStream(f);
      out.print(content);
      out.close();
    } catch(IOException e){System.err.println("Saving failed " + e);}
  }

  public static void copyContents(File source, Path dest) {
    try {
      Files.copy(source.toPath(), dest, StandardCopyOption.valueOf("REPLACE_EXISTING"));
    } catch(IOException e){System.err.println("Copying failed " + e);}
    if (source.isDirectory()) {
      for (File fi : source.listFiles()) {
        IOHelp.copyContents(fi, dest.resolve(fi.toPath().getFileName()));
      }
    }
  }

  public static void copyContents(InputStream source, Path dest) {
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
  public static boolean createDir(String filename) {
    File fi = new File(filename);
    if (fi.exists()) {
      for (File f : fi.listFiles()) {
        if (!delete(f)) return false;
      }
      return true;
    }
    else {return fi.mkdirs();}
  }

  public static boolean exists(String filename) {return new File(filename).exists();}

  public static boolean delete(File f) {
    if (f.isDirectory()) {
      for (File fi : f.listFiles()) {
        IOHelp.delete(fi);
      }
    }
    return f.delete();
  }

  public static List<String> readAllLines(String filename, boolean inJar) {
    try {
      if (inJar) {
        BufferedReader file = new BufferedReader(new InputStreamReader(Scene.class.getResourceAsStream(filename)));
        List<String> allLines = new ArrayList<String>();
        String line;
        while ((line = file.readLine()) != null) {
          allLines.add(line);
        }
        return allLines;
      }
      else {
        return Files.readAllLines(Paths.get(filename));
      }
    } catch(IOException e){System.err.println("Reading failed: " + e);}
    return new ArrayList<String>();
  }

  /**
  * @param filename The path of the texture file desired
  *
  * @return a buffered image of the desired texture
  */
  public static BufferedImage readImage(String filename) {
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
  public static int[] readImageInt(String filename) {
    BufferedImage img = IOHelp.readImage(filename);

    return img.getRGB(0, 0, img.getHeight(), img.getHeight(), null, 0, img.getWidth());
  }
}
