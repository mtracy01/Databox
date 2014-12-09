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
import java.sql.*;

public class Server extends Activity{
    private static ServerSocket serverSocket;
    Thread serverThread = null;
    public static final int SERVERPORT = 6000;
    Connection cnn;
    public void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        this.serverThread = new Thread(new ServerThread());
        this.serverThread.start();
    }

    protected void onStop() {
        super.onStop();
        try {
            serverSocket.close();
        } catch( IOException e) {
            e.printStackTrace();
        }
    }
    /**public static void main(String args[]){
        try{
            ServerSocket s = new ServerSocket(6000);
            while(true){
                Socket incoming = s.accept();
                System.out.println("Here\n");
                Runnable r = new ServerThread();
                Thread t = new Thread(r);
                t.start();
            }
        }catch(IOException e){
            e.printStackTrace();
        }
    }**/
   class ServerThread implements Runnable{
       public void run() {
           Socket socket = null;
           try{
               serverSocket = new ServerSocket(SERVERPORT);
           } catch (IOException e) {
               e.printStackTrace();
           }
           while(!Thread.currentThread().isInterrupted()){
               try{
                   socket = serverSocket.accept();
                   InteractClientServer interact = new InteractClientServer(socket);
                   new Thread(interact).start();
               } catch( IOException e) {
                   e.printStackTrace();
               }
           }
       }
        /**String drivers = null;
        private Socket incoming;
        private ServerThread st = null;
        Connection conn = null;
        private BufferedReader br = null;
        private BufferedWriter bw = null;
        ServerThread(Socket oneSocket){
            incoming = oneSocket;
        }
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
                    String success = "SUCESS";
                    bw.write(success, 0,  success.length());
                    serverSocket.close();
                } else {
                        String fail = "FAILURE";
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

         void executeRequest(String buffer){ //, Connection conn
            if(buffer.contains("USERID")){

            }
            else if (buffer.contains("GETFILES")){

            }

        }

        public void run() {
            Socket socket;
            try {
                serverSocket = new ServerSocket(SERVERPORT);

            } catch (IOException e) {
                e.printStackTrace();
            }
            while (!Thread.currentThread().isInterrupted()) {
                try {
                    socket = serverSocket.accept();
                    br = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                    String read = br.readLine();
                    executeRequest(read);//, conn

                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }**/
    }
    class InteractClientServer implements Runnable {
        private Socket clientSocket;
        private BufferedReader input;
        public InteractClientServer( Socket clientSocket) {
            this.clientSocket = clientSocket;
            try {
                this.input = new BufferedReader(new InputStreamReader(this.clientSocket.getInputStream()));
            } catch(IOException e) {
                e.printStackTrace();
            }
        }
        public void run() {
            while(!Thread.currentThread().isInterrupted()) {
                try {
                    String read = input.readLine();
                } catch (IOException e) {
                    e.printStackTrace();
                }

            }
        }
    }
}
