/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 */

package com.mycompany._virginiah_client;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ConnectException;
import java.net.Socket;
import java.util.List;

/**
 *
 * @author vvtat
 */
public class App {
    //Defininh the host ip address and same port as server
    private static final String HOST = "localhost";
    private static final int PORT = 1234;
    
    public static void main(String[] args) {
        try (Socket link = new Socket(HOST, PORT);
            BufferedReader in = new BufferedReader(new InputStreamReader(link.getInputStream()));
            PrintWriter out = new PrintWriter(link.getOutputStream(), true);
            BufferedReader userEntry = new BufferedReader(new InputStreamReader(System.in))) {

            System.out.println("Connected to Event Board Server" + "\n");
            System.out.println("Enter commands (add/remove/list) for events:" + "\n");
            System.out.println("Type 'STOP' to exit, or 'import; URL' to load events.");

            String message, response;
            
            // Creating an instance of HTTP class
            EventHTTPImporter importer = new EventHTTPImporter();

            do {
                System.out.print("Enter message> ");
                message = userEntry.readLine();
                
                if (message == null) break;

                //HTTP Import logic
                if (message.toLowerCase().startsWith("import;")) {
                    String[] parts = message.split(";");
                    if (parts.length < 2) {
                        System.out.println("Usage: import; <URL>");
                        continue;
                    }

                    try {
                        String url = parts[1].trim();
                        // 1. Using the HTTP Class
                        List<String> events = importer.downloadEvents(url);
                        
                        System.out.println("Found " + events.size() + " valid events. Sending to server...");

                        // 2. Loop through the returned list and send to TCP server
                        for (String eventLine : events) {
                            // Construct the protocol message: "add; Date; Time; Desc"
                            String tcpCommand = "add; " + eventLine;
                            
                            out.println(tcpCommand); // Send to Server
                            
                            String serverResp = in.readLine(); // Wait for Server Reply
                            System.out.println("Imported [" + eventLine + "]: " + serverResp);
                        }
                        System.out.println("Import complete.");

                    } catch (Exception e) {
                        System.out.println("Import failed: " + e.getMessage());
                    }
                    continue; // loop back to start
                }
                

                //  Message
                out.println(message);
                response = in.readLine();
                System.out.println("SERVER RESPONSE> " + response);

            } while (!message.equalsIgnoreCase("STOP"));

        } catch (ConnectException e) {
            System.out.println("Connection Refused: Ensure the server is running before starting the client.");
        } catch (IOException e) {
            //e.printStackTrace();  This prints the hwole error stack
            System.out.println("Client Error: " + e.getMessage());
        }
    }    
}
