package client;

import com.google.gson.Gson;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URI;
import java.net.URL;

public class ServerFacadeForHttpCalls { // This class is make to communicate with the server part using the http protocols for the application network logic
    private final String serverUrlStringPathForConnection; // The url string is store here for the connections usages
    private final Gson gsonConverterObjectForJsonParsing; // The gson object is store here to convert the data classes
    public record RegisterRequestDataFormat(String username, String password, String email) {} // Record for register data format to send string
    public record LoginRequestDataFormat(String username, String password) {} // Record for login data format to send string
    public record CreateGameRequestDataFormat(String gameName) {} // Record for create game format to send string
    public record JoinGameRequestDataFormat(String playerColor, int gameID) {} // Record for join game format to send string and int
    public record AuthResponseDataFormat(String username, String authToken) {} // Record for auth response to receive string
    public record GameResponseDataFormat(int gameID) {} // Record for game response to receive int
    public record GameDataObjectFormat(int gameID, String whiteUsername, String blackUsername, String gameName) {} // Record for game object to receive strings
    public record ListGamesResponseDataFormat(GameDataObjectFormat[] games) {} // Record for list games response to receive array list

    public ServerFacadeForHttpCalls(int portNumberForServerConnectionArgument) { // This constructor is make to set the url string using the port number provide
        this.serverUrlStringPathForConnection = "http://localhost:" + portNumberForServerConnectionArgument; // Set the url string variable memory
        this.gsonConverterObjectForJsonParsing = new Gson(); // Initialize the gson object in memory
    }

    public AuthResponseDataFormat registerUserOnServer(String usernameStringInputRaw, String passwordStringInputRaw, String emailStringInputRaw) throws Exception { // This function is make to send the register data to the server and get the auth token back
        RegisterRequestDataFormat requestDataObjectForRegisterInstance = new RegisterRequestDataFormat(usernameStringInputRaw, passwordStringInputRaw, emailStringInputRaw); // Create the request object
        return makeHttpCallToServerAndReturnResult("POST", "/user", null, requestDataObjectForRegisterInstance, AuthResponseDataFormat.class); // Call the helper and return
    }

    public AuthResponseDataFormat loginUserOnServer(String usernameStringInputRaw, String passwordStringInputRaw) throws Exception { // This function is make to send the login data and receive the token from the server
        LoginRequestDataFormat requestDataObjectForLoginInstance = new LoginRequestDataFormat(usernameStringInputRaw, passwordStringInputRaw); // Create the request object
        return makeHttpCallToServerAndReturnResult("POST", "/session", null, requestDataObjectForLoginInstance, AuthResponseDataFormat.class); // Call the helper and return
    }

    public void logoutUserOnServer(String authorizationTokenStringHeaderRaw) throws Exception { // This function is make to logout the user using the token string
        makeHttpCallToServerAndReturnResult("DELETE", "/session", authorizationTokenStringHeaderRaw, null, null); // Call the helper
    }

    public GameResponseDataFormat createGameOnServer(String authorizationTokenStringHeaderRaw, String gameNameStringInputRaw) throws Exception { // This function is make to create the game passing the name string
        CreateGameRequestDataFormat requestDataObjectForCreateInstance = new CreateGameRequestDataFormat(gameNameStringInputRaw); // Create the request object
        return makeHttpCallToServerAndReturnResult("POST", "/game", authorizationTokenStringHeaderRaw, requestDataObjectForCreateInstance, GameResponseDataFormat.class); // Call the helper and return
    }

    public ListGamesResponseDataFormat listGamesOnServer(String authorizationTokenStringHeaderRaw) throws Exception { // This function is make to list all the games from the server db using token
        return makeHttpCallToServerAndReturnResult("GET", "/game", authorizationTokenStringHeaderRaw, null, ListGamesResponseDataFormat.class); // Call the helper and return
    }

    public void joinGameOnServer(String authorizationTokenStringHeaderRaw, String playerColorStringInputRaw, int gameNumericIdForJoiningTarget) throws Exception { // This function is make to join the user in the game with the color string
        JoinGameRequestDataFormat requestDataObjectForJoinInstance = new JoinGameRequestDataFormat(playerColorStringInputRaw, gameNumericIdForJoiningTarget); // Create the request object
        makeHttpCallToServerAndReturnResult("PUT", "/game", authorizationTokenStringHeaderRaw, requestDataObjectForJoinInstance, null); // Call the helper
    }

    public void clearApplicationDataOnServer() throws Exception { // This function is make to send the delete request to the db endpoint to clear all data memory
        makeHttpCallToServerAndReturnResult("DELETE", "/db", null, null, null); // Call the helper
    }

    private <T> T makeHttpCallToServerAndReturnResult(String httpMethodTypeStringVal, String urlPathStringDestVal, String authStringHeaderVal, Object reqBodyObjectDataVal, Class<T> resClassTypeVal) throws Exception { // This function is make to send the request to the server and read the response back
        URL targetUrlObjectForConnectionProcess = new URI(this.serverUrlStringPathForConnection + urlPathStringDestVal).toURL(); // Create the url object
        HttpURLConnection httpConnectionObjectToServerProcess = (HttpURLConnection) targetUrlObjectForConnectionProcess.openConnection(); // Open the connection object
        httpConnectionObjectToServerProcess.setRequestMethod(httpMethodTypeStringVal); // Set the method type string
        if (authStringHeaderVal != null) { // If the auth token is provide then it must be put in the header of the request
            httpConnectionObjectToServerProcess.addRequestProperty("authorization", authStringHeaderVal); // Add the header
        }
        if (reqBodyObjectDataVal != null) { // If there is body data then the output is enable and the json is write
            httpConnectionObjectToServerProcess.setDoOutput(true); // Enable the output flag
            httpConnectionObjectToServerProcess.addRequestProperty("Content-Type", "application/json"); // Add the content type header
            String jsonBodyStringConvertedValue = this.gsonConverterObjectForJsonParsing.toJson(reqBodyObjectDataVal); // Convert to json format
            try (OutputStream outputStreamObjectForWritingData = httpConnectionObjectToServerProcess.getOutputStream()) { // Try with resources for the stream write
                outputStreamObjectForWritingData.write(jsonBodyStringConvertedValue.getBytes()); // Write the bytes
            }
        }
        httpConnectionObjectToServerProcess.connect(); // Connect to the server address
        int responseCodeIntegerFromServerProcess = httpConnectionObjectToServerProcess.getResponseCode(); // Get the response code
        if (responseCodeIntegerFromServerProcess >= 400) { // If the code is error then it throw an exception with the message
            throw new Exception("Error from server with code: " + responseCodeIntegerFromServerProcess); // Throw the exception
        }
        if (resClassTypeVal != null) { // If the response class is provide then it read the input stream and parse the json format
            try (InputStream inputStreamObjectForReadingData = httpConnectionObjectToServerProcess.getInputStream()) { // Try with resources for the input stream read
                InputStreamReader readerObjectForInputStreamProcess = new InputStreamReader(inputStreamObjectForReadingData); // Create the reader object
                return this.gsonConverterObjectForJsonParsing.fromJson(readerObjectForInputStreamProcess, resClassTypeVal); // Return the parsed object data
            }
        }
        return null; // Return null if no class is provide for response
    }
}