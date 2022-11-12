package code.board;

import code.core.Core;

//import java.io.*;
//import java.nio.file.*;

// import java.util.*;

/**
* class for generating a random map for playing the game
*/
public class GenerateRandom
{
  
  
  
  public static TileGrid[][] generate() {
    long seed = System.nanoTime();
    return generate(seed, Core.DEFAULT_MAP_SIZE, Core.DEFAULT_MAP_SIZE);
  }
  
  public static TileGrid[][] generate(long seed, int width, int height) {
    // Random rand = new Random(seed);
    TileGrid[][] map = new TileGrid[width][height];
    for (int x = 0; x < width; x++) {
      for (int y = 0; y < height; y++) {
        map[x][y] = new TileGrid();
      }
    }
    
    return map;
  }
}
