package code.core;

import java.io.IOException;
import java.io.InputStream;
import java.net.InetSocketAddress;
import java.net.Socket;

import code.core.scene.elements.TilePiece;

import code.math.IOHelp;
import code.math.Vector2I;
import code.server.Server;

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
   * 
   * @return {@code -1} if something went wrong, {@code 0} if the connection was cancelled, or {@code 1} if connection was successful
   */
  public static int connect(String ip, int port) {
    sock = new Socket();
    try {
      sock.connect(new InetSocketAddress(ip, port));//TODO reconnect if there's a hiccup
    } catch(IOException e){System.out.println("clientcon "+e); return sock.isClosed() ? 0 : -1;}
    
    //Input
    new Thread() {
      public void run() {
        try {
          while (true) {
            handleInput(sock.getInputStream().read());
          }
        } catch(IOException e){System.out.println("clientin  "+e); Core.toMenu();}
      }
    }.start();

    return 1;
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

  public static int getTotPlayers() {
    int tot = 0;
    for (byte[] player : players) if (player != null) tot++;
    return tot;
  }
  
  /**
  * Toggles this player's ready status for the lobby.
  */
  public static void sendReadyToggle() {
    try {
      writeToServer(IOHelp.RDY);
    } catch (IOException e) {System.out.println("clientrdy " + e);}
  }
  
  public static void doMove(Vector2I pos1, char c1, boolean pile1, Vector2I pos2, char c2, boolean pile2) {
    byte[] move = IOHelp.encodeFromTo(pos1, c1, pile1, pos2, c2, pile2);
    
    try {
      writeToServer(IOHelp.MVE, move);
    } catch (IOException e) {System.out.println("clientmve " + e);}
  }
  
  /**
  * Handles the outputting of a {@code String} typed by the client.
  * 
  * @param msg the {@code String} to send to the Server
  * 
  * @throws IOException if when writing to the server, the connection is interrupted.
  */
  public static synchronized void handleTextOut(String msg) throws IOException {
    sock.getOutputStream().write((IOHelp.MSG + msg + IOHelp.END).getBytes());
  }
  
  /**
  * Handles input of data from the connected server.
  * 
  * @param header The first byte of a message, containing the type of data to process.
  * @throws IOException if when reading the data from the server, the connection is interrupted.
  */
  private static void handleInput(int header) throws IOException {
    // System.out.println("Reading Message " + header);

    InputStream sockIn = sock.getInputStream();

    switch(header) {
      
      case IOHelp.MSG:
      int c = sockIn.read();
      while(c!=IOHelp.END) {
        System.out.print((char)c);
        c = sockIn.read();
      }
      return;
      
      case IOHelp.SET:     handleSetCommand(); return;
      case IOHelp.MVE:     handleMoveCommand(); return;
      case IOHelp.BGN:     Core.beginMatch(); sockIn.read(); return;
      case IOHelp.USR_REQ: handleUserDataRequest(); sockIn.read(); return;
      case IOHelp.USR_SND: handleUserDataInput(); return;

      default:
      if(IOHelp.isExitCondition(header)) {
        System.out.println("Returning to Menu");
        Core.toMenu();
        UIController.displayWarning(0.015, 0.075, "Returning to menu...", IOHelp.decodeExitCondition(header));
      }
    }
  }
  
  /**
  * Reads in and processes a set order, placing a tile on the board.
  * 
  * @throws IOException if there's an issue holding a connection to the server during the reading process
  */
  private static void handleSetCommand() throws IOException {
    byte[] bytes = readBytesFromServer(3);
    int setData = IOHelp.decodeTilePos(bytes, 0);
    
    boolean pile = IOHelp.extractPile(setData);
    char letter = IOHelp.extractLetter(setData);
    
    Core.getCurrentScene().placeTile(IOHelp.extractPos(setData), letter == '[' ? null : new TilePiece(letter, pile), pile ? -1 : bytes[2]);
  }
  
  /**
  * Reads in and processes a move order, swapping two tiles on the board.
  * 
  * @throws IOException if there's an issue holding a connection to the server during the reading process
  */
  private static void handleMoveCommand() throws IOException {
    byte[] bytes = readBytesFromServer(5);
    int fromData = IOHelp.decodeTilePos(bytes, 0);
    int toData = IOHelp.decodeTilePos(bytes, 2);
    
    Vector2I fromPos = IOHelp.extractPos(fromData);
    Vector2I toPos   = IOHelp.extractPos(toData  );
    char fromLetter = IOHelp.extractLetter(fromData);
    char toLetter   = IOHelp.extractLetter(toData  );
    boolean fromPile = IOHelp.extractPile(fromData);
    boolean toPile   = IOHelp.extractPile(toData  );
    
    Core.getCurrentScene().placeTile(fromPos, toLetter   == '[' ? null : new TilePiece(toLetter  , fromPile), fromPile ? -1 : bytes[4]);
    Core.getCurrentScene().placeTile(toPos  , fromLetter == '[' ? null : new TilePiece(fromLetter, toPile  ), toPile   ? -1 : bytes[4]);
    if (bytes[4] == playerNum) Core.getCurrentScene().selectTile(fromPos, fromPile);
  }
  
  /**
  * Delivers the client's username to the server.
  * 
  * @throws IOException if there's an issue holding a connection to the server during the writing process
  */
  private static void handleUserDataRequest() throws IOException {
    writeToServer(IOHelp.USR_SND, Core.GLOBAL_SETTINGS.getStringSetting("nickname").getBytes());
  }
  
  /**
  * Reads in and processes data sent by the server about a player.
  * 
  * @throws IOException if there's an issue holding a connection to the server during the reading process
  */
  private static void handleUserDataInput() throws IOException {
    byte[] player = readBytesFromServer(0);
    if (playerNum == -1) playerNum = player[0]-48;
    players[player[0]-48] = player.length == 1 ? null : player;
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
  * @param required Establishes a required number of bytes to read, regardless of whether or not EOM is detected.
  * 
  * @return the bytes read from the server.
  * 
  * @throws IOException if there's a problem holding the connection to the server during the reading process
  */
  private static byte[] readBytesFromServer(int required) throws IOException {
    if (buffer == null) buffer = new byte[IOHelp.MAX_MESSAGE_LENGTH];

    InputStream sockIn = sock.getInputStream();

    int b = sockIn.read();
    int i;
    for (i = 0; b != IOHelp.END || i < required; i++) {
      if (i < buffer.length) buffer[i] = (byte)b;
      b = sockIn.read();
    }
    byte[] msg = new byte[i];
    for (i = 0; i < msg.length; i++) msg[i] = buffer[i];
    
    // System.out.println("Read in " + msg.length + " bytes from server");
    
    return msg;
  }
}
