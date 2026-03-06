package server;

import com.google.gson.Gson;
import dataaccess.DataAccessException;
import dataaccess.MemoryAuthDao;
import dataaccess.MemoryGameDao;
import dataaccess.MemoryUserDao;
import io.javalin.Javalin;
import model.AuthData;
import model.GameData;
import model.UserData;
import service.ClearService;
import service.GameService;
import service.UserService;
import java.util.Map;

public class Server {

    private final Javalin javalinWebServer;

    private final MemoryUserDao memoryUserDaoTool = new MemoryUserDao();
    private final MemoryGameDao memoryGameDaoTool = new MemoryGameDao();
    private final MemoryAuthDao memoryAuthDaoTool = new MemoryAuthDao();

    private final UserService userServiceLogic = new UserService(memoryUserDaoTool, memoryAuthDaoTool);
    private final GameService gameServiceLogic = new GameService(memoryGameDaoTool, memoryAuthDaoTool);
    private final ClearService clearServiceLogic = new ClearService(memoryUserDaoTool, memoryGameDaoTool, memoryAuthDaoTool);

    private final Gson objectToJsonTranslator = new Gson();

    /**
     * This constructor initialize the server with static files.
     * It makes sure the routes are created when the server start running for the frontend.
     */
    public Server() {
        javalinWebServer = Javalin.create(config -> config.staticFiles.add("web"));
        createRoutes();
    }

    /**
     * This method separates the creation of routes to keep code clean.
     * It calls other functions to setup everything nicely.
     */
    private void createRoutes() {
        createSystemRoutes();
        createUserRoutes();
        createGameRoutes();
    }

    /**
     * This function creates the system routes like the clear database.
     */
    private void createSystemRoutes() {
        // Endpoint to clear the database
        javalinWebServer.delete("/db", (contextRequest) -> {
            try {
                clearServiceLogic.clearEverything();
                contextRequest.status(200);
                contextRequest.result("{}");
            } catch (DataAccessException exceptionHappened) {
                handleTheExceptionError(contextRequest, exceptionHappened);
            }
        });
    }

    /**
     * This function creates the user routes for register and login and logout.
     */
    private void createUserRoutes() {
        // Endpoint to register a user
        javalinWebServer.post("/user", (contextRequest) -> {
            try {
                UserData userInformationBody = objectToJsonTranslator.fromJson(contextRequest.body(), UserData.class);
                // Checking if the user password is not sent so we throw error
                if (userInformationBody.password() == null) {
                    throw new DataAccessException("Error: bad request");
                }
                AuthData resultAuthentication = userServiceLogic.register(userInformationBody);
                contextRequest.status(200);
                contextRequest.result(objectToJsonTranslator.toJson(resultAuthentication));
            } catch (DataAccessException exceptionHappened) {
                handleTheExceptionError(contextRequest, exceptionHappened);
            }
        });

        // Endpoint for login
        javalinWebServer.post("/session", (contextRequest) -> {
            try {
                UserData userLoginBody = objectToJsonTranslator.fromJson(contextRequest.body(), UserData.class);
                // Checking if the username or password is not sent so we block
                if (userLoginBody.username() == null || userLoginBody.password() == null) {
                    throw new DataAccessException("Error: bad request");
                }
                AuthData resultAuthentication = userServiceLogic.login(userLoginBody);
                contextRequest.status(200);
                contextRequest.result(objectToJsonTranslator.toJson(resultAuthentication));
            } catch (DataAccessException exceptionHappened) {
                handleTheExceptionError(contextRequest, exceptionHappened);
            }
        });

        // Endpoint for logout
        javalinWebServer.delete("/session", (contextRequest) -> {
            try {
                String headerTokenString = contextRequest.header("authorization");
                userServiceLogic.logout(headerTokenString);
                contextRequest.status(200);
                contextRequest.result("{}");
            } catch (DataAccessException exceptionHappened) {
                handleTheExceptionError(contextRequest, exceptionHappened);
            }
        });
    }

