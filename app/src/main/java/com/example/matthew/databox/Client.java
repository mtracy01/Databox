package com.example.matthew.databox;

import java.io.IOException;
import java.io.BufferedWriter;
import java.io.OutputStreamWriter;
import java.io.InputStreamReader;
import java.io.BufferedReader;
import java.net.Socket;

/**
 * Created by Cris on 12/6/2014.
 */
public class Client {
    private static final int SERVERPORT = 6000;
    private final static String SERVER = "PUT THE SERVER ADDRESS HERE";
    private final static int CHUNK = 4096;

    // Different messages to send to server :)
    private final static String GETFILES = "GETFILES";
    private final static String USERID = "USERID";
    private final static String UPLOAD = "UPLOAD";
    private final static String DOWNLOAD = "DOWNLOAD";

    private static String username = "";
    private static String password = "";
    private static Socket socket;
    private static OutputStreamWriter osw;
    private static BufferedWriter bw;
    private static InputStreamReader isr;
    private static BufferedReader br;

    public Client(String username, String password) {
        this.username = username;
        this.password = password;

        try {
            socket = new Socket(SERVER, SERVERPORT);
            osw = new OutputStreamWriter(socket.getOutputStream());
            bw = new BufferedWriter(osw);
            isr = new InputStreamReader(socket.getInputStream());
            br = new BufferedReader(isr);
        }
        catch (IOException e) {
            System.out.println("IO Exception: " + e.toString());
        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }

    public int checkUserID(String userID, String password) {
        // Send userID + password to server so it can check if that pair exists
        try {
            bw.write(USERID, 0, USERID.length());
            bw.write(" " + userID + " " + password, 0, userID.length() + password.length() + 2);
            bw.flush();

            // TODO read back some stuff from server

            return 0;
        }
        catch (IOException e) {
            System.out.println("IO Exception: " + e.toString());
            return 1;
        }
        catch (Exception e) {
            e.printStackTrace();
            return 1;
        }
    }

    public int addUser(String userID, String password) {
        // TODO add a user to the database

        return 0;
    }

    public void send(String msg) {
        try {
            bw.write(msg, 0, msg.length());
            bw.flush();

            if (msg.substring(0, 8).equals("GETFILES")) {
                // If the message is requesting files read what the server returns

                // Read the files the server writes back
                String file = br.readLine();
                while (file.length() != 0) {
                    MainActivity.addFile(file);
                    file = br.readLine();
                }

                // Tell MainActivity to update its TextView with the files
                MainActivity.updateFiles();
            }
        }
        catch (IOException e) {
            System.out.println("IO Exception: " + e.toString());
        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getUsername() {
        return username;
    }
}
