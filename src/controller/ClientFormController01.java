package controller;

import java.awt.image.BufferedImage;
import java.io.*;
import java.net.Socket;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.ResourceBundle;

import javafx.application.Platform;
import javafx.embed.swing.SwingFXUtils;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Background;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.scene.text.TextFlow;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import javax.imageio.ImageIO;

public class ClientFormController01 {

    public static String name = "default name";

    public ImageView imghappyMood;
    public TextFlow txtTextArea;

    public TextArea txtArea;
    public VBox vBox;

    public boolean isImg;
    public ImageView selectedImage;
    public ScrollPane imgScroll;
    public ScrollPane imojiScroll;
    public VBox imojiVboxContainer;

    @FXML
    private ResourceBundle resources;

    @FXML
    private URL location;

    @FXML
    private AnchorPane clientContext;

    @FXML
    private Label lblChatingName;

    @FXML
    private TextField txtMessage;

    @FXML
    private Button btnSend;


    @FXML
    private ImageView imgCamera;
    public FileChooser fileChooser;
    public File selectedFile;
    Socket socket;
    DataInputStream dataInputStream;
    DataOutputStream dataOutputStream;
    String message = "";
    static String username;
    List<String> extensions;

    @FXML
    void initialize() {

        selectedImage.setFitWidth(200.00);
        selectedImage.setPreserveRatio(true);
        imgScroll.setVisible(false);

        imojiScroll.setVisible(false);
        loadImojiToUI();

        new Thread(() -> {

            try {
                name = LoginFormController.username;
                lblChatingName.setText(name);

                socket = new Socket("localhost", 5002);
                dataInputStream = new DataInputStream(socket.getInputStream());
                dataOutputStream = new DataOutputStream(socket.getOutputStream());

                dataOutputStream.writeUTF(username);
                dataOutputStream.flush();

                while (true) {
                    boolean isImg = dataInputStream.readBoolean();
                    if (isImg) {
                        String name = dataInputStream.readUTF();
                        int imgArraySize = dataInputStream.readInt();
                        byte[] imgArray = new byte[imgArraySize];
                        dataInputStream.read(imgArray);
                        ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(imgArray);
                        BufferedImage bufferedImage = ImageIO.read(byteArrayInputStream);
                        //Convert BufferedImage to FXImage
                        Image image = SwingFXUtils.toFXImage(bufferedImage, null);

                        Platform.runLater(() -> {
                            VBox vBox = new VBox();
                            vBox.setPadding(new Insets(5, 0, 5, 0));
                            Text text = new Text(name.concat(" : "));

                            ImageView imageView = new ImageView(image);
                            imageView.setPreserveRatio(true);
                            imageView.setSmooth(true);
                            imageView.setFitWidth(200.00);
                            vBox.getChildren().addAll(text, imageView);
                            this.vBox.getChildren().add(vBox);
                        });
                    } else {
                        String message = dataInputStream.readUTF();
                        Platform.runLater(() -> {
                            HBox hBox = new HBox();
                            hBox.setPadding(new Insets(5, 0, 5, 0));
                            Text text = new Text(message);
                            hBox.getChildren().add(text);
                            vBox.getChildren().add(hBox);
                        });
                    }

                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }).start();
    }

    private void loadImojiToUI() {
        try {
            InputStreamReader inputStreamReader = new InputStreamReader(new FileInputStream(new File("Imoji.txt")));
            StringBuffer stringBuffer = new StringBuffer();
            int ch;
            while ((ch = inputStreamReader.read()) != -1) {
                stringBuffer.append((char) ch);
            }
            System.out.println(stringBuffer.toString());
            String s = stringBuffer.toString();
            String[] split = s.split("[ ]");
            ArrayList<Button> buttons = new ArrayList<>();
            for (int i = 0; i < split.length; i++) {
                Button button = new Button(split[i]);
                button.setBackground(Background.EMPTY);
                button.setFont(new Font(button.getFont().getName(), 25));
                button.setOnAction(event -> {
                    txtMessage.setText(txtMessage.getText().concat(button.getText()));
                    txtMessage.positionCaret(2);
                });
                button.setPrefWidth(60);
                buttons.add(button);
            }

            int index = 0;
            for (int i = 0; i < buttons.size() - 3; i = i + 8) {
                HBox hBox = new HBox();
                for (int j = 0; j < 8; j++) {
                    hBox.getChildren().add(buttons.get(index));
                    index++;
                }
                imojiVboxContainer.getChildren().add(hBox);
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    void btnSendOnAction(ActionEvent event) throws IOException {
        String msg = txtMessage.getText();
        System.out.println(msg);
        isImg = false;
        dataOutputStream.writeBoolean(isImg);
        dataOutputStream.writeUTF((username + " : " + msg.trim()));
        dataOutputStream.flush();

        HBox container = new HBox();
        container.setPadding(new Insets(5, 0, 5, 0));
        container.getChildren().add(new Text("You : ".concat(msg)));
        vBox.getChildren().add(container);
        txtMessage.clear();

        imojiScroll.setVisible(false);
        imgScroll.setVisible(false);
    }

    @FXML
    void btnCloseSendImgOnAction(ActionEvent event) {
        selectedImage.setImage(null);
        imgScroll.setVisible(false);
    }

    @FXML
    void btnSendImgOnAction(ActionEvent event) {
        try {
            BufferedImage readImage = ImageIO.read(selectedFile);

//            Find extension
            String imageFormat = "";
            for (String item : extensions) {
                boolean b = selectedFile.getName().endsWith(item.replace("*.", ""));
                if (b) {
                    imageFormat = item.replace("*.", "");
                    break;
                }

            }

//            convert Image to ByteArrayOutputStream for get byte array of the image
            ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
            ImageIO.write(readImage, imageFormat, byteArrayOutputStream);

//            get byte array of image from created ByteArrayOutputStream
            byte[] imgArray = byteArrayOutputStream.toByteArray();
            int imgArrayLength = imgArray.length;

            isImg = true;
//            Send Data to server
            dataOutputStream.writeBoolean(isImg);
            dataOutputStream.writeUTF(username);
            dataOutputStream.writeInt(imgArrayLength);
            dataOutputStream.write(imgArray);
            dataOutputStream.flush();


//            Add to UI
            VBox vBox = new VBox();
            vBox.setPadding(new Insets(5, 0, 5, 0));
            Text text = new Text("You : ");
            Image image = SwingFXUtils.toFXImage(readImage, null);
            ImageView imageView = new ImageView(image);
            imageView.setPreserveRatio(true);
            imageView.setSmooth(true);
            imageView.setFitWidth(200.00);
            vBox.getChildren().addAll(text, imageView);
            this.vBox.getChildren().add(vBox);

        } catch (IOException e) {
            e.printStackTrace();
        }
        imojiScroll.setVisible(false);
        imgScroll.setVisible(false);
    }


    public void imagebtnClickOnMouse(MouseEvent mouseEvent) {
        extensions = new ArrayList<>();
        Collections.addAll(extensions, "*.jpeg", "*.jpg", "*.png");
        Stage stage = (Stage) ((Node) mouseEvent.getSource()).getScene().getWindow();
        fileChooser = new FileChooser();
        FileChooser.ExtensionFilter imgFilter = new FileChooser.ExtensionFilter("Images", extensions);
        fileChooser.getExtensionFilters().add(imgFilter);

        fileChooser.setTitle("Open Image");
        this.selectedFile = fileChooser.showOpenDialog(stage);

        if (selectedFile != null) {
            Image image = new Image(selectedFile.toURI().toString());
            selectedImage.setImage(image);
            imgScroll.setVisible(true);
        } else {
            imgScroll.setVisible(false);
        }
    }

    public void imghappyMoodOnMouseClick(MouseEvent mouseEvent) {
        if (imojiScroll.isVisible()) {
            imojiScroll.setVisible(false);
        } else {
            imojiScroll.setVisible(true);
        }
    }
}

