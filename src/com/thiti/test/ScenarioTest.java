package com.thiti.test;

import com.thiti.client.Client;
import com.thiti.server.CommunicationChannelHandler;
import com.thiti.server.Server;

import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.Observable;
import java.util.Observer;
import java.util.Scanner;

public class ScenarioTest implements Observer {

    private CommunicationChannelHandler _handler1;
    private Server _server1;
    private Client _client1;

    private Client _client2;
    private CommunicationChannelHandler _handler2;



    public ScenarioTest(){

        _handler1 = new CommunicationChannelHandler();
        _handler1.setObserver(this);
        try {
            _server1 = new Server(_handler1);
        } catch (SocketException e) {
            e.printStackTrace();
        }
        try {
            _client1 = new Client(_handler1);
        } catch (SocketException e) {
            e.printStackTrace();
        }

        new Thread(_server1).start();

        _handler2 = new CommunicationChannelHandler();
        _handler2.setObserver(this);
        try {
            _client2 = new Client(_handler2);
        } catch (SocketException e) {
            e.printStackTrace();
        }
    }

    public void lunch(){
        System.out.println("---- Lunch ----");
        try {
            _client2.createCommunication("127.0.0.1");

            Scanner in = new Scanner(System.in);
            while (true) {
                System.out.print("[CLIENT 1] SEND : ");
                _handler1.sendAll(in.nextLine());
                Thread.sleep(2000);
                System.out.print("[CLIENT 2] SEND : ");
                _handler2.sendAll(in.nextLine());
                Thread.sleep(2000);
            }

        } catch (SocketException e) {
            e.printStackTrace();
        } catch (UnknownHostException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void update(Observable o, Object arg) {
        if(o == _handler1)
            System.out.println("[ON CLIENT 1] " + (String)arg);
        if(o == _handler2)
            System.out.println("[ON CLIENT 2] " + (String)arg);
    }

    public static void main(String[] args){
        System.out.println("---- Start ----");

        ScenarioTest test = new ScenarioTest();
        test.lunch();
    }
}
