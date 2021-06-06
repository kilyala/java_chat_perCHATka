package client;

import commands.Command;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;

public class RegController {
    @FXML
    private TextField loginField;
    @FXML
    private PasswordField passwordField;
    @FXML
    private TextField nicknameField;
    @FXML
    private TextArea textArea;
    private Controller controller;

    public void setController(Controller controller) {
        this.controller = controller;
    }

    public void setResultTryToReg(String command) {
        if(command.equals(Command.REG_OK)) {
            System.out.println("Register successful\n");
            textArea.appendText("Register successful\n");
        }
        if(command.equals(Command.REG_NON)) {
            System.out.println("Register failed\n");
            textArea.appendText("Register failed\n");
        }
    }


    public void tryToReg(ActionEvent actionEvent) {
        String login = loginField.getText().trim();
        String password = passwordField.getText().trim();
        String nickname = nicknameField.getText().trim();

        if(login.length() * password.length() * nickname.length() == 0) {
            return;
        }

        controller.registration(login, password, nickname);

    }
}
