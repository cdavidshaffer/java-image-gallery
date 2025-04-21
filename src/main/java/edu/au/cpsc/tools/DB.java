package edu.au.cpsc.tools;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;

public class DB {

    public static final String DB_HOST = "demo.c923ckbw7nvl.us-east-2.rds.amazonaws.com";
    public static final String DB_NAME = "image_gallery";
    public static final String DB_USER = "image_gallery";

    private static String getPassword() {
        try (BufferedReader br = new BufferedReader(new FileReader("/home/ec2-user/.pgpasswd"))) {
            return br.readLine();
        } catch (IOException ex) {
            System.err.println("Error reading passwd from .pgpasswd.  Make sure the file exists in /home/ec2-user: "
                    + ex.getMessage());
            System.exit(1);
        }
        return null;
    }

    private Connection connection;

    public void connect() throws SQLException {
        String url = "jdbc:postgresql://" + DB_HOST + "/" + DB_NAME;
        Properties props = new Properties();
        props.setProperty("user", DB_USER);
        props.setProperty("password", getPassword());
        connection = DriverManager.getConnection(url, props);
    }

    /**
     * select username,password,full_name from users;
     * 
     * List of:
     * 1) "username" -> "fred", "password" -> "foo", "full_name" -> "Fred
     * Flintstone"
     * 2) "username" -> "barney", "password" -> "wqwdwq", "full_name" -> "Barney
     * Rubbly"
     * ...
     * 
     * @param query
     * @return
     * @throws SQLException
     */
    public List<Map<String, Object>> execute(String query) throws SQLException {
        Statement st = connection.createStatement();
        ResultSet rs = st.executeQuery(query);
        ResultSetMetaData rsmd = rs.getMetaData();
        List<Map<String, Object>> result = new ArrayList<>();
        while (rs.next()) {
            Map<String, Object> row = new HashMap<>();
            for (int i = 1; i <= rsmd.getColumnCount(); i++) {
                row.put(rsmd.getColumnName(i), rs.getObject(i));
            }
            result.add(row);
        }
        rs.close();
        st.close();
        return result;
    }

    /**
     * Could be used for update or insert statement.
     */
    public void executeUpdate(String query, Object[] args) throws SQLException {
        PreparedStatement st = connection.prepareStatement(query);
        for (int i = 1; i <= args.length; i++)
            st.setObject(i, args[i - 1]);
        st.execute();
    }

    public static void main(String[] args) throws Exception {
        DB db = new DB();
        db.connect();
        System.out.println("\n\nBefore\n\n");
        var rowSet = db.execute("select username, password, full_name from users");
        for (var row : rowSet) {
            System.out.println(row.get("username") + "," + row.get("password") + "," + row.get("full_name"));
        }

        String newPassword = "new"; // user input
        String username = "fred"; // user input
        db.executeUpdate("update users set password=? where username=?", new String[] { newPassword, username });

        System.out.println("\n\nAfter\n\n");
        rowSet = db.execute("select username, password, full_name from users");
        for (var row : rowSet) {
            System.out.println(row.get("username") + "," + row.get("password") + "," + row.get("full_name"));
        }
    }

}
