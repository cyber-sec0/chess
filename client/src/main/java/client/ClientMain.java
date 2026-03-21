package client;

import chess.*;

public class ClientMain { // The main class for the client application project
    public static void main(String[] args) { // The main function that start execution
        System.out.println("♕ 240 Chess Client: Started"); // Print start message
        TerminalReplUserInterface loopObjectInstanceForRunningTheApp =
                new TerminalReplUserInterface(8080); // Create the loop object instance
        loopObjectInstanceForRunningTheApp.runTheProgramLoop(); // Call the run method
    }
}