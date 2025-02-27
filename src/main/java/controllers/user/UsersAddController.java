package controllers.user;

import entities.Location;
import entities.User;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import services.LocationService;
import services.UserService;

import java.sql.SQLException;
import java.util.List;

public class UsersAddController {
    @FXML
    public PasswordField passwordField;
    @FXML
    public ComboBox<Location> locationComboBox; // Use ComboBox<Location> instead of raw ComboBox
    @FXML
    private TextField nameField, emailField, phoneField;
    @FXML private ComboBox<String> roleComboBox, genderComboBox, verifiedComboBox, accountStatusComboBox;
    @FXML private DatePicker dobDatePicker;

    private UserService userService = new UserService();
    private LocationService locationService = new LocationService();

    @FXML
    private void initialize() throws SQLException {
        initializeComboBoxes();
    }

    private void initializeComboBoxes() throws SQLException {
        // Initialize role, gender, and account status ComboBoxes
        roleComboBox.getItems().addAll("ADMIN", "CLIENT");
        genderComboBox.getItems().addAll("MALE", "FEMALE");
        accountStatusComboBox.getItems().addAll("ACTIVE", "BANNED", "DEACTIVATED");

        List<Location> locations = locationService.read();
        locationComboBox.getItems().addAll(locations);
    }

    @FXML
    private void saveUser(ActionEvent event) {
        try {
            // Retrieve the selected location from the ComboBox
            Location selectedLocation = locationComboBox.getValue();

            // Create a new User object and set its properties
            User newUser = new User();
            newUser.setName(nameField.getText());
            newUser.setEmail(emailField.getText());
            newUser.setPhone(phoneField.getText());
            newUser.setPassword(passwordField.getText());
            newUser.setRole(User.Role.valueOf(roleComboBox.getValue()));
            newUser.setGender(User.Gender.valueOf(genderComboBox.getValue()));
            newUser.setLocation(selectedLocation); // Set the selected location
            newUser.setVerified(false);
            newUser.setAccountStatus(User.AccountStatus.valueOf(accountStatusComboBox.getValue()));
            newUser.setDateOfBirth(dobDatePicker.getValue());

            // Save the user to the database
            userService.create(newUser);

            // Close the modal window
            closeModal(event);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void closeModal(ActionEvent event) {
        Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        stage.close();
    }
}