package code.board;

import java.io.InputStreamReader;
import java.io.BufferedReader;
import java.util.HashMap;

import code.math.IOHelp;

import java.io.PrintWriter;
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
  
  public static void broadcast(String msg) {
    // System.out.println(msg);
    synchronized (clients) {
      for (int i : clients.keySet()) {
        clients.get(i).send(-1, msg, "SERVER");
      }
    }
  }
  
  public static void broadcast(int id, String msg, String sender) {
    // System.out.println(msg);
    synchronized (clients) {
      for (int i : clients.keySet()) {
        clients.get(i).send(id, msg, sender);
      }
    }
  }
  
  /**
  * Shuts down the server if it is currently running.
  * Closes the server socket and all the client sockets connected to the server at runtime.
  */
  public static void shutdown() {
    try {
      for (int id : clients.keySet()) {removeClient(id);}
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
              clientSock.getOutputStream().write(IOHelp.ERR_GAME_IN_PROGRESS);
              clientSock.close();
              continue;
            }
            int playerNum = lowestFreePlayerNum();
            if (playerNum < 0) {
              clientSock.getOutputStream().write(IOHelp.ERR_LOBBY_FULL);
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
  
  private static int lowestFreePlayerNum() {
    for (int playerNum = 0; playerNum < MAX_PLAYERS; playerNum++) {
      if (!playerExists(playerNum)) return playerNum;
    }
    return -1;
  }
  
  private static boolean playerExists(int playerNum) {
    synchronized (clients) {
      for (ClientHandler ch : clients.values()) {
        if (ch.player.getPlayerNum()==playerNum) return true;
      }
    }
    return false;
  }
  
  public static final void removeClient(int id) {
    synchronized (clients) {
      if (!clients.containsKey(id)) return;
      try {
        clients.remove(id).clientSock.close();
      } catch (IOException e) {System.out.println("clientcls "+e);}
    }
  }
  
  public static final int numActiveUsers() {
    synchronized (clients) {
      return clients.size();
    }
  }
}

class ClientHandler extends Thread {
  public final Socket clientSock;
  public final int id;
  public final Player player;
  
  private PrintWriter out;
  private String username;
  
  public ClientHandler(Socket clientSock, int id, int playerNum) {
    this.clientSock = clientSock;
    this.id = id;
    this.player = new Player(playerNum);
  }
  
  public void send(int id, String msg, String user) {
    if (this.id == id) return;
    out.println(user + "> " + msg);
  }
  
  public void run() {
    try {
      System.out.println("Client Connected");
      clientSock.getOutputStream().write('P');
      out = new PrintWriter(clientSock.getOutputStream(), true);
      out.println("Welcome, new user!");
      out.print("Username: ");
      out.flush();
      BufferedReader in = new BufferedReader(new InputStreamReader(clientSock.getInputStream()));
      
      username = in.readLine();
      
      Server.broadcast("Welcome, " + username);
      while (true) {
        String msg = in.readLine();
        if (msg == null || msg.equals("exit")) break;
        Server.broadcast(id, msg, username);
      }
      Server.broadcast("User " + username + " disconnected");
      Server.removeClient(id);
    } catch(IOException e){Server.broadcast("User " + username + " disconnected poorly: " + e); Server.removeClient(id);}
    System.out.println("number of active users: " + Server.numActiveUsers());
  }
}
