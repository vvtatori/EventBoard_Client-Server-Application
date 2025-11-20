/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany._virginiah_server;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 *
 * @author vvtat
 */
public class ClientHandler implements Runnable{
    private Socket clientLink; //client socket
    private EventBoard board;

    public ClientHandler(Socket clientLink, EventBoard board) {
        this.clientLink = clientLink;
        this.board = board;
    }
    
    public void run(){
        try{
            BufferedReader in = new BufferedReader(new InputStreamReader(clientLink.getInputStream()));
            PrintWriter out = new PrintWriter(clientLink.getOutputStream(), true);

            String message;
            while((message = in.readLine()) != null){
                //Check for STOP to terminate
                if(message.equalsIgnoreCase("STOP")){
                    out.println("TERMINATE");
                    break;
                }
                try{
                    String response = processCommand(message);
                    out.println(response);
                } catch(InvalidCommandException e){
                    //error sent to the client
                    out.println(e.getMessage());
                } catch(Exception e){
                    out.println("Error: " + e.getMessage());
                }
            }
        } catch(IOException e){
            e.printStackTrace();
        } finally {
            try{
                clientLink.close();
            } catch(IOException e){
                e.printStackTrace();
            }
        }       
    }
    
    //method to compute how the commands are executed by the server
    private String processCommand(String message) throws InvalidCommandException{
        //To use to check if the command being sent by the client follows hass all the parts of the event, plus the action(should have 4 parts)
        String parts[] = message.split(";");
        
        //Validation
        if(parts.length != 4){
            throw new InvalidCommandException("Message must have 4 parts (Action; Date; Time; Desc)");
        }
        
        String action = parts[0].trim().toLowerCase();
        //Cganging the date before using it in the eventboard
        String date = normalizeDate(parts[1]);
        
        //String date = parts[1].trim();
        String time = parts[2].trim();
        String description = parts[3].trim();
        
        //Switch for each action to perform the corresponding message
        switch(action){
            case "add":
                //Validate time format
//                if(!time.contains("am") && !time.contains("pm")){
//                    throw new InvalidCommandException("Time must include am or pm");
//                }
                validateTime(time);
                return board.addEvent(date, time, description);
                
            case "remove":
                return board.removeEvent(date, time, description);
                
            case "list":
                return board.getEventsForDate(date);
                
            default: //Exception for any other action
                throw new InvalidCommandException("Unknown action: " + action + " action must be add/remove/list");
        }
    }
    
        //Validation methods
    //Correcting the space issue on the date - disregard the space in the date, to mean the same date
    //"5nov 2024"  -> "5 November 2024"
    //Mapping short month versions to their full month versions
    //"5 Nov 2024" -> "5 November 2024"
    private String normalizeDate(String inputDate) throws InvalidCommandException {
        // Logic used Explanation:
        // (\d{1,2})   -> Group 1: Captures 1 or 2 digits for (Day) input
        // \s* -> Ignores any amount of whitespace (spaces) between the day and motnh
        // ([a-zA-Z]+) -> Group 2: Captures any letters (Month)
        // \s* -> Ignores whitespace
        // (\d{4})     -> Group 3: Captures 4 digits (Year)
        Pattern pattern = Pattern.compile("(\\d{1,2})\\s*([a-zA-Z]+)\\s*(\\d{4})");
        Matcher matcher = pattern.matcher(inputDate.trim());

        if (!matcher.matches()) {
            throw new InvalidCommandException("Wrong date. Date format must be 'dd Month yyyy' (e.g., 2 Nov 2024)");
        }

        String day = matcher.group(1);
        String monthRaw = matcher.group(2).toLowerCase();
        String year = matcher.group(3);

        // Map short months to full names
        String fullMonth = getFullMonthName(monthRaw);
        if (fullMonth == null) {
            throw new InvalidCommandException("Invalid month name: " + monthRaw);
        }

        return day + " " + fullMonth + " " + year;
    }
    
    // "5 Nov 2024" -> "5 November 2024"
    private String getFullMonthName(String shortMonth) {
        // Check the first 3 letters
        String key = shortMonth.substring(0, Math.min(shortMonth.length(), 3));

        //using a hash map to store the values of the short versions to the full versions
        Map<String, String> months = new HashMap<>();
        
        //mapping
        months.put("jan", "January"); months.put("feb", "February");
        months.put("mar", "March");   months.put("apr", "April");
        months.put("may", "May");     months.put("jun", "June");
        months.put("jul", "July");    months.put("aug", "August");
        months.put("sep", "September"); months.put("oct", "October");
        months.put("nov", "November"); months.put("dec", "December");

        return months.get(key);
    }
    
    //Validates time 12am-12pm with minutes .00-.59
    private void validateTime(String time) throws InvalidCommandException {
        // logic Explanation:
        // ^(1[0-2]|[1-9]) -> Starts with 10-12 OR 1-9 (for the hours)
        // (\.[0-5][0-9])? -> Optional: a dot followed by 00-59 (for the minutes)
        // \s* -> Optional: whitespace
        // (am|pm)$ -> Ends with am or pm (case insensitive)
        String regex = "^(1[0-2]|[1-9])(\\.[0-5][0-9])?\\s*(am|pm)$";

        Pattern pattern = Pattern.compile(regex, Pattern.CASE_INSENSITIVE);
        if (!pattern.matcher(time.trim()).matches()) {
            throw new InvalidCommandException("Wrong time format. Time must be 12hr format (e.g., '6 pm' or '7.30 pm')");
        }
    }
}
