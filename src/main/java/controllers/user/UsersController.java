package controllers.user;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.Pagination;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.stage.Modality;
import javafx.stage.Stage;
import entities.User;
import services.UserService;

import java.io.IOException;
import java.net.URL;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;

public class UsersController implements Initializable {
    @FXML
    private FlowPane usersFlowPane;
    @FXML
    private Pagination pagination;

    private UserService userService = new UserService();
    private List<User> allUsers;
    private final int pageSize = 6;
    private List<VBox> cachedCards = new ArrayList<>();

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        try {
            allUsers = userService.read();
            int totalPages = (int) Math.ceil((double) allUsers.size() / pageSize);
            pagination.setPageCount(totalPages);
            pagination.setCurrentPageIndex(0);

            preloadUserCards();

            loadPage(0);

            pagination.currentPageIndexProperty().addListener((obs, oldIndex, newIndex) -> {
                loadPage(newIndex.intValue());
            });
        } catch (SQLException e) {
            System.out.println("Connection to Database Cannot Be Established");
            e.printStackTrace();
        }
    }

    private void preloadUserCards() {
        cachedCards.clear(); // Clear the cache
        for (User user : allUsers) {
            VBox userCard = createUserCard(user);
            cachedCards.add(userCard);
        }
    }

    private void loadPage(int pageIndex) {
        usersFlowPane.getChildren().clear(); // Clear existing cards
        int startIndex = pageIndex * pageSize;
        int endIndex = Math.min(startIndex + pageSize, allUsers.size());

        for (int i = startIndex; i < endIndex; i++) {
            usersFlowPane.getChildren().add(cachedCards.get(i));
        }
    }

    private VBox createUserCard(User user) {
        VBox card = new VBox(10);
        card.setPadding(new Insets(15));
        card.setStyle("-fx-background-color: #ffffff; -fx-border-color: #e0e0e0; -fx-border-radius: 5; -fx-background-radius: 5;");
        card.setPrefWidth(250);
        card.setPrefHeight(300);

        ImageView userImage = new ImageView(new Image(getClass().getResourceAsStream("/images/icons/user.png")));
        userImage.setFitHeight(80);
        userImage.setFitWidth(80);
        userImage.setPreserveRatio(true);

        Label nameLabel = createDetailLabel("Name: " + user.getName(), FontWeight.BOLD, 14);
        Label emailLabel = createDetailLabel("Email: " + user.getEmail(), FontWeight.NORMAL, 12);
        Label phoneLabel = createDetailLabel("Phone: " + user.getPhone(), FontWeight.NORMAL, 12);
        Label roleLabel = createDetailLabel("Role: " + user.getRole().toString(), FontWeight.NORMAL, 12);
        Label statusLabel = createDetailLabel("Status: " + user.getStatus(), FontWeight.NORMAL, 12);

        HBox actionButtons = new HBox(5);
        actionButtons.setAlignment(Pos.TOP_RIGHT);

        Button editButton = createIconButton("../images/icons/edit.png", "#4CAF50", event -> openEditModal(user));
        Button deleteButton = createIconButton("../images/icons/delete.png", "#f44336", event -> deleteUser(user));

        actionButtons.getChildren().addAll(editButton, deleteButton);

        card.getChildren().addAll(actionButtons, userImage, nameLabel, emailLabel, phoneLabel, roleLabel, statusLabel);

        return card;
    }

    private Label createDetailLabel(String text, FontWeight fontWeight, int size) {
        Label label = new Label(text);
        label.setFont(Font.font("System", fontWeight, size));
        label.setTextFill(Color.DARKGRAY);
        return label;
    }

    private Button createIconButton(String iconPath, String color, javafx.event.EventHandler<javafx.event.ActionEvent> handler) {
        Button button = new Button();
        button.setStyle("-fx-background-color: " + color + "; -fx-text-fill: white; -fx-background-radius: 5;");

        try {
            ImageView icon = new ImageView(new Image(getClass().getResourceAsStream(iconPath)));
            icon.setFitWidth(16);
            icon.setFitHeight(16);
            button.setGraphic(icon);
        } catch (Exception e) {
            System.err.println("Failed to load icon: " + iconPath);
            e.printStackTrace();
        }

        button.setOnAction(handler);
        return button;
    }

    private void openEditModal(User user) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/user.back/user/edit.fxml"));
            Stage stage = new Stage();
            stage.setScene(new Scene(loader.load()));

            UsersEditController controller = loader.getController();
            controller.setUser(user);

            stage.initModality(Modality.APPLICATION_MODAL);
            stage.showAndWait();
            refreshCards();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void deleteUser(User user) {
        try {
            userService.delete(user.getId());
            refreshCards();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void refreshCards() {
        try {
            allUsers = userService.read();
            int totalPages = (int) Math.ceil((double) allUsers.size() / pageSize);
            pagination.setPageCount(totalPages);
            preloadUserCards();
            loadPage(pagination.getCurrentPageIndex());
        } catch (SQLException e) {
            System.out.println("Connection to Database Cannot Be Established");
            e.printStackTrace();
        }
    }
}