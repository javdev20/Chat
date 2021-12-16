package server;

import java.io.*;
import java.net.Socket;
import java.util.List;

public class ClientHandler{

    Server server;
    DataOutputStream out;
    DataInputStream in;
    Socket socket;
    String nickname;
    List<String> blackList;

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
                            // for all service commands
                            if (message.startsWith("/") || message.startsWith("@")) {
                                if ("/end".equalsIgnoreCase(message)){
                                    out.writeUTF("/serverClosed");
                                    System.out.println("Client (" + socket.getInetAddress() + ") exited");
                                    break;
                                }
                                if (message.startsWith("@")) {
                                    String[] tokens = message.split(" ", 2);
                                    server.sendPrivateMsg(this, tokens[0].substring(1), tokens[1]);
                                }
                                if (message.startsWith("/blacklist ")) {
                                    String[] tokens = message.split(" ");
                                    blackList.add(tokens[1]);
                                    sendMessage("You added " + tokens[1] + " to blacklist");
                                } else {
                                    server.broadcastMessage(nickname, message);
                                }
                            }
                        }
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }  finally {
                    try {
                        in.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    try {
                        out.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    try {
                        socket.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    server.unsubscribe(this);
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
