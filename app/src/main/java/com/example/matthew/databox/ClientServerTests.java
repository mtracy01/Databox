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

        System.out.printf("Testing USERID ... ");
        if (client.checkUserID(client.getUsername(), "pword") == 0)
            System.out.println("USERID worked");
        else
            System.out.println("USERID did not work");

        System.out.printf("Testing ADDUSER ... ");
        if (client.addUser(client.getUsername(), "pword") == 0)
            System.out.println("ADDUSER worked");
        else
            System.out.println("ADDUSER did not work");

        /*
        assert(client.checkUserID(client.getUsername(), "pword") == 1);
        assert(client.addUser(client.getUsername(), "pword") == 0);
        */

        /*
        assert(client.upload("C:/Users/Cris/Desktop/test.txt") == 0);
        */

        /*
        assert(client.getFiles() == 0);
        assert(client.download("file.txt") == 0);
        */
    }
}
