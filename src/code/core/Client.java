package code.core;

import java.io.IOException;
import java.net.Socket;

public abstract class Client {
  public static volatile Socket sock = new Socket();
  
  /**
  * Disconnects the client from a server if it is connected to one.
  * Closes the client socket.
  */
  public static void disconnect() {
    try {
      sock.close();
    } catch(IOException e){System.out.println("clientcls "+e);}
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
    
    new Thread(){
      public void run() {
        try {
          while(true) {
            System.in.transferTo(sock.getOutputStream());
          }
        } catch(IOException e){System.out.println("clientout "+e);}
      }
    }.start();
    
    new Thread() {
      public void run() {
        try {
          while (true) {
            sock.getInputStream().transferTo(System.out);
          }
        } catch(IOException e){System.out.println("clientin  "+e);}
      }
    }.start();
  }

  /**
   * Checks to see if the client has a current connection to a server.
   * 
   * @return True if the client is connected
   */
  public static boolean isConnected() {
    return !sock.isClosed() && sock.isConnected();
  }
}
