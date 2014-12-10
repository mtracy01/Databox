//package com.example.matthew.databox;

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
            System.out.println("Start main\n");
            while(true){
                System.out.println("Need Accept\n");
                Socket incoming = s.accept();
                System.out.println("Accept\n");
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
        System.out.println("Connection is : url: "+url+username+" " +password+"\n");
        return DriverManager.getConnection(url,username,password);
    }
    String executeRequest(String msg,Connection conn) {
        String ret;
        if(msg.substring(0, 6).equals("USERID")){
            ret = checkUserID(msg,conn);
        }
        else if(msg.substring(0, 6).equals("UPLOAD")){
            ret = upload(msg,conn);
        }
        else if(msg.substring(0, 7).equals("ADDUSER")){
            ret = addUser(msg,conn);
        }
        else if (msg.substring(0, 8).equals("GETFILES")) {
            ret = getFiles(msg,conn);
        }
        else if (msg.substring(0, 8).equals("DOWNLOAD")) {
            ret = download(msg,conn);
        }
        else {
            ret = "FAILURE";
        }

        return ret;
    }
    public String checkUserID(String read, Connection c ){
        System.out.println("String is "+read+"\n");
        String nameAndPw = read.substring(read.indexOf(" ")+1);
        String userName = nameAndPw.substring(0,nameAndPw.indexOf(" "));
        String password = nameAndPw.substring(nameAndPw.indexOf(" ")+1);
        System.out.println("String is "+userName+" \n" +password+"\n");
        Statement stmt = null;

        try{
            // Class.forName(drivers);
            stmt = c.createStatement();
            String sql = "SELECT * FROM user WHERE userName ='"+userName+"' AND password ='"+password+"'";
            System.out.println(sql);
            ResultSet result = stmt.executeQuery(sql);
            if(result.next()){
                System.out.println("hi");
                String success = "SUCCESS";
                return "SUCCESS";
            } else {
                String fail = "FAILURE";
                return fail;
                //bw.write(fail, 0,  fail.length());
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "FAILURE";
    }

    public String addUser(String read, Connection c){
        System.out.println("adduser\n");
        //String exist = checkUserID(read,c);
        String nameAndPw = read.substring(read.indexOf(" ")+1);
        String userName = nameAndPw.substring(0,nameAndPw.indexOf(" "));
        String password = nameAndPw.substring(nameAndPw.indexOf(" ")+1);

        //	if(exist.equals("SUCCESS")){ return "FAILURE";}
        PreparedStatement stmt = null;
        try {
            //Class.forName(drivers);

            String sql = "INSERT INTO user (userName,password) values(?,?)";
            stmt = c.prepareStatement(sql);
            stmt.setString(1,userName);
            stmt.setString(2,password);
            if(stmt.executeUpdate()> 0)
                System.out.println("success");
        } catch (SQLException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "SUCCESS";
    }

    String getFiles(String read, Connection c){
        System.out.println(read);
        String uploading = read.substring(read.indexOf(" ")+1);
        String userName = uploading;
        PreparedStatement stmt = null;
        String files = "";
        try{

            String sql = "SELECT * FROM file WHERE userName ='"+userName+"'";
            System.out.println(sql);
            stmt = c.prepareStatement(sql);
            ResultSet result = stmt.executeQuery(sql);
            while(result.next()){
                System.out.println(files);
                files += result.getString("fileName")+"\n";
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
        finally{
            if(stmt!= null){
                try {
                    stmt.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        }
        return files;
    }
    String upload(String read, Connection c){
        System.out.println(read);
        String uploading = read.substring(read.indexOf(" ")+1);
        String userName = uploading.substring(0,uploading.indexOf(" "));
        uploading = uploading.substring(uploading.indexOf(" ")+1);
        String fileName = uploading.substring(0,uploading.indexOf(" "));
        System.out.println("fileName: "+fileName);
        String reverse = new StringBuilder(fileName).reverse().toString();
        int slash = reverse.indexOf("/");
        fileName = fileName.substring(fileName.length()-slash);
        uploading = uploading.substring(uploading.indexOf(" ")+1);
        String fileContent = uploading;
        PreparedStatement stmt = null;
        System.out.println("userName: "+userName);
        System.out.println("fileName: "+fileName);
        System.out.println("fileContent: "+fileContent);

        try{

            String sql = "INSERT INTO file (userName,fileName,content) values (?, ?, ?)";
            stmt = c.prepareStatement(sql);
            stmt.setString(1, userName);
            stmt.setString(2, fileName);
            stmt.setString(3,fileContent);

            if(stmt.executeUpdate() > 0) {System.out.println("success\n");}
        } catch (SQLException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
        System.out.println("go here\n");
        return "SUCCESS";
    }
    String download(String read, Connection c){
        String nameAndFile = read.substring(read.indexOf(" ")+1);
        String userName = nameAndFile.substring(0,nameAndFile.indexOf(" "));
        String fileName = nameAndFile.substring(nameAndFile.indexOf(" ")+1,nameAndFile.indexOf("\n") );
        Statement stmt = null;
        try{
            // Class.forName(drivers);
            stmt = c.createStatement();
            String sql = "SELECT * FROM file WHERE userName ='"+userName+"'";
            System.out.println(sql);
            ResultSet result = stmt.executeQuery(sql);
            if(result.next()){
                System.out.println("hi");
                String success = "SUCCESS";
                return "SUCCESS";
            } else {
                String fail = "FAILURE";
                return fail;
                //bw.write(fail, 0,  fail.length());
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "SUCCESS";
    }
    public void run(){
        Connection conn = null;
        try
        {
            conn = getConnection();
            try
            {
                String read = input.readLine();
                String returnString = executeRequest(read,conn);
                output.write(returnString, 0, returnString.length());

                output.flush();
            }
            catch (IOException e)
            {
                e.printStackTrace();
            }
            finally
            {
                incoming.close();
            }
        }catch(SQLException e){
            e.printStackTrace();
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
    }

}

