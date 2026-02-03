# ♕ Chess

This project demonstrates mastery of proper software design, client/server architecture, networking using HTTP and WebSocket, database persistence, unit testing, serialization, and security.

## 10k Architecture Overview

The application implements a multiplayer chess server and a command line chess client.

[![Sequence Diagram](10k-architecture.png)](https://sequencediagram.org/index.html#initialData=C4S2BsFMAIGEAtIGckCh0AcCGAnUBjEbAO2DnBElIEZVs8RCSzYKrgAmO3AorU6AGVIOAG4jUAEyzAsAIyxIYAERnzFkdKgrFIuaKlaUa0ALQA+ISPE4AXNABWAexDFoAcywBbTcLEizS1VZBSVbbVc9HGgnADNYiN19QzZSDkCrfztHFzdPH1Q-Gwzg9TDEqJj4iuSjdmoMopF7LywAaxgvJ3FC6wCLaFLQyHCdSriEseSm6NMBurT7AFcMaWAYOSdcSRTjTka+7NaO6C6emZK1YdHI-Qma6N6ss3nU4Gpl1ZkNrZwdhfeByy9hwyBA7mIT2KAyGGhuSWi9wuc0sAI49nyMG6ElQQA)

**[📄 View Detailed Phase 2 Implementation Sequence Diagram](https://sequencediagram.org/index.html?presentationMode=readOnly#initialData=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-IbXIEFKo1Fr1YHvTtGWP8JaUUYWDf02q-IAA8NqYyHEdf805bIsDyutcxZAxLwDT3HGwrJecbFeWpRC5A9KzilxuKy+KorglpCSSUcylxqjWQFf4kVa70jiv6KESAv4Y3TFJsAM9F6SzmRMYwRVyrnKqrarADumqimMBNaUws3kRDgEQGe0g2AuD7VICFWlls-lbUNX2tpOrOnzTNWyC1u0ABWj6bWjLWt-P5dtAWotdXMsBCzPXg1Qas1cmDwHYNweGn6Tb-lYFDY8ntkajXUMue865WaE1nyTbpFNSsVa8bjVmvh2adjx1-kRm6gCNQ4dpeWxtWEq3no+iYG1eilmKq-NDaAcMTZmU2bEAN8mI45OIYc9jCdKFcf5pc4klZVpigE8woTTzs6ps4R5FzZk3NMm+dJ6UTovU4HAzwL05ZKyXmvMPTZEYyGExgCTWYJgKYiJwHgdg7oQCUwhfESE8AwCQFTH5aAABCAAvAc0hXmOPNIQ9GksZYLzVkEh52WsGJ0vM4fOJcpAVxrlDiF7thH9l9HpjACF5NvAYa0-+SsZlAPAbIKhSZUjiPQGm6C5TzEoXfV2eplMIgZvPt+eBHFzXWnGNMfqkTHDKW2I+Cuw9a7mjhLcavRwSNHCwFgB90VJ6B6kGAyPa9XQIdTgrLwagxIX3NRyW1Zeq914PACGY27PZrJcGgN4FDmlenWd2uwKA4gy16P0N6KAYaCNyZ+lRCnpBUyls2zi7bCnydw5LVT-NUyYXM7h8HcB7OpMNojnCzBpATbsFBZgnwUB3BXYjTjvS93QvJpzJOqx+dXvvFZUAA)**

## Modules

The application has three modules.

- **Client**: The command line program used to play a game of chess over the network.
- **Server**: The command line program that listens for network requests from the client and manages users and games.
- **Shared**: Code that is used by both the client and the server. This includes the rules of chess and tracking the state of a game.

## Starter Code

As you create your chess application you will move through specific phases of development. This starts with implementing the moves of chess and finishes with sending game moves over the network between your client and server. You will start each phase by copying course provided [starter-code](starter-code/) for that phase into the source code of the project. Do not copy a phases' starter code before you are ready to begin work on that phase.

## IntelliJ Support

Open the project directory in IntelliJ in order to develop, run, and debug your code using an IDE.

## Maven Support

You can use the following commands to build, test, package, and run your code.

| Command                    | Description                                     |
| -------------------------- | ----------------------------------------------- |
| `mvn compile`              | Builds the code                                 |
| `mvn package`              | Run the tests and build an Uber jar file        |
| `mvn package -DskipTests`  | Build an Uber jar file                          |
| `mvn install`              | Installs the packages into the local repository |
| `mvn test`                 | Run all the tests                               |
| `mvn -pl shared test`      | Run all the shared tests                        |
| `mvn -pl client exec:java` | Build and run the client `Main`                 |
| `mvn -pl server exec:java` | Build and run the server `Main`                 |

These commands are configured by the `pom.xml` (Project Object Model) files. There is a POM file in the root of the project, and one in each of the modules. The root POM defines any global dependencies and references the module POM files.

## Running the program using Java

Once you have compiled your project into an uber jar, you can execute it with the following command.

```sh
java -jar client/target/client-jar-with-dependencies.jar

♕ 240 Chess Client: chess.ChessPiece@7852e922