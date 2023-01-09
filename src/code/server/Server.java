package code.server;

import java.util.HashMap;

import code.math.IOHelp;
import code.math.Vector2I;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.ServerSocket;
import java.net.Socket;

public abstract class Server {
  
  public static final int MAX_PLAYERS = 8;
  
  private static volatile ServerSocket sock;
  private static volatile HashMap<Integer, ClientHandler> clients = new HashMap<Integer, ClientHandler>();
  
  private static volatile Board pile = GenerateBoard.empty();
  
  private static volatile byte[] buffer = new byte[IOHelp.MAX_MESSAGE_LENGTH];
  
  private static volatile boolean lobby = true;
  
  static {
    try {
      sock = new ServerSocket();
    } catch(IOException e){System.out.println(e);}
  }
  
  /**
  * Checks to see if the server is currently running.
  * 
  * @return true if the server is bound and open
  */
  public static boolean isRunning() {return sock.isBound() && !sock.isClosed();}
  
  public static Board getPile() {return pile;}
  
  /**
  * Broadcasts a message to all players from the faceless server.
  * 
  * @param msg The message to deliver
  */
  public static void broadcastText(String msg) {
    broadcastText(-1, msg);
  }
  
  /**
  * Broadcasts a message to all players from a given player id.
  * 
  * @param id The id of the player who sent the message
  * @param msg The message to deliver
  */
  public static void broadcastText(int id, String msg) {
    ClientHandler sender = clients.get(id);
    byte[] data = ((sender == null ? "SERVER" : sender.getUserName()) + "> " + msg + "\n").getBytes();
    try {
      synchronized (clients) {
        for (ClientHandler ch : clients.values()) {
          if (ch.id != id)
          writeToClient(ch.clientSock.getOutputStream(), IOHelp.MSG, data);
        }
      }
    } catch (IOException e) {System.out.println("broadcast error: " + e);}
  }
  
  /**
  * Sends a message consisiting of bytes to every currently connected client.
  * 
  * @param header The header of the desired message, indicating what the topic of the message is
  * @param msg The message to send
  */
  public static void broadcastBytes(byte header, byte[] msg) {
    try {
      synchronized (clients) {
        for (ClientHandler ch : clients.values()) {
          writeToClient(ch.clientSock.getOutputStream(), header, msg);
        }
      }
    } catch (IOException e) {System.out.println("broadcast error: " + e);}
  }
  
  /**
  * Shuts down the server if it is currently running.
  * Closes the server socket and all the client sockets connected to the server at runtime.
  */
  public static void shutdown() {
    try {
      synchronized (clients) {
        if (!clients.isEmpty()) for (int id : clients.keySet().toArray(new Integer[0])) {removeClient(id, IOHelp.EXIT_SERVER_CLOSED);}
      }
      sock.close();
    } catch(IOException e){System.out.println("servercls "+e);}
    synchronized (clients) {clients.clear();}
  }
  
  /**
  * Starts a fresh server on a given port to host a game through.
  * Creates a lobby that will wait for connections from potential clients.
  * 
  * @param port the port number to host the server on.
  */
  public static void startup(int port) {
    try {
      sock = new ServerSocket(port);
    } catch(IOException e){System.out.println("serveropn "+e);}
    System.out.println("\nCreating new Server!");
    
    lobby = true;
    
    //Lobby
    //
    //Attempts to add new players to the lobby until 
    //game is started or the server closes.
    //Max of 8 players
    new Thread(){
      public void run() {
        lobby();
      }
    }.start();
  }
  
  /**
  * Runs a lobby, handling connections to the server while open, and denying entry while closed
  */
  private static void lobby() {
    try {
      for(int id = 0;; id++) {
        Socket clientSock = sock.accept();
        if (!lobby) {
          writeToClient(clientSock.getOutputStream(), IOHelp.EXIT_GAME_IN_PROGRESS);
          clientSock.close();
          continue;
        }
        int playerNum = lowestFreePlayerNum();
        if (playerNum < 0) {
          writeToClient(clientSock.getOutputStream(), IOHelp.EXIT_LOBBY_FULL);
          clientSock.close();
          continue;
        }
        
        writeToClient(clientSock.getOutputStream(), IOHelp.USR_REQ);
        
        if (clientSock.getInputStream().read() != IOHelp.USR_SND) {
          writeToClient(clientSock.getOutputStream(), IOHelp.EXIT_KICKED);
          clientSock.close();
          continue;
        }
        
        String username = new String(readBytesFromClient(clientSock.getInputStream(), 0));
        if (username.equals("")) username = "Player " + (playerNum+1);
        
        ClientHandler newClient = new ClientHandler(clientSock, id, playerNum, username);
        
        synchronized (clients) {clients.put(id, newClient);}
        newClient.start();
        System.out.println("number of active users: " + numActiveUsers());
        
        broadcastBytes(IOHelp.USR_SND, getUserInfo(playerNum));
        for (int i = 0; i < Server.MAX_PLAYERS; i++) writeToClient(newClient.clientSock.getOutputStream(), IOHelp.USR_SND, getUserInfo(i));
      }
    }
    catch (IOException e) {System.out.println("serverlby "+e);}
  }
  
  public static void beginMatch() {
    lobby = false;
    
    pile = GenerateBoard.random();
    
    synchronized (clients) {
      for (int i : clients.keySet()) {
        ClientHandler ch = clients.get(i);
        try {
          writeToClient(ch.clientSock.getOutputStream(), IOHelp.BGN);
          ch.handleRefreshBoard(-1);
        } catch (IOException e) {System.out.println("serverbgn "+e);}
      }
    }
  }
  
  /**
  * finds the lowest valued free position in the player roster,
  * or negative one if no free spaces are available
  * 
  * @return the lowest free player slot for a client to join to
  */
  private static int lowestFreePlayerNum() {
    for (int playerNum = 0; playerNum < MAX_PLAYERS; playerNum++) {
      if (!playerExists(playerNum)) return playerNum;
    }
    return -1;
  }
  
  /**
  * Checks if player-x exists, where x is a number less than MAX_PLAYERS
  * 
  * @param playerNum the player number to check for
  * @return true if this player is active
  */
  private static boolean playerExists(int playerNum) {
    if (getPlayer(playerNum) != null) return true;
    return false;
  }
  
  /**
  * Retrieves player-x if they exist, where x is a number less than MAX_PLAYERS
  * 
  * @param playerNum the player number to check for
  * @return the player of this position in the game, or null if they do not exist
  */
  public static final Player getPlayer(int playerNum) {
    synchronized (clients) {
      for (ClientHandler ch : clients.values()) 
      if (ch.player.getPlayerNum()==playerNum) 
      return ch.player;
    }
    return null;
  }
  
  /**
  * Removes a client from the server
  * 
  * @param id the client's id
  * @param message the reason for the client's removal
  */
  public static final void removeClient(int id, byte message) {
    ClientHandler ch = null;
    synchronized (clients) {
      if (!clients.containsKey(id)) return;
      ch = clients.remove(id);
    }
    
    if (message != IOHelp.EXIT_DISCONNECTED) try {
      writeToClient(ch.clientSock.getOutputStream(), message);
      ch.clientSock.close();
    } catch (IOException e) {System.out.println("serverrmv "+e);}
    
    ch.interrupt();
  }
  
  /**
  * Gives the number of active clients connected to the server
  * 
  * @return the number of active users
  */
  public static final int numActiveUsers() {
    synchronized (clients) {return clients.size();}
  }
  
  public static boolean allReady() {
    for (ClientHandler ch : clients.values()) {
      if (!ch.player.isReady()) return false;
    }
    return true;
  }
  
  /**
  * Writes a message out to a client.
  * 
  * @param clientOut The OutputStream belonging to the client.
  * @param header The first byte of the output, representing the type of data being written.
  * @param msg The bytes of data to send over to the client.
  * 
  * @throws IOException if there's a problem holding the connection to the client during the writing process
  */
  public static synchronized void writeToClient(OutputStream clientOut, byte header, byte... msg) throws IOException {
    // System.out.println("Sending message " + header + " with " + msg.length + " bytes");
    
    clientOut.write(header);
    clientOut.write(msg);
    clientOut.write(IOHelp.END);
    clientOut.flush();
  }
  
