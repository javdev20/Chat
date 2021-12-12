package server;

import java.io.*;
import java.net.Socket;

public class ClientHandler{
    Server server;
    Socket socket;
    private DataOutputStream out;
    private DataInputStream in;


    public ClientHandler(Server server, Socket clientSocket) {
        try {
            this.server = server;
            socket = clientSocket;
            in = new DataInputStream(socket.getInputStream());
            out = new DataOutputStream(socket.getOutputStream());
            server.connect(ClientHandler.this);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    public void sendMessage(String message) {
        try {
            out.writeUTF(message);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
