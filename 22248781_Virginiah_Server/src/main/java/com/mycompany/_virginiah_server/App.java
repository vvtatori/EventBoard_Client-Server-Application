/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 */

package com.mycompany._virginiah_server;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

/**
 *
 * @author vvtat
 */
public class App {
    //The port to be used for the server
    private static final int PORT = 1234;
    
    //A single instance of the board shared by every thread
    private static EventBoard sharedBoard = new EventBoard();
    
    public static void main(String[] args) {
        System.out.println("Server started at port " + PORT);
        try(ServerSocket servSock = new ServerSocket(PORT)){
            while(true){
                //accept connection
                Socket link = servSock.accept();
                
                //create the handler, passing the shared board
                ClientHandler handler = new ClientHandler(link, sharedBoard);
                
                //Strat the thread
                Thread t = new Thread(handler);
                t.start();
            }
        } catch(IOException e){
            e.printStackTrace();
        }
    }
}
