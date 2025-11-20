/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany._virginiah_client;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author vvtat
 */
public class EventHTTPImporter {
    // This method connects to the URL, reads the file, and returns a list of valid event strings
    public List<String> downloadEvents(String urlString) throws IOException {
        List<String> validEvents = new ArrayList<>();
        
        System.out.println("Connecting to: " + urlString);
        URL url = new URL(urlString);
        
        //HttpURLConnection
        HttpURLConnection httpCon = (HttpURLConnection) url.openConnection();
        httpCon.setRequestMethod("GET");
        
        // Check if connection is successful (HTTP 200)
        int responseCode = httpCon.getResponseCode();
        if (responseCode != 200) {
            throw new IOException("Server returned code: " + responseCode);
        }

        // Read the stream
        try (BufferedReader webReader = new BufferedReader(new InputStreamReader(httpCon.getInputStream()))) {
            String line;
            while ((line = webReader.readLine()) != null) {
                // Skip empty lines
                if (line.trim().isEmpty()) continue;

                // Validation: Checking if the line has 3 parts (Date; Time; Desc) to only return valid data to the client
                String[] parts = line.split(";");
                if (parts.length == 3) {
                    validEvents.add(line.trim());
                } else {
                    System.out.println("HTTP Importer skipping invalid line: " + line);
                }
            }
        } finally {
            httpCon.disconnect();
        }
        
        return validEvents;
    }
}
