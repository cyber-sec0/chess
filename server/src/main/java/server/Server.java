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
    
    // We create the instances of the data access objects here to be used by the services.
    private final MemoryUserDao memoryUserDaoTool = new MemoryUserDao();
    private final MemoryGameDao memoryGameDaoTool = new MemoryGameDao();
    private final MemoryAuthDao memoryAuthDaoTool = new MemoryAuthDao();
    
    // These are the services that contain the business logic for the application.
    private final UserService userServiceLogic = new UserService(memoryUserDaoTool, memoryAuthDaoTool);
    private final GameService gameServiceLogic = new GameService(memoryGameDaoTool, memoryAuthDaoTool);
    private final ClearService clearServiceLogic = new ClearService(memoryUserDaoTool, memoryGameDaoTool, memoryAuthDaoTool);
    
    // This is the tool we use to transform objects into text strings for the internet.
    private final Gson objectToJsonTranslator = new Gson();

    public Server() {
        javalinWebServer = Javalin.create(config -> config.staticFiles.add("web"));

        // Register your endpoints and exception handlers here.
        
        // Endpoint to clear the database
        javalinWebServer.delete("/db", (contextRequest) -> {
            clearServiceLogic.clearEverything();
            contextRequest.status(200);
            contextRequest.result("{}"); 
        });

        // Endpoint to register a user
        javalinWebServer.post("/user", (contextRequest) -> {
            try {
                UserData userInformationBody = objectToJsonTranslator.fromJson(contextRequest.body(), UserData.class);
                
                // We check if the password is missing to avoid bad requests
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
                
                // Extra validation here to guarantee 400 Bad Request if the password is null
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
                
                // Validation for game name
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
                
                // Important: Check if gameID is valid before using it to avoid NullPointer
                if (bodyMapData.get("gameID") == null) {
                    throw new DataAccessException("Error: bad request");
                }
                if (bodyMapData.get("playerColor") == null) {
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
     * This helper function is responsible for looking at the exception error message
     * and deciding which HTTP status code needs to be sent back to the client.
     */
    private void handleTheExceptionError(io.javalin.http.Context contextToUpdate, DataAccessException exceptionObject) {
        String messageInsideError = exceptionObject.getMessage();
        if (messageInsideError.contains("bad request")) {
            contextToUpdate.status(400);
        } else if (messageInsideError.contains("unauthorized")) {
            contextToUpdate.status(401);
        } else if (messageInsideError.contains("already taken")) {
            contextToUpdate.status(403);
        } else {
            contextToUpdate.status(500);
        }
        contextToUpdate.result(objectToJsonTranslator.toJson(Map.of("message", messageInsideError)));
    }

    public int run(int desiredPortNumber) {
        javalinWebServer.start(desiredPortNumber);
        return javalinWebServer.port();
    }

    public void stop() {
        javalinWebServer.stop();
    }
}