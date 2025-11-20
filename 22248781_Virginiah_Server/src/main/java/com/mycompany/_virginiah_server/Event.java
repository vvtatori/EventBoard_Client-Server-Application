/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany._virginiah_server;

/**
 *
 * @author vvtat
 */
public class Event {
    private String date;
    private String time;
    private String desc;

    public Event(String date, String time, String desc) {
        this.date = date.trim();
        this.time = time.trim();
        this.desc = desc.trim();
    }


    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }
    
    @Override
    public String toString() {
        return date + "; " + time + ", " + desc;
    }
}
