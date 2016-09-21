package com.example;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;

import javax.json.Json;
import javax.json.stream.JsonGenerator;

public class MyClass {

    private static ServerSocket serverSocket;
    private static Socket clientSocket;
    private static InputStreamReader inputStreamReader;
    private static BufferedReader bufferedReader;
    private static String message;

    static String USER_NAME = "root";
    static String PASSWORD = "111111";
    static String url = "jdbc:mysql://localhost:3306/fifty";

    static String DBNAME = "fifty";
    static String TABLE_NAME = "Gigs";
    private static PrintWriter printwriter;

    public static void main(String[] args) {
        try {
            serverSocket = new ServerSocket(4444); // Server socket

        } catch (IOException e) {
            System.out.println("Could not listen on port: 4444");
        }

        System.out.println("Server started. Listening to the port 4444");

        while (true) {
            try {
                clientSocket = serverSocket.accept(); // accept the client connection
                inputStreamReader = new InputStreamReader(clientSocket.getInputStream());
                bufferedReader = new BufferedReader(inputStreamReader); // get the client message
                message = bufferedReader.readLine();
                System.out.println(message);
                 if (message.equals("initiate"))
                 {
                     // Establish the connection.
                    /* SQLServerDataSource ds = new SQLServerDataSource();
                     ds.setUser("root");
                     ds.setPassword("111111");
                     ds.setServerName("localhost");
                     ds.setPortNumber(3306);
                     ds.setDatabaseName("fifty");
                     ds.setDescription("database for fifty");*/
                     //Connection con = DriverManager.getConnection(url, USER_NAME, PASSWORD);
                     /*try{
                         Context ctx = new InitialContext();
                         ctx.bind("jdbc/fiftyDB", ds);
                     }catch (Exception ex) {
                         ex.printStackTrace();
                     }*/
                     Connection con = null;
                     Statement stmt = null;
                     String query = "select * " +
                             "from " + DBNAME + "."+ TABLE_NAME;
                     try {
                         //con = ds.getConnection(USER_NAME, PASSWORD);
                         //DriverManager.registerDriver(new com.microsoft.sqlserver.jdbc.SQLServerDriver());
                         //Class.forName("com.microsoft.sqlserver.jdbc.SQLServerDriver");
                         //con = DriverManager.getConnection(url, USER_NAME, PASSWORD);
                         Class.forName("com.mysql.jdbc.Driver").newInstance();
                         con = DriverManager.getConnection(url, USER_NAME, PASSWORD);
                         stmt = con.createStatement();
                         ResultSet rs = stmt.executeQuery(query);
                         System.out.println("rs = " + rs.toString());

                         PrintWriter printwriter = new PrintWriter(clientSocket.getOutputStream(),
                                 true);
                         JsonGenerator generator = Json.createGenerator(printwriter);
                         /*generate json stream*/
                         generator.writeStartObject().writeStartArray("gigs");
                         while (rs.next()) {
                             String title = rs.getString("title");
                             int keyId = rs.getInt("id");
                             String author = rs.getString("author");
                             int price = rs.getInt("price");
                             int score = rs.getInt("score");
                             System.out.println(title + "\t" + keyId +
                                     "\t" + author + "\t" + price +
                                     "\t" + score);

                             generator

                                     .writeStartObject()
                                     .write("id", keyId)
                                     .write("title", title)
                                     .write("author", author)
                                     .write("price", price)
                                     .write("score", score)
                                             .writeEnd();

                         }
                         generator.writeEnd().writeEnd();
                         generator.close();
                         // ... code to use the connection con
                     } catch (Exception ex) {
                     // ... code to handle exceptions
                     ex.printStackTrace();
                     } finally {
                             try{
                                 if (con != null) con.close();
                             }catch (Exception ex) {
                                 // ... code to handle exceptions
                                 ex.printStackTrace();
                             }
                     }

                     //con = ds.getConnection();
                 }

                //send message back to client
                /*PrintWriter printwriter = new PrintWriter(clientSocket.getOutputStream(),
                        true);*/

                //printwriter.println(message);
                //close client
                inputStreamReader.close();
                clientSocket.close();

            } catch (IOException ex) {
                System.out.println("Problem in message reading");
            }
        }

    }

}
