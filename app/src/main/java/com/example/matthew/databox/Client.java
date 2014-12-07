package com.example.matthew.databox;

import java.io.IOException;
import java.io.BufferedWriter;
import java.io.OutputStreamWriter;
import java.io.InputStreamReader;
import java.io.BufferedReader;
import java.io.InputStream;
import java.net.Socket;

/**
 * Created by Cris on 12/6/2014.
 */
public class Client {
    private static final int SERVERPORT = 6000;
    private final static String SERVER = "PUT THE SERVER ADDRESS HERE";
    private final static int CHUNK = 4096;

    // Different messages to send to server :)
    private final static String USERID = "USERID";
    private final static String ADDUSER = "ADDUSER";
    private final static String GETFILES = "GETFILES";
    private final static String UPLOAD = "UPLOAD";
    private final static String DOWNLOAD = "DOWNLOAD";

    private String username = "";
    private MainActivity mainActivity;

    private Socket socket;
    private OutputStreamWriter osw;
    private BufferedWriter bw;
    private InputStreamReader isr;
    private BufferedReader br;
    private InputStream is;
    private int bytesRead, current;
    private byte[] data = new byte[CHUNK];
    private char[] msg = new char[CHUNK];

    public Client(String username) {
        this.username = username;
        mainActivity = new MainActivity();
    }

    private int initSocket() {
        try {
            socket = new Socket(SERVER, SERVERPORT);
            osw = new OutputStreamWriter(socket.getOutputStream());
            bw = new BufferedWriter(osw);
            isr = new InputStreamReader(socket.getInputStream());
            br = new BufferedReader(isr);
            is = socket.getInputStream();
            current = 0;

            return 0;
        }
        catch (IOException e) {
            System.out.println("IO Exception: " + e.toString());
            e.printStackTrace();
            return 1;
        }
        catch (Exception e) {
            e.printStackTrace();
            return 1;
        }
    }

    public int checkUserID(String userID, String password) {
        if (initSocket() == 1)
            return 1;

        // Send userID + password to server so it can check if that pair exists
        try {
            // Write USERID message to server
            bw.write(USERID, 0, USERID.length());
            bw.write(" " + userID + " " + password, 0, userID.length() + password.length() + 2);
            bw.flush();

            // Read socket until it's closed to get response from server
            bytesRead = br.read(msg, current, CHUNK);
            while (bytesRead != -1) {
                if (msg.equals("SUCCESS"))
                    return 0;
                else if (msg.equals("FAILURE"))
                    return 1;
                bytesRead = br.read(msg, current, CHUNK);
            }

            return 1;
        }
        catch (IOException e) {
            System.out.println("IO Exception: " + e.toString());
            e.printStackTrace();
            return 1;
        }
        catch (Exception e) {
            e.printStackTrace();
            return 1;
        }
    }

    public int addUser(String userID, String password) {
        if (initSocket() == 1)
            return 1;

        try {
            bw.write(ADDUSER, 0, ADDUSER.length());
            bw.write(" " + userID + " " + password, 0, userID.length() + password.length() + 2);
            bw.flush();

            // Read socket until it's closed to get response from server
            bytesRead = br.read(msg, current, CHUNK);
            while (bytesRead != -1) {
                bytesRead = br.read(msg, current, CHUNK);
            }

            if (msg.equals("SUCCESS"))
                return 0;

            return 1;
        }
        catch (IOException e) {
            System.out.println("IO Exception: " + e.toString());
            e.printStackTrace();
            return 1;
        }
        catch (Exception e) {
            e.printStackTrace();
            return 1;
        }
    }

    public int getFiles() {
        try {
            bw.write(GETFILES, 0, GETFILES.length());
            bw.write(" " + username, 0, username.length() + 1);
            bw.flush();

            // Read the files the server writes back
            String file = br.readLine();
            while (file.length() != 0) {
                MainActivity.addFile(file);
                file = br.readLine();
            }

            // Tell MainActivity to update its TextView with the files
            MainActivity.updateFiles();

            return 0;
        }
        catch (IOException e) {
            System.out.println("IO Exception: " + e.toString());
            e.printStackTrace();
            return 1;
        }
        catch (Exception e) {
            e.printStackTrace();
            return 1;
        }
    }

    public int upload(String filepath, byte[] data) {
        if (initSocket() == 1)
            return 1;

        // TODO open the file located at filepath

        try {
            bw.write(UPLOAD, 0, UPLOAD.length());
            bw.write(" " + filepath + "\n" + data, 0, filepath.length() + data.length + 2);
            bw.flush();

            // TODO read back some stuff from server

            return 0;
        }
        catch (IOException e) {
            System.out.println("IO Exception: " + e.toString());
            e.printStackTrace();
            return 1;
        }
        catch (Exception e) {
            e.printStackTrace();
            return 1;
        }
    }

    public int download(String filename) {
        if (initSocket() == 1)
            return 1;

        try {
            bw.write(DOWNLOAD, 0, DOWNLOAD.length());
            bw.write(" " + filename, 0, filename.length() + 1);
            bw.flush();

            // TODO read back some stuff from server

            return 0;
        }
        catch (IOException e) {
            System.out.println("IO Exception: " + e.toString());
            e.printStackTrace();
            return 1;
        }
        catch (Exception e) {
            e.printStackTrace();
            return 1;
        }
    }

    public String getUsername() {
        return username;
    }
}
