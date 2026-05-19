package edu.cit.carin.payweac;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.Statement;

public class DbFix {
    public static void main(String[] args) {
        String url = "jdbc:postgresql://ep-restless-smoke-an8zs3xi-pooler.c-6.us-east-1.aws.neon.tech/neondb?sslmode=require";
        String user = "neondb_owner";
        String pass = "npg_aIOy7gqerhR9";

        try (Connection conn = DriverManager.getConnection(url, user, pass)) {
            System.out.println("Connected to DB. Fixing constraints...");
            try (Statement stmt = conn.createStatement()) {
                stmt.execute("ALTER TABLE rents DROP CONSTRAINT rents_status_check");
                System.out.println("Successfully dropped rents_status_check constraint!");
                
                // Also drop constraint on payments if it exists for status?
                // The payments status is PENDING, APPROVED, REJECTED which hasn't changed.
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
