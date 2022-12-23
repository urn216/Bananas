package code.server;

import java.util.Random;

import code.core.Core;

import code.math.MathHelp;

/**
* class for generating a random pile of tiles
*/
public abstract class GenerateBoard extends Board {

  protected GenerateBoard(char[][] map) {
    super(map);
  }

  private static final double mu = Core.DEFAULT_MAP_SIZE/2.0;
  private static final double sigma = 3;

  public static Board random() {
    long seed = System.nanoTime();
    return random(seed, Core.DEFAULT_MAP_SIZE, Core.DEFAULT_MAP_SIZE);
  }
  
  private static Board random(long seed, int width, int height) {
    Random rand = new Random(seed);
    char[][] pile = emptyChars(width, height);

    for (int i = 0; i < LETTERS.length; i++) {
      int x = (int)MathHelp.clamp(rand.nextGaussian(mu, sigma), 0, width-1);
      int y = (int)MathHelp.clamp(rand.nextGaussian(mu, sigma), 0, height-1);
      if (pile[x][y] != '[') {
        i--;
        continue;
      }
      pile[x][y] = LETTERS[i];
    }
    return new Board(pile);
  }

  public static Board empty() {
    return empty(Core.DEFAULT_MAP_SIZE, Core.DEFAULT_MAP_SIZE);
  }

  private static Board empty(int width, int height) {
    return new Board(emptyChars(width, height));
  }

  private static char[][] emptyChars(int width, int height) {
    char[][] map = new char[width][height];
    for (int x = 0; x < width; x++) {
      for (int y = 0; y < height; y++) {
        map[x][y] = '[';
      }
    }
    return map;
  }
  
  /**
   * Testing suite for letter assortment
   * 
   * @param args Ignored
   */
  public static void main(String[] args) {
    int tot = 1;
    for (char c = 'A'; c <= 'Z'; c++) {
      int let = 1;
      System.out.print(c+": ");
      while(tot < LETTERS.length && LETTERS[tot++] == c) let++;
      System.out.println(let);
    }
    System.out.println(tot+" letters found out of "+LETTERS.length+"!");
  }

  private static final char[] LETTERS = {
    'A','A','A','A','A','A','A','A','A','A','A','A','A',
    'B','B','B',
    'C','C','C',
    'D','D','D','D','D','D',
    'E','E','E','E','E','E','E','E','E','E','E','E','E','E','E','E','E','E',
    'F','F','F',
    'G','G','G','G',
    'H','H','H',
    'I','I','I','I','I','I','I','I','I','I','I','I',
    'J','J',
    'K','K',
    'L','L','L','L','L',
    'M','M','M',
    'N','N','N','N','N','N','N','N',
    'O','O','O','O','O','O','O','O','O','O','O',
    'P','P','P',
    'Q','Q',
    'R','R','R','R','R','R','R','R','R',
    'S','S','S','S','S','S',
    'T','T','T','T','T','T','T','T','T',
    'U','U','U','U','U','U',
    'V','V','V',
    'W','W','W',
    'X','X',
    'Y','Y','Y',
    'Z','Z',
  };
}

// [(int)MathHelp.clamp((int)MathHelp.centered(rand.nextDouble(max-0.00001)+0.00001, mu, sigma, max)[rand.nextInt(2)], 0, width-1)]
// [(int)MathHelp.clamp((int)MathHelp.centered(rand.nextDouble(max-0.00001)+0.00001, mu, sigma, max)[rand.nextInt(2)], 0, height-1)];