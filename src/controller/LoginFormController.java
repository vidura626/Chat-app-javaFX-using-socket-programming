package controller;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;
import javafx.stage.Window;

import java.io.IOException;

public class LoginFormController {

    public static String username;
    public AnchorPane loginContext;
    public Label lblWrong;
    @FXML
    private TextField txtUsername;

    @FXML
    private Button btnLogin;

    @FXML
    void btnLoginOnAction(ActionEvent event) {

        if (!txtUsername.getText().isEmpty()) {
            try {
                username = txtUsername.getText();
                ClientFormController01.username = txtUsername.getText();
                Stage stage = (Stage) loginContext.getScene().getWindow();
                stage.setScene(new Scene(FXMLLoader.load(this.getClass().getResource("../view/ClientForm01.fxml"))));
            } catch (IOException e) {
                e.printStackTrace();
            }

        } else {
            /*  lblWrong.setText("Please your name !!!");*/
            new Alert(Alert.AlertType.WARNING, "Please enter your name !").show();


        }
    }

}
