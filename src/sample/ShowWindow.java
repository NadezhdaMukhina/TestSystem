package sample;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.stage.Modality;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;

import java.io.IOException;

import static javafx.stage.Modality.APPLICATION_MODAL;

public class ShowWindow {
    public void showWindow (String fileName) { // отображаем новое окно
        ShowWindow sw = new ShowWindow();
        FXMLLoader loader = new FXMLLoader();
        loader.setLocation(getClass().getResource(fileName));
        try {
            loader.load();
        } catch (IOException e) {
            e.printStackTrace();
        }
        Parent root = loader.getRoot();
        Stage stage = new Stage();
        stage.setScene(new Scene(root));
        stage.initModality(APPLICATION_MODAL);
        stage.showAndWait();
    }

    public void alertWindow (String title, String text) { // окно с информацией
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(text);
        alert.showAndWait();
    }





}