  /**
  * Reads bytes in from a client into an array.
  * 
  * @param clientIn The InputStream belonging to the client.
  * @param required Establishes a required number of bytes to read, regardless of whether or not EOM is detected.
  * 
  * @return the bytes read in from the client
  * @throws IOException if there's a problem holding the connection to the client during the reading process
  */
  public static synchronized byte[] readBytesFromClient(InputStream clientIn, int required) throws IOException {
    if (buffer == null) buffer = new byte[IOHelp.MAX_MESSAGE_LENGTH];
    int b = clientIn.read();
    int i;
    for (i = 0; b != IOHelp.END || i < required; i++) {
      if (i < buffer.length) buffer[i] = (byte)b;
      b = clientIn.read();
    }
    byte[] msg = new byte[i];
    for (i = 0; i < msg.length; i++) msg[i] = buffer[i];
    return msg;
  }
  
  /**
  * Gathers information about a player and packages it into a byte array consiting of:
  * {{@code int playerNum}, {@code boolean readyStatus}, {@code String username}}.
  * 
  * @param playerNum The desired player to collect the info of
  * 
  * @return a byte array containing either the desired information or just playerNum if the player does not exist
  */
  public static byte[] getUserInfo(int playerNum) {
    Player player = getPlayer(playerNum);
    
    if (player==null) return new byte[]{(byte)(playerNum+48)};
    
    return getUserInfo(player);
  }
  
  /**
  * Gathers information about a player and packages it into a byte array consiting of:
  * {{@code int playerNum}, {@code boolean readyStatus}, {@code String username}}.
  * 
  * @param player The desired player to collect the info of
  * 
  * @return a byte array containing the desired information
  */
  public static byte[] getUserInfo(Player player) {
    int playerNum = player.getPlayerNum();
    
    byte[] username = player.getUsername().getBytes();
    int ready = player.isReady() ? 49 : 48;
    
    byte[] userInfo = new byte[username.length+2];
    userInfo[0] = (byte)(playerNum+48);
    userInfo[1] = (byte)ready;
    for (int i = 0; i < username.length; i++) userInfo[i+2] = username[i];
    
    return userInfo;
  }
}

//----------------------------------------------------------------

/**
* Handles reading from and writing to clients attatched to the server
*/
class ClientHandler extends Thread {
  /**
  * The socket connection to the client
  */
  public final Socket clientSock;
  /**
  * The unique identifier for this client
  */
  public final int id;
  /**
  * The player object associated with this client
  */
  public final Player player;
  
  /**
  * Constructs a client handler with corresponding client socket, unique id, and player number.
  * 
  * @param clientSock The socket holding the connection to the client.
  * @param id This client's unique identifier.
  * @param playerNum The number assigned to this player within the context of the game ('Player 1', for example)
  */
  public ClientHandler(Socket clientSock, int id, int playerNum, String username) {
    this.clientSock = clientSock;
    this.id = id;
    this.player = new Player(playerNum, username);
  }
  
  /**
  * @return This client's chosen username
  */
  public String getUserName() {return player.getUsername();}
  
  /**
  * Reads bytes in from a client, and processes them accordingly
  * 
  * @param clientSock The Socket through which the client is connected
  * @param header The first byte of the input, representing the type of data being read.
  * 
  * @return TBD
  * @throws IOException if there's a problem holding the connection to the client during the reading process
  */
  private void handleInput(int header) throws IOException {
    switch(header) {
      case IOHelp.MSG: Server.broadcastText(id, textInput()); return;
      case IOHelp.RDY: handleReady(); readByte(); return;
      case IOHelp.USR_REQ: handleUserInfoRequest(readByte()); readByte(); return;
      case IOHelp.SET: handleRefreshBoard(readByte()); readByte(); return;
      case IOHelp.MVE: handleMoveCommand(); return;
      
      default: 
      //Command not recognised, clear the buffer
      Server.readBytesFromClient(clientSock.getInputStream(), 0);
    }
  }
  
  /**
  * Reads bytes in from a client, and processes them as text.
  * 
  * @return the String read in from the client
  * @throws IOException if there's a problem holding the connection to the client during the reading process
  */
  private String textInput() throws IOException {
    return new String(Server.readBytesFromClient(clientSock.getInputStream(), 0));
  }
  
  private int readByte() throws IOException {
    return clientSock.getInputStream().read();
  }
  
  private void handleReady() {
    player.setReady(!player.isReady());
    Server.broadcastBytes(IOHelp.USR_SND, Server.getUserInfo(player));
  }
  
  /**
  * Processes a request by a client for information about another player.
  * 
  * @throws IOException if there's a problem holding the connection to the client during the writing process
  */
  private void handleUserInfoRequest(int playerNum) throws IOException {
    Server.writeToClient(clientSock.getOutputStream(), IOHelp.USR_SND, Server.getUserInfo(playerNum));
  }
  
  /**
  * Gathers up-to-date information about the desired player's board, and sends it to this client.
  * 
  * @param playerNum The player to gather board info from. If this is not a valid player, central pile info will be retrieved.
  * 
  * @throws IOException if there's a problem holding the connection to the client during the writing process
  */
  public void handleRefreshBoard(int playerNum) throws IOException {
    refreshBoard(Server.getPlayer(playerNum));
  }
  
  /**
  * Gathers up-to-date information about the desired player's board, and sends it to this client.
  * 
  * @param player The player to gather board info from. If this is not a valid player, central pile info will be retrieved.
  * 
  * @throws IOException if there's a problem holding the connection to the client during the writing process
  */
  private void refreshBoard(Player player) throws IOException {
    Board board = player == null ? Server.getPile() : player.getBoard();
    
    for (int x = 0; x < board.getMap().length; x++) {
      for (int y = 0; y < board.getMap()[x].length; y++) {
        char c = board.getMap()[x][y];
        Server.writeToClient(clientSock.getOutputStream(), IOHelp.SET, IOHelp.encodeTilePos(x, y, c, player==null));
      }
    }
  }
  
  /**
  * Reads in and processes a move order, swapping two tiles on the board.
  * First ensures the client has correct information, if not, update it.
  * Then echoes the move to the client, assuming everything went fine.
  * 
  * @throws IOException if there's an issue holding a connection to the server during the reading/writing process
  */
  private void handleMoveCommand() throws IOException {
    byte[] bytes = Server.readBytesFromClient(clientSock.getInputStream(), 4);
    int fromData = IOHelp.decodeTilePos(bytes, 0);
    int toData = IOHelp.decodeTilePos(bytes, 2);
    
    Vector2I fromPos = IOHelp.extractPos(fromData);
    Vector2I toPos   = IOHelp.extractPos(toData  );
    boolean fromPile = IOHelp.extractPile(fromData);
    boolean toPile   = IOHelp.extractPile(toData  );
    char fromLetter = IOHelp.extractLetter(fromData);
    char toLetter   = IOHelp.extractLetter(toData  );
    
    Board fromBoard = fromPile ? Server.getPile() : player.getBoard();
    Board toBoard   = toPile   ? Server.getPile() : player.getBoard();
    
    if (!fromBoard.validate(fromPos, fromLetter) || !toBoard.validate(toPos, toLetter)) {
      refreshBoard(player);
      refreshBoard(null);
      return;
    }
    
    fromBoard.setPiece(fromPos, toLetter);
    toBoard.setPiece  (toPos, fromLetter);
    Server.broadcastBytes(IOHelp.MVE, bytes);
  }
  
  @Override
  public void run() {
    try {
      System.out.println("Client Connected");
      
      Server.broadcastText("Welcome, " + player.getUsername());
      while (true) {
        int header = readByte();
        if (header == -1) break;
        handleInput(header);
      }
      Server.removeClient(id, IOHelp.EXIT_DISCONNECTED);
      Server.broadcastText("User " + player.getUsername() + " disconnected");
    } catch(IOException e){
      Server.removeClient(id, IOHelp.EXIT_DISCONNECTED); 
      Server.broadcastText("User " + player.getUsername() + " disconnected poorly: " + e);
    }
    Server.broadcastBytes(IOHelp.USR_SND, Server.getUserInfo(player.getPlayerNum()));
    System.out.println("number of active users: " + Server.numActiveUsers());
  }
}
