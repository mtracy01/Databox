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

    }
    class InteractClientServer implements Runnable {
        private Socket clientSocket;
        private BufferedReader input;
        String drivers = null;
        private Socket incoming;
        private BufferedWriter bw = null;
        public InteractClientServer( Socket clientSocket) {
            this.clientSocket = clientSocket;
            try {
                this.input = new BufferedReader(new InputStreamReader(this.clientSocket.getInputStream()));
            } catch(IOException e) {
                e.printStackTrace();
            }
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
                    //serverSocket.close();   //DO I NEED TO KEEP IT OPEN??
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

        public void addUser(String read, Connection c){
            String nameAndPw = read.substring(read.indexOf(" ")+1);
            String userName = nameAndPw.substring(0,nameAndPw.indexOf(" "));
            String password = nameAndPw.substring(nameAndPw.indexOf(" ")+1);
            Statement stmt = null;
            try {
                Class.forName(drivers);
                stmt = c.createStatement();
                String sql = "VALUE('"+userName+"', '"+password+ "')";
                stmt.executeUpdate("INSERT INTO user" +sql);

            } catch (ClassNotFoundException e) {
                e.printStackTrace();
            } catch (SQLException e) {
                e.printStackTrace();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        public void getFiles(String read, Connection c){
            String nameAndPw = read.substring(read.indexOf(" ")+1);
            String userName = nameAndPw.substring(0,nameAndPw.indexOf(" "));
            // Check database tables, find all file names

        }

        public void uploadFile(String read, Connection c){
            String userName = read.substring(0,5); // NEED TO BE MODIFIED
            String type = read.substring(0,1);
            String uploading = read.substring(read.indexOf(" ")+1);
            String fileName = uploading.substring(0,uploading.indexOf("\n"));
            String fileContent = uploading.substring(uploading.indexOf("\n")+1);
            Statement stmt = null;
            try{
                Class.forName(drivers);
                stmt = c.createStatement();
                String sql = "VALUE('"+ userName +"', '"+fileName+"', '"+fileContent+"', '"+type;
                stmt.executeUpdate("INSERT INTO file" +sql);

            } catch (ClassNotFoundException e) {
                e.printStackTrace();
            } catch (SQLException e) {
                e.printStackTrace();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        public void downloadFile(String read, Connection c){
            String userName = read.substring(0,5); // NEED TO BE MODIFIED
            String fileName = read.substring(read.indexOf(" ")+1);
            PreparedStatement stmt = null;
            try{
                Class.forName(drivers);
                stmt =c.prepareStatement("SELECT * FROM file");
                ResultSet result = stmt.executeQuery();
                while(result.next()){
                    String username = result.getString(1);
                    if(username.equals(userName)){
                       /** if(result.getString(5)) {
                        }**/
                    }
                }


            }catch (ClassNotFoundException e) {
                e.printStackTrace();
            } catch (SQLException e) {
                e.printStackTrace();
            } catch (Exception e) {
                e.printStackTrace();
            }



        }
        void executeRequest(String buffer, Connection conn){ //, Connection conn
            if(buffer.contains("USERID")){
                checkUserID(buffer,conn);
            }
            else if (buffer.contains("GETFILES")){

            }

        }

        public void run() {
            Connection conn = null;
            try{
                conn = getConnection();
                while(!Thread.currentThread().isInterrupted()) {
                    try {
                        String read = input.readLine();
                        executeRequest(read, conn);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }catch (Exception e){
                e.printStackTrace();
            } finally {
                try {
                    incoming.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
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
