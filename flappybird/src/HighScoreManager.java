import java.sql.*;

public class HighScoreManager {

    // Store database file in user home directory for persistence
    // private final String DB_PATH = System.getProperty("user.home") +
    // "/flappy_highscore.db";
    private final String DB_URL = "jdbc:mysql://localhost:3306/flappy_highscore";

    public HighScoreManager() {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
        } catch (ClassNotFoundException e) {
            System.out.println("❌ MySQL JDBC Driver not found.");
            e.printStackTrace();
        }
        // System.out.println("📂 Database path: " + DB_PATH);
        createTableIfNotExists();
    }

    private void createTableIfNotExists() {
        try (Connection conn = DriverManager.getConnection(DB_URL,"root","");
                Statement stmt = conn.createStatement()) {

            stmt.executeUpdate("CREATE TABLE IF NOT EXISTS scores (id INTEGER PRIMARY KEY, highscore INTEGER)");

            ResultSet rs = stmt.executeQuery("SELECT COUNT(*) as count FROM scores");
            if (rs.next() && rs.getInt("count") == 0) {
                stmt.executeUpdate("INSERT INTO scores (id, highscore) VALUES (1, 0)");
                System.out.println("🆕 New high score row created.");
            } else {
                System.out.println("✅ High score row already exists.");
            }

        } catch (SQLException e) {
            System.out.println("❌ Error creating table:");
            e.printStackTrace();
        }
    }

    public int getHighScore() {
        try (Connection conn = DriverManager.getConnection(DB_URL,"root","");
                Statement stmt = conn.createStatement()) {

            ResultSet rs = stmt.executeQuery("SELECT highscore FROM scores WHERE id = 1");
            if (rs.next()) {
                int score = rs.getInt("highscore");
                System.out.println("📤 Loaded high score: " + score);
                return score;
            }

        } catch (SQLException e) {
            System.out.println("❌ Error reading high score:");
            e.printStackTrace();
        }
        return 0;
    }

    public void updateHighScore(int newScore) {
        try (Connection conn = DriverManager.getConnection(DB_URL,"root","");
                PreparedStatement pstmt = conn.prepareStatement("UPDATE scores SET highscore = ? WHERE id = 1")) {

            pstmt.setInt(1, newScore);
            pstmt.executeUpdate();
            System.out.println("✅ Updated high score to: " + newScore);

        } catch (SQLException e) {
            System.out.println("❌ Error updating high score:");
            e.printStackTrace();
        }
    }
}
