package code.board;

import java.util.HashMap;

import code.math.IOHelp;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.ServerSocket;
import java.net.Socket;

public abstract class Server {
  
  private static final int MAX_PLAYERS = 8;
  
  private static volatile ServerSocket sock;
  private static volatile HashMap<Integer, ClientHandler> clients = new HashMap<Integer, ClientHandler>();
  
  private static volatile boolean lobby = true;
  
  static {
    try {
      sock = new ServerSocket();
    } catch(IOException e){System.out.println(e);}
  }
  
  /**
  * Broadcasts a message to all players from the faceless server
  * 
  * @param msg The message to deliver
  */
  public static void broadcast(String msg) {
    broadcast(-1, msg);
  }
  
  /**
  * Broadcasts a message to all players from a given player id
  * 
  * @param id The id of the player who sent the message
  * @param msg The message to deliver
  */
  public static void broadcast(int id, String msg) {
    ClientHandler sender = clients.get(id);
    msg = (sender == null ? "SERVER" : sender.getUserName()) + "> " + msg + "\n";
    // System.out.print(msg);
    synchronized (clients) {
      for (int i : clients.keySet()) {
        clients.get(i).send(id, msg);
      }
    }
  }
  
  /**
  * Shuts down the server if it is currently running.
  * Closes the server socket and all the client sockets connected to the server at runtime.
  */
  public static void shutdown() {
    try {
      for (int id : clients.keySet().toArray(new Integer[] {})) {removeClient(id, IOHelp.EXIT_SERVER_CLOSED);}
      sock.close();
    } catch(IOException e){System.out.println("servercls "+e);}
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
    
    //Lobby
    //
    //Attempts to add new players to the lobby until 
    //game is started or the server closes.
    //Max of 8 players
    new Thread(){
      public void run() {
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
            
            ClientHandler newClient = new ClientHandler(clientSock, id, playerNum);
            
            synchronized (clients) {clients.put(id, newClient);}
            newClient.start();
            System.out.println("number of active users: " + numActiveUsers());
          }
        }
        catch (IOException e) {System.out.println("serverlby "+e);}
      }
    }.start();
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
    synchronized (clients) {
      for (ClientHandler ch : clients.values()) {
        if (ch.player.getPlayerNum()==playerNum) return true;
      }
    }
    return false;
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
  
  public static void writeToClient(OutputStream clientOut, byte header, byte... msg) throws IOException {
    clientOut.write(header);
    clientOut.write(msg);
    clientOut.write(IOHelp.END);
    clientOut.flush();
  }
  
  public static byte[] readFromClient(InputStream clientIn, int header) throws IOException {
    if (header == -1) return new byte[] {'e','x','i','t'};
    if (header == IOHelp.MSG) {
      byte[] buffer = new byte[1024];
      int c = clientIn.read();
      int i;
      for (i = 0; c != IOHelp.END; i++) {
        buffer[i] = (byte)c;
        c = clientIn.read();
      }
      byte[] msg = new byte[i];
      for (i = 0; i < msg.length; i++) msg[i] = buffer[i];
      return msg;
    }
    return new byte[] {};
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
  
  private String username;
  
  /**
   * Constructs a client handler with corresponding client socket, unique id, and player number.
   * 
   * @param clientSock The socket holding the connection to the client.
   * @param id This client's unique identifier.
   * @param playerNum The number assigned to this player within the context of the game ('Player 1', for example)
   */
  public ClientHandler(Socket clientSock, int id, int playerNum) {
    this.clientSock = clientSock;
    this.id = id;
    this.player = new Player(playerNum);
  }
  
  /**
   * Delivers a message to the client held by this handler
   * 
   * @param id the id of the client which sent the message
   * @param msg The message to send
   */
  public void send(int id, String msg) {
    if (this.id == id) return;
    try {
      Server.writeToClient(clientSock.getOutputStream(), IOHelp.MSG, IOHelp.toBytes(msg));
    } catch (IOException e) {System.out.println("clientmsg "+e);}
  }
  
  /**
  * @return This client's chosen username
  */
  public String getUserName() {return username;}
  
  @Override
  public void run() {
    try {
      System.out.println("Client Connected");
      
      username = new String(Server.readFromClient(clientSock.getInputStream(), clientSock.getInputStream().read()));
      
      Server.broadcast("Welcome, " + username);
      while (true) {
        String msg = new String(Server.readFromClient(clientSock.getInputStream(), clientSock.getInputStream().read()));
        if (msg == null || msg.equals("exit")) break;
        Server.broadcast(id, msg);
      }
      Server.removeClient(id, IOHelp.EXIT_DISCONNECTED);
      Server.broadcast("User " + username + " disconnected");
    } catch(IOException e){Server.removeClient(id, IOHelp.EXIT_DISCONNECTED); Server.broadcast("User " + username + " disconnected poorly: " + e);}
    System.out.println("number of active users: " + Server.numActiveUsers());
  }
}