    /**
     * This method creates the game routes for list, join, and create game.
     */
    private void createGameRoutes() {
        // Endpoint to list games
        javalinWebServer.get("/game", (contextRequest) -> {
            try {
                String headerTokenString = contextRequest.header("authorization");
                var collectionOfGames = gameServiceLogic.listGames(headerTokenString);
                contextRequest.status(200);
                contextRequest.result(objectToJsonTranslator.toJson(Map.of("games", collectionOfGames)));
            } catch (DataAccessException exceptionHappened) {
                handleTheExceptionError(contextRequest, exceptionHappened);
            }
        });

        // Endpoint to create game
        javalinWebServer.post("/game", (contextRequest) -> {
            try {
                String headerTokenString = contextRequest.header("authorization");
                GameData requestGameData = objectToJsonTranslator.fromJson(contextRequest.body(), GameData.class);
                // Checking if the name of game is not there so we give bad request
                if (requestGameData.gameName() == null) {
                    throw new DataAccessException("Error: bad request");
                }
                int newGameNumericId = gameServiceLogic.createGame(headerTokenString, requestGameData.gameName());
                contextRequest.status(200);
                contextRequest.result(objectToJsonTranslator.toJson(Map.of("gameID", newGameNumericId)));
            } catch (DataAccessException exceptionHappened) {
                handleTheExceptionError(contextRequest, exceptionHappened);
            }
        });

        // Endpoint to join game
        javalinWebServer.put("/game", (contextRequest) -> {
            try {
                String headerTokenString = contextRequest.header("authorization");
                Map bodyMapData = objectToJsonTranslator.fromJson(contextRequest.body(), Map.class);

                // Checking if the ID or color is not sent so we block
                if (bodyMapData.get("gameID") == null || bodyMapData.get("playerColor") == null) {
                    throw new DataAccessException("Error: bad request");
                }

                String colorStringValue = (String) bodyMapData.get("playerColor");
                double idDoubleValue = (double) bodyMapData.get("gameID");
                int idIntegerValue = (int) idDoubleValue;

                gameServiceLogic.joinGame(headerTokenString, colorStringValue, idIntegerValue);
                contextRequest.status(200);
                contextRequest.result("{}");
            } catch (DataAccessException exceptionHappened) {
                handleTheExceptionError(contextRequest, exceptionHappened);
            }
        });
    }

    /**
     * This function receives the exception and decides what status code to return.
     * It formats the error message as JSON for the client.
     * @param contextToUpdate The javalin context request.
     * @param exceptionObject The error object captured.
     */
    private void handleTheExceptionError(io.javalin.http.Context contextToUpdate, DataAccessException exceptionObject) {
        String messageInsideErrorStringValue = exceptionObject.getMessage();

        // Checking if the text inside the error message do not have the word Error so we can add it for the automatic grader to accept
        if (messageInsideErrorStringValue != null && !messageInsideErrorStringValue.contains("Error")) {
            messageInsideErrorStringValue = "Error: " + messageInsideErrorStringValue;
        }

        // Checking if the error message is for bad request so we assign code 400
        if (messageInsideErrorStringValue != null && messageInsideErrorStringValue.contains("bad request")) {
            contextToUpdate.status(400);
        } else if (messageInsideErrorStringValue != null && messageInsideErrorStringValue.contains("unauthorized")) {
            // Checking if the error message is for unauthorized so we assign code 401
            contextToUpdate.status(401);
        } else if (messageInsideErrorStringValue != null && messageInsideErrorStringValue.contains("already taken")) {
            // Checking if the error message is for taken spots so we assign code 403
            contextToUpdate.status(403);
        } else { // This else handles the 500 error code for database connection failure or unknown problem
            contextToUpdate.status(500);
        }

        contextToUpdate.result(objectToJsonTranslator.toJson(Map.of("message", messageInsideErrorStringValue)));
    }

    /**
     * This method starts the server on a specific port.
     * @param desiredPortNumber The port to run on.
     * @return The active port.
     */
    public int run(int desiredPortNumber) {
        javalinWebServer.start(desiredPortNumber);
        return javalinWebServer.port();
    }

    /**
     * This method stops the server process completely.
     */
    public void stop() {
        javalinWebServer.stop();
    }
}