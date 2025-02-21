package utils;

import utils.ResizeHelper.*;
import controllers.Home;
import javafx.event.Event;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Rectangle2D;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.stage.Modality;
import javafx.stage.Screen;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

import java.io.IOException;

public class GeneralFunctions {
    private static int nanCounter = 0;
    private final Rectangle2D screenBounds = Screen.getPrimary().getVisualBounds();

    public void switchScene(Event event, String fxml) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(Home.class.getResource(fxml));
        Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        Scene scene = new Scene(fxmlLoader.load());
        stage.setScene(scene);
        stage.centerOnScreen();
        stage.show();
    }

    public void switchScene(String fxml) throws IOException {
        Stage stage = new Stage();
        Scene scene = new Scene(FXMLLoader.load(Home.class.getResource(fxml)));
        stage.setScene(scene);
        stage.centerOnScreen();
        stage.initStyle(StageStyle.UNDECORATED);
        stage.show();
    }

    public void switchSceneModality(String fxml) throws IOException {
        Stage stage = new Stage();
        Scene scene = new Scene(FXMLLoader.load(Home.class.getResource(fxml)));
        stage.setScene(scene);
        stage.centerOnScreen();
        stage.initModality(Modality.APPLICATION_MODAL);
        stage.initStyle(StageStyle.UNDECORATED);
        stage.showAndWait();
    }

    public void close(Button button) {
        Stage stage = (Stage) button.getScene().getWindow();
        stage.close();
    }

    public void modalityClose(Button button) {
        Stage stage = (Stage) button.getScene().getWindow();
        stage.close();
    }

    public void switchSceneFXHelper(Event event, String fxml) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(Home.class.getResource(fxml));
        Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        Scene scene = new Scene(fxmlLoader.load());
        stage.setScene(scene);

        // Add resize functionality for undecorated stages
        FXResizeHelper fxResizeHelper = new FXResizeHelper(stage, 10, 10);
        stage.show();
        stage.centerOnScreen();
    }

    public void restoreWindow(Button button) {
        Stage stage = (Stage) button.getScene().getWindow();
        stage.setIconified(true);
    }

    public void maximizeOrMinimize(Button button) {
        Stage stage = (Stage) button.getScene().getWindow();
        double width = stage.getWidth();
        double height = stage.getHeight();

        if (nanCounter == 0 || (height < screenBounds.getHeight() && width < screenBounds.getWidth())) {
            stage.setHeight(screenBounds.getHeight());
            stage.setWidth(screenBounds.getWidth());
            stage.centerOnScreen();
            nanCounter = 1;
        } else {
            stage.setHeight(600);
            stage.setWidth(1024);
            stage.centerOnScreen();
            nanCounter = 0;
        }
    }
}