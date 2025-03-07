package controllers.user;

import entities.User;
import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.effect.DropShadow;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.stage.Modality;
import javafx.stage.Stage;
import services.UserService;

import java.io.IOException;
import java.net.URL;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;
public class UsersController implements Initializable {
    public Button addUser;
    @FXML
    private FlowPane usersFlowPane;
    @FXML
    private Pagination pagination;
    @FXML
    private TextField searchField;
    @FXML
    private ComboBox<String> sortField;

    private final UserService userService = new UserService();
    private List<User> allUsers;
    private final int pageSize = 6; // Number of cards per page
    private final List<VBox> cachedCards = new ArrayList<>(); // Cache for user cards

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        try {
            // Load all users from the database
            allUsers = userService.read();

            // Calculate total pages for pagination
            int totalPages = (int) Math.ceil((double) allUsers.size() / pageSize);
            pagination.setPageCount(totalPages);
            pagination.setCurrentPageIndex(0);

            // Configure FlowPane for responsiveness
            configureFlowPane();

            // Preload user cards for better performance
            preloadUserCards();

            // Load the first page
            loadPage(0);

            // Add listener to handle page changes
            pagination.currentPageIndexProperty().addListener((obs, oldIndex, newIndex) -> {
                loadPage(newIndex.intValue());
            });

            // Add listeners for search and sort actions
            searchField.textProperty().addListener((obs, oldText, newText) -> {
                handleSearch(newText);
            });

            sortField.valueProperty().addListener((obs, oldSort, newSort) -> {
                handleSort(newSort);
            });

        } catch (SQLException e) {
            System.err.println("Connection to Database Cannot Be Established: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private void handleSearch(String searchText) {
        Task<List<User>> searchTask = new Task<>() {
            @Override
            protected List<User> call() throws SQLException {
                return userService.searchUsers("name", searchText); // Adjust the search field as needed
            }
        };

        searchTask.setOnSucceeded(event -> {
            allUsers = searchTask.getValue();
            refreshCards();
        });

        searchTask.setOnFailed(event -> {
            searchTask.getException().printStackTrace();
        });

        new Thread(searchTask).start();
    }

    private void handleSort(String sortField) {
        try {
            allUsers = userService.sortUsers(sortField, true); // Assuming ascending order
            refreshCards();
        } catch (SQLException e) {
            System.err.println("Sort failed: " + e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * Configures the FlowPane for responsiveness and alignment.
     */
    private void configureFlowPane() {
        usersFlowPane.setAlignment(Pos.CENTER);
        usersFlowPane.setHgap(20); // Horizontal gap between cards
        usersFlowPane.setVgap(20); // Vertical gap between rows
        usersFlowPane.setPadding(new Insets(20)); // Padding around the FlowPane

        // Bind the prefWrapLength to the FlowPane's width for responsive wrapping
        usersFlowPane.widthProperty().addListener((obs, oldVal, newVal) -> {
            usersFlowPane.setPrefWrapLength(newVal.doubleValue() - 40); // Account for padding
        });
    }

    /**
     * Preloads all user cards into the cache.
     */
    private void preloadUserCards() {
        cachedCards.clear(); // Clear the cache
        for (User user : allUsers) {
            VBox userCard = createUserCard(user);
            cachedCards.add(userCard);
        }
    }

    /**
     * Loads the specified page of user cards into the FlowPane.
     *
     * @param pageIndex The index of the page to load.
     */
    private void loadPage(int pageIndex) {
        usersFlowPane.getChildren().clear(); // Clear existing cards

        // Calculate the range of cards to display
        int startIndex = pageIndex * pageSize;
        int endIndex = Math.min(startIndex + pageSize, allUsers.size());

        // Add the cards for the current page
        for (int i = startIndex; i < endIndex; i++) {
            usersFlowPane.getChildren().add(cachedCards.get(i));
        }
    }

    /**
     * Creates a user card for the given user.
     *
     * @param user The user to create the card for.
     * @return A VBox representing the user card.
     */
    private VBox createUserCard(User user) {
        VBox card = new VBox(10);
        card.setPadding(new Insets(20));
        card.setStyle("-fx-background-color: #ffffff; -fx-border-color: #e0e0e0; -fx-border-radius: 10; -fx-background-radius: 10;");
        card.setPrefWidth(300);
        card.setMinWidth(280); // Allow slight resizing
        card.setMaxWidth(320);
        card.setSpacing(10);

        // Add elevation (shadow effect)
        DropShadow shadow = new DropShadow();
        shadow.setRadius(5);
        shadow.setOffsetX(3);
        shadow.setOffsetY(3);
        shadow.setColor(Color.rgb(0, 0, 0, 0.3));
        card.setEffect(shadow);

        // User image
        ImageView userImage = new ImageView(new Image(getClass().getResourceAsStream("/images/icons/user.png")));
        userImage.setFitHeight(100);
        userImage.setFitWidth(100);
        userImage.setPreserveRatio(true);

        // User details
        Label nameLabel = createDetailLabel("Name: " + user.getName(), FontWeight.BOLD, 16);
        Label emailLabel = createDetailLabel("Email: " + user.getEmail(), FontWeight.NORMAL, 14);
        Label phoneLabel = createDetailLabel("Phone: " + user.getPhone(), FontWeight.NORMAL, 14);
        Label roleLabel = createDetailLabel("Role: " + user.getRole().toString(), FontWeight.NORMAL, 14);
        Label statusLabel = createDetailLabel("Status: " + user.getStatus(), FontWeight.NORMAL, 14);

        // Action buttons (edit and delete)
        HBox adminActions = new HBox(10);
        adminActions.setAlignment(Pos.TOP_RIGHT);

        Button editButton = createIconButton("/images/icons/edit.png", "#4CAF50", event -> openEditModal(user));
        Button deleteButton = createIconButton("/images/icons/delete.png", "#f44336", event -> deleteUser(user));

        adminActions.getChildren().addAll(editButton, deleteButton);

        // Add all elements to the card
        card.getChildren().addAll(adminActions, userImage, nameLabel, emailLabel, phoneLabel, roleLabel, statusLabel);

        return card;
    }

    /**
     * Creates a styled label for user details.
     *
     * @param text       The text to display.
     * @param fontWeight The font weight.
     * @param size       The font size.
     * @return A styled Label.
     */
    private Label createDetailLabel(String text, FontWeight fontWeight, int size) {
        Label label = new Label(text);
        label.setFont(Font.font("System", fontWeight, size));
        label.setTextFill(Color.DARKGRAY);
        return label;
    }

    /**
     * Creates an icon button with the specified icon and color.
     *
     * @param iconPath The path to the icon image.
     * @param color    The background color of the button.
     * @param handler  The event handler for the button.
     * @return A styled Button.
     */
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

    /**
     * Opens the edit modal for the specified user.
     *
     * @param user The user to edit.
     */
    private void openEditModal(User user) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/user.back/user/edit.fxml"));
            Stage stage = new Stage();
            stage.setScene(new Scene(loader.load()));

            UsersEditController controller = loader.getController();
            controller.setUser(user);

            stage.initModality(Modality.APPLICATION_MODAL);
            stage.showAndWait();

            // Refresh the cards after editing
            refreshCards();
        } catch (IOException e) {
            System.err.println("Failed to load edit modal: " + e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * Deletes the specified user from the database.
     *
     * @param user The user to delete.
     */
    private void deleteUser(User user) {
        try {
            userService.delete(user.getId());
            refreshCards(); // Refresh the cards after deletion
        } catch (SQLException e) {
            System.err.println("Failed to delete user: " + e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * Sends a message to the specified user.
     *
     * @param user The user to send a message to.
     */
    private void sendMessage(User user) {
        // Placeholder for send message functionality
        System.out.println("Sending message to: " + user.getName());
    }

    /**
     * Views the history of the specified user.
     *
     * @param user The user to view history for.
     */
    private void viewHistory(User user) {
        // Placeholder for view history functionality
        System.out.println("Viewing history for: " + user.getName());
    }

    /**
     * Refreshes the user cards by reloading data from the database.
     */
    private void refreshCards() {
        try {
            allUsers = userService.read(); // Reload users from the database
            int totalPages = (int) Math.ceil((double) allUsers.size() / pageSize);
            pagination.setPageCount(totalPages); // Update pagination
            preloadUserCards(); // Rebuild the cache
            loadPage(pagination.getCurrentPageIndex()); // Reload the current page
        } catch (SQLException e) {
            System.err.println("Connection to Database Cannot Be Established: " + e.getMessage());
            e.printStackTrace();
        }
    }
}