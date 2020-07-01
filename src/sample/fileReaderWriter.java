package sample;

import javafx.event.ActionEvent;
import javafx.scene.Node;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import java.io.*;
import java.sql.ResultSet;
import java.sql.SQLException;
public class fileReaderWriter {

    public fileReaderWriter() {
    }

    public void fileWriter(ActionEvent event) {
        String line;// считываемая целиком строка
        String[] subLine;// массив элементов строки
        String tableName; // название текущей таблицы
        boolean flag = false; //признак того, что в следующей строке будут заголовки столбцов
        String queryLine;

        // выбираем файл и грузим из него данные
        Node sourse = (Node) event.getSource();
        Stage primaryStage = (Stage) sourse.getScene().getWindow();

        FileChooser fileChooser = new FileChooser();
        FileChooser.ExtensionFilter tXTFilter = new FileChooser.ExtensionFilter("TXT files (*.txt)", "*.txt");
        fileChooser.getExtensionFilters().add(tXTFilter);
        fileChooser.getExtensionFilters().addAll(tXTFilter);
        fileChooser.setTitle("Выбор файла");

        try {
            File file = fileChooser.showSaveDialog(primaryStage);
            System.out.println("Выбран файл для записи: " + file.getPath() + "\n"); // пишем путь выбранного файла в лог
            // записываем в файл

            if (file != null) {
                ResultSet rs = null;
                if (file.exists())
                    file.createNewFile();
                DataBase db = new DataBase();
                db.open();
                PrintWriter pw = new PrintWriter(file);

                // пишем таблицу "Задачи:"
                pw.println("Задачи:\nПроект\t Тема\t Тип\t Приоритет\t Исполнитель\t Описание");
                rs = db.selectAllBugs();
                while (rs.next()) {
                    pw.println(rs.getString("progect") + "\t" +
                            rs.getString("topic") + "\t" +
                    rs.getString("type") + "\t" +
                    rs.getInt("priority") + "\t" +
                    rs.getString("person") + "\t" +
                    rs.getString("description"));
                }

                // пишем таблицу "Пользователи:"
                pw.println("\nПользователи:");
                rs = db.selectOneColumnAllTable("persons", "person");
                while (rs.next())
                    pw.println(rs.getString("person"));


                // пишем таблицу "Проекты:"
                pw.println("\nПроекты:");
                rs = db.selectOneColumnAllTable("progects", "progect");
                while (rs.next())
                    pw.println(rs.getString("progect"));

                pw.close();
                db.close();
            }

        } catch (IOException | SQLException e) {
            System.out.print("Error: " + e);
        }
    }

    public void fileReader(ActionEvent event) {
        String line;// считываемая целиком строка
        String[] subLine;// массив элементов строки
        String tableName; // название текущей таблицы
        boolean flag = false; //признак того, что в следующей строке будут заголовки столбцов
        boolean flagBug = false;
        boolean flagPers = false;
        boolean flagProg = false;

        // выбираем файл и грузим из него данные
        Node sourse = (Node) event.getSource();
        Stage primaryStage = (Stage) sourse.getScene().getWindow();

        FileChooser fileChooser = new FileChooser(); // Перенести в другое место !!!!!!
        FileChooser.ExtensionFilter tXTFilter = new FileChooser.ExtensionFilter("TXT files (*.txt)", "*.txt");
        fileChooser.getExtensionFilters().add(tXTFilter);
        fileChooser.getExtensionFilters().addAll(tXTFilter);
        fileChooser.setTitle("Выбор файла");

        BufferedReader br = null;
        try {
            File file = fileChooser.showOpenDialog(primaryStage);
            if (file != null) {
                System.out.println("Выбран файл для загрузки данных: " + file.getPath() + "\n"); // пишем путь выбранного файла в лог
                // читаем файл
                FileReader reader = new FileReader(file);
                br = new BufferedReader(reader);

                DataBase db = new DataBase();
                db.open();
                int counter = 0;// счетчик считанных таблиц
                while ((line = br.readLine()) != null) {
                    if (line.equals("")) {// таблицы должны быть разделены пустой строкой - это признак окончания таблицы
                        flagBug = false;
                        flagPers = false;
                        flagProg = false;
                    }

                    line = line.replace("\t\t", "\t"); // заменяем 2 таба подряд на 1
                    subLine = line.split("\t");

                    if ((subLine[0].equals("Задачи") || subLine[0].equals("Задачи:")) && (subLine.length == 1)) {
                        counter++;
                        db.createTableBugs();//создаем таблицу "Задачи", если она еще не существует
                        flag = true; // признак того, что в следующей строке будут заголовки столбцов
                        flagBug = true;
                        continue; //  название таблицы не пишем в БД
                    }
                    if (flag == true) {
                        flag = false;
                        continue;// тут заголовки столбцов, их не пишем в БД
                    }

                    if ((subLine.length > 1) && (flag == false) && (flagBug == true)) { // если не заголовок, то читаем таблицу
                        db.updateTableBugs(subLine);
                        continue;
                    }

                    if ((subLine[0].equals("Пользователи") || subLine[0].equals("Пользователи:")) && (subLine.length == 1)) {
                        flagPers = true;
                        counter++;
                        db.createTable("persons", "person");
                        continue;
                    }
                    if (flagPers == true) {
                        db.insertOneColumn("persons", "person", subLine[0]);
                        continue;
                    }

                    if ((subLine[0].equals("Проекты") || subLine[0].equals("Проекты:")) && (subLine.length == 1)) {
                        flagProg = true;
                        counter++;
                        db.createTable("progects", "progect");
                        continue;
                    }
                    if (flagProg == true) {
                        db.insertOneColumn("progects", "progect", subLine[0]);
                        continue;
                    }
                }
                System.out.println("считано таблиц: " + counter);

                if (counter < 2) { // если в файле только таблица с задачами
                    db.createTablePersons();// создаем таблицы "Пользователи", "Проекты" выборкой SELECT
                    db.createTableProgects();
                }
                br.close();
                db.close();
            }
        } catch (IOException | SQLException e) {
            System.out.print("Error: " + e);
        }
    }
}
