package client;

import com.google.gson.Gson;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URI;
import java.net.URL;

public class ServerFacadeForHttpCalls { // This class is make to communicate with server http
    private final String serverUrlStringPathForConnection; // The url string is store here
    private final Gson gsonConverterObjectForJsonParsing; // The gson object is store here

    public record RegisterRequestDataFormat(
            String username, String password, String email
    ) {} // Record for register data format to send string

    public record LoginRequestDataFormat(
            String username, String password
    ) {} // Record for login data format to send string

    public record CreateGameRequestDataFormat(
            String gameName
    ) {} // Record for create game format to send string

    public record JoinGameRequestDataFormat(
            String playerColor, int gameID
    ) {} // Record for join game format to send string and int

    public record AuthResponseDataFormat(
            String username, String authToken
    ) {} // Record for auth response to receive string

    public record GameResponseDataFormat(
            int gameID
    ) {} // Record for game response to receive int

    public record GameDataObjectFormat(
            int gameID, String whiteUsername, String blackUsername, String gameName
    ) {} // Record for game object to receive strings

    public record ListGamesResponseDataFormat(
            GameDataObjectFormat[] games
    ) {} // Record for list games response to receive array

    public ServerFacadeForHttpCalls(int portNumberForServerConnectionArgument) { // This constructor set url
        this.serverUrlStringPathForConnection =
                "http://localhost:" + portNumberForServerConnectionArgument; // Set the url string memory
        this.gsonConverterObjectForJsonParsing = new Gson(); // Initialize the gson object
    }

    public AuthResponseDataFormat registerUserOnServer(
            String usernameStringInputRaw, String passwordStringInputRaw, String emailStringInputRaw
    ) throws Exception { // This function is make to send register data and get auth token back
        RegisterRequestDataFormat requestDataObjectForRegisterInstance =
                new RegisterRequestDataFormat(usernameStringInputRaw, passwordStringInputRaw, emailStringInputRaw); // Create object
        return makeHttpCallToServerAndReturnResult(
                "POST", "/user", null,
                requestDataObjectForRegisterInstance, AuthResponseDataFormat.class
        ); // Call helper
    }

    public AuthResponseDataFormat loginUserOnServer(
            String usernameStringInputRaw, String passwordStringInputRaw
    ) throws Exception { // This function is make to send login data and receive token
        LoginRequestDataFormat requestDataObjectForLoginInstance =
                new LoginRequestDataFormat(usernameStringInputRaw, passwordStringInputRaw); // Create object
        return makeHttpCallToServerAndReturnResult(
                "POST", "/session", null,
                requestDataObjectForLoginInstance, AuthResponseDataFormat.class
        ); // Call helper
    }

    public void logoutUserOnServer(
            String authorizationTokenStringHeaderRaw
    ) throws Exception { // This function is make to logout user using token string
        makeHttpCallToServerAndReturnResult(
                "DELETE", "/session", authorizationTokenStringHeaderRaw, null, null
        ); // Call helper
    }

    public GameResponseDataFormat createGameOnServer(
            String authorizationTokenStringHeaderRaw, String gameNameStringInputRaw
    ) throws Exception { // This function is make to create game passing name string
        CreateGameRequestDataFormat requestDataObjectForCreateInstance =
                new CreateGameRequestDataFormat(gameNameStringInputRaw); // Create object
        return makeHttpCallToServerAndReturnResult(
                "POST", "/game", authorizationTokenStringHeaderRaw,
                requestDataObjectForCreateInstance, GameResponseDataFormat.class
        ); // Call helper
    }

    public ListGamesResponseDataFormat listGamesOnServer(
            String authorizationTokenStringHeaderRaw
    ) throws Exception { // This function list all games from server db
        return makeHttpCallToServerAndReturnResult(
                "GET", "/game", authorizationTokenStringHeaderRaw,
                null, ListGamesResponseDataFormat.class
        ); // Call helper
    }

    public void joinGameOnServer(
            String authorizationTokenStringHeaderRaw, String playerColorStringInputRaw,
            int gameNumericIdForJoiningTarget
    ) throws Exception { // This function join user in game with color string
        JoinGameRequestDataFormat requestDataObjectForJoinInstance =
                new JoinGameRequestDataFormat(playerColorStringInputRaw, gameNumericIdForJoiningTarget); // Create object
        makeHttpCallToServerAndReturnResult(
                "PUT", "/game", authorizationTokenStringHeaderRaw,
                requestDataObjectForJoinInstance, null
        ); // Call helper
    }

    public void clearApplicationDataOnServer() throws Exception { // This function send delete request db
        makeHttpCallToServerAndReturnResult(
                "DELETE", "/db", null, null, null
        ); // Call helper
    }

    private <T> T makeHttpCallToServerAndReturnResult(
            String httpMethodTypeStringVal, String urlPathStringDestVal,
            String authStringHeaderVal, Object reqBodyObjectDataVal, Class<T> resClassTypeVal
    ) throws Exception { // This function send request to server and read response
        URL targetUrlObjectForConnectionProcess =
                new URI(this.serverUrlStringPathForConnection + urlPathStringDestVal).toURL(); // Create url
        HttpURLConnection httpConnectionObjectToServerProcess =
                (HttpURLConnection) targetUrlObjectForConnectionProcess.openConnection(); // Open connection
        httpConnectionObjectToServerProcess.setRequestMethod(httpMethodTypeStringVal); // Set method string
        if (authStringHeaderVal != null) { // If auth token is provide put in header
            httpConnectionObjectToServerProcess.addRequestProperty(
                    "authorization", authStringHeaderVal
            ); // Add header
        }
        if (reqBodyObjectDataVal != null) { // If body data exist output is enable write json
            httpConnectionObjectToServerProcess.setDoOutput(true); // Enable output flag
            httpConnectionObjectToServerProcess.addRequestProperty(
                    "Content-Type", "application/json"
            ); // Add content header
            String jsonBodyStringConvertedValue =
                    this.gsonConverterObjectForJsonParsing.toJson(reqBodyObjectDataVal); // Convert json
            try (OutputStream outputStreamObjectForWritingData =
                         httpConnectionObjectToServerProcess.getOutputStream()) { // Try resources stream
                outputStreamObjectForWritingData.write(jsonBodyStringConvertedValue.getBytes()); // Write bytes
            }
        }
        httpConnectionObjectToServerProcess.connect(); // Connect to server address
        int responseCodeIntegerFromServerProcess =
                httpConnectionObjectToServerProcess.getResponseCode(); // Get response code
        if (responseCodeIntegerFromServerProcess >= 400) { // If code error throw exception message
            throw new Exception("Error from server code: " + responseCodeIntegerFromServerProcess); // Throw
        }
        if (resClassTypeVal != null) { // If response class provide read stream parse json
            try (InputStream inputStreamObjectForReadingData =
                         httpConnectionObjectToServerProcess.getInputStream()) { // Try resources read
                InputStreamReader readerObjectForInputStreamProcess =
                        new InputStreamReader(inputStreamObjectForReadingData); // Create reader
                return this.gsonConverterObjectForJsonParsing.fromJson(
                        readerObjectForInputStreamProcess, resClassTypeVal
                ); // Return parsed object
            }
        }
        return null; // Return null if no class
    }
}