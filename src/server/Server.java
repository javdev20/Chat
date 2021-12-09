package server;


import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;


public class Server {

    public static void main(String[] args) {
        new Server().go();
    }

    public void go() {
        ClientHandler.clientOutputStreams = new ArrayList();
        try {
            ServerSocket serverSocket = new ServerSocket(5000);
            while(true) {
                Socket clientSocket = serverSocket.accept();
                PrintWriter out = new PrintWriter(clientSocket.getOutputStream());
                ClientHandler.clientOutputStreams.add(out);

                Thread t = new Thread(new ClientHandler(clientSocket));
                t.start();
                System.out.println("got a connection");

                // connection to database
                AuthService.connect();

            }
        } catch (Exception ex) { ex.printStackTrace(); }
    }

}
