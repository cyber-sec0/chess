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

    private final Javalin javalin;

    // These are the instances of the classes for the server logic
    private final MemoryUserDao userDao = new MemoryUserDao();
    private final MemoryGameDao gameDao = new MemoryGameDao();
    private final MemoryAuthDao authDao = new MemoryAuthDao();

    private final UserService userService = new UserService(userDao, authDao);
    private final GameService gameService = new GameService(gameDao, authDao);
    private final ClearService clearService = new ClearService(userDao, gameDao, authDao);

    private final Gson jsonSerializer = new Gson();

    public Server() {
        javalin = Javalin.create(config -> config.staticFiles.add("web"));

        // Register your endpoints and exception handlers here.

        // Endpoint to clear the database
        javalin.delete("/db", (ctx) -> {
            clearService.clearEverything();
            ctx.status(200);
            // We use result() instead of json() to avoid the "Missing Jackson" error
            ctx.result("{}");
        });

        // Endpoint to register a user
        javalin.post("/user", (ctx) -> {
            try {
                UserData bodyData = jsonSerializer.fromJson(ctx.body(), UserData.class);
                AuthData resultAuth = userService.register(bodyData);
                ctx.status(200);
                // Convert object to JSON string manually
                ctx.result(jsonSerializer.toJson(resultAuth));
            } catch (DataAccessException exceptionVariable) {
                handleException(ctx, exceptionVariable);
            }
        });

        // Endpoint for login
        javalin.post("/session", (ctx) -> {
            try {
                UserData bodyData = jsonSerializer.fromJson(ctx.body(), UserData.class);
                AuthData resultAuth = userService.login(bodyData);
                ctx.status(200);
                ctx.result(jsonSerializer.toJson(resultAuth));
            } catch (DataAccessException exceptionVariable) {
                handleException(ctx, exceptionVariable);
            }
        });

        // Endpoint for logout
        javalin.delete("/session", (ctx) -> {
            try {
                String headerToken = ctx.header("authorization");
                userService.logout(headerToken);
                ctx.status(200);
                ctx.result("{}");
            } catch (DataAccessException exceptionVariable) {
                handleException(ctx, exceptionVariable);
            }
        });

        // Endpoint to list games
        javalin.get("/game", (ctx) -> {
            try {
                String headerToken = ctx.header("authorization");
                var listOfGames = gameService.listGames(headerToken);
                // We need to wrap the list in a map for the JSON format "games": [...]
                ctx.status(200);
                ctx.result(jsonSerializer.toJson(Map.of("games", listOfGames)));
            } catch (DataAccessException exceptionVariable) {
                handleException(ctx, exceptionVariable);
            }
        });

        // Endpoint to create game
        javalin.post("/game", (ctx) -> {
            try {
                String headerToken = ctx.header("authorization");
                GameData requestGame = jsonSerializer.fromJson(ctx.body(), GameData.class);
                int newId = gameService.createGame(headerToken, requestGame.gameName());
                ctx.status(200);
                ctx.result(jsonSerializer.toJson(Map.of("gameID", newId)));
            } catch (DataAccessException exceptionVariable) {
                handleException(ctx, exceptionVariable);
            }
        });

        // Endpoint to join game
        javalin.put("/game", (ctx) -> {
            try {
                String headerToken = ctx.header("authorization");
                // Need a helper class or map because body has playerColor and gameID
                Map bodyMap = jsonSerializer.fromJson(ctx.body(), Map.class);
                String colorString = (String) bodyMap.get("playerColor");
                // The number comes as double from Gson sometimes so we cast carefully
                double idDouble = (double) bodyMap.get("gameID");
                int idInt = (int) idDouble;

                gameService.joinGame(headerToken, colorString, idInt);
                ctx.status(200);
                ctx.result("{}");
            } catch (DataAccessException exceptionVariable) {
                handleException(ctx, exceptionVariable);
            }
        });
    }

    /**
     * This function is a helper to set the status code based on the exception message.
     * It looks at the string inside the exception to decide.
     */
    private void handleException(io.javalin.http.Context ctx, DataAccessException ex) {
        String messageOfError = ex.getMessage();
        if (messageOfError.contains("bad request")) {
            ctx.status(400);
        } else if (messageOfError.contains("unauthorized")) {
            ctx.status(401);
        } else if (messageOfError.contains("already taken")) {
            ctx.status(403);
        } else {
            ctx.status(500);
        }
        ctx.result(jsonSerializer.toJson(Map.of("message", messageOfError)));
    }

    public int run(int desiredPort) {
        javalin.start(desiredPort);
        return javalin.port();
    }

    public void stop() {
        javalin.stop();
    }
}