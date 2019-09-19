
import java.sql.*;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;

public class Customer {

    private DBConnection connection = new DBConnection();
    final String SELECT_QUERY = "SELECT NAME FROM CUSTOMERS";
    final String CUSTOMER_BOOKINGS = "SELECT CUSTOMER, FLIGHT, DAY, TIMESTAMP FROM BOOKINGS WHERE CUSTOMER = ?";
    final String CUSTOMER_WAITLIST = "SELECT CUSTOMER, FLIGHT, DAY, TIMESTAMP FROM WAITLIST WHERE CUSTOMER = ?";

    public boolean setCustomer(String name) {
        Connection con = connection.getConnection();

        try {
            PreparedStatement statement = con.prepareStatement(
                    "INSERT INTO CUSTOMERS (NAME)"
                    + "VALUES (?)"
            );
            
            statement.setString(1, name);
            statement.execute();
            return true;
        } catch (SQLException ex) {
            return false;
        }
    }

    public ArrayList<String> getCustomers() {
        Connection con = connection.getConnection();
        ArrayList<String> customers = new ArrayList();

        try {
            PreparedStatement statement = con.prepareStatement(SELECT_QUERY);
            ResultSet resultSet = statement.executeQuery();

            while (resultSet.next()) {
                customers.add(resultSet.getObject("name", String.class));
            }
        } catch (SQLException ex) {
            Logger.getLogger(Customer.class.getName()).log(Level.SEVERE, null, ex);
        }

        return customers;
    }
    
    public String getCustomerStatus(String name) {
        
        Connection con = connection.getConnection();
        StringBuilder builder = new StringBuilder();
        StringBuilder builder2 = new StringBuilder();

        try {
            PreparedStatement statement = con.prepareStatement(CUSTOMER_BOOKINGS);
            statement.setString(1, name);
            ResultSet resultSet = statement.executeQuery();
            ResultSetMetaData metaData = resultSet.getMetaData();
            int numberOfColumns = metaData.getColumnCount();
            
            PreparedStatement statement2 = con.prepareStatement(CUSTOMER_WAITLIST);
            statement2.setString(1, name);
            ResultSet resultSet2 = statement2.executeQuery();
            ResultSetMetaData metaData2 = resultSet2.getMetaData();

            builder.append(String.format("Flights that %s has booked:\n\n", name));
            builder2.append(String.format("Flights that %s are on the waitlist for:\n\n", name));

            for (int i = 1; i <= numberOfColumns; i++) {
                builder.append(String.format("%-8s\t", metaData.getColumnName(i)));
                builder2.append(String.format("%-8s\t", metaData2.getColumnName(i)));
            }
            builder.append("\n");
            builder2.append("\n");

            while (resultSet.next()) {
                for (int i = 1; i <= numberOfColumns; i++) {
                    builder.append(String.format("%-8s\t", resultSet.getObject(i)));
                }
                builder.append("\n");
            }
            
            while (resultSet2.next()) {
                for (int i = 1; i <= numberOfColumns; i++) {
                    builder2.append(String.format("%-8s\t", resultSet2.getObject(i)));
                }
                builder2.append("\n");
            }
        } catch (SQLException ex) {
            Logger.getLogger(Customer.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        String combined = builder.toString() + "\n\n" + builder2.toString();
        return combined;
    }
}
