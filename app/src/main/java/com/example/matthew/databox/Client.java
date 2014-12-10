package com.example.matthew.databox;

import java.io.IOException;
import java.io.FileNotFoundException;
import java.io.BufferedWriter;
import java.io.OutputStreamWriter;
import java.io.InputStreamReader;
import java.io.BufferedReader;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.net.Socket;

/**
 * Created by Cris on 12/6/2014.
 *
 * Databox - Client
 *
 * Provides functionality for communicating with Databox Server. Allows communication for
 * uploading/downloading files, requesting a user's file list, checking if a username/password pair
 * exits, and creating new users.
 */
public class Client {
    private static final int SERVERPORT = 6000;
    private final static String SERVER = "127.0.0.1";
    private final static int CHUNK = 4096;
    private final static int MSG_SIZE = 64;

    // Different messages to send to server :)
    private final static String USERID = "USERID";
    private final static String ADDUSER = "ADDUSER";
    private final static String GETFILES = "GETFILES";
    private final static String UPLOAD = "UPLOAD";
    private final static String DOWNLOAD = "DOWNLOAD";

    private String username = "";
    private MainActivity mainActivity;

    private BufferedWriter bw;
    private BufferedReader br;
    private InputStream is;
    private OutputStream os;
    private int bytesRead, current;
    private byte[] data = new byte[CHUNK];
    private char[] msg = new char[MSG_SIZE];

    /**
     * Constructor for Client class. Sets user name and initializes a MainActivity.
     *
     * @param username - username of the Client
     */
    public Client(String username) {
        this.username = username;
        mainActivity = new MainActivity(username);
    }

