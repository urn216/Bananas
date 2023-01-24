package code.server;

import java.util.Arrays;
import java.util.Random;

import code.core.Core;

import code.math.MathHelp;

/**
* class for generating a random pile of tiles
*/
abstract class GenerateBoard extends Board {

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
    char[] letters = shuffleLetters(rand);

    for (int i = 0; i < letters.length; i++) {
      int x = (int)MathHelp.clamp(rand.nextGaussian(mu, sigma), 0, width-1);
      int y = (int)MathHelp.clamp(rand.nextGaussian(mu, sigma), 0, height-1);
      if (pile[x][y] != '[') {
        i--;
        continue;
      }
      pile[x][y] = letters[i];
    }
    return new Board(pile);
  }

  /**
   * Creates an empty {@code Board} of default size.
   * Empty implies in this situation that all chars within the
   * {@code Board} are set to the default state of {@code [}
   * 
   * @return a new empty {@code Board}
   */
  public static Board empty() {
    return empty(Core.DEFAULT_MAP_SIZE, Core.DEFAULT_MAP_SIZE);
  }

  /**
   * Creates an empty {@code Board} of specified size.
   * Empty implies in this situation that all chars within the
   * {@code Board} are set to the default state of {@code [}
   * 
   * @param width the width of the {@code Board}
   * @param height the height of the {@code Board}
   * 
   * @return a new empty {@code Board}
   */
  private static Board empty(int width, int height) {
    return new Board(emptyChars(width, height));
  }

  /**
   * Creates a 2D grid of {@code char}s all equal to {@code [},
   * used as an empty char for online transmission reasons
   * 
   * @param width the width of the grid
   * @param height the height of the grid
   * 
   * @return a {@code width}X{@code height} array of {@code [}
   */
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
   * Shuffles characters in the {@code LETTERS} array, using swapping of random {@code char}s {@code LETTERS.length} times
   * 
   * @param rand the random number generator to use for the shuffle.
   * 
   * @return a new array with all the contents of {@code LETTERS}, but in random order
   */
  private static char[] shuffleLetters(Random rand) {
    char[] res = Arrays.copyOf(LETTERS, LETTERS.length);

    for (int i = res.length-1; i > 0; i--) {
      int j = rand.nextInt(i+1);

      // System.out.println("Swapping " + res[i] + " with " + res[j]);
      char c = res[i];
      res[i] = res[j];
      res[j] = c;
      // System.out.println("Now have " + res[i] + " and  " + res[j]);
    }

    return res;
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

    System.out.println("\n----------------------------------------\n");

    System.out.println("Testing shuffling");

    int[] totals = new int[26];
    for (char c : LETTERS) {
      totals[c-65]++;
    }

    System.out.print(Arrays.toString(totals));
  }

  /**
   * An array of all the characters within the game
   */
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