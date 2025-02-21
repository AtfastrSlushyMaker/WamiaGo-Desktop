package controllers.user;

import entities.User;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import controllers.dashboard.DashboardController;
public class ProfileController extends DashboardController {

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
        pageNavigation();
    }
    public void setUserData(User user) {
        if (user != null) {
            userNameLabel.setText(user.getName());
            emailLabel.setText(user.getEmail());
            phoneLabel.setText(user.getPhone());
            addressLabel.setText(user.getLocation().getAddress());

        }
    }
}
