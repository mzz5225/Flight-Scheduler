
import java.sql.*;
import java.util.ArrayList;
import java.util.logging.*;

public class BookingEntry {

    final private DBConnection connection = new DBConnection();
    final String SELECT_QUERY = "SELECT CUSTOMER, FLIGHT, DAY, TIMESTAMP FROM BOOKINGS WHERE DAY = ? AND FLIGHT = ?";
    final String SELECT_QUERY2 = "SELECT CUSTOMER, FLIGHT, DAY, TIMESTAMP FROM WAITLIST WHERE DAY = ? AND FLIGHT = ?";
    final String SELECT_CUSTOMER = "SELECT COUNT(*) FROM BOOKINGS WHERE CUSTOMER = ?";
    final String NUM_SEATS = "SELECT SEATS FROM FLIGHT WHERE NAME = ?";
    final String DELETE_BOOKING = "DELETE FROM BOOKINGS WHERE CUSTOMER = ? AND FLIGHT = ? AND DAY = ?";
    final String DELETE_WAITLIST = "DELETE FROM WAITLIST WHERE CUSTOMER = ? AND FLIGHT = ? AND DAY = ?";
    private int numSeats;
    private Boolean isFilled;
    

    public boolean setBookingEntry(String customer, String flight, String date) {
        Connection con = connection.getConnection();
        Day day = new Day();

        try {
            PreparedStatement statement = con.prepareStatement(
                    "INSERT INTO BOOKINGS (CUSTOMER, FLIGHT, DAY, TIMESTAMP)"
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
    
    public boolean checkIfBooked(String customer, String flight, String date) {
        int flightsBooked = 0;
        Connection con = connection.getConnection();
        
        try {
            PreparedStatement statement = con.prepareStatement(SELECT_QUERY);
            statement.setString(1, date);
            statement.setString(2, flight);
            ResultSet resultSet = statement.executeQuery();
            
            PreparedStatement statement2 = con.prepareStatement(NUM_SEATS);
            statement2.setString(1, flight);
            ResultSet resultSet2 = statement2.executeQuery();
            
            while (resultSet2.next()) {
                numSeats = resultSet2.getObject("seats", Integer.class);
            }
            while (resultSet.next()) {
                flightsBooked += 1;
            }
            
            if (flightsBooked >= numSeats) {
                isFilled = true;
            } else {
                isFilled = false;
            }
        } catch (SQLException ex) {
            Logger.getLogger(Customer.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        return isFilled;
    }
    
    public boolean onBookingList(String customer) {
        Connection con = connection.getConnection();
        int count = 0;
        boolean onList = false;
        
        try {
            PreparedStatement statement = con.prepareStatement(SELECT_CUSTOMER);
            statement.setString(1, customer);
            ResultSet resultSet = statement.executeQuery();
            
            if (resultSet.next()) {
                count = resultSet.getInt(1);
            }
            
            if (count != 0) {
                onList = true;
            } else {
                onList = false;
            }    
        } catch (SQLException ex) {
            Logger.getLogger(Customer.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        return onList;
    }

    public String getBookingEntry(String flight, String date) {
        Connection con = connection.getConnection();
        StringBuilder builder = new StringBuilder();

        try {
            PreparedStatement statement = con.prepareStatement(SELECT_QUERY);
            statement.setString(1, date);
            statement.setString(2, flight);
            ResultSet resultSet = statement.executeQuery();
            ResultSetMetaData metaData = resultSet.getMetaData();
            int numberOfColumns = metaData.getColumnCount();

            builder.append(String.format("Customers who have booked flight %s on %s:\n\n", flight, date));

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
    
    public ArrayList<String> removeBookingEntry(String customer, String flight, String date) {
        Connection con = connection.getConnection();
        ArrayList<String> newBookings = new ArrayList();
        
        try {
            PreparedStatement statement = con.prepareStatement(DELETE_BOOKING);
            statement.setString(1, customer);
            statement.setString(2, flight);
            statement.setString(3, date);
            statement.execute();
            
            PreparedStatement statement2 = con.prepareStatement(SELECT_QUERY2);
            statement2.setString(1, date);
            statement2.setString(2, flight);
            ResultSet resultSet = statement2.executeQuery();
            
            if (resultSet.next()) {
                String customer2 = resultSet.getString(1);
                String flight2 = resultSet.getString(2);
                String date2 = resultSet.getString(3);
                
                newBookings.add(customer2);
                newBookings.add(flight2);
                newBookings.add(date2);
                
                setBookingEntry(customer2, flight2, date2);
                
                PreparedStatement statement3 = con.prepareStatement(DELETE_WAITLIST);
                statement3.setString(1, customer2);
                statement3.setString(2, flight2);
                statement3.setString(3, date2);
                statement3.execute();
            }
            

        } catch (SQLException ex) {
            Logger.getLogger(Customer.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        return newBookings;
    }
}
