package controller;

import javafx.fxml.FXML;
import javafx.scene.control.TextArea;
import javafx.scene.layout.VBox;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.text.SimpleDateFormat;
import java.util.Date;


public class ServerFormController {

    public TextArea txtArea;
    ServerSocket serverSocket;
    Socket socket;
    DataInputStream dataInputStream;
    DataOutputStream dataOutputStream;
    String message = "";
    @FXML
    private VBox vBox;

    public void initialize() {
        new Thread(() -> {
            try {
                serverSocket = new ServerSocket(5002);
                txtArea.appendText("Server Started!");

                while (true) {

                    socket = serverSocket.accept();
                    txtArea.appendText("\nClient Accepted!");
                    Date d1 = new Date();
                    SimpleDateFormat df = new SimpleDateFormat("MM/dd/YYYY HH:mm a");
                    String formattedDate = df.format(d1);
                    txtArea.appendText("\nNew client connected. " + formattedDate);

                    dataInputStream = new DataInputStream(socket.getInputStream());
                    dataOutputStream = new DataOutputStream(socket.getOutputStream());

                    String messageUsername = dataInputStream.readUTF();
                    txtArea.appendText("\nServer: " + messageUsername + " joined the chat room. ");

                    new AcceptSocketServer(socket, messageUsername).start();


                }


            } catch (IOException e) {
                e.printStackTrace();
            }
        }).start();

    }

}
