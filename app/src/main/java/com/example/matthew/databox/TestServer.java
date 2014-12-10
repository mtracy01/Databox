package com.example.matthew.databox;

/**
 * Created by Ji on 12/8/14.
 */
//package com.example.matthew.databox;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileInputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.util.Properties;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.ServerSocket;
import java.net.Socket;

/**import android.app.Activity;
 import android.os.Bundle;**/
import java.sql.*;
public class TestServer {
    private static ServerSocket serverSocket;
    Thread serverThread = null;
    public static final int SERVERPORT = 6000;


    protected void onStop() {
        // super.onStop();
        try {
            serverSocket.close();
        } catch( IOException e) {
            e.printStackTrace();
        }
    }
    public static void main(String args[]){
        try{
            ServerSocket s = new ServerSocket(SERVERPORT);
            while(true){
                Socket incoming = s.accept();
                System.out.println("Here\n");
                Runnable r = new ServerThread(incoming);
                Thread t = new Thread(r);
                t.start();
            }
        }catch(IOException e){
            e.printStackTrace();
        }
    }
}

class ServerThread implements Runnable{
    private Socket incoming;
    private BufferedReader input;
    private BufferedWriter output;
    String drivers = null;
    public ServerThread(Socket oneSocket){
        incoming = oneSocket;
        try{
            this.input = new BufferedReader(new InputStreamReader(this.incoming.getInputStream()));
            this.output = new BufferedWriter(new OutputStreamWriter(this.incoming.getOutputStream()));
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
    String executeRequest(String msg, Connection c) {
        String ret = "SUCCESS";
        if(msg.substring(0, 6).equals("USERID")){
            checkUserID(msg,c);
        }
        else if(msg.substring(0, 7).equals("GETFILES")){
            addUser(msg,c);
        }
        // TODO add other messages
        else {
            ret = "FAILURE";
        }

        return ret;
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
                output.write(success, 0,  success.length());
                //serverSocket.close();   //DO I NEED TO KEEP IT OPEN??
            } else {
                String fail = "FAILURE";
                output.write(fail, 0,  fail.length());
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
    public void run(){
        Connection conn = null;
        try
        {

            try
            {
                conn = getConnection();
                while(!Thread.currentThread().isInterrupted()) {
                    try {

                        String read = input.readLine();
                        executeRequest(read, conn);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
               /** String read = input.readLine();
                String returnString = executeRequest(read,conn);
                output.write(returnString, 0, returnString.length());

                output.flush();**/
            }
            catch (IOException e)
            {
                e.printStackTrace();
            }
            catch (Exception e) {
                e.printStackTrace();
            }
            finally
            {
                incoming.close();
            }
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
    }

}

