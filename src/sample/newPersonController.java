package sample;

import java.net.URL;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ResourceBundle;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;

public class newPersonController {

    @FXML
    private ResourceBundle resources;

    @FXML
    private URL location;

    @FXML
    private TextField newPersonText;

    @FXML
    private Button addPersonButton;

    @FXML
    private Button deletePersonButton;


    @FXML
    void initialize() {

        addPersonButton.setOnAction(event -> {
            if (newPersonText.getText().equals("")) {
                ShowWindow sw = new ShowWindow();
                sw.alertWindow("Пользователь не указан", "Название пользователя не может быть пустым.\n Укажите пользователя для добавления.");
            }
            else addPerson(newPersonText.getText());
        });
        
        deletePersonButton.setOnAction(event -> {
            delPerson(newPersonText.getText());
        });
    }

    private void delPerson(String person) {// если задач для пользователя нет, то удаляем его
        DataBase db = new DataBase();
        db.open();
        try {
            ResultSet rs = db.selectOneColumnTable("bugs", "person", person);// проверяем, есть ли задачи по исполнителю
            int counter = 0;
            while (rs.next())
                counter++;

            if (counter == 0) {
                db.deleteOneColumn("persons", "person", person);
                System.out.println("Пользователь " + person + " удален"); // вывести в лог
                addPersonButton.getScene().getWindow().hide();
            }

            else if (counter >= 1) {
                String str = "Существуют задачи для пользователя " + person + ". Удалите все задачи для данного пользователя!";
                ShowWindow sw = new ShowWindow();
                sw.alertWindow("Невозможно удалить пользователя", str);
            }
            rs.close();
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
        db.close();
    }

    private void addPerson(String person) {// если пользователя еще нет в таблице, то добавляем
        try {
            DataBase db = new DataBase();
            db.open();
            ResultSet rs = db.selectOneColumnTable("persons", "person", person);// проверяем, есть ли такой пользователь в таблице
            int counter = 0;
            while (rs.next())
                counter++;

            if (counter == 0) {// если такого польозвателя еще нет, то добавляем
                db.insertOneColumn("persons", "person", person);
                System.out.println("Добавлен пользователь " + person); // вывести в лог
                addPersonButton.getScene().getWindow().hide();
            }
            else if (counter >= 1) {
                String str = "Пользователь " + person + " уже создан!";
                ShowWindow sw = new ShowWindow();
                sw.alertWindow("Невозможно добавить пользователя", str);
            }
            rs.close();
            db.close();
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }

    }


}

