/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany._virginiah_server;

/**
 *
 * @author vvtat
 */
public class InvalidCommandException extends Exception{
    public InvalidCommandException(String message){
        super("InvalidCommandException: " + message);
    }
}
