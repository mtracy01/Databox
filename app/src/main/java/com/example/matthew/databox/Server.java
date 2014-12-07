package com.example.matthew.databox;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileInputStream;
import java.util.Properties;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.ServerSocket;
import java.net.Socket;

import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.widget.TextView;
import java.sql.*;

public class Server extends Activity{
    private static ServerSocket serverSocket;

    Handler updateHandler;
    Thread serverThread = null;
    public static final int SERVERPORT = 6000;

    public void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        updateHandler = new Handler();
        this.serverThread = new Thread(new ServerThread());

    }

    protected void onStop() {
        super.onStop();
        try {
            serverSocket.close();
        } catch( IOException e) {
            e.printStackTrace();
        }
    }
    static class ServerThread implements Runnable{
        String drivers = null;
        static ServerThread st = null;
        Connection conn = null;
        private BufferedReader input = null;
        private BufferedWriter bw = null;
        public Connection getConnection() throws SQLException, IOException {
            Properties props = new Properties();
            props.load(new FileInputStream("database.properties"));
            drivers = props.getProperty("jdbc.drivers");
            if(drivers != null){
                System.setProperty("jdbc.drivers", drivers);
            }
            String url = props.getProperty("jdbc.url");
            String username = props.getProperty("jdbc.username");
            String password = props.getProperty("jdbc.password");
            return DriverManager.getConnection(url,username,password);
        }
        /**public void uploadDatabase(String read){

        }**/

        public void checkUserID(String read, Connection c ){
            String nameAndPw = read.substring(read.indexOf(" ")+1);
            String userName = nameAndPw.substring(0,nameAndPw.indexOf(" "));
            String password = nameAndPw.substring(nameAndPw.indexOf(" ")+1);
            Statement stmt = null;
            try{
                Class.forName(drivers);
                stmt = c.createStatement();
                String sql = "SELECT * FROM user WHERE userName ='"+userName+"' AND password ='"+password+"'";
                ResultSet result = stmt.executeQuery(sql);
                if(result.next()){
                    String success = "Success";
                    bw.write(success, 0,  success.length());
                } else {
                        String fail = "Failure";
                        bw.write(fail, 0,  fail.length());
                }
            } catch (ClassNotFoundException e) {
                e.printStackTrace();
            } catch (SQLException e) {
                e.printStackTrace();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        private static void executeRequest(String buffer, Connection conn){
            if(buffer.contains("USERID")){
                st.checkUserID(buffer,conn);
            }
            else if (buffer.contains("GETFILES"){
                System.out.println("starving");
            }

        }

        public void run() {
            Socket socket;
            try {
                conn = st.getConnection();  //NOT SURE IF IT'S CORRECT
                serverSocket = new ServerSocket(SERVERPORT);
            } catch (IOException e) {
                e.printStackTrace();
            } catch (SQLException e) {
                e.printStackTrace();
            }
            while (!Thread.currentThread().isInterrupted()) {
                try{
                    socket = serverSocket.accept();
                    input = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                    String read = input.readLine();
                    executeRequest(read, conn);

                } catch( IOException e){
                    e.printStackTrace();
                }
            }
        }
    }

}
