package sample;

import java.sql.*;

import static java.lang.System.*;

public class DataBase {
    private static Connection co;

    public void open() //открытие БД
    {
        try
        {
            Class.forName("org.sqlite.JDBC");
            co = DriverManager.getConnection("jdbc:sqlite:bugs.db");
        } catch (Exception e) {
            out.println (e.getMessage());
        }
    }

    public void createTable(String table, String column) throws SQLException //создание таблицы "Пользователи"
    {
        String create = "CREATE TABLE IF NOT EXISTS " + table + " ('" + column + "' VARCHAR(50) NOT NULL);";
        updateTable(create);
    }

    public void createTableBugs() throws SQLException //создание таблицы "Задачи"
    {
            String create = "CREATE TABLE IF NOT EXISTS bugs ('id' INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, " +
                    "'progect' VARCHAR(50), 'topic' VARCHAR(50), 'type' VARCHAR(50), " +
                    "'priority' INTEGER, 'person' VARCHAR(50), 'description' TEXT);";
            updateTable(create);
    }

    public void createTablePersons() throws SQLException //создание таблицы "Пользователи"
    {
        String create = "CREATE TABLE IF NOT EXISTS persons AS SELECT DISTINCT person FROM bugs;";
        updateTable(create);
    }

    public void createTableProgects() throws SQLException //создание таблицы "Проекты"
    {
        String create = "CREATE TABLE IF NOT EXISTS progects AS SELECT DISTINCT progect FROM bugs;";
        updateTable(create);
    }

    private void updateTable(String query) throws SQLException //выполнение запроса и обновление таблицы
    {
        try {
            Statement statement = co.createStatement();
            statement.executeUpdate(query);
            statement.close();

        } catch (Exception e) {
            out.println (e.getMessage());
        }
    }

    public void updateTableBugs(String[] arrayStringQuary) throws SQLException //добавление записи в таблицу
    {
        String line ="'";
        for (int i = 0; i < arrayStringQuary.length-1; i++)
            line += arrayStringQuary[i] + "', '";
        line += arrayStringQuary[arrayStringQuary.length-1] + "'";
        line = "INSERT INTO bugs (progect, topic, type, priority, person, description) VALUES (" + line + ");";
        if (arrayStringQuary.length > 1)
            updateTable(line);
    }

    public void updateTableBugs(String progect, String topic, String type, String priority, String person, String description) throws SQLException //добавление записи в таблицу
    {
       String insert = "INSERT INTO bugs (progect, topic, type, priority, person, description) VALUES ('" +
                progect + "', '" + topic + "', '" +  type + "', '" + priority + "', '" + person + "', '" + description + "');";
        updateTable(insert);
    }

    public void insertOneColumn (String table, String column, String addRow) throws SQLException {
        String insert = "INSERT INTO " + table + " (" + column + ") VALUES ('" + addRow + "');";
        updateTable(insert);
    }

    public void deleteOneColumn (String table, String column, String delRow) throws SQLException {
        String delete = "DELETE FROM " + table + " WHERE " + column + "='" + delRow + "';";
        updateTable(delete);
    }

    public int deleteBug (String whereStr) throws SQLException {
        int rs = 0;
        String delete = "DELETE FROM bugs WHERE " + whereStr + ";";
        try {
            Statement statement = co.createStatement();
            rs = statement.executeUpdate(delete);
        } catch (Exception e) {
        out.println(e.getMessage());
        }
        return rs;
    }

    public ResultSet selectOneColumnTable(String table, String column, String row)
    {
        ResultSet rs = null;
        try {
            String select = "SELECT * FROM " + table +" WHERE " + column + "='" + row +"';";
            Statement statement = co.createStatement();
            rs = statement.executeQuery(select);

            rs.last();
            statement.close();
        } catch (Exception e) {
            out.println(e.getMessage());
        }
        return rs;
    }

    public ResultSet selectOneColumnAllTable(String table, String column)// вывод всей таблицы без условия
    {
        ResultSet rs = null;
        try {
            String select = "SELECT DISTINCT * FROM " + table +" ORDER BY " + column + ";";
            Statement statement = co.createStatement();
            rs = statement.executeQuery(select);

            rs.last();
            statement.close();
        } catch (Exception e) {
            out.println(e.getMessage());
        }
        return rs;
    }

   public ResultSet selectAllBugs() //вывод всей таблицы "Задачи" (без id)
   {
       ResultSet rs = null;
       try {
           Statement statement = co.createStatement();
           String select =
                   "SELECT progect, topic, type, priority, person, description " +
                           "FROM bugs " +
                           "ORDER BY progect;";

           rs = statement.executeQuery(select);

           rs.last();
           statement.close();
       } catch (Exception e) {
           out.println(e.getMessage());
       }
       return rs;
   }

    public ResultSet selectBug(String progect, String topic, String type, String priority, String person, String description) //вывод Задачи (без id)
    {
        ResultSet rs = null;
        try {
            Statement statement = co.createStatement();
            String select =
                    "SELECT * FROM bugs WHERE " +
                            "progect='" + progect + "' AND topic='" + topic + "' AND type='" + type +
                            "' AND priority='" + priority + "' AND person='" + person + "' AND description='" + description + "';";

            rs = statement.executeQuery(select);
            rs.last();
            statement.close();
        } catch (Exception e) {
            out.println(e.getMessage());
        }
        return rs;
    }

    public ResultSet selectBugWhere(String whereStr) //вывод Задачи с условием (без id)
    {
        ResultSet rs = null;
        try {
            Statement statement = co.createStatement();
            String select =
                    "SELECT * FROM bugs WHERE " + whereStr + ";";

            rs = statement.executeQuery(select);
            rs.last();
            statement.close();
        } catch (Exception e) {
            out.println(e.getMessage());
        }
        return rs;
    }

    public ResultSet selectBugWhere(String column, String value, String order) //вывод Задачи по условию (без id)
    {
        ResultSet rs = null;
        try {
            Statement statement = co.createStatement();
            String select =
                    "SELECT DISTINCT * FROM bugs WHERE " + column + "='" + value + "' ORDER BY " + order + ";";

            rs = statement.executeQuery(select);
            rs.last();
            statement.close();
        } catch (Exception e) {
            out.println(e.getMessage());
        }
        return rs;
    }

    public void dropTable(String table) throws SQLException {
       String drop = "DROP TABLE " + table + ";";
       updateTable(drop);
    }

    public void close()
    {
        try {
            co.close();
        } catch (Exception e) {
        out.println (e.getMessage());
        }
    }
}