    /**
     * Initializes the sockets and input/output objects that will be needed. Resets current to 0.
     *
     * @return 0 on success, 1 on failure
     */
    private int initSocket() {
        try {
            Socket socket = new Socket(SERVER, SERVERPORT);
            OutputStreamWriter osw = new OutputStreamWriter(socket.getOutputStream());
            InputStreamReader isr = new InputStreamReader(socket.getInputStream());
            bw = new BufferedWriter(osw);
            br = new BufferedReader(isr);
            is = socket.getInputStream();
            os = socket.getOutputStream();
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

    /**
     * Sends a USERID message to the server to check if the specified userID/password pair exists.
     *
     * USERID messages should look like this:
     * USERID<sp>[userID]<sp>[password]
     *
     * @param userID - userID to check
     * @param password - password to check
     *
     * @return 0 on success, 1 on failure
     */
    public int checkUserID(String userID, String password) {
        if (initSocket() == 1)
            return 1;

        // Send userID + password to server so it can check if that pair exists
        try {
            // Write USERID message to server
            bw.write(USERID, 0, USERID.length());
            bw.write(" " + userID + " " + password + "\n", 0, userID.length() + password.length() + 3);
            bw.flush();

            // Read socket until it's closed to get response from server
            bytesRead = br.read(msg, current, MSG_SIZE);
            while (bytesRead != -1) {
                if (new String(msg).trim().equals("SUCCESS"))
                    return 0;
                else if (new String(msg).trim().equals("FAILURE"))
                    return 1;
                bytesRead = br.read(msg, current, MSG_SIZE);
            }

            // We didn't get an expected message (or any message) from the server, return 1
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

    /**
     * Sends an ADDUSER message to the server to register a new username and password pair.
     *
     * ADDUSER messages should look like this:
     * ADDUSER<sp>[userID]<sp>[password]
     *
     * @param userID - userID to add to database
     * @param password - password to add to database
     *
     * @return 0 on success, 1 on failure
     */
    public int addUser(String userID, String password) {
        if (initSocket() == 1)
            return 1;

        try {
            // Write the ADDUSER message to the server
            bw.write(ADDUSER, 0, ADDUSER.length());
            bw.write(" " + userID + " " + password + "\n", 0, userID.length() + password.length() + 3);
            bw.flush();

            // Read socket until it's closed to get response from server
            bytesRead = br.read(msg, current, MSG_SIZE);
            while (bytesRead != -1) {
                if (new String(msg).trim().equals("SUCCESS"))
                    return 0;
                else if (new String(msg).trim().equals("FAILURE"))
                    return 1;
                bytesRead = br.read(msg, current, MSG_SIZE);
            }

            // We didn't get an expected message (or any message) from the server, return 1
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

    /**
     * Sends a GETFILES request to the server to retrieve the file list of a user.
     *
     * GETFILES messages should look like this:
     * GETFILES<sp>[username]
     *
     * @return 0 on success, 1 on failure
     */
    public int getFiles() {
        if (initSocket() == 1)
            return 1;

        try {
            // Write the GETFILES request to the server
            bw.write(GETFILES, 0, GETFILES.length());
            bw.write(" " + username + "\n", 0, username.length() + 2);
            bw.flush();

            // Read the files the server writes back
            String file = br.readLine();
            while (file != null) {
                mainActivity.addFile(file);
                file = br.readLine();
            }

            // Tell MainActivity to update its TextView with the files
            mainActivity.updateFiles();

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

    /**
     * Sends UPLOAD messages to the server to add files to a user's file list.
     *
     * UPLOAD messages should look like this:
     * UPLOAD<sp>[filepath]<\n>[data]
     *
     * @param filepath - location of file to be uploaded
     *
     * @return 0 on success, 1 on failure
     */
    public int upload(String filepath) {
        if (initSocket() == 1)
            return 1;

        try {
            // Open the file at filepath
            File f = new File(filepath);
            FileInputStream fis = new FileInputStream(f);

            try {
                // Write an UPLOAD message to the server, data to follow new line
                bw.write(UPLOAD, 0, UPLOAD.length());
                bw.write(" " + username + " " + filepath + " ", 0, username.length() + filepath.length() + 3);
                bw.flush();

                // Read data from file, then write it to the server
                bytesRead = fis.read(data);
                while (bytesRead != -1) {
                    os.write(data, 0, bytesRead);
                    bytesRead = fis.read(data, 0, data.length);
                }
                bw.flush();

                bw.write("\n", 0, 1);
                bw.flush();

                // Read socket until it's closed to get response from server
                String response = br.readLine();
                if (response.equals("SUCCESS"))
                    return 0;

                // We didn't get an expected message (or any message) from the server, return 1
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
        catch (FileNotFoundException e) {
            System.out.println("File not found! : " + e.toString());
            e.printStackTrace();
            return 1;
        }
        catch (Exception e) {
            e.printStackTrace();
            return 1;
        }
    }

    /**
     * Sends a download request to the server to download a file from a user's file list.
     *
     * DOWNLOAD messages should look like this:
     * DOWNLOAD<sp>[filename]
     *
     * @param filename - name of the file to be downloaded from server
     *
     * @return 0 on success, 1 on failure
     */
    public int download(String filename) {
        if (initSocket() == 1)
            return 1;

        try {
            // Create the file that's being downloaded
            File f = new File(filename);
            FileOutputStream fos = new FileOutputStream(f);

            try {
                // Write the download request to the server
                bw.write(DOWNLOAD, 0, DOWNLOAD.length());
                bw.write(" " + username + " " + filename + "\n", 0, username.length() + filename.length() + 3);
                bw.flush();

                // Read socket until it's closed to get response from server
                bytesRead = is.read(data, current, CHUNK);
                while (bytesRead != -1) {
                    fos.write(data, 0, bytesRead);
                    current += bytesRead;
                    bytesRead = is.read(data, current, CHUNK);
                }

                // If we reached this point without an exception we can assume the file was copied
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
        catch (FileNotFoundException e) {
            System.out.println("File not found! : " + e.toString());
            e.printStackTrace();
            return 1;
        }
        catch (Exception e) {
            e.printStackTrace();
            return 1;
        }
    }

    /**
     * Fetches the username of this Client object.
     *
     * @return username of Client
     */
    public String getUsername() {
        return username;
    }
}
