
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;

public class Waitlist {

    final private DBConnection connection = new DBConnection();
    final String SELECT_QUERY = "SELECT CUSTOMER, FLIGHT, DAY, TIMESTAMP FROM WAITLIST WHERE day = ?";
    final String SELECT_QUERY2 = "SELECT CUSTOMER, FLIGHT, DAY, TIMESTAMP FROM WAITLIST WHERE DAY = ? AND FLIGHT = ?";
    final String DELETE_WAITLIST = "DELETE FROM WAITLIST WHERE CUSTOMER = ? AND FLIGHT = ? AND DAY = ?";

    public boolean addWaitlistEntry(String customer, String flight, String date) {
        
        Connection con = connection.getConnection();
        Day day = new Day();

        try {
            PreparedStatement statement = con.prepareStatement(
                    "INSERT INTO WAITLIST (CUSTOMER, FLIGHT, DAY, TIMESTAMP)"
                    + "VALUES (?, ?, ?, ?)"
            );
            
            statement.setString(1, customer);
            statement.setString(2, flight);
            statement.setString(3, date);
            statement.setString(4, day.getCurrentTimestamp());
            statement.execute();
            
            return false;
        } catch (SQLException ex) {
            return true;
        }
    }

    public String getWaitlist(String date) {
        
        Connection con = connection.getConnection();
        StringBuilder builder = new StringBuilder();

        try {
            PreparedStatement statement = con.prepareStatement(SELECT_QUERY);
            statement.setString(1, date);
            ResultSet resultSet = statement.executeQuery();
            ResultSetMetaData metaData = resultSet.getMetaData();
            int numberOfColumns = metaData.getColumnCount();

            builder.append(String.format("Customers who are waitlisted on %s:\n\n", date));

            for (int i = 1; i <= numberOfColumns; i++) {
                builder.append(String.format("%-8s\t", metaData.getColumnName(i)));
            }
            builder.append("\n");

            while (resultSet.next()) {
                for (int i = 1; i <= numberOfColumns; i++) {
                    builder.append(String.format("%-8s\t", resultSet.getObject(i)));
                }
                builder.append("\n");
            }
        } catch (SQLException ex) {
            Logger.getLogger(Customer.class.getName()).log(Level.SEVERE, null, ex);
        }

        return builder.toString();
    }
    
    public boolean removeWaitlistEntry(String customer, String flight, String date) {
        Connection con = connection.getConnection();
        
        try {
            PreparedStatement statement = con.prepareStatement(DELETE_WAITLIST);
            statement.setString(1, customer);
            statement.setString(2, flight);
            statement.setString(3, date);
            statement.execute();
            
            return true;
        } catch (SQLException ex) {
            return false;
        }
    }
}