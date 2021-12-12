package server;


import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;


public class Server {

    private ArrayList<ClientHandler> users;
    ServerSocket serverSocket = null;
    Socket socket = null;
    private static int newClientIndex = 1;
    private String clientName;

    public Server() {
        users = new ArrayList();

        try {
            AuthService.connect();
            serverSocket = new ServerSocket(7007);
            System.out.println("Server started");

            while(true) {
                socket = serverSocket.accept();
                System.out.printf("Client %s connected\n", socket.getInetAddress());
                new ClientHandler(this, socket);
            }
        } catch (Exception ex) { ex.printStackTrace(); }
    }

    public void connect(ClientHandler client) {
        users.add(client);
        clientName = "Клиент #" + newClientIndex;
        newClientIndex++;
        broadcastMessage("SERVER", "Подключился новый клиент: " + clientName);
        System.out.println(String.format("User [%s] connected", clientName));
        broadcastMessage("SERVER", "Connected new client: " + clientName);
    }

    public void broadcastMessage(String clientName, String message) {
        String out = String.format("[%s]: %s\n", clientName, message);
        for (ClientHandler c : users) {
            c.sendMessage(out);
        }
    }

}
