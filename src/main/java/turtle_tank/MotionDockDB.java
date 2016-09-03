package turtle_tank;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.Statement;
import java.sql.ResultSet;

public class MotionDockDB implements Runnable {
    private static volatile boolean motion = false;
    private static final String TEMPVARS = "INSERT INTO MotionDockDB(date_time, motion) " +
            "VALUES (now(), ?)";

    @Override
    public void run() {
        String temp = "jdbc:mysql://localhost:3306/TurtleTank";
        String user = "";
        String password = "";

        try {
            Class.forName("com.mysql.jdbc.Driver").newInstance();
            Connection conn = DriverManager.getConnection(temp, user, password);

            // prepared statement temps
            PreparedStatement pStmt = conn.prepareStatement(TEMPVARS);
            pStmt.setBoolean(1, motion);
            pStmt.execute();

            // get count of columns, only care for last hours data
            Statement stmt = conn.createStatement();
            ResultSet rs = stmt.executeQuery("SELECT COUNT(*) FROM MotionDockDB");
            rs.next();
            int rowCount = rs.getInt(1);

            if(rowCount > 60) {
                int difference = rowCount - 60;
                String query = "DELETE FROM MotionDockDB ORDER BY date_time LIMIT " + difference;
                stmt.executeUpdate(query);
            }

            // close connection
            rs.close();
            stmt.close();
            conn.close();
        } catch(Exception e) {
            System.err.println("Error: MotionDockDB Database");
        }
    }

    public static void setMotion(boolean motion) {
        MotionDockDB.motion = motion;
    }
}
