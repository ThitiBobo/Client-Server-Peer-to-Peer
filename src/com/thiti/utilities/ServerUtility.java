package com.thiti.utilities;

import java.net.DatagramSocket;
import java.net.SocketException;
import java.util.ArrayList;
import java.util.List;

public class ServerUtility {

    public static List<Integer> scanAllPort(){
        List<Integer> ports = new ArrayList<>();
        for (int i = 1; i <= 65535; i++){
            if(scanUDP(i))
                ports.add(i);
        }
        return ports;
    }

    public static boolean scanUDP(int port){
        try {
            DatagramSocket socket = new DatagramSocket(port);
            socket.close();
            return true;
        } catch (SocketException e) {
            return false;

        }
    }
}
