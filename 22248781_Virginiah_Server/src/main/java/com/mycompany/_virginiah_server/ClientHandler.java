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

/**
 *
 * @author vvtat
 */
public class ClientHandler implements Runnable{
    private Socket clientLink;
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
    
    private String processCommand(String message) throws InvalidCommandException{
        //To use to check if the command being sent by the client follows hass all the parts of the event, plus the action(should have 4 parts)
        String parts[] = message.split(";");
        
        //Validation
        if(parts.length != 4){
            throw new InvalidCommandException("Message must have 4 parts (Action; Date; Time; Desc)");
        }
        
        String action = parts[0].trim().toLowerCase();
        String date = parts[1].trim();
        String time = parts[2].trim();
        String description = parts[3].trim();
        
        //Switch for each action to perform the corresponding message
        switch(action){
            case "add":
                //Validate time format
                if(!time.contains("am") && !time.contains("pm")){
                    throw new InvalidCommandException("Time must include am or pm");
                }
                return board.addEvent(date, time, description);
                
            case "remove":
                return board.removeEvent(date, time, description);
                
            case "list":
                return board.getEventsForDate(date);
                
            default:
                throw new InvalidCommandException("Unknown action: " + action);
        }
    }
}
