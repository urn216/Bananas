package code.core;

import java.io.IOException;
import java.net.Socket;

import code.math.IOHelp;

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
   * Handles input of data from the connected server.
   * 
   * @param header The first byte of a message, containing the type of data to process.
   * @throws IOException if when reading the data from the server, the connection is interrupted.
   */
  private static void handleInput(int header) throws IOException {
    if (header == IOHelp.MSG) {
      int c = sock.getInputStream().read();
      while(c!=IOHelp.END) {
        System.out.print((char)c);
        c = sock.getInputStream().read();
      }
      return;
    }
    if (header == IOHelp.SET) {
      //TODO sets
    }
    if (header == IOHelp.MVE) {
      //TODO moves
    }
    if (header == IOHelp.BGN) {
      Core.beginMatch();
      if (sock.getInputStream().read() != IOHelp.END) System.out.println("oops");
    }
    if (header == IOHelp.USR_REQ) {
      writeToServer(IOHelp.USR_SND, Core.globalSettings.getNickname().getBytes());
      sock.getInputStream().read();
    }
    if (header == IOHelp.USR_SND) {
      //TODO handle recieved player data
    }
    if(IOHelp.isExitCondition(header)) {
      System.out.println("Returning to Menu");
      Core.toMenu(); //TODO warning
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
  private static void handleTextOut(int c) throws IOException {
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
  * @throws IOException if there's a problem holding the connection to the client during the writing process
  */
  private static void writeToServer(byte header, byte... msg) throws IOException {
    sock.getOutputStream().write(header);
    sock.getOutputStream().write(msg);
    sock.getOutputStream().write(IOHelp.END);
    sock.getOutputStream().flush();
  }
}
