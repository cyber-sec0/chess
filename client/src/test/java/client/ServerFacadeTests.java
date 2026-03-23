package client;

import org.junit.jupiter.api.*;
import server.Server;

public class ServerFacadeTests { // This class is make for testing facade methods against real server

    private static Server server; // The server object must be exactly this name for the regex
    private static ServerFacadeForHttpCalls serverFacadeObjectTestingInstanceMainTest; // Facade object

    @BeforeAll
    public static void initAllTestsFunctionCall() { // This function start server before all test execution
        server = new Server(); // Init server object
        var port = server.run(0); // The auto grader strictly need this exact line to work properly
        System.out.println("Started test HTTP server on " + port); // Print port string
        serverFacadeObjectTestingInstanceMainTest =
                new ServerFacadeForHttpCalls(port); // Init facade
    }

    @AfterAll
    static void stopTheServerAfterTestsFunctionCall() { // This function stop server after test finish
        server.stop(); // Stop server execution
    }

    @BeforeEach
    public void clearTheDatabaseStateBeforeTestRun() throws Exception { // This function clear database before test
        serverFacadeObjectTestingInstanceMainTest.clearApplicationDataOnServer(); // Call clear logic
    }

    @Test
    public void registerPositiveTestCaseExecutionProcess() throws Exception { // This function test register correct
        ServerFacadeForHttpCalls.AuthResponseDataFormat responseDataResultObjectData =
                serverFacadeObjectTestingInstanceMainTest.registerUserOnServer(
                        "player1testrun", "password1testrun", "p1@email.test"
                ); // Call register logic
        Assertions.assertNotNull(responseDataResultObjectData.authToken()); // Assert not null
        Assertions.assertTrue(responseDataResultObjectData.authToken().length() > 5); // Assert length
    }

    @Test
    public void registerNegativeTestCaseExecutionProcess() throws Exception { // This function test register duplicate
        serverFacadeObjectTestingInstanceMainTest.registerUserOnServer(
                "player2testrun", "password2testrun", "p2@email.test"
        ); // Call register first time logic
        Assertions.assertThrows(Exception.class, () -> { // Lambda execute see exception duplicate
            serverFacadeObjectTestingInstanceMainTest.registerUserOnServer(
                    "player2testrun", "password2testrun", "p2@email.test"
            ); // Call register second time logic
        });
    }

    @Test
    public void loginPositiveTestCaseExecutionProcess() throws Exception { // This function test login correct
        serverFacadeObjectTestingInstanceMainTest.registerUserOnServer(
                "player3testrun", "password3testrun", "p3@email.test"
        ); // Call register first logic
        ServerFacadeForHttpCalls.AuthResponseDataFormat responseDataResultObjectData =
                serverFacadeObjectTestingInstanceMainTest.loginUserOnServer(
                        "player3testrun", "password3testrun"
                ); // Call login logic
        Assertions.assertNotNull(responseDataResultObjectData.authToken()); // Assert not null
    }

    @Test
    public void loginNegativeTestCaseExecutionProcess() throws Exception { // This function test login wrong password
        serverFacadeObjectTestingInstanceMainTest.registerUserOnServer(
                "player4testrun", "password4testrun", "p4@email.test"
        ); // Call register first logic
        Assertions.assertThrows(Exception.class, () -> { // Lambda execute see exception wrong pass
            serverFacadeObjectTestingInstanceMainTest.loginUserOnServer(
                    "player4testrun", "wrongpassword"
            ); // Call login wrong logic
        });
    }

    @Test
    public void logoutPositiveTestCaseExecutionProcess() throws Exception { // This function test logout correct token
        ServerFacadeForHttpCalls.AuthResponseDataFormat responseDataResultObjectData =
                serverFacadeObjectTestingInstanceMainTest.registerUserOnServer(
                        "player5testrun", "password5testrun", "p5@email.test"
                ); // Call register first logic
        serverFacadeObjectTestingInstanceMainTest.logoutUserOnServer(
                responseDataResultObjectData.authToken()
        ); // Call logout logic
        Assertions.assertTrue(true); // Assert true no exception
    }

    @Test
    public void logoutNegativeTestCaseExecutionProcess() throws Exception { // This function test logout fake token
        Assertions.assertThrows(Exception.class, () -> { // Lambda execute see exception fake token
            serverFacadeObjectTestingInstanceMainTest.logoutUserOnServer(
                    "faketokenstringheretest"
            ); // Call logout fake logic
        });
    }

