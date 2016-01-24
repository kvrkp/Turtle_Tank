package turtle_tank;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.Statement;
import java.sql.ResultSet;

public class SunRSDB implements Runnable {

    private static volatile String sunrise, sunset, breakfast, dinner;
    private static final String SUNRISESETVARS = "INSERT INTO SunRSDB(date_time, sunrise, sunset, breakfast, dinner) " +
            "VALUES (now(), ?, ?, ?, ?)";
    private static volatile boolean nightTime;

    @Override
    public void run() {
        String temp = "jdbc:mysql://localhost:3306/TurtleTank";
        String user = "*********";
        String password = "*********";

        try {
            Class.forName("com.mysql.jdbc.Driver").newInstance();
            Connection conn = DriverManager.getConnection(temp, user, password);

            // prepared statement temps
            PreparedStatement pStmt = conn.prepareStatement(SUNRISESETVARS);
            pStmt.setString(1, sunrise);
            pStmt.setString(2, sunset);
            pStmt.setString(3, breakfast);
            pStmt.setString(4, dinner);
            pStmt.execute();

            // get count of columns, only care for 7 days worth of data
            Statement stmt = conn.createStatement();
            ResultSet rs = stmt.executeQuery("SELECT COUNT(*) FROM TmpDB");
            rs.next();
            int rowCount = rs.getInt(1);

            if(rowCount > 7) {
                int difference = rowCount - 7;
                String query = "DELETE FROM TmpDB ORDER BY date_time LIMIT " + difference;
                stmt.executeUpdate(query);
            }

            // close connection
            rs.close();
            stmt.close();
            conn.close();
        } catch(Exception e) {
            System.err.println("Error: SunRSDB Database");
        }
    }

    synchronized void setSunRSDB_Values(String sunrise, String sunset, String breakfast, String dinner) {
        this.sunrise = sunrise;
        this.sunset = sunset;
        this.breakfast = breakfast;
        this.dinner = dinner;
    }

    public static String getSunrise() {
        return sunrise;
    }

    public static String getSunset() {
        return sunset;
    }

    public static String getBreakfast() {
        return breakfast;
    }

    public static String getDinner() {
        return dinner;
    }

    public static void setNightimeBool(String a) {
        SunRSDB.nightTime = (a.equals("true")) ? true : false;
    }

    public static boolean getNighttimeBool(){
        return SunRSDB.nightTime;
    }
}
