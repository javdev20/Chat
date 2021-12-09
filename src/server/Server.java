package server;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Iterator;


public class Server {
    ArrayList clientOutputStreams;
    
    public class ClientHandler implements Runnable {
        BufferedReader in;
        Socket socket;
        
        public ClientHandler(Socket clientSocket) {
            try {
                socket = clientSocket;
                InputStreamReader isReader = new InputStreamReader(socket.getInputStream());
                in = new BufferedReader(isReader);
                
            } catch (Exception ex) { ex.printStackTrace(); }
        }
        
        public void run() {
            String message;
            try {
                while ((message = in.readLine()) != null) {
                    // регистрация
                    if (message.startsWith("/signup ")) {
                        String[] tokens = message.split(" ");
                        int result = AuthService.addUser(tokens[1], tokens[2], tokens[3]);
                        if (result > 0) {
                            tellEveryone("Successful registration");
                        } else {
                            tellEveryone("Registration failed");
                        }
                    } else {
                        System.out.println("read " + message);
                        tellEveryone(message);
                    }
                }
            } catch (Exception ex) { ex.printStackTrace(); }
        }
    }
    
    public static void main(String[] args) {
        new Server().go();
    }
    
    public void go() {
        clientOutputStreams = new ArrayList();
        try {
            ServerSocket serverSocket = new ServerSocket(5000);
            while(true) {
                Socket clientSocket = serverSocket.accept();
                PrintWriter out = new PrintWriter(clientSocket.getOutputStream());
                clientOutputStreams.add(out);
                
                Thread t = new Thread(new ClientHandler(clientSocket));
                t.start();
                System.out.println("got a connection");

                // connection to database
                AuthService.connect();

            }
        } catch (Exception ex) { ex.printStackTrace(); }
    }
    
    public void tellEveryone(String message) {
        Iterator it = clientOutputStreams.iterator();
        while (it.hasNext()) {
            try {
                PrintWriter out = (PrintWriter) it.next();
                out.println(message);
                out.flush();
            } catch (Exception ex) { ex.printStackTrace(); }
        }
    }
}
