package com.thiti.server;

import com.thiti.exception.IllegalOperationCodeException;
import com.thiti.utilities.ServerUtility;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.SocketException;

public class Server implements Runnable{

    private final int _port = 5000;

    private byte[] _buffer;
    private DatagramSocket _socket;
    private CommunicationChannelHandler _handler;

    public void setCommunicationChannelHandler(CommunicationChannelHandler handler){
        if(handler == null)
            throw new IllegalArgumentException();
        _handler = handler;
    }

    public Server(CommunicationChannelHandler handler) throws SocketException {
        _buffer = new byte[5000];
        _socket = new DatagramSocket(_port);
        setCommunicationChannelHandler(handler);
    }

    @Override
    public void run() {

        DatagramPacket packet = new DatagramPacket(_buffer,_buffer.length);
        try {
            _socket.receive(packet);
            createConnection(packet);
            
        } catch (IOException e) {
            e.printStackTrace();
        } catch (IllegalOperationCodeException e) {
            e.printStackTrace();
        }
    }

    private void createConnection(DatagramPacket packet) throws IllegalOperationCodeException {
        try {
            // create communication channel
            int port = ServerUtility.scanAllPort().get(0);
            CommunicationChannel channel = _handler.createCommunication(port,packet.getAddress().getHostName());

            CommunicationPacket data = new CommunicationPacket(packet.getData());
            channel.setPortDst(data.getPortCommunication());
            channel.receive();

            // create communication packet
            CommunicationPacket packetCO = new CommunicationPacket(CommunicationPacket.OP_CODE_CONNECTION,data.getId(),port);
            byte[] byteCO = packetCO.getByteArray();

            DatagramPacket datagram = new DatagramPacket(byteCO,byteCO.length,packet.getAddress(),packet.getPort());
            _socket.send(datagram);
        } catch (IOException e) {
            e.printStackTrace();
        }


    }
}




