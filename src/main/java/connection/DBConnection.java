package connection;

import io.github.cdimascio.dotenv.Dotenv;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DBConnection {
    private final Dotenv dotenv = Dotenv.configure().load();

    private final String jdbcUrl = dotenv.get("jdbcUrl");
    private final String username = dotenv.get("username");
    private final String password = dotenv.get("password");

    public Connection getDBConnection(){
        try {
            assert jdbcUrl != null;
            return DriverManager.getConnection(jdbcUrl, username, password);
        } catch (SQLException e){
            throw new RuntimeException (e);
        }
    }
}
