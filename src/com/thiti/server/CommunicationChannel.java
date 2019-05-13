package com.thiti.server;

import com.thiti.exception.IllegalOperationCodeException;

import java.io.IOException;
import java.net.*;
import java.util.Observable;
import java.util.Observer;

public class CommunicationChannel extends Observable {

    private int _portSrc;
    private int _portDst;
    private String _ip;
    private CommunicationPacket _receipt;
    private DatagramSocket _socket;
    private InetAddress _adresse;
    private byte[] _buffer;
    private boolean _listening;

    public int getPortSrc() {
        return _portSrc;
    }

    public int getPortDst() { return _portDst; }

    public String getIp() {
        return _ip;
    }

    public CommunicationPacket getReceipt() { return _receipt; }

    public void setPortDst(int portDst) { _portDst = portDst; }

    public void setObserver(Observer observer){
        this.addObserver(observer);
        this.setChanged();
    }

    public CommunicationChannel(int portSrc, String ip) throws SocketException, UnknownHostException {
        _portSrc = portSrc;
        _ip = ip;
        _buffer = new byte[5000];
        init();
    }

    private void init() throws SocketException, UnknownHostException {
        _socket = new DatagramSocket(_portSrc);
        _adresse = InetAddress.getByName(_ip);
    }

    public void receive(){
        // save instance of the thread in field
        new Thread(() -> {
            _listening = true;
            DatagramPacket packet = new DatagramPacket(_buffer, _buffer.length);
            while(_listening) {
                try {
                    _socket.receive(packet);
                    _receipt = new CommunicationPacket(packet.getData());
                    System.out.println("[COMM] RECEIVE : " + _receipt.getData() + " : " + _portSrc + "<-" + _portDst);
                    this.notifyObservers(_receipt);
                } catch (IOException e) {
                    this.notifyObservers(e);
                } catch (IllegalOperationCodeException e) {
                    this.notifyObservers(e);
                }
            }
        }).start();
    }

    public void send(CommunicationPacket data) throws IOException {
        byte[] byteData = data.getByteArray();
        System.out.println("[COMM] SEND    : " + data.getData() + " : " + _portSrc + "->" + _portDst);
        DatagramPacket packet = new DatagramPacket(byteData,byteData.length,_adresse,_portDst);
        _socket.send(packet);
    }

    public void close(){
        _listening = false;
        // send a message to close the other channel
        if (_socket != null)
            _socket.close();

    }
}
