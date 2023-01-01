package code.server;

class Player {

  private final String username;
  private final int playerNum;

  private Board board = GenerateBoard.empty();
  private boolean ready = false;

  public Player(int playerNum, String username) {
    this.playerNum = playerNum;
    this.username = username;
  }

  public String getUsername() {return username;}

  public int getPlayerNum() {return playerNum;}

  public final Board getBoard() {return board;}

  public final void setBoard(Board board) {this.board = board;}

  public boolean isReady() {return ready;}

  public void setReady(boolean ready) {this.ready = ready;}
}
