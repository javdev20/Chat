package server;

import java.io.*;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

public class ClientHandler{

    Server server;
    DataOutputStream out;
    DataInputStream in;
    String clientName;
    Socket socket;



    public ClientHandler(Server server, Socket clientSocket) {
        try {
            this.server = server;
            socket = clientSocket;
            in = new DataInputStream(socket.getInputStream());
            out = new DataOutputStream(socket.getOutputStream());

            new Thread(() -> {
                try {
                    server.connectClient(this);
                    while (true) {
                        String message = in.readUTF();
                        server.broadcastMessage(clientName, message);
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }).start();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public String getClientName() {
        return clientName;
    }

    public void setClientName(String clientName) {
        this.clientName = clientName;
    }

    public void sendMessage(String message) {
        try {
            out.writeUTF(message);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
