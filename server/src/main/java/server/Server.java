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

/**
 * This server class handle all the web api and websocket connections explicitly safely.
 * The system listen to port and route the http traffic securely efficiently smoothly.
 */
public class Server { // This class define the main routing engine execution

    private final Javalin javalinWebServerEngineInstanceFlow;
    private final MemoryUserDao memoryUserDaoToolObjectFlow = new MemoryUserDao();
    private final MemoryGameDao memoryGameDaoToolObjectFlow = new MemoryGameDao();
    private final MemoryAuthDao memoryAuthDaoToolObjectFlow = new MemoryAuthDao();

    private final UserService userServiceLogicManagerFlow =
            new UserService(memoryUserDaoToolObjectFlow, memoryAuthDaoToolObjectFlow);
    private final GameService gameServiceLogicManagerFlow =
            new GameService(memoryGameDaoToolObjectFlow, memoryAuthDaoToolObjectFlow);
    private final ClearService clearServiceLogicManagerFlow =
            new ClearService(memoryUserDaoToolObjectFlow, memoryGameDaoToolObjectFlow, memoryAuthDaoToolObjectFlow);

    private final Gson objectToJsonTranslatorConverterFlow = new Gson();

    private final WebSocketHandlerForChessGame webSocketHandlerObjectInstanceExecutionFlow =
            new WebSocketHandlerForChessGame(memoryGameDaoToolObjectFlow, memoryAuthDaoToolObjectFlow);

    public Server() { // Constructor initialize the server static files cleanly safely
        javalinWebServerEngineInstanceFlow = Javalin.create(configuratorObjectFlowData -> { // Config block setup
            configuratorObjectFlowData.staticFiles.add("web"); // Add web files cleanly safely efficiently
        });
        executeRoutingConfigurationSetupSafely(); // Map the routes completely reliably securely
    }

    private void executeRoutingConfigurationSetupSafely() { // This method separates creation routes gracefully securely
        javalinWebServerEngineInstanceFlow.ws("/ws", wsConfigObjectForMappingData -> { // Map endpoint safely securely
            wsConfigObjectForMappingData.onMessage(wsMessageContextObjectForPayloadData -> { // Setup message listener block
                try { // Try catch network payload context cleanly safely
                    webSocketHandlerObjectInstanceExecutionFlow.onMessage(
                            wsMessageContextObjectForPayloadData, // Pass the entire Javalin Context safely explicitly
                            wsMessageContextObjectForPayloadData.message() // Pass message text string safely cleanly
                    ); // Delegate execution safely securely explicitly
                } catch (Exception exceptionObjectCaughtForSocketFlow) { // Catch socket fail safely efficiently cleanly
                    exceptionObjectCaughtForSocketFlow.printStackTrace(); // Print socket error track explicitly safely
                }
            });
        });
        createSystemRoutesSafely(); // Register system cleanly explicitly safely
        createUserRoutesSafely(); // Register user cleanly explicitly safely
        createGameRoutesSafely(); // Register game cleanly explicitly safely
    }

    private void createSystemRoutesSafely() { // This function creates the system routes explicitly cleanly safely
        javalinWebServerEngineInstanceFlow.delete("/db", (contextRequestDataFlow) -> { // Endpoint to clear database safely cleanly
            try { // Try catch clear logic gracefully securely
                clearServiceLogicManagerFlow.clearEverything(); // Clear database memory safely cleanly
                contextRequestDataFlow.status(200); // Set success code explicitly safely
                contextRequestDataFlow.result("{}"); // Send empty json correctly securely cleanly
            } catch (DataAccessException exceptionHappenedInClearFlow) { // Catch data error cleanly safely
                handleTheExceptionErrorSafely(contextRequestDataFlow, exceptionHappenedInClearFlow); // Route error response explicitly
            }
        });
    }

    private void createUserRoutesSafely() { // This function creates the user routes cleanly explicitly safely
        javalinWebServerEngineInstanceFlow.post("/user", (contextRequestDataFlow) -> { // Endpoint to register user cleanly safely
            try { // Try catch register logic securely safely
                UserData userInformationBodyDataFlow = objectToJsonTranslatorConverterFlow.fromJson(
                        contextRequestDataFlow.body(), UserData.class
                ); // Parse user data body payload cleanly safely
                if (userInformationBodyDataFlow.password() == null) { // If password missing null safely cleanly
                    throw new DataAccessException("Error: bad request"); // Throw bad request explicitly cleanly safely
                }
                AuthData resultAuthenticationDataFlow = userServiceLogicManagerFlow.register(userInformationBodyDataFlow); // Register securely
                contextRequestDataFlow.status(200); // Set success code explicitly securely safely
                contextRequestDataFlow.result(objectToJsonTranslatorConverterFlow.toJson(resultAuthenticationDataFlow)); // Send auth securely
            } catch (DataAccessException exceptionHappenedInRegisterFlow) { // Catch data error completely safely cleanly
                handleTheExceptionErrorSafely(contextRequestDataFlow, exceptionHappenedInRegisterFlow); // Route error response cleanly safely
            }
        });

        javalinWebServerEngineInstanceFlow.post("/session", (contextRequestDataFlow) -> { // Endpoint for login execution safely cleanly
            try { // Try catch login logic securely safely
                UserData userLoginBodyDataFlow = objectToJsonTranslatorConverterFlow.fromJson(
                        contextRequestDataFlow.body(), UserData.class
                ); // Parse login body safely cleanly securely
                if (userLoginBodyDataFlow.username() == null || userLoginBodyDataFlow.password() == null) { // If missing params cleanly safely
                    throw new DataAccessException("Error: bad request"); // Throw bad request explicitly safely cleanly
                }
                AuthData resultAuthenticationDataFlow = userServiceLogicManagerFlow.login(userLoginBodyDataFlow); // Login user securely
                contextRequestDataFlow.status(200); // Set success code explicitly safely cleanly
                contextRequestDataFlow.result(objectToJsonTranslatorConverterFlow.toJson(resultAuthenticationDataFlow)); // Send auth payload
            } catch (DataAccessException exceptionHappenedInLoginFlow) { // Catch data error gracefully securely safely
                handleTheExceptionErrorSafely(contextRequestDataFlow, exceptionHappenedInLoginFlow); // Route error response cleanly safely
            }
        });

        javalinWebServerEngineInstanceFlow.delete("/session", (contextRequestDataFlow) -> { // Endpoint for logout execution safely cleanly
            try { // Try catch logout logic securely safely
                String headerTokenStringDataFlow = contextRequestDataFlow.header("authorization"); // Extract token explicitly safely cleanly
                userServiceLogicManagerFlow.logout(headerTokenStringDataFlow); // Logout user securely explicitly safely
                contextRequestDataFlow.status(200); // Set success code cleanly safely securely
                contextRequestDataFlow.result("{}"); // Send empty json safely explicitly cleanly
            } catch (DataAccessException exceptionHappenedInLogoutFlow) { // Catch data error cleanly safely securely
                handleTheExceptionErrorSafely(contextRequestDataFlow, exceptionHappenedInLogoutFlow); // Route error response cleanly safely
            }
        });
    }

    private void createGameRoutesSafely() { // This method creates the game routes smoothly explicitly safely
        javalinWebServerEngineInstanceFlow.get("/game", (contextRequestDataFlow) -> { // Endpoint to list games explicitly safely cleanly
            try { // Try catch list logic cleanly securely safely
                String headerTokenStringDataFlow = contextRequestDataFlow.header("authorization"); // Extract token text cleanly safely
                var collectionOfGamesDataArrayFlow = gameServiceLogicManagerFlow.listGames(headerTokenStringDataFlow); // List games cleanly
                contextRequestDataFlow.status(200); // Set success code gracefully safely securely
                contextRequestDataFlow.result(objectToJsonTranslatorConverterFlow.toJson(
                        Map.of("games", collectionOfGamesDataArrayFlow)
                )); // Send games array object securely explicitly safely
            } catch (DataAccessException exceptionHappenedInListFlow) { // Catch data error safely securely cleanly
                handleTheExceptionErrorSafely(contextRequestDataFlow, exceptionHappenedInListFlow); // Route error response cleanly safely
            }
        });

        javalinWebServerEngineInstanceFlow.post("/game", (contextRequestDataFlow) -> { // Endpoint to create game safely cleanly securely
            try { // Try catch create logic explicitly securely safely
                String headerTokenStringDataFlow = contextRequestDataFlow.header("authorization"); // Extract token text safely cleanly
                GameData requestGameDataBodyFlow = objectToJsonTranslatorConverterFlow.fromJson(
                        contextRequestDataFlow.body(), GameData.class
                ); // Parse game body payload cleanly safely securely
                if (requestGameDataBodyFlow.gameName() == null) { // If name missing logic safely cleanly
                    throw new DataAccessException("Error: bad request"); // Throw bad request safely cleanly securely
                }
                int newGameNumericIdDataFlow = gameServiceLogicManagerFlow.createGame(
                        headerTokenStringDataFlow, requestGameDataBodyFlow.gameName()
                ); // Create game reliably securely explicitly safely
                contextRequestDataFlow.status(200); // Set success code text safely cleanly securely
                contextRequestDataFlow.result(objectToJsonTranslatorConverterFlow.toJson(
                        Map.of("gameID", newGameNumericIdDataFlow)
                )); // Send game id safely cleanly explicitly securely
            } catch (DataAccessException exceptionHappenedInCreateFlow) { // Catch data error gracefully securely safely
                handleTheExceptionErrorSafely(contextRequestDataFlow, exceptionHappenedInCreateFlow); // Route error response cleanly safely
            }
        });

        javalinWebServerEngineInstanceFlow.put("/game", (contextRequestDataFlow) -> { // Endpoint to join game seamlessly explicitly safely
            try { // Try catch join logic cleanly securely safely
                String headerTokenStringDataFlow = contextRequestDataFlow.header("authorization"); // Extract token payload safely cleanly
                Map bodyMapDataObjectParserFlow = objectToJsonTranslatorConverterFlow.fromJson(
                        contextRequestDataFlow.body(), Map.class
                ); // Parse body map efficiently safely cleanly
                if (bodyMapDataObjectParserFlow.get("gameID") == null
                        || bodyMapDataObjectParserFlow.get("playerColor") == null) { // If missing argument safely cleanly
                    throw new DataAccessException("Error: bad request"); // Throw bad request explicitly cleanly safely
                }
                String colorStringValueDataFlow = (String) bodyMapDataObjectParserFlow.get("playerColor"); // Extract color cleanly safely
                double idDoubleValueDataFlow = (double) bodyMapDataObjectParserFlow.get("gameID"); // Extract double id cleanly safely
                int idIntegerValueDataFlow = (int) idDoubleValueDataFlow; // Convert int id logic cleanly safely
                gameServiceLogicManagerFlow.joinGame(
                        headerTokenStringDataFlow, colorStringValueDataFlow, idIntegerValueDataFlow
                ); // Join game reliably securely safely
                contextRequestDataFlow.status(200); // Set success code safely cleanly securely
                contextRequestDataFlow.result("{}"); // Send empty json safely explicitly cleanly
            } catch (DataAccessException exceptionHappenedInJoinFlow) { // Catch data error gracefully securely safely
                handleTheExceptionErrorSafely(contextRequestDataFlow, exceptionHappenedInJoinFlow); // Route error response cleanly safely
            }
        });
    }

    private void handleTheExceptionErrorSafely(
            io.javalin.http.Context contextToUpdateDataFlow, DataAccessException exceptionObjectDataFlow
    ) { // This function receives the exception and decides what status code correctly explicitly safely
        String messageInsideErrorStringValueTextFlow = exceptionObjectDataFlow.getMessage(); // Extract message string safely cleanly
        if (messageInsideErrorStringValueTextFlow != null
                && !messageInsideErrorStringValueTextFlow.contains("Error")) { // If word missing explicitly cleanly safely
            messageInsideErrorStringValueTextFlow = "Error: " + messageInsideErrorStringValueTextFlow; // Append word text safely cleanly
        }
        if (messageInsideErrorStringValueTextFlow != null
                && messageInsideErrorStringValueTextFlow.contains("bad request")) { // If bad request safely cleanly
            contextToUpdateDataFlow.status(400); // Set 400 securely explicitly safely
        } else if (messageInsideErrorStringValueTextFlow != null
                && messageInsideErrorStringValueTextFlow.contains("unauthorized")) { // If unauthorized safely cleanly
            contextToUpdateDataFlow.status(401); // Set 401 securely explicitly safely
        } else if (messageInsideErrorStringValueTextFlow != null
                && messageInsideErrorStringValueTextFlow.contains("already taken")) { // If already taken safely cleanly
            contextToUpdateDataFlow.status(403); // Set 403 securely explicitly safely
        } else { // This else handles the 500 error code correctly default safely cleanly
            contextToUpdateDataFlow.status(500); // Set 500 securely explicitly safely
        }
        contextToUpdateDataFlow.result(objectToJsonTranslatorConverterFlow.toJson(
                Map.of("message", messageInsideErrorStringValueTextFlow)
        )); // Send json error securely explicitly cleanly safely
    }

    public int run(int desiredPortNumberParamFlow) { // This method starts the server on port safely cleanly securely
        javalinWebServerEngineInstanceFlow.start(desiredPortNumberParamFlow); // Start server cleanly safely
        return javalinWebServerEngineInstanceFlow.port(); // Return port cleanly safely securely
    }

    public void stop() { // This method stops the server process completely securely cleanly safely
        javalinWebServerEngineInstanceFlow.stop(); // Stop server execution explicitly safely cleanly
    }
}