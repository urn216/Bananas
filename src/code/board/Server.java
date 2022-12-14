package code.board;

import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.BufferedReader;
import java.util.HashMap;

import code.math.IOHelp;

import java.io.IOException;
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
    String user = sender == null ? "SERVER" : sender.getUserName();
    // System.out.println(sender + "> " + msg);
    synchronized (clients) {
      for (int i : clients.keySet()) {
        clients.get(i).send(id, msg, user);
      }
    }
  }
  
  /**
  * Shuts down the server if it is currently running.
  * Closes the server socket and all the client sockets connected to the server at runtime.
  */
  public static void shutdown() {
    try {
      for (int id : clients.keySet().toArray(new Integer[] {})) {removeClient(id, IOHelp.ERR_SERVER_CLOSED);}
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
          for(int id = 0; ; id++) {
            Socket clientSock = sock.accept();
            if (!lobby) {
              writeToClient(clientSock.getOutputStream(), IOHelp.ERR_GAME_IN_PROGRESS);
              clientSock.close();
              continue;
            }
            int playerNum = lowestFreePlayerNum();
            if (playerNum < 0) {
              writeToClient(clientSock.getOutputStream(), IOHelp.ERR_LOBBY_FULL);
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

  public static void writeToClient(OutputStream clientOut, byte... msg) throws IOException {
    clientOut.write(msg);
    clientOut.write(IOHelp.END);
    clientOut.flush();
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

    try {
      writeToClient(ch.clientSock.getOutputStream(), message);
      ch.clientSock.close();
      ch.interrupt();
    } catch (IOException e) {System.out.println("serverrmv "+e);}
  }
  
  /**
  * Gives the number of active clients connected to the server
  * 
  * @return the number of active users
  */
  public static final int numActiveUsers() {
    synchronized (clients) {return clients.size();}
  }
}

class ClientHandler extends Thread {
  public final Socket clientSock;
  public final int id;
  public final Player player;
  
  private String username;
  
  public ClientHandler(Socket clientSock, int id, int playerNum) {
    this.clientSock = clientSock;
    this.id = id;
    this.player = new Player(playerNum);
  }
  
  public void send(int id, String msg, String user) {
    if (this.id == id) return;
    byte[] res = IOHelp.toBytes(user + "> " + msg + "\n", 1);
    res[0] = IOHelp.MSG;
    try {
      Server.writeToClient(clientSock.getOutputStream(), res);
    } catch (IOException e) {System.out.println("clientmsg "+e);}
  }

  /**
   * @return This client's chosen username
   */
  public String getUserName() {return username;}
  
  public void run() {
    try {
      System.out.println("Client Connected");
      BufferedReader in = new BufferedReader(new InputStreamReader(clientSock.getInputStream()));
      
      username = in.readLine();
      
      Server.broadcast("Welcome, " + username);
      while (true) {
        String msg = in.readLine();
        if (msg == null || msg.equals("exit")) break;
        Server.broadcast(id, msg);
      }
      Server.broadcast("User " + username + " disconnected");
      Server.removeClient(id, IOHelp.ERR_KICKED);
    } catch(IOException e){Server.broadcast("User " + username + " disconnected poorly: " + e); Server.removeClient(id, IOHelp.ERR_KICKED);}
    System.out.println("number of active users: " + Server.numActiveUsers());
  }
}
