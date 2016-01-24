package turtle_tank;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.Statement;
import java.sql.ResultSet;

public class TmpDB implements Runnable {

    private static volatile double baskingTemp = 0, waterTemp = 0, airTemp = 0;
    private static final String TEMPVARS = "INSERT INTO TmpDB(date_time, basking_temp, water_temp, air_temp) " +
            "VALUES (now(), ?, ?, ?)";

    @Override
    public void run() {
        String temp = "jdbc:mysql://localhost:3306/TurtleTank";
        String user = "*********";
        String password = "*********";

        try {
            Class.forName("com.mysql.jdbc.Driver").newInstance();
            Connection conn = DriverManager.getConnection(temp, user, password);

            // prepared statement temps
            PreparedStatement pStmt = conn.prepareStatement(TEMPVARS);
            pStmt.setDouble(1, baskingTemp);
            pStmt.setDouble(2, waterTemp);
            pStmt.setDouble(3, airTemp);
            pStmt.execute();

            // get count of columns, only care for last hours data
            Statement stmt = conn.createStatement();
            ResultSet rs = stmt.executeQuery("SELECT COUNT(*) FROM TmpDB");
            rs.next();
            int rowCount = rs.getInt(1);

            if(rowCount > 60) {
                int difference = rowCount - 60;
                String query = "DELETE FROM TmpDB ORDER BY date_time LIMIT " + difference;
                stmt.executeUpdate(query);
            }

            // close connection
            rs.close();
            stmt.close();
            conn.close();
        } catch(Exception e) {
            System.err.println("Error: TmpDB Database");
        }
    }

    synchronized void setBaskingWaterAir(float baskingTemp, float waterTemp, float airTemp) {
        this.baskingTemp = baskingTemp;
        this.waterTemp = waterTemp;
        this.airTemp = airTemp;
    }
}
