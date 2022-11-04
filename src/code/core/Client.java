package code.core;

import java.io.PrintWriter;
import java.io.InputStreamReader;
import java.io.BufferedReader;
import java.util.Scanner;
import java.io.IOException;
import java.net.Socket;

public class Client {

  public static void main(String[] args) {
    try {
      // Socket clientSock = new Socket("122.58.44.177", 2556);
      Socket clientSock = new Socket("localhost", 2556);
      BufferedReader in = new BufferedReader(new InputStreamReader(clientSock.getInputStream()));
      // Scanner in = new Scanner(clientSock.getInputStream());
      new OutGoing(clientSock).start();
      while (OutGoing.isActive) {
        System.out.print((char)in.read());
        // System.out.println(in.nextLine());
      }

      in.close();
      clientSock.close();
    } catch(IOException e){System.out.println(e);}
    Scanner scanner = new Scanner(System.in);
    scanner.nextLine();
    scanner.close();
  }
}

class OutGoing extends Thread {
  public static boolean isActive = true;
  Socket clientSock;

  public OutGoing(Socket clientSock) {
    this.clientSock = clientSock;
  }

  public void run() {
    try {
      PrintWriter out = new PrintWriter(clientSock.getOutputStream(), true);
      Scanner scanner = new Scanner(System.in);
      System.out.print("Username: ");
      while(true) {
        String msg = scanner.nextLine();
        out.println(msg);
        if (msg.equals("exit")) break;
      }
      isActive = false;
      out.close();
      scanner.close();
      clientSock.close();
    } catch(IOException e){System.out.println(e);}
  }
}
