package com.example.matthew.databox;

/**
 * Created by Cris on 12/7/2014.
 *
 * Databox - ClientServerTests
 *
 * This class tests communications between the Client and Server classes.
 */
public class ClientServerTests {
    public static void test() {
        Client client = new Client("Toaster");

        System.out.println("Test");

        assert(client.checkUserID(client.getUsername(), "pword") == 0);
        assert(client.addUser(client.getUsername(), "pword") == 0);

        /*
        assert(client.upload("C:/Users/Cris/Desktop/test.txt") == 0);
        */

        /*
        assert(client.getFiles() == 0);
        assert(client.download("file.txt") == 0);
        */
    }
}
