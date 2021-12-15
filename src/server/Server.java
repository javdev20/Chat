package server;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

public class Server {
    private List<ClientHandler> users;
    private static int newClientIndex = 1;

    public Server() {
        users = new ArrayList<>();

        try {
            AuthService.connect();
            ServerSocket serverSocket = new ServerSocket(7007);
            System.out.println("Server started");

            while(true) {
                Socket socket = serverSocket.accept();
                new ClientHandler(this, socket);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void connectClient(ClientHandler client) {
        String clientName;
        users.add(client);
        System.out.println(users.size());
        clientName = "Client #" + newClientIndex;
        newClientIndex++;
        System.out.println(String.format("[%s] connected", clientName));
        client.setClientName(clientName);
        broadcastMessage("SERVER", "Connected new client: " + clientName);
    }

    public void broadcastMessage(String clientName, String message) {
        String out = String.format("[%s]: %s\n", clientName, message);
        for (ClientHandler c : users) {
            c.sendMessage(out);
        }
    }

//    public void disConnect(ClientHandler client) {
//        users.remove(client);
//        newClientIndex--;
//        clientName = "Client #" + newClientIndex;
//        System.out.println(String.format("[%s] disConnected", clientName));
//        broadcastMessage("SERVER", "Disconnected client: " + clientName);
//    }


}
