package com.thiti;

import com.thiti.client.Client;
import com.thiti.server.CommunicationChannelHandler;
import com.thiti.server.Server;

import java.net.*;
import java.util.Observable;
import java.util.Observer;
import java.util.Scanner;

public class Main {

    public static void main(String[] args) throws SocketException, UnknownHostException, InterruptedException {

        CommunicationChannelHandler _handler;
        Server _server = null;
        Client _client = null;

        _handler = new CommunicationChannelHandler();
        _handler.setObserver(new Observer() {
            @Override
            public void update(Observable o, Object arg) {
                System.out.println("Receive: " + "\033[0;32m" + (String)arg + "\033[0;37m");
            }
        });

        try {
            _server = new Server(_handler);
            _client = new Client(_handler);
            new Thread(_server).start();
        } catch (SocketException e) {
            e.printStackTrace();
            return;
        }
        System.out.println("---- Start ----");

        Scanner in = new Scanner(System.in);
        System.out.print("Saisir un ip: ");
        _client.createCommunication(in.nextLine());

        while (true) {
            System.out.print("Send: ");
            _handler.sendAll(in.nextLine());
            Thread.sleep(1);
        }





    }

}
