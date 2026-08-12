import java.io.*;
import java.net.*;
import java.util.*;

public class LDAPServer {

    // LDAP standard port
    static final int PORT = 389;
    static final String BASE_DN = "ou=Automobiles,dc=assets,dc=com";

    // Directory Information Tree (DIT) — stored as two HashMaps
    static HashMap<String, String> maxSpeedMap = new HashMap<>();
    static HashMap<String, String> valueMap = new HashMap<>();

    // Populate the DIT with asset entries
    static {
        maxSpeedMap.put("BMW_M3",  "290");
        maxSpeedMap.put("Golf8R",  "250");
        maxSpeedMap.put("AudiRS7", "305");

        valueMap.put("BMW_M3",  "1450000");
        valueMap.put("Golf8R",  "920000");
        valueMap.put("AudiRS7", "2100000");
    }

    public static void main(String[] args) throws IOException {
        System.out.println("LDAP Server started on port " + PORT);
        // Create a server socket to listen for client connections on port 389
        ServerSocket serverSocket = new ServerSocket(PORT);

        while (true) {
            Socket client = serverSocket.accept();
            System.out.println("Client connected");

            // Get raw byte streams for reading/writing
            InputStream in = client.getInputStream();
            OutputStream out = client.getOutputStream();
            byte[] buffer = new byte[1024];
            int bytesRead;

            // Read raw bytes from the client until they disconnect
            while ((bytesRead = in.read(buffer)) != -1) {
                String request = new String(buffer, 0, bytesRead).trim(); 
                System.out.println("Received: " + request);

                String response;

                // Route the request based on the LDAP operation type
                if (request.startsWith("BIND")) {
                    // BIND = authentication request, respond with success
                    response = "BIND_OK";

                } else if (request.startsWith("SEARCH")) {
                    // SEARCH = directory lookup request
                    response = handleSearch(request);

                } else if (request.equals("UNBIND")) {
                    // UNBIND = client is disconnecting
                    response = "UNBIND_OK";

                } else {
                    response = "ERROR";
                }

                // Send response back as raw bytes
                out.write(response.getBytes());
                out.flush();
            }

            client.close();
            System.out.println("Client disconnected");
        }
    }

    // Handles a SEARCH request by looking up the asset in the DIT
    static String handleSearch(String request) {
        String[] parts = request.split("\\|");
        if (parts.length < 3) return "NOT_FOUND";

        String filter = parts[2].trim();
        if (!filter.startsWith("cn=")) return "NOT_FOUND";

        // Get just the asset name from the filter
        String name = filter.substring(3);

        // Look up the asset in our directory
        if (maxSpeedMap.containsKey(name)) {
            return "FOUND|cn=" + name
                + "|maxSpeed=" + maxSpeedMap.get(name)
                + "|value=" + valueMap.get(name);
        }

        return "NOT_FOUND";
    }
}
