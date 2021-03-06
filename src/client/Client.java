package client;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.net.Socket;

public class Client {

    JTextArea incoming;
    JTextField outgoing;
    DataInputStream in;
    DataOutputStream out;
    Socket socket;

    public static void main(String[] args) {
        Client client = new Client();
        client.go();
    }

    public void go() {
        JFrame frame = new JFrame("My Chat");

        JPanel panel = new JPanel();

        incoming = new JTextArea(20, 50);
        incoming.setLineWrap(true);
        incoming.setWrapStyleWord(true);
        incoming.setEditable(false);

        JScrollPane qScroller = new JScrollPane(incoming);
        qScroller.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_ALWAYS);
        qScroller.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_ALWAYS);

        outgoing = new JTextField(20);

        JButton sendButton = new JButton("Send");

        sendButton.addActionListener(new SendButtonListener());
        outgoing.addKeyListener(new KeyAdapter() {
            @Override
            public void keyTyped(KeyEvent e) {
                super.keyTyped(e);
            }

            @Override
            public void keyPressed(KeyEvent e) {
                super.keyPressed(e);
            }

            @Override
            public void keyReleased(KeyEvent e) {
                int key = e.getKeyCode();
                if (key == KeyEvent.VK_ENTER) {
                    sendMessage();
                }
            }
        });
        setUpNetworking();

        JMenuBar menuBar = new JMenuBar();
        JMenu main = new JMenu("Program");
        JMenuItem exit = new JMenuItem("exit");

        exit.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                System.exit(0);
            }
        });

        main.add(exit);
        menuBar.add(main);

        Thread readerThread = new Thread(new IncomingReader());
        readerThread.start();

        panel.add(qScroller);
        panel.add(outgoing);
        panel.add(sendButton);


        frame.setJMenuBar(menuBar);
        frame.getContentPane().add(BorderLayout.CENTER, panel);
        frame.setSize(650, 500);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setVisible(true);
    }

    private void setUpNetworking() {
        try {
            socket = new Socket("127.0.0.1", 7007);
            in = new DataInputStream(socket.getInputStream());
            out = new DataOutputStream(socket.getOutputStream());
            System.out.println("networking established");
        }
        catch(IOException ex) {
            ex.printStackTrace();
        }
    }

    public class SendButtonListener implements ActionListener {
        public void actionPerformed(ActionEvent ev) {
            sendMessage();
        }
    }

    public void sendMessage() {
        try {
            out.writeUTF(outgoing.getText());
            out.flush();
        }
        catch (Exception ex) {
            ex.printStackTrace();
        }
        outgoing.setText("");
        outgoing.requestFocus();
    }

    class IncomingReader implements Runnable {
        public void run() {
            try {
                while (true) {
                    String message = in.readUTF();
                    System.out.println("client read " + message);
                    if (message.equals("/close")) {
                        System.exit(0);
                    }
                    incoming.append(message + "\n");
                }
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        }
    }
}
