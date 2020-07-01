package sample;

import java.net.URL;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ResourceBundle;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.control.Alert;

public class newProgectController {

    @FXML
    private ResourceBundle resources;

    @FXML
    private URL location;

    @FXML
    private TextField newProgectText;

    @FXML
    private Button addProgectButton;

    @FXML
    private Button deleteProgectButton;


    @FXML
    void initialize() {
        addProgectButton.setOnAction(event -> {// добавляем проект
            if (newProgectText.getText().equals("")) {
                ShowWindow sw = new ShowWindow();
                sw.alertWindow("Проект не указан", "Название проекта не может быть пустым.\n Укажите проект для добавления.");
            }
            else  addProgect(newProgectText.getText());

        });

        deleteProgectButton.setOnAction(event -> {// если задач по проекту нет, то удаляем
            delProgect(newProgectText.getText());
        });
    }

    private void addProgect(String progect) {// добавляем проект
        try {
            DataBase db = new DataBase();
            db.open();

            ResultSet rs = db.selectOneColumnTable("progects", "progect", progect);// проверяем, есть ли такой проект в таблице
            int counter = 0;
            while (rs.next()) {
                counter++;
            }

            if (counter == 0) {// если такого проекта еще нет, то добавляем
                db.insertOneColumn("progects", "progect", progect);
                System.out.println("Добавлен проект " + progect); // вывести в лог
                addProgectButton.getScene().getWindow().hide();

            }
            else if (counter >= 1) {
                String str = "Проект " + progect + " уже создан!";
                ShowWindow sw = new ShowWindow();
                sw.alertWindow("Невозможно добавить проект", str);
            }
            rs.close();
            db.close();
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
    }

    private void delProgect(String progect) {// если задач по проекту нет, то удаляем проект
        DataBase db = new DataBase();
        db.open();
        try {
            ResultSet rs = db.selectOneColumnTable("bugs", "progect", progect);// проверяем, есть ли задачи по исполнителю
            int counter = 0;
            while (rs.next()) {
                counter++;
            }

            if (counter == 0) {
               db.deleteOneColumn("progects", "progect", progect);
                System.out.println("Проект " + progect + " удален"); // вывести в лог
                addProgectButton.getScene().getWindow().hide();
            }

            else if (counter >= 1) {
                String str = "Существуют задачи по проекту " + progect + ". Удалите все задачи по данному проекту!";
                ShowWindow sw = new ShowWindow();
                sw.alertWindow("Невозможно удалить проект", str);
            }
            rs.close();
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
        db.close();
    }
}
