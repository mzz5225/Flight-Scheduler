
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;

public class Flight {

    DBConnection connection = new DBConnection();
    final String SELECT_QUERY = "SELECT NAME FROM FLIGHT";
    final String SELECT_QUERY2 = "SELECT FLIGHT FROM BOOKINGS WHERE CUSTOMER = ? AND DAY = ?";
    final String SELECT_QUERY3 = "SELECT FLIGHT FROM WAITLIST WHERE CUSTOMER = ? AND DAY = ?";
    final String SELECT_CUSTOMERS_BOOKINGS = "SELECT CUSTOMER, DAY FROM BOOKINGS WHERE FLIGHT = ?";
    final String SELECT_CUSTOMERS_WAITLIST = "SELECT CUSTOMER, DAY FROM WAITLIST WHERE FLIGHT = ?";
    final String DELETE_FLIGHT_BOOKING = "DELETE FROM BOOKINGS WHERE FLIGHT = ?";
    final String DELETE_FLIGHT_WAITLIST = "DELETE FROM WAITLIST WHERE FLIGHT = ?";
    final String DELETE_FLIGHT = "DELETE FROM FLIGHT WHERE NAME = ?";

    public ArrayList<String> getFlights() {
        
        Connection con = connection.getConnection();
        ArrayList<String> flight = new ArrayList();

        try {
            PreparedStatement statement = con.prepareStatement(SELECT_QUERY);
            ResultSet resultSet = statement.executeQuery();

            while (resultSet.next()) {
                flight.add(resultSet.getObject("name", String.class));
            }
        } catch (SQLException ex) {
            Logger.getLogger(Customer.class.getName()).log(Level.SEVERE, null, ex);
        }

        return flight;
    }
    
    public ArrayList<String> getCustomerBookingFlights(String customer, String date) {
        
        Connection con = connection.getConnection();
        ArrayList<String> flight = new ArrayList();

        try {
            PreparedStatement statement = con.prepareStatement(SELECT_QUERY2);
            statement.setString(1, customer);
            statement.setString(2, date);
            ResultSet resultSet = statement.executeQuery();

            while (resultSet.next()) {
                flight.add(resultSet.getObject("flight", String.class));
            }
        } catch (SQLException ex) {
            Logger.getLogger(Customer.class.getName()).log(Level.SEVERE, null, ex);
        }

        return flight;
    }
    
    public ArrayList<String> getCustomerWaitlistFlights(String customer, String date) {
        
        Connection con = connection.getConnection();
        ArrayList<String> flight = new ArrayList();

        try {
            PreparedStatement statement = con.prepareStatement(SELECT_QUERY3);
            statement.setString(1, customer);
            statement.setString(2, date);
            ResultSet resultSet = statement.executeQuery();

            while (resultSet.next()) {
                flight.add(resultSet.getObject("flight", String.class));
            }
        } catch (SQLException ex) {
            Logger.getLogger(Customer.class.getName()).log(Level.SEVERE, null, ex);
        }

        return flight;
    }
    
    public boolean addFlight(String flight, int seats) {
        
        Connection con = connection.getConnection();
        
        try {
            PreparedStatement statement = con.prepareStatement(
                    "INSERT INTO FLIGHT (NAME, SEATS)"
                    + "VALUES (?, ?)"
            );
            
            statement.setString(1, flight);
            statement.setInt(2, seats);
            statement.execute();
            return true;
        } catch (SQLException ex) {
            return false;
        }
    }
    
    public String removeFlight(String flight) {
        
        BookingEntry bookingEntry = new BookingEntry();
        
        Connection con = connection.getConnection();
        StringBuilder builder = new StringBuilder();
        
        try {
            PreparedStatement statement = con.prepareStatement(SELECT_CUSTOMERS_BOOKINGS);
            statement.setString(1, flight);
            ResultSet resultSet = statement.executeQuery();
            
            PreparedStatement statement6 = con.prepareStatement(SELECT_CUSTOMERS_BOOKINGS);
            statement6.setString(1, flight);
            ResultSet resultSet3 = statement6.executeQuery();
            
            PreparedStatement statement2 = con.prepareStatement(SELECT_CUSTOMERS_WAITLIST);
            statement2.setString(1, flight);
            ResultSet resultSet2 = statement2.executeQuery();
            
            PreparedStatement statement3 = con.prepareStatement(DELETE_FLIGHT_BOOKING);
            statement3.setString(1, flight);
            statement3.execute();
            
            PreparedStatement statement4 = con.prepareStatement(DELETE_FLIGHT_WAITLIST);
            statement4.setString(1, flight);
            statement4.execute();
            
            PreparedStatement statement5 = con.prepareStatement(DELETE_FLIGHT);
            statement5.setString(1, flight);
            statement5.execute();
            
            builder.append(String.format("Flight %s has been removed!\n", flight));
            
            while (resultSet.next()) {
                String customer = resultSet.getObject("customer", String.class);
                String date = resultSet.getObject("day", String.class);
                
                builder.append(String.format("%s was removed from flight %s on %s\n", customer, flight, date));
            }
            
            builder.append("\n");
            
            while (resultSet2.next()) {
                String customer = resultSet2.getObject("customer", String.class);
                String date = resultSet2.getObject("day", String.class);
                
                builder.append(String.format("%s was removed from flight %s waitlist on %s\n", customer, flight, date));
            }
            
            builder.append("\n");
            
            while (resultSet3.next()) {
                String customer = resultSet3.getObject("customer", String.class);
                String date = resultSet3.getObject("day", String.class);
                boolean rebooked = false;
                
                ArrayList<String> flights = getFlights();
                
                for (String flightName : flights) {
                    boolean isFilled = bookingEntry.checkIfBooked(customer, flightName, date);
                    
                    if (isFilled == false) {
                        bookingEntry.setBookingEntry(customer, flightName, date);
                        builder.append(String.format("%s has been rebooked on flight %s on %s\n", customer, flightName, date));
                        rebooked = true;
                        break;
                    }
                }
                
                if (rebooked == false) {
                    builder.append(String.format("%s could not be rebooked for any flights on %s\n", customer, date));
                }
            }
        } catch (SQLException ex) {
            Logger.getLogger(Customer.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        return builder.toString();
    }
}