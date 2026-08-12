import java.io.*;
import java.net.*;
import java.util.*;

public class LDAPClient {

    static final String HOST = "localhost";
    static final int PORT = 389;  
    static final String BASE_DN = "ou=Automobiles,dc=assets,dc=com";

    public static void main(String[] args) throws IOException {
        // Open a raw TCP socket connection to the LDAP server
        Socket socket = new Socket(HOST, PORT);
        OutputStream out = socket.getOutputStream();
        InputStream in = socket.getInputStream();
        byte[] buffer = new byte[1024];

        // BIND (authenticate with the server) 
        String bindMsg = "BIND|anonymous";
        // Convert string to bytes and write to socket
        out.write(bindMsg.getBytes());
        out.flush();

        // Read response bytes from the server
        int bytesRead = in.read(buffer);
        String bindResp = new String(buffer, 0, bytesRead);
        System.out.println("Bind response: " + bindResp);

        // SEARCH loop
        Scanner scanner = new Scanner(System.in);

        while (true) {
            System.out.print("\nEnter asset name (or 'quit'): ");
            String name = scanner.nextLine().trim();
            if (name.equalsIgnoreCase("quit")) break;

            String searchMsg = "SEARCH|" + BASE_DN + "|cn=" + name;
            out.write(searchMsg.getBytes());
            out.flush();

            bytesRead = in.read(buffer);
            String response = new String(buffer, 0, bytesRead);
            if (response.startsWith("FOUND")) {
                // Response format: FOUND|cn=name|maxSpeed=val|value=val
                String[] parts = response.split("\\|");
                for (String part : parts) {
                    if (part.startsWith("maxSpeed=")) {
                        System.out.println("Maximum Speed: " + part.substring(9) + " km/h");
                    }
                    if (part.startsWith("value=")) {
                        System.out.println("Asset Value: R" + part.substring(6));
                    }
                }
            } else {
                System.out.println("Asset not found.");
            }
        }

        // UNBIND 
        out.write("UNBIND".getBytes());
        out.flush();

        socket.close();
        System.out.println("Disconnected.");
    }
}
