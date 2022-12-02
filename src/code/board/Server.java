package code.board;

import java.io.InputStreamReader;
import java.io.BufferedReader;
import java.util.HashMap;
import java.io.PrintWriter;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

public abstract class Server {
  private static volatile ServerSocket sock;
  public static volatile HashMap<Integer, ClientHandler> clients = new HashMap<Integer, ClientHandler>();
  
  static {
    try {
      sock = new ServerSocket();
    } catch(IOException e){System.out.println(e);}
  }
  
  public static void broadcast(String msg) {
    // System.out.println(msg);
    for (int i : clients.keySet()) {
      clients.get(i).send(-1, msg, "SERVER");
    }
  }
  
  /**
  * Shuts down the server if it is currently running.
  * Closes the server socket and all the client sockets connected to the server at runtime.
  */
  public static void shutdown() {
    try {
      for (ClientHandler ch : clients.values()) {ch.clientSock.close();}
      clients.clear();
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
    
    new Thread(){
      public void run() {
        try {
          for(int id = 0; true; id++) {
            for (int i = 0; i < 8; i++) {
              boolean found = false;
              for (ClientHandler ch : Server.clients.values()) {
                if (ch.player.getPlayerNum()==i) {found = true; break;}
              }
              if (found) continue;
              ClientHandler newClient = new ClientHandler(sock.accept(), id, i);
              clients.put(id, newClient);
              newClient.start();
              System.out.println("number of active users: " + clients.size());
              break;
            }
          }
        }
        catch (IOException e) {System.out.println("serverlby "+e);}
      }
    }.start();
  }
}

class ClientHandler extends Thread {
  public final Socket clientSock;
  public final int id;
  public final Player player;
  
  PrintWriter out;
  String username;
  
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
        for (ClientHandler c : Server.clients.values()) {
          c.send(id, msg, username);
        }
      }
      clientSock.close();
      Server.broadcast("User " + username + " disconnected");
      Server.clients.remove(id);
    } catch(IOException e){Server.broadcast("User " + username + " disconnected poorly: " + e); Server.clients.remove(id);}
    System.out.println("number of active users: " + Server.clients.size());
  }
}
