/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package server;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.UUID;
import java.util.logging.Level;
import java.util.logging.Logger;
import shared.ClientType;

/**
 *
 * @author Jeroen0606 & Igor
 */
public class Databasemanager {

    Connection conn;

    public Databasemanager() {
        String url = "jdbc:mysql://db4free.net:3306/";
        String dbName = "pts4photostore";
        String userName = "pts4";
        String password = "photostore";
        try {
            Class.forName("com.mysql.jdbc.Driver");
            conn = DriverManager.getConnection(url + dbName, userName, password);
        } catch (Exception ex) {
            System.out.println("Connecting to database failed");
            Logger.getLogger(Databasemanager.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public boolean login(ClientType type, String email, String password) {
        try {
            String sql = "SELECT * FROM `" + type.toString() + "` WHERE email = ? AND password = ? AND status = '1';";
            PreparedStatement ps = conn.prepareStatement(sql);
            ps.setString(1, email.toLowerCase());
            ps.setString(2, password);
            return ps.executeQuery().first();
        } catch (SQLException ex) {
            Logger.getLogger(Databasemanager.class.getName()).log(Level.SEVERE, null, ex);
            return false;
        }
    }

    public boolean registerUser(String email, String password, String name, String address, String zipcode, String city, String country, String phone) {
        try {
            String sql = "INSERT INTO user(email, password, name, address, zipcode, city, country, phone) VALUES (?, ?, ?, ?, ?, ?, ?, ?);";
            PreparedStatement ps = conn.prepareStatement(sql);
            ps.setString(1, email.toLowerCase());
            ps.setString(2, password);
            ps.setString(3, name);
            ps.setString(4, address);
            ps.setString(5, zipcode);
            ps.setString(6, city);
            ps.setString(7, country);
            ps.setString(8, phone);
            return ps.executeUpdate() != 0;
        } catch (SQLException ex) {
            Logger.getLogger(Databasemanager.class.getName()).log(Level.SEVERE, null, ex);
            return false;
        }
    }

    public boolean registerPhotographer(String email, String password, String name, String phone, String address, String zipcode, String city, String country, String kvk, String auth) {
        try {
            String sql = "UPDATE photographer SET password = ?, name = ?, address = ?, zipcode = ?, city = ?, country = ?, phone = ?, kvk = ?, status = '1' WHERE email = ? AND password = ? AND status = '0';";
            PreparedStatement ps = conn.prepareStatement(sql);
            ps.setString(1, password);
            ps.setString(2, name);
            ps.setString(3, address);
            ps.setString(4, zipcode);
            ps.setString(5, city);
            ps.setString(6, country);
            ps.setString(7, phone);
            ps.setString(8, kvk);
            ps.setString(9, email.toLowerCase());
            ps.setString(10, password);
            return ps.executeUpdate() != 0;
        } catch (SQLException ex) {
            Logger.getLogger(Databasemanager.class.getName()).log(Level.SEVERE, null, ex);
            return false;
        }
    }

    public boolean registerProducer(String email, String password, String name) {
        try {
            String sql = "INSERT INTO producer(email, password, name) VALUES (?, ?, ?);";
            PreparedStatement ps = conn.prepareStatement(sql);
            ps.setString(1, email.toLowerCase());
            ps.setString(2, password);
            ps.setString(3, name);
            return ps.executeUpdate() != 0;
        } catch (SQLException ex) {
            Logger.getLogger(Databasemanager.class.getName()).log(Level.SEVERE, null, ex);
            return false;
        }
    }

    public boolean createPhotographer(String email, String auth) {
        try {
            String sql = "INSERT INTO photographer(email, password) VALUES (?, ?);";
            PreparedStatement ps = conn.prepareStatement(sql);
            ps.setString(1, email.toLowerCase());
            ps.setString(2, auth);
            return ps.executeUpdate() != 0;
        } catch (SQLException ex) {
            Logger.getLogger(Databasemanager.class
                    .getName()).log(Level.SEVERE, null, ex);
            return false;
        }
    }
}
