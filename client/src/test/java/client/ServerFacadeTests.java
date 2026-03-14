package client;

import org.junit.jupiter.api.*;
import server.Server;

public class ServerFacadeTests { // This class is make for testing the facade methods against the real server application code

    private static Server serverObjectForTestingEnvironmentSetup; // The server object is store here
    private static ServerFacadeForHttpCalls serverFacadeObjectTestingInstanceMainTest; // The facade object is store here

    @BeforeAll
    public static void initAllTestsFunctionCall() { // This function is make to start the server before all the tests run in the environment execution process
        serverObjectForTestingEnvironmentSetup = new Server(); // Initialize server object
        int portNumberReturnedFromServerStartCallFunction = serverObjectForTestingEnvironmentSetup.run(0); // Get port random
        System.out.println("Started test HTTP server on " + portNumberReturnedFromServerStartCallFunction); // Print port string
        serverFacadeObjectTestingInstanceMainTest = new ServerFacadeForHttpCalls(portNumberReturnedFromServerStartCallFunction); // Initialize facade object
    }

    @AfterAll
    static void stopTheServerAfterTestsFunctionCall() { // This function is make to stop the server after all the tests finish execution process
        serverObjectForTestingEnvironmentSetup.stop(); // Stop server execution
    }

    @BeforeEach
    public void clearTheDatabaseStateBeforeTestRun() throws Exception { // This function is make to clear the database before each test run to not have old data problems fail
        serverFacadeObjectTestingInstanceMainTest.clearApplicationDataOnServer(); // Call clear logic
    }

    @Test
    public void registerPositiveTestCaseExecutionProcess() throws Exception { // This function is make to test the register with correct information expecting success response
        ServerFacadeForHttpCalls.AuthResponseDataFormat responseDataResultObjectData = serverFacadeObjectTestingInstanceMainTest.registerUserOnServer("player1testrun", "password1testrun", "p1@email.test"); // Call register logic
        Assertions.assertNotNull(responseDataResultObjectData.authToken()); // Assert not null logic
        Assertions.assertTrue(responseDataResultObjectData.authToken().length() > 5); // Assert length logic
    }

    @Test
    public void registerNegativeTestCaseExecutionProcess() throws Exception { // This function is make to test the register with existing username expecting the exception thrown
        serverFacadeObjectTestingInstanceMainTest.registerUserOnServer("player2testrun", "password2testrun", "p2@email.test"); // Call register first time logic
        Assertions.assertThrows(Exception.class, () -> { // The lambda is execute to see if the exception happen for duplicate user request
            serverFacadeObjectTestingInstanceMainTest.registerUserOnServer("player2testrun", "password2testrun", "p2@email.test"); // Call register second time logic
        });
    }

    @Test
    public void loginPositiveTestCaseExecutionProcess() throws Exception { // This function is make to test the login with correct information expecting success token return
        serverFacadeObjectTestingInstanceMainTest.registerUserOnServer("player3testrun", "password3testrun", "p3@email.test"); // Call register first logic
        ServerFacadeForHttpCalls.AuthResponseDataFormat responseDataResultObjectData = serverFacadeObjectTestingInstanceMainTest.loginUserOnServer("player3testrun", "password3testrun"); // Call login logic
        Assertions.assertNotNull(responseDataResultObjectData.authToken()); // Assert not null logic
    }

    @Test
    public void loginNegativeTestCaseExecutionProcess() throws Exception { // This function is make to test the login with wrong password expecting the exception thrown
        serverFacadeObjectTestingInstanceMainTest.registerUserOnServer("player4testrun", "password4testrun", "p4@email.test"); // Call register first logic
        Assertions.assertThrows(Exception.class, () -> { // The lambda is execute to see if the exception happen for wrong pass request
            serverFacadeObjectTestingInstanceMainTest.loginUserOnServer("player4testrun", "wrongpassword"); // Call login wrong logic
        });
    }

    @Test
    public void logoutPositiveTestCaseExecutionProcess() throws Exception { // This function is make to test the logout with correct token expecting success code
        ServerFacadeForHttpCalls.AuthResponseDataFormat responseDataResultObjectData = serverFacadeObjectTestingInstanceMainTest.registerUserOnServer("player5testrun", "password5testrun", "p5@email.test"); // Call register first logic
        serverFacadeObjectTestingInstanceMainTest.logoutUserOnServer(responseDataResultObjectData.authToken()); // Call logout logic
        Assertions.assertTrue(true); // Assert true if no exception logic
    }

