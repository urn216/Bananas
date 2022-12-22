package code.core;

import java.io.IOException;
import java.net.Socket;

import code.board.Server;
import code.board.TilePiece;
import code.math.IOHelp;
import code.ui.UIController;

public abstract class Client {

  private static volatile Socket sock = new Socket();

  private static volatile byte[][] players = new byte[Server.MAX_PLAYERS][];
  private static volatile int playerNum = -1;

  private static volatile byte[] buffer = new byte[IOHelp.MAX_MESSAGE_LENGTH];
  
  /**
  * Disconnects the client from a server if it is connected to one.
  * Closes the client socket.
  */
  public static void disconnect() {
    try {
      sock.close();
    } catch(IOException e){System.out.println("clientcls "+e);}
    playerNum = -1;
  }
  
  /**
  * Connects the client to a server as chosen by the supplied IPv4 address and port number.
  * 
  * @param ip the IPv4 address to attempt to connect to
  * @param port the port number to access the server through
  */
  public static void connect(String ip, int port) {
    try {
      sock = new Socket(ip, port);
    } catch(IOException e){System.out.println("clientcon "+e);}
    
    //Text output
    new Thread(){
      public void run() {
        try {
          while(true) {
            handleTextOut(System.in.read());
          }
        } catch(IOException e){System.out.println("clientout "+e);}
      }
    }.start();
    
    //Input
    new Thread() {
      public void run() {
        try {
          while (true) {
            handleInput(sock.getInputStream().read());
          }
        } catch(IOException e){System.out.println("clientin  "+e);}
      }
    }.start();
  }
  
  /**
  * Checks to see if the client has a current connection to a server.
  * 
  * @return true if the client is connected.
  */
  public static boolean isConnected() {
    return !sock.isClosed() && sock.isConnected();
  }

  /**
   * Retrieves this Client's understanding of another player on the server.
   * 
   * @param playerNum The player to retrieve the information of
   * 
   * @return a byte array consisting of:
   * {{@code int playerNum}, {@code boolean readyStatus}, {@code String username}}.
   */
  public static byte[] getPlayer(int playerNum) {
    return players[playerNum];
  }

  /**
   * @return The Client's current player number
   */
  public static int getPlayerNum() {
    return playerNum;
  }

  public static void sendReady(boolean ready) {
    try {
      writeToServer(IOHelp.RDY);
    } catch (IOException e) {System.out.println("clientrdy " + e);}
  }

  public static boolean allReady() {
    for (byte[] player : players) {
      if (player != null && player[1] != 49) return false;
    }
    return true;
  }
  
  /**
   * Handles input of data from the connected server.
   * 
   * @param header The first byte of a message, containing the type of data to process.
   * @throws IOException if when reading the data from the server, the connection is interrupted.
   */
  private static void handleInput(int header) throws IOException {
    // System.out.println("Reading Message " + header);
    if (header == IOHelp.MSG) {
      int c = sock.getInputStream().read();
      while(c!=IOHelp.END) {
        System.out.print((char)c);
        c = sock.getInputStream().read();
      }
      return;
    }
    if (header == IOHelp.SET) {
      int setData = IOHelp.decodeTilePos(readBytesFromServer(), 0);
      boolean pile = IOHelp.extractPile(setData);
      Core.getCurrentScene().placeTile(IOHelp.extractPos(setData), new TilePiece(IOHelp.extractLetter(setData), pile), pile);
    }
    if (header == IOHelp.MVE) {
      byte[] bytes = readBytesFromServer();
      int fromData = IOHelp.decodeTilePos(bytes, 0);
      int toData = IOHelp.decodeTilePos(bytes, 2);
      //TODO moves
    }
    if (header == IOHelp.BGN) {
      Core.beginMatch();
      sock.getInputStream().read();
    }
    if (header == IOHelp.USR_REQ) {
      writeToServer(IOHelp.USR_SND, Core.globalSettings.getNickname().getBytes());
      sock.getInputStream().read();
    }
    if (header == IOHelp.USR_SND) {
      byte[] player = readBytesFromServer();
      if (playerNum == -1) playerNum = player[0]-48;
      players[player[0]-48] = player.length == 1 ? null : player;
    }
    if(IOHelp.isExitCondition(header)) {
      System.out.println("Returning to Menu");
      Core.toMenu();
      UIController.displayWarning(0.015, 0.075, "Returning to menu...", IOHelp.decodeExitCondition(header));
    }
  }
  
  /**
   * Handles the outputting of text typed into the terminal by the client.
   * Triggered by entering a newline character,
   * and will read an entire line of input from the user
   * 
   * @param c The first char in the line to read.
   * @throws IOException if when writing to the server, the connection is interrupted.
   */
  private static synchronized void handleTextOut(int c) throws IOException {
    sock.getOutputStream().write(IOHelp.MSG);
    while (c != '\n' && c != '\r') {
      sock.getOutputStream().write(c);
      c = System.in.read();
    }
    if (c == '\r') System.in.read();
    sock.getOutputStream().write(IOHelp.END);
  }

  /**
  * Writes a message out to a server.
  * 
  * @param header The first byte of the output, representing the type of data being written.
  * @param msg The bytes of data to send over to the client.
  * 
  * @throws IOException if there's a problem holding the connection to the server during the writing process
  */
  private static synchronized void writeToServer(byte header, byte... msg) throws IOException {
    sock.getOutputStream().write(header);
    sock.getOutputStream().write(msg);
    sock.getOutputStream().write(IOHelp.END);
    sock.getOutputStream().flush();
  }

  /**
   * Reads an arbitrary number of bytes in from the server until an 'end of message' byte is read.
   * These bytes are packaged into an array (excluding the EOM byte) and returned for processing
   * 
   * @return the bytes read from the server.
   * 
   * @throws IOException if there's a problem holding the connection to the server during the reading process
   */
  private static byte[] readBytesFromServer() throws IOException {
    if (buffer == null) buffer = new byte[IOHelp.MAX_MESSAGE_LENGTH];
    int b = sock.getInputStream().read();
    int i;
    for (i = 0; b != IOHelp.END; i++) {
      if (i < buffer.length) buffer[i] = (byte)b;
      b = sock.getInputStream().read();
    }
    byte[] msg = new byte[i];
    for (i = 0; i < msg.length; i++) msg[i] = buffer[i];
    return msg;
  }
}
