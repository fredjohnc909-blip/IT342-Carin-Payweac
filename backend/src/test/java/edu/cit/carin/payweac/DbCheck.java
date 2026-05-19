package edu.cit.carin.payweac;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

public class DbCheck {
    public static void main(String[] args) {
        String url = "jdbc:postgresql://ep-restless-smoke-an8zs3xi-pooler.c-6.us-east-1.aws.neon.tech/neondb?sslmode=require";
        String user = "neondb_owner";
        String pass = "npg_aIOy7gqerhR9";

        try (Connection conn = DriverManager.getConnection(url, user, pass)) {
            System.out.println("Connected to DB.");
            
            // Check column type for 'status' in 'rents' table
            try (PreparedStatement ps = conn.prepareStatement(
                    "SELECT data_type, udt_name FROM information_schema.columns WHERE table_name = 'rents' AND column_name = 'status'")) {
                try (ResultSet rs = ps.executeQuery()) {
                    if (rs.next()) {
                        System.out.println("Rents.status type: " + rs.getString("data_type") + " / " + rs.getString("udt_name"));
                    }
                }
            }

            // Check constraints
            try (PreparedStatement ps = conn.prepareStatement(
                    "SELECT conname, pg_get_constraintdef(c.oid) FROM pg_constraint c " +
                    "JOIN pg_class t ON c.conrelid = t.oid " +
                    "WHERE t.relname = 'rents' AND contype = 'c'")) {
                try (ResultSet rs = ps.executeQuery()) {
                    while (rs.next()) {
                        System.out.println("Constraint: " + rs.getString(1) + " -> " + rs.getString(2));
                    }
                }
            }
            
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