    @Test
    public void createGamePositiveTestCaseExecutionProcess() throws Exception { // This function test create game correct
        ServerFacadeForHttpCalls.AuthResponseDataFormat responseDataResultObjectData =
                serverFacadeObjectTestingInstanceMainTest.registerUserOnServer(
                        "player6testrun", "password6testrun", "p6@email.test"
                ); // Call register first logic
        ServerFacadeForHttpCalls.GameResponseDataFormat responseGameResultObjectData =
                serverFacadeObjectTestingInstanceMainTest.createGameOnServer(
                        responseDataResultObjectData.authToken(), "testgamenamerun"
                ); // Call create logic
        Assertions.assertNotNull(responseGameResultObjectData); // Assert not null
        Assertions.assertTrue(responseGameResultObjectData.gameID() > 0); // Assert valid id
    }

    @Test
    public void createGameNegativeTestCaseExecutionProcess() throws Exception { // This function test create fake token
        Assertions.assertThrows(Exception.class, () -> { // Lambda execute see exception fake token
            serverFacadeObjectTestingInstanceMainTest.createGameOnServer(
                    "faketokenstringheretest", "testgamenamerun"
            ); // Call create fake logic
        });
    }

    @Test
    public void listGamesPositiveTestCaseExecutionProcess() throws Exception { // This function test list games correct
        ServerFacadeForHttpCalls.AuthResponseDataFormat responseDataResultObjectData =
                serverFacadeObjectTestingInstanceMainTest.registerUserOnServer(
                        "player7testrun", "password7testrun", "p7@email.test"
                ); // Call register first logic
        serverFacadeObjectTestingInstanceMainTest.createGameOnServer(
                responseDataResultObjectData.authToken(), "testgamename1run"
        ); // Call create first logic
        ServerFacadeForHttpCalls.ListGamesResponseDataFormat responseListResultObjectData =
                serverFacadeObjectTestingInstanceMainTest.listGamesOnServer(
                        responseDataResultObjectData.authToken()
                ); // Call list logic
        Assertions.assertNotNull(responseListResultObjectData.games()); // Assert not null array
        Assertions.assertTrue(responseListResultObjectData.games().length > 0); // Assert length
    }

    @Test
    public void listGamesNegativeTestCaseExecutionProcess() throws Exception { // This function test list games fake token
        Assertions.assertThrows(Exception.class, () -> { // Lambda execute see exception fake token
            serverFacadeObjectTestingInstanceMainTest.listGamesOnServer(
                    "faketokenstringheretest"
            ); // Call list fake logic
        });
    }

    @Test
    public void joinGamePositiveTestCaseExecutionProcess() throws Exception { // This function test join game correct
        ServerFacadeForHttpCalls.AuthResponseDataFormat responseDataResultObjectData =
                serverFacadeObjectTestingInstanceMainTest.registerUserOnServer(
                        "player8testrun", "password8testrun", "p8@email.test"
                ); // Call register first logic
        ServerFacadeForHttpCalls.GameResponseDataFormat responseGameResultObjectData =
                serverFacadeObjectTestingInstanceMainTest.createGameOnServer(
                        responseDataResultObjectData.authToken(), "testgamename2run"
                ); // Call create first logic
        serverFacadeObjectTestingInstanceMainTest.joinGameOnServer(
                responseDataResultObjectData.authToken(), "WHITE",
                responseGameResultObjectData.gameID()
        ); // Call join logic
        Assertions.assertTrue(true); // Assert true no exception
    }

    @Test
    public void joinGameNegativeTestCaseExecutionProcess() throws Exception { // This function test join fake id
        ServerFacadeForHttpCalls.AuthResponseDataFormat responseDataResultObjectData =
                serverFacadeObjectTestingInstanceMainTest.registerUserOnServer(
                        "player9testrun", "password9testrun", "p9@email.test"
                ); // Call register first logic
        Assertions.assertThrows(Exception.class, () -> { // Lambda execute see exception fake id
            serverFacadeObjectTestingInstanceMainTest.joinGameOnServer(
                    responseDataResultObjectData.authToken(), "WHITE", 9999999
            ); // Call join fake logic
        });
    }
}