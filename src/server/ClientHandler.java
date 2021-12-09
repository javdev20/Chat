package server;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Iterator;

public class ClientHandler implements Runnable {
    BufferedReader in;
    Socket socket;
    static ArrayList clientOutputStreams;

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
