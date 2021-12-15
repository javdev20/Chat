package server;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

public class Server {
    private List<ClientHandler> users;
    private static int newClientIndex = 1;
    private String clientName;

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

    public void broadcastMessage(String clientName, String message) {
        String out = String.format("[%s]: %s\n", clientName, message);
        for (ClientHandler c : users) {
            c.sendMessage(out);
        }
    }


    public boolean isNickBusy(String nick) {
        for (ClientHandler c : users) {
            if (c.getNickname().equals(nick)) {
                return true;
            }
        }
        return false;
    }

    public void subscribe(ClientHandler client) {
        users.add(client);
        System.out.println(String.format("User [%s] connected", client.getNickname()));
        broadcastMessage("[SERVER]", "User " + client.getNickname()
                                    + " connected");
    }

    public void unsubscribe(ClientHandler client) {
        users.remove(client);
        System.out.println(String.format("User [%s] disconnected", client.getNickname()));
        broadcastMessage("[SERVER]", "User " + client.getNickname()
                + "disconnected");
    }

}
