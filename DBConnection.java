import java.sql.*;
import java.util.logging.Level;
import java.util.logging.Logger;

public class DBConnection {
    
    Connection con;
    
    public DBConnection() {
        try {
             con = DriverManager.getConnection(
                    "jdbc:derby://localhost:1527/FlightSchedulerDBMzz5225", "java", "java");
        } catch(Exception ex) {
            Logger.getLogger(Customer.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    public Connection getConnection() {
        return con;
    }

}
