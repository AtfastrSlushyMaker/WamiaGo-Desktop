package controllers.user;

import entities.User;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.TextField;
import services.UserService;

import java.sql.SQLException;

public class UsersEditController {

    @FXML
    private TextField nameField;

    @FXML
    private TextField emailField;

    @FXML
    private TextField phoneField;

    @FXML
    private ComboBox<User.Gender> genderComboBox;

    @FXML
    private DatePicker dobDatePicker;

    private UserService userService;
    private User user;

    public UsersEditController() {
        userService = new UserService();
    }

    @FXML
    public void initialize() {
        genderComboBox.getItems().setAll(User.Gender.values());
    }

    public void setUser(User user) {
        this.user = user;
        if (user != null) {
            nameField.setText(user.getName());
            emailField.setText(user.getEmail());
            phoneField.setText(user.getPhone());
            genderComboBox.setValue(user.getGender());
            dobDatePicker.setValue(user.getDateOfBirth());
        }
    }

    @FXML
    public void saveUser(ActionEvent event) {
        if (user != null) {
            user.setName(nameField.getText());
            user.setEmail(emailField.getText());
            user.setPhone(phoneField.getText());
            user.setGender(genderComboBox.getValue());
            user.setDateOfBirth(dobDatePicker.getValue());

            try {
                userService.update(user);
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }
}