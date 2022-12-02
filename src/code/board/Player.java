package code.board;

public class Player {
  private Board board = new Board();
  private int playerNum = -1;

  public Player(int playerNum) {
    this.playerNum = playerNum;
  }

  public int getPlayerNum() {return playerNum;}

  public void setPlayerNum(int playerNum) {this.playerNum = playerNum;}

  public final Board getBoard() {return board;}

  public final void setBoard(Board board) {this.board = board;}
}
