package client;

import chess.*;

public class ClientMain { // The main class for the client application project that run the system logic
    public static void main(String[] args) { // The main function that start the execution of the program loop to not stop running
        System.out.println("♕ 240 Chess Client: Started"); // Print the start message in the terminal screen
        TerminalReplUserInterface loopObjectInstanceForRunningTheApp = new TerminalReplUserInterface(8080); // Create the loop object instance using the port integer
        loopObjectInstanceForRunningTheApp.runTheProgramLoop(); // Call the run method to begin asking for inputs continuously
    }
}