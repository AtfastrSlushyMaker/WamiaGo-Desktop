package utils;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DataBase {
    private final String URL="jdbc:mysql://127.0.0.1:3306/wamia_go";
    private final String LOGIN="root";
    private final String PWD="";

    private Connection connection;
    private static DataBase instance;

    private DataBase(){
        try {
            connection = DriverManager.getConnection(URL,LOGIN,PWD);
            System.out.println("Connected");
        } catch (SQLException e) {
            System.err.println(e.getMessage());
        }
    }

    public static DataBase getInstance(){
        if(instance == null)
            instance = new DataBase();
        return instance;
    }

    public Connection getConnection() {
        return connection;
    }
}
