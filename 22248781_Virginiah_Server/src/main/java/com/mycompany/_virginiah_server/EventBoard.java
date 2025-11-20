/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany._virginiah_server;

import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author vvtat
 */
public class EventBoard {
    //Using a list to store the events
    private List<Event> eventList = new ArrayList<>();
    
    //Synchronized - to allow only one thread to access this method at a time. 
    public synchronized String addEvent(String date, String time, String description) throws InvalidCommandException{
        eventList.add(new Event(date, time, description));
        //return the updated list for that date
        return getEventsForDate(date); 
    }
    
    //Sync - removing an eventt
    public synchronized String removeEvent(String date, String time, String description) throws InvalidCommandException {
        boolean removed = eventList.removeIf(e -> 
            e.getDate().equalsIgnoreCase(date.trim()) && 
            e.getTime().equalsIgnoreCase(time.trim()) && 
            e.getDesc().equalsIgnoreCase(description.trim())
        );
        
        if (!removed) {
            throw new InvalidCommandException("Event not found, cannot remove.");
        }
        return getEventsForDate(date);
    }
    
    //Synchronized  - geting a list of all events "list" action
    public synchronized String getEventsForDate(String date){
        StringBuilder sb = new StringBuilder();
        boolean found = false;
        
        //Checking if the list is empty first
        if(eventList.isEmpty()){
            return "EventList is empty";
        } 
        else{
            //filter events by date
            for(Event e : eventList){
                if(e.getDate().equalsIgnoreCase(date.trim())){
                    if(found){
                        sb.append("; "); //adding ; as a separator for multiple events
                    } else{
                        sb.append(e.getDate() + "; "); //the header for the line
                    }
                    sb.append(e.getTime() + ", " + e.getDesc());
                    found = true;
                }
            }
            if(!found){
                return "No events found for " + date;
            }
        }
        
        return sb.toString();
    }
}
