package server;

import java.io.*;
import java.net.Socket;

public class ClientHandler{

    Server server;
    DataOutputStream out;
    DataInputStream in;
    Socket socket;
    String nickname;

    public ClientHandler(Server server, Socket clientSocket) {
        try {
            this.server = server;
            socket = clientSocket;
            in = new DataInputStream(socket.getInputStream());
            out = new DataOutputStream(socket.getOutputStream());

            new Thread(() -> {
                boolean isExit = false;
                try {
                    while (true) {
                        String message = in.readUTF();
                        if(message.startsWith("/signup")) {
                            String[] tokens = message.split(" ");
                            int result = AuthService.addUser(tokens[1], tokens[2], tokens[3]);
                            if (result > 0) {
                                sendMessage("Successful registration");
                            } else {
                                sendMessage( "Registration failed");
                            }
                        }

                        if (message.startsWith("/auth")){
                            String[] tokens = message.split(" ");
                            String nick = AuthService.getNicknameByLoginAndPass(tokens[1], tokens[2]);
                            if (nick != null) {
                                if (!server.isNickBusy(nick)) {
                                    sendMessage("/auth-OK");
                                    setNickname(nick);
                                    server.subscribe(ClientHandler.this);
                                    break;
                                } else {
                                    sendMessage("Account is already in use!");
                                }
                            } else {
                                sendMessage("Incorrect login/password");
                            }
                        }

                        if ("/end".equals(message)) {
                            isExit = true;
                            break;
                        }
                    }

                    if (!isExit) {
                        while (true) {
                            String message = in.readUTF();

                            if ("/end".equalsIgnoreCase(message)){
                                // для оповещения клиента, т.к. без сервера клиент работать не должен
                                out.writeUTF("/serverClosed");
                                System.out.println("Client (" + socket.getInetAddress() + ") exited");
                                break;
                            } else {
                                server.broadcastMessage(nickname, message);
                            }
                        }
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }).start();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public String getNickname() {
        return nickname;
    }

    public void setNickname(String nickname) {
        this.nickname = nickname;
    }

    public void sendMessage(String message) {
        try {
            out.writeUTF(message);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