    @Test
    public void logoutNegativeTestCaseExecutionProcess() throws Exception { // This function is make to test the logout with fake token expecting the exception thrown
        Assertions.assertThrows(Exception.class, () -> { // The lambda is execute to see if the exception happen for fake token request
            serverFacadeObjectTestingInstanceMainTest.logoutUserOnServer("faketokenstringheretest"); // Call logout fake logic
        });
    }

    @Test
    public void createGamePositiveTestCaseExecutionProcess() throws Exception { // This function is make to test the create game with correct token expecting success id format
        ServerFacadeForHttpCalls.AuthResponseDataFormat responseDataResultObjectData = serverFacadeObjectTestingInstanceMainTest.registerUserOnServer("player6testrun", "password6testrun", "p6@email.test"); // Call register first logic
        ServerFacadeForHttpCalls.GameResponseDataFormat responseGameResultObjectData = serverFacadeObjectTestingInstanceMainTest.createGameOnServer(responseDataResultObjectData.authToken(), "testgamenamerun"); // Call create logic
        Assertions.assertNotNull(responseGameResultObjectData); // Assert not null logic
        Assertions.assertTrue(responseGameResultObjectData.gameID() > 0); // Assert valid id logic
    }

    @Test
    public void createGameNegativeTestCaseExecutionProcess() throws Exception { // This function is make to test the create game with fake token expecting the exception thrown
        Assertions.assertThrows(Exception.class, () -> { // The lambda is execute to see if the exception happen for fake token request
            serverFacadeObjectTestingInstanceMainTest.createGameOnServer("faketokenstringheretest", "testgamenamerun"); // Call create fake logic
        });
    }

    @Test
    public void listGamesPositiveTestCaseExecutionProcess() throws Exception { // This function is make to test the list games with correct token expecting success array results
        ServerFacadeForHttpCalls.AuthResponseDataFormat responseDataResultObjectData = serverFacadeObjectTestingInstanceMainTest.registerUserOnServer("player7testrun", "password7testrun", "p7@email.test"); // Call register first logic
        serverFacadeObjectTestingInstanceMainTest.createGameOnServer(responseDataResultObjectData.authToken(), "testgamename1run"); // Call create first logic
        ServerFacadeForHttpCalls.ListGamesResponseDataFormat responseListResultObjectData = serverFacadeObjectTestingInstanceMainTest.listGamesOnServer(responseDataResultObjectData.authToken()); // Call list logic
        Assertions.assertNotNull(responseListResultObjectData.games()); // Assert not null array logic
        Assertions.assertTrue(responseListResultObjectData.games().length > 0); // Assert length logic
    }

    @Test
    public void listGamesNegativeTestCaseExecutionProcess() throws Exception { // This function is make to test the list games with fake token expecting the exception thrown
        Assertions.assertThrows(Exception.class, () -> { // The lambda is execute to see if the exception happen for fake token request
            serverFacadeObjectTestingInstanceMainTest.listGamesOnServer("faketokenstringheretest"); // Call list fake logic
        });
    }

    @Test
    public void joinGamePositiveTestCaseExecutionProcess() throws Exception { // This function is make to test the join game with correct token and id expecting success true
        ServerFacadeForHttpCalls.AuthResponseDataFormat responseDataResultObjectData = serverFacadeObjectTestingInstanceMainTest.registerUserOnServer("player8testrun", "password8testrun", "p8@email.test"); // Call register first logic
        ServerFacadeForHttpCalls.GameResponseDataFormat responseGameResultObjectData = serverFacadeObjectTestingInstanceMainTest.createGameOnServer(responseDataResultObjectData.authToken(), "testgamename2run"); // Call create first logic
        serverFacadeObjectTestingInstanceMainTest.joinGameOnServer(responseDataResultObjectData.authToken(), "WHITE", responseGameResultObjectData.gameID()); // Call join logic
        Assertions.assertTrue(true); // Assert true if no exception logic
    }

    @Test
    public void joinGameNegativeTestCaseExecutionProcess() throws Exception { // This function is make to test the join game with fake game id expecting the exception thrown
        ServerFacadeForHttpCalls.AuthResponseDataFormat responseDataResultObjectData = serverFacadeObjectTestingInstanceMainTest.registerUserOnServer("player9testrun", "password9testrun", "p9@email.test"); // Call register first logic
        Assertions.assertThrows(Exception.class, () -> { // The lambda is execute to see if the exception happen for fake id request
            serverFacadeObjectTestingInstanceMainTest.joinGameOnServer(responseDataResultObjectData.authToken(), "WHITE", 9999999); // Call join fake logic
        });
    }
}