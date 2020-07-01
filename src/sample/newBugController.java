package sample;

import java.net.URL;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ResourceBundle;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import sun.lwawt.macosx.CSystemTray;

public class newBugController {

    @FXML
    private ResourceBundle resources;

    @FXML
    private URL location;

    @FXML
    private TextField progectBugText;

    @FXML
    private TextField topicBugText;

    @FXML
    private TextField typeBugText;

    @FXML
    private TextField priorityBugText;

    @FXML
    private TextField personBugText;

    @FXML
    private TextArea descripBugText;

    @FXML
    private Button addBugButton;

    @FXML
    private Button deleteBugButton;

    @FXML
    private CheckBox progectCheck;

    @FXML
    private CheckBox topicCheck;

    @FXML
    private CheckBox typeCheck;

    @FXML
    private CheckBox priorityCheck;

    @FXML
    private CheckBox personCheck;

    @FXML
    private CheckBox descriptionCheck;


    @FXML
    void initialize() {

        addBugButton.setOnAction(event -> {
            String personText = personBugText.getText();//.trim();
            String progectText = progectBugText.getText();//.trim();
            String topicText = topicBugText.getText();
            String typeText = typeBugText.getText();
            String priorityText = priorityBugText.getText();
            String descriptText = descripBugText.getText();

            if(!personText.equals("") && !progectText.equals("")) {
                addNewBug (progectText, topicText, typeText, priorityText,personText, descriptText);

            } else {
                ShowWindow sw = new ShowWindow();
                sw.alertWindow("Проект и исполнитель не заполнены", "Для добавления задачи необходимо указать название проекта и исполнителя");
            }
        });

        deleteBugButton.setOnAction(event -> {

            try {
                delBug (progectBugText.getText(), topicBugText.getText(), typeBugText.getText(), priorityBugText.getText(), personBugText.getText(), descripBugText.getText());
            } catch (SQLException throwables) {
                throwables.printStackTrace();
            }
        });

    }

    private void delBug(String progect, String topic, String type, String priority, String person, String descript) throws SQLException {
       DataBase db = new DataBase();
       db.open();
        String whereStr = "";
        int counter = 0;
        if (progectCheck.isSelected()) {
            whereStr += "progect='" + progectBugText.getText() + "'";
            counter++;
        }
        if (topicCheck.isSelected()) {
            if (counter > 0) whereStr += " AND ";
            whereStr += "topic='" + topicBugText.getText() + "'";
            counter++;
        }
        if (typeCheck.isSelected()) {
            if (counter > 0) whereStr += " AND ";
            whereStr += "type='" + typeBugText.getText() + "'";
            counter++;
        }
        if (priorityCheck.isSelected()) {
            if (counter > 0) whereStr += " AND ";
            whereStr += "priority='" + priorityBugText.getText() + "'";
            counter++;
        }
        if (personCheck.isSelected()) {
            if (counter > 0) whereStr += " AND ";
            whereStr += "person='" + personBugText.getText() + "'";
            counter++;
        }
        if (descriptionCheck.isSelected()) {
            if (counter > 0) whereStr += " AND ";
            whereStr += "description='" + descripBugText.getText() + "'";
            counter++;
        }
        whereStr += ";";

        ResultSet rs = db.selectBugWhere(whereStr);
        counter = 0;
        while (true) {
            if (!rs.next()) break;
            counter++;
        }
        if (counter >= 1) {// если такая задача есть, то удаляем
            int countDel = db.deleteBug(whereStr);
            System.out.println("Удалено " + countDel + " задач по проекту " + progect + " для исполнителя " + person); // вывести в лог
            addBugButton.getScene().getWindow().hide();// закрываем окно
        }
        else if (counter == 0){
            ShowWindow sw = new ShowWindow();
            //String str = "Указанной задачи по проекту " + progect + " для исполнителя " + person + " не существует";
            sw.alertWindow("Невозможно удалить задачу", "Указанной задачи не существует!");
        }
       db.close();
        rs.close();
    }

    private void addNewBug(String progect, String topic, String type, String priority, String person, String descript) {
        DataBase db = new DataBase();
        db.open();
        try {
            ResultSet rs = db.selectOneColumnTable("persons", "person", person);// проверяем, существует ли такой исполнитель
            int counterPers = 0;
            while (rs.next()) counterPers++;
            rs = db.selectOneColumnTable("progects", "progect", progect);// проверяем, существует ли такой проект
            int counterProg = 0;
            while (rs.next()) counterProg++;

            if ((counterPers >= 1) && (counterProg >=1)) {// если такие исполнитель и проект существуют, то проверяем, существует ли такая задача
                rs = db.selectBug(progect, topic, type, priority, person, descript);// проверяем, существует ли такая задача
                int counter = 0;
                while (true) {
                    if (!rs.next()) break;
                    counter++;
                }
                if (counter == 0) {// если такой задачи нет, то создаем
                    if (priority.equals("")){// если приоритет не заполнен, тогда нам необходимо поставить следующий в проекте
                        rs = db.selectBugWhere("progect", progect, "priority"); // формируем список из таблицы "Задачи" по условию
                        int automatPrior = 0;
                        while (rs.next()) {
                            if (rs.getInt("priority") > automatPrior) automatPrior = rs.getInt("priority");
                        }
                        automatPrior++; // назначаем приоритет +1 к предыдущей задаче по проекту
                        priority += automatPrior;
                    }

                    db.updateTableBugs(progect, topic, type, priority, person, descript);
                    System.out.println("Добавлена задача по теме " + topic + " проекта " + progect + " для исполнителя " + person +
                      ", тип " + type + ", приоритет " + priority + ", описание задачи: " + descript); // вывести в лог
                    addBugButton.getScene().getWindow().hide();// закрываем окно
                }
                else {
                    ShowWindow sw = new ShowWindow();
                    String str = "Задача по теме " + topic + " проекта " + progect + " для исполнителя " + person +
                    ", тип " + type + ", приоритет " + priority + ", описание задачи: " + descript + "\n уже существует";
                    sw.alertWindow("Невозможно добавить задачу", str);
                }
            }
            else {
                ShowWindow sw = new ShowWindow();
                String str = "";
                if (counterPers == 0) {
                    str = "Пользователь " + person + " отсутствует!";
                }
                if (counterProg == 0) {
                    str = "Проект " + progect + " отсутствует!";
                }
                sw.alertWindow("Невозможно добавить задачу", str);
            }
            rs.close();

        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
        db.close();
    }
}