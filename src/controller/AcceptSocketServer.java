package controller;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.lang.reflect.Array;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;

public class AcceptSocketServer extends Thread {
    private static ArrayList<Socket> acceptSocket = new ArrayList<>();
    private Socket socket;
    private DataInputStream dataInputStream;
    private DataOutputStream dataOutputStream;
    private String username;
    boolean isImg;


    public AcceptSocketServer(Socket socket, String messageUsername) {
        this.socket = socket;
        this.username = messageUsername;

        try {
            dataInputStream = new DataInputStream(socket.getInputStream());
            dataOutputStream = new DataOutputStream(socket.getOutputStream());
            String message = username + " joined the chat room. ";
            for (Socket accept : acceptSocket) {
                DataOutputStream data = new DataOutputStream(accept.getOutputStream());
                isImg = false;
                data.writeBoolean(isImg);
                data.writeUTF("\n" + message);
                data.flush();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        acceptSocket.add(socket);

    }

    @Override
    public void run() {
        while (true) {
            try {
                isImg = dataInputStream.readBoolean();
                if (isImg) {
                    String name = dataInputStream.readUTF();
                    int imgArrayLength = dataInputStream.readInt();
                    byte[] imgArray = new byte[imgArrayLength];
                    dataInputStream.read(imgArray);
                    for (Socket accept : acceptSocket) {
                        if (socket.getPort() == accept.getPort()) continue;
                        DataOutputStream dos = new DataOutputStream(accept.getOutputStream());

                        dos.writeBoolean(isImg);
                        dos.writeUTF(name);
                        dos.writeInt(imgArrayLength);
                        dos.write(imgArray);
                        dos.flush();
                    }
                    System.out.println("Server received");
                } else {
                    String message = dataInputStream.readUTF();
                    for (Socket accept : acceptSocket) {
                        if (socket.getPort() == accept.getPort()) continue;
                        DataOutputStream dos = new DataOutputStream(accept.getOutputStream());
                        dos.writeBoolean(isImg);
                        dos.writeUTF(message);
                        dos.flush();
                    }

                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
