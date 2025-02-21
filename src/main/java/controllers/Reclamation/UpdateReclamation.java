package controllers.Reclamation;

import entities.Reclamation;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import services.ReclamationService;
import javafx.event.ActionEvent;

import java.io.IOException;
import java.sql.SQLException;

public class UpdateReclamation {
    @FXML
    private TextField titleField;
    
    @FXML
    private TextArea contentArea;
    
    @FXML
    private Button updateButton;
    
    @FXML
    private Button cancelButton;
    
    @FXML
    private Button btn_workbench1;
    
    @FXML
    private Button btn_workbench11;
    
    private final ReclamationService reclamationService;
    private Reclamation currentReclamation;
    
    public UpdateReclamation() {
        reclamationService = new ReclamationService();
    }
    
    @FXML
    void initialize() {
        updateButton.setOnAction(this::handleUpdate);
        cancelButton.setOnAction(this::navigateToList);
        btn_workbench1.setOnAction(this::navigateToHome);
        btn_workbench11.setOnAction(this::navigateToRide);
    }
    
    public void initData(Reclamation reclamation) {
        this.currentReclamation = reclamation;
        titleField.setText(reclamation.getTitle());
        contentArea.setText(reclamation.getContent());
    }
    
    @FXML
    private void handleUpdate(ActionEvent event) {
        String title = titleField.getText().trim();
        String content = contentArea.getText().trim();
        
        if (title.isEmpty() || content.isEmpty()) {
            showAlert(Alert.AlertType.ERROR, "Error", "Please fill all fields");
            return;
        }
        
        try {
            currentReclamation.setTitle(title);
            currentReclamation.setContent(content);
            
            reclamationService.update(currentReclamation);
            showAlert(Alert.AlertType.INFORMATION, "Success", "Reclamation updated successfully");
            navigateToList(event);
            
        } catch (SQLException e) {
            showAlert(Alert.AlertType.ERROR, "Error", "Failed to update reclamation: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    private void navigateToList(ActionEvent event) {
        try {
            Parent root = FXMLLoader.load(getClass().getResource("/Reclamation/ListReclamation.fxml"));
            Stage stage = (Stage)((Node)event.getSource()).getScene().getWindow();
            Scene scene = new Scene(root);
            stage.setScene(scene);
            stage.show();
        } catch (IOException e) {
            showAlert(Alert.AlertType.ERROR, "Error", "Navigation failed");
            e.printStackTrace();
        }
    }
    
    private void navigateToHome(ActionEvent event) {
        try {
            Parent root = FXMLLoader.load(getClass().getResource("/dashboard/dashboard.fxml"));
            Stage stage = (Stage)((Node)event.getSource()).getScene().getWindow();
            Scene scene = new Scene(root);
            stage.setScene(scene);
            stage.show();
        } catch (IOException e) {
            showAlert(Alert.AlertType.ERROR, "Error", "Navigation failed");
            e.printStackTrace();
        }
    }
    
    private void navigateToRide(ActionEvent event) {
        try {
            Parent root = FXMLLoader.load(getClass().getResource("/rides/rides.fxml"));
            Stage stage = (Stage)((Node)event.getSource()).getScene().getWindow();
            Scene scene = new Scene(root);
            stage.setScene(scene);
            stage.show();
        } catch (IOException e) {
            showAlert(Alert.AlertType.ERROR, "Error", "Navigation failed");
            e.printStackTrace();
        }
    }
    
    private void showAlert(Alert.AlertType alertType, String title, String content) {
        Alert alert = new Alert(alertType);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }
}
