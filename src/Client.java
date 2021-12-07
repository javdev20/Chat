import examples.SimpleChatClient;

import javax.swing.*;
import java.awt.*;
import java.io.BufferedReader;
import java.io.PrintWriter;
import java.net.Socket;

public class Client {

    JTextArea incoming;
    JTextField outgoing;
    BufferedReader reader;
    PrintWriter writer;
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

        panel.add(qScroller);
        panel.add(outgoing);
        panel.add(sendButton);

        frame.getContentPane().add(BorderLayout.CENTER, panel);
        frame.setSize(650, 500);
        frame.setVisible(true);



    }
}
