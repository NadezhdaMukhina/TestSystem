package sample;

import java.net.URL;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ResourceBundle;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.AnchorPane;
import javafx.scene.Node;
import javafx.application.Platform;


public class Controller {

    @FXML
    private ResourceBundle resources;

    @FXML
    private URL location;

    @FXML
    private Button fileReadButton;

    @FXML
    private AnchorPane PaneLocal;


    @FXML
    private Button selectButton;

    @FXML
    private ComboBox<String> comboSelect;

    @FXML
    private TextField selectText;

    @FXML
    private Button saveToFileButton;

    @FXML
    private TextArea resultText;

    @FXML
    private Button updatePersonButton;

    @FXML
    private Button updateProgectButton;

    @FXML
    private Button updateBugButton;

    @FXML
    private Button exitButton;


    @FXML
    void select(ActionEvent event) {
        if (comboSelect.getSelectionModel().getSelectedIndex() >= 2) selectText.setDisable(false);
        else selectText.setDisable(true);
    }

    @FXML
    void initialize() {


        ObservableList<String> selectList = FXCollections.observableArrayList("Получить список всех пользователей",
                "Получить список всех проектов",
                "Получить список всех задач в проекте",
                "Получить список всех задач, назначенных на исполнителя");
        comboSelect.setItems(selectList);
        fileReadButton.setOnAction(this::fileReadButtonAction);
        updatePersonButton.setOnAction(this::updatePersonButtonAction);
        updateProgectButton.setOnAction(this::UpdateProgectButtonAction);
        updateBugButton.setOnAction(this::UpdateBugButtonAction);
        selectButton.setOnAction(this::selectButtonAction);
        saveToFileButton.setOnAction(this::saveToFileAction);
        exitButton.setOnAction(actionEvent -> {
            try {
                exitButtonAction(actionEvent);
            } catch (SQLException throwables) {
                throwables.printStackTrace();
            }
        });
    }

    private void exitButtonAction(ActionEvent actionEvent) throws SQLException {
        if (PaneLocal.isDisable() == false) { // если вообще был загружен файл, то удаляем все таблицы
            DataBase db = new DataBase();
            db.open();
            db.dropTable("bugs");
            db.dropTable("progects");
            db.dropTable("persons");
            db.close();
        }
            Platform.exit();
    }

    private void selectButtonAction(ActionEvent actionEvent) {
        String table = "";
        String column = "";
        String value = "";
        String order = "";
        resultText.setText(null);
        ResultSet rs = null;
        int index = 0;

        String str = comboSelect.getSelectionModel().getSelectedItem().toString();

        if (comboSelect.getSelectionModel().getSelectedIndex() == 0) {// получить список всех пользователей
            table = "persons";
            column = "person";
            resultText.appendText("Пользователи:" + "\n");     // заголовок таблицы
            index = 0;
        }
        if (comboSelect.getSelectionModel().getSelectedIndex() == 1) {// получить список всех проектов
            table = "progects";
            column = "progect";
            resultText.appendText("Проекты:" + "\n");     // заголовок таблицы
            index = 1;
        }
        if (comboSelect.getSelectionModel().getSelectedIndex() == 2) {// Получить список всех задач в проекте
            column = "progect";
            value = selectText.getText();
            order = "priority";
            resultText.appendText("Проект" + "\t|" + "Тема" + "\t|" + "Тип" +
                    "\t|" + "Приоритет" + "\t|" + "Исполнитель" + "\t|" + "Описание" + "\t|" +  "\n");     // заголовок таблицы
            index = 2;
        }
        if (comboSelect.getSelectionModel().getSelectedIndex() == 3) {// Получить список всех задач, назначенных на исполнителя
            column = "person";
            value = selectText.getText();
            order = "progect";
            resultText.appendText("Проект\t| Тема\t| Тип\t| Приоритет\t| Исполнитель\t| Описание\t" +  "\n");     // заголовок таблицы
            index = 3;
        }
            try {
                DataBase db = new DataBase();
                db.open();
                int counter = 0;
                resultText.appendText("\n");

                if (1 >= index) {
                    rs = db.selectOneColumnAllTable(table, column);// формируем список из таблицы
                    while (rs.next()) {
                        counter++;
                        resultText.appendText(rs.getString(column) + "\n");
                    }
                }
                else {
                    int countP = 0;
                    if (2 == index) {
                        str = "проект";
                        rs = db.selectOneColumnTable("progects", "progect", value);// проверяем, существует ли такой проект
                        while (rs.next()) countP++;
                    }
                    if (3 == index) {
                        str = "исполнитель";
                        rs = db.selectOneColumnTable("persons", "person", value);// проверяем, существует ли такой исполнитель
                        while (rs.next()) countP++;
                    }
                    if (0 < countP) { // если указанный проект/пользователь существует, то выполняем запрос
                        rs = db.selectBugWhere(column, value, order); // формируем список из таблицы "Задачи" по условию
                        while (rs.next()) {
                            counter++;
                            resultText.appendText(rs.getString("progect") + "\t| " +
                                    rs.getString("topic") + "\t| " +
                                    rs.getString("type") + "\t| " +
                                    rs.getInt("priority") + "\t| " +
                                    rs.getString("person") + "\t| " +
                                    rs.getString("description") + "\n");
                        }
                    }
                    else {
                        str = "Указанный " + str + " отсутствует в таблице!";
                        ShowWindow sw = new ShowWindow();
                        sw.alertWindow("Невозможно выполнить команду", str);
                    }
                }

        resultText.appendText("\n");
                resultText.appendText("Количество записей: " + counter + "\n");
                rs.close();
                db.close();
            } catch (SQLException throwables) {
                throwables.printStackTrace();
            }
    }

    private void updatePersonButtonAction(ActionEvent event) {// кнопка "Создать/удалить пользователя"
        ShowWindow sw = new ShowWindow();
        sw.showWindow("/sample/newPerson.fxml");// отображаем окно для ввода данных

    }

    private void UpdateProgectButtonAction(ActionEvent event) {// кнопка "Создать/удалить пользователя"
        ShowWindow sw = new ShowWindow();
        sw.showWindow("/sample/newProgect.fxml");// отображаем окно для ввода данных

    }

    private void UpdateBugButtonAction(ActionEvent event) {// кнопка "Создать/удалить пользователя"
        ShowWindow sw = new ShowWindow();
        sw.showWindow("/sample/newBug.fxml");// отображаем окно для ввода данных
    }


    private void fileReadButtonAction(ActionEvent event) { // нажатие на кнопку "Выбрать файл для загрузки данных"
        PaneLocal.setDisable(false); // делаем видимой рабочую область
        fileReaderWriter flRW = new fileReaderWriter();
        flRW.fileReader(event);
    }

    private void saveToFileAction(ActionEvent event) {
        fileReaderWriter frw = new fileReaderWriter();
        frw.fileWriter(event);
    }
}