package com.thiti.client;

import com.thiti.exception.IllegalOperationCodeException;
import com.thiti.server.CommunicationChannel;
import com.thiti.server.CommunicationChannelHandler;
import com.thiti.server.CommunicationPacket;
import com.thiti.utilities.ServerUtility;

import java.io.IOException;
import java.net.*;
import java.util.Random;

public class Client {

    private CommunicationChannelHandler _handler;
    private DatagramSocket _socket;
    private byte[] _buffer;

    public void setCommunicationChannelHandler(CommunicationChannelHandler handler){
        if(handler == null)
            throw new IllegalArgumentException();
        _handler = handler;
    }

    public Client(CommunicationChannelHandler handler) throws SocketException {
        setCommunicationChannelHandler(handler);
        _socket = new DatagramSocket();
        _buffer = new byte[5000];
    }

    public void createCommunication(String ip) throws SocketException, UnknownHostException {

        // create communication channel
        int port = ServerUtility.scanAllPort().get(0);
        CommunicationChannel channel = _handler.createCommunication(port,ip);

        // build packet with op code = "CONNECTION" and communication channel's port
        Random rdom = new Random();
        int id = rdom.nextInt();
        CommunicationPacket packet = null;
        try {
            packet = new CommunicationPacket(CommunicationPacket.OP_CODE_CONNECTION,id,port);
        } catch (IllegalOperationCodeException e) {
            e.printStackTrace();
        }
        byte data[] = packet.getByteArray();

        // send packet
        InetAddress ia = InetAddress.getByName(ip);
        DatagramPacket datagram = new DatagramPacket(data, data.length, ia, 5000);
        try {
            _socket.send(datagram);
        } catch (IOException e) {
            e.printStackTrace();
        }

        DatagramPacket receivePacket = new DatagramPacket(_buffer,_buffer.length);
        try {
            _socket.receive(receivePacket);
            CommunicationPacket receiveData = new CommunicationPacket(receivePacket.getData());
            channel.setPortDst(receiveData.getPortCommunication());
            channel.receive();

            CommunicationPacket ack = new CommunicationPacket(CommunicationPacket.OP_CODE_ACK,receiveData.getId());
            data = ack.getByteArray();
            datagram = new DatagramPacket(data, data.length, ia, 5000);
            _socket.send(datagram);

        } catch (IOException e) {
            e.printStackTrace();
        } catch (IllegalOperationCodeException e) {
            e.printStackTrace();
        }


    }
}
