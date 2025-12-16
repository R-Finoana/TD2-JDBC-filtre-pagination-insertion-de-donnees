package connection;

import io.github.cdimascio.dotenv.Dotenv;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DBConnection {
    private static final Dotenv dotenv = Dotenv.configure().directory("/src/main/resources").load();

    private static final String jdbcUrl = dotenv.get("jdbcUrl");
    private static final String username = dotenv.get("username");
    private static final String password = dotenv.get("password");

    public static Connection getDBConnection(){
        try {
            assert jdbcUrl != null;
            return DriverManager.getConnection(jdbcUrl, username, password);
        } catch (SQLException e){
            throw new RuntimeException (e);
        }
    }
}
