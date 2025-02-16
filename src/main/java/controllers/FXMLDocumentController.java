package controllers;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
//import com.jfoenix.controls.Button;

public class FXMLDocumentController {

    @FXML
    private Label label;

    @FXML
    private Button button;

    @FXML
    private void handleButtonAction() {
        label.setText("Hello, World!");
    }
}