package controllers.user;

import entities.User;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import controllers.dashboard.DashboardController;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.io.IOException;

public class ProfileController  {

    public Label genderLabel;
    public Label dobLabel;
    @FXML
    private Label userNameLabel;

    @FXML
    private Label emailLabel;

    @FXML
    private Label phoneLabel;

    @FXML
    private Label addressLabel;
    @FXML
    void initialize() {

    }
    public void setUserData(User user) {
        if (user != null) {
            userNameLabel.setText(user.getName());
            emailLabel.setText(user.getEmail());
            phoneLabel.setText(user.getPhone());
            addressLabel.setText(user.getLocation().getAddress());
            genderLabel.setText(user.getGender().toString());
            dobLabel.setText(user.getDateOfBirth().toString());


        }
    }

    public void edit(ActionEvent actionEvent) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/user.back/user/edit.fxml"));
            Stage stage = new Stage();
            stage.setScene(new Scene(loader.load()));

            stage.initModality(Modality.APPLICATION_MODAL);
            stage.showAndWait();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
