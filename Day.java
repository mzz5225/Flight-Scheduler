
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.sql.*;
import java.util.logging.Level;
import java.util.logging.Logger;

public class Day {

    private static final SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    final String SELECT_QUERY = "SELECT DATES FROM DATE";
    final String SELECT_QUERY2 = "SELECT DAY FROM BOOKINGS WHERE CUSTOMER = ?";
    final String SELECT_QUERY3 = "SELECT DAY FROM WAITLIST WHERE CUSTOMER = ?";
    DBConnection connection = new DBConnection();

    public String getCurrentTimestamp() {
        Timestamp timestamp = new Timestamp(System.currentTimeMillis());
        return sdf.format(timestamp);
    }
    
    public boolean addDay(String date) {
        Connection con = connection.getConnection();
        
        try {
            PreparedStatement statement = con.prepareStatement(
                    "INSERT INTO DATE (DATES)"
                    + "VALUES (?)"
            );
            
            statement.setString(1, date);
            statement.execute();
            return true;
        } catch (SQLException ex) {
            return false;
        }
    }

    public ArrayList<String> getDay() {
        Connection con = connection.getConnection();
        ArrayList<String> days = new ArrayList();

        try {
            PreparedStatement statement = con.prepareStatement(SELECT_QUERY);
            ResultSet resultSet = statement.executeQuery();
            
            while (resultSet.next()) {
                days.add(resultSet.getObject("dates", String.class));
            }
        } catch (SQLException ex) {
            Logger.getLogger(Customer.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        return days;
    }
    
    public ArrayList<String> getCustomerBookingDays(String customer) {
        Connection con = connection.getConnection();
        ArrayList<String> days = new ArrayList();

        try {
            PreparedStatement statement = con.prepareStatement(SELECT_QUERY2);
            statement.setString(1, customer);
            ResultSet resultSet = statement.executeQuery();
            
            while (resultSet.next()) {
                days.add(resultSet.getObject("day", String.class));
            }
        } catch (SQLException ex) {
            Logger.getLogger(Customer.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        return days;
    }
    
    public ArrayList<String> getCustomerWaitlistDays(String customer) {
        Connection con = connection.getConnection();
        ArrayList<String> days = new ArrayList();

        try {
            PreparedStatement statement = con.prepareStatement(SELECT_QUERY3);
            statement.setString(1, customer);
            ResultSet resultSet = statement.executeQuery();
            
            while (resultSet.next()) {
                days.add(resultSet.getObject("day", String.class));
            }
        } catch (SQLException ex) {
            Logger.getLogger(Customer.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        return days;
    }
}