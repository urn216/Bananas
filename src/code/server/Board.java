package code.server;

import code.math.Vector2I;

class Board {
  private final char[][] map;

  protected Board(char[][] map) {
    this.map = map;
  }

  public char[][] getMap() {
    return map;
  }

  public synchronized boolean validate(Vector2I pos, char c) {
    return map[pos.x][pos.y] == c;
  }

  public synchronized void setPiece(Vector2I pos, char c) {
    map[pos.x][pos.y] = c;
  }
}
