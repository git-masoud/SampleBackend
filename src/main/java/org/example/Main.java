package org.example;

import com.google.gson.Gson;

import java.io.FileOutputStream;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.channels.Channels;
import java.nio.channels.ReadableByteChannel;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import static spark.Spark.port;
import static spark.Spark.post;

public class Main {

    static int fileInc=1;
    public static void main(String[] args) throws IOException {

        port(8085); // Change this to your desired port
        // Endpoint to handle POST requests
        post("/api/v1/public", "application/json", (request, response) -> {
            // Parse JSON payload
            Gson gson = new Gson();
            Data data = gson.fromJson(request.body(), Data.class);
            download(data.authCode, data.endpoint, data.id);
            // Respond with a success message
            response.type("application/json");
            return "{\"message\":\"Success\"}";
        });
    }
    private static void createDirectoryIfNotExists(String directoryPath) {
        Path path = Paths.get(directoryPath);

        if (!Files.exists(path)) {
            try {
                Files.createDirectories(path);
                System.out.println("Directory '" + path.toAbsolutePath() + "' created.");
            } catch (Exception e) {
                System.err.println("Error creating directory: " + e.getMessage());
            }
        }
    }
static void download(String authCode, String endPointUrl,String dataId) throws IOException {
    int bufferSize = 8192;
    // Process the received JSON data
    // In this example, we'll just log it
    System.out.println("Received data: " + dataId );
    createDirectoryIfNotExists("data");
    URL url = new URL(endPointUrl);
    HttpURLConnection connection = (HttpURLConnection) url.openConnection();
    connection.setRequestProperty("Authorization", authCode);
    // Set up the connection properties
    connection.setRequestMethod("GET");

    String outputFile="data/"+ fileInc + "-" + dataId;
    // Use NIO to transfer data directly from the InputStream to the file
    try (ReadableByteChannel readableByteChannel = Channels.newChannel(connection.getInputStream());
         FileOutputStream fileOutputStream = new FileOutputStream(outputFile)) {

        // Transfer data from the channel to the file
        fileOutputStream.getChannel().transferFrom(readableByteChannel, 0, Long.MAX_VALUE);

        // Close the connection
        connection.disconnect();

        System.out.println("Data downloaded and saved to " + outputFile);

    } catch (IOException e) {
        e.printStackTrace();
    }

}
    // Class to represent the JSON data structure
    static class Data {
        String id;
        String endpoint;
        String authKey;
        String authCode;
        Properties properties;

        // Add getters and setters as needed
    }

    // Class to represent the "properties" field in the JSON data
    static class Properties {
        // Add properties as needed
    }
}