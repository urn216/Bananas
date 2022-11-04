package code.board;

import java.io.InputStreamReader;
import java.io.BufferedReader;
import java.util.HashMap;
import java.io.PrintWriter;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Scanner;

public class Server {
  HashMap<Integer, ClientHandler> sockets = new HashMap<Integer, ClientHandler>();

  public static void main(String[] args) {
    new Server().startup();
  }

  public void broadcast(String msg) {
    System.out.println(msg);
    for (ClientHandler c : sockets.values()) {
      c.send(msg, "SERVER");
    }
  }

  private void startup() {
    ServerSocket sock;
    System.out.println("Creating new Server!");
    try {
      int id = 0;
      sock = new ServerSocket(2556);
      while (true) {
        ClientHandler newClient = new ClientHandler(sock.accept(), sockets, id, this);
        sockets.put(id, newClient);
        newClient.start();
        id++;
        System.out.println("number of active users: " + sockets.size());
      }
    } catch(IOException e){System.out.println(e);}
    System.out.println("Server closed unexpectedly!");
    System.out.println("Press Enter to continue...");
    Scanner scanner = new Scanner(System.in);
    scanner.nextLine();
    scanner.close();
  }
}

class ClientHandler extends Thread {
  final Server s;
  HashMap<Integer, ClientHandler> sockets;
  Socket clientSock;
  PrintWriter out;
  String uid;
  final int id;

  public ClientHandler(Socket clientSock, HashMap<Integer, ClientHandler> sockets, int id, Server s) {
    this.s = s;
    this.clientSock = clientSock;
    this.sockets = sockets;
    this.id = id;
  }

  public void send(String msg, String user) {
    if (user.equals(uid)) return;
    out.println(user + "> " + msg);
  }

  public void run() {
    try {
      System.out.println("Client Connected");
      out = new PrintWriter(clientSock.getOutputStream(), true);
      out.println("Welcome, new user!");
      out.println(getQuote());
      BufferedReader in = new BufferedReader(new InputStreamReader(clientSock.getInputStream()));

      uid = in.readLine();

      s.broadcast("Welcome, " + uid);
      while (true) {
        String msg = in.readLine();
        if (msg.equals("exit")) break;
        System.out.println(uid + "> " + msg);
        for (ClientHandler c : sockets.values()) {
          c.send(msg, uid);
        }
      }
      in.close();
      out.close();
      clientSock.close();
      s.broadcast("User " + uid + " disconnected");
      sockets.remove(id);
    } catch(IOException e){s.broadcast("User " + uid + " disconnected poorly: " + e); sockets.remove(id);}
    System.out.println("number of active users: " + sockets.size());
  }

  private static String getQuote() {
    String[] quotes = {
      "Wow, you're so cool!",
      "[Insert inspiration here]",
      "I bet you could achieve that task if you put your mind to it for once!",
      "Fold your laundry!",
      "[Something funny]",
      "Someone smart probably said something about missing basketball shots or something...",
      "Hang in there!"
    };
    return quotes[(int)(Math.random()*quotes.length)];
  }
}
