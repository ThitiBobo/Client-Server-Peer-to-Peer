package com.thiti.server;

import com.thiti.exception.IllegalOperationCodeException;
import sun.reflect.generics.reflectiveObjects.NotImplementedException;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.List;
import java.util.Observable;
import java.util.Observer;

public class CommunicationChannelHandler extends Observable implements Observer {

    private List<CommunicationChannel> communicationChannels = new ArrayList<>();

    public void setObserver(Observer observer){
        this.addObserver(observer);
        this.setChanged();
    }

    public CommunicationChannel getCommunication(int portSrc){
        for(CommunicationChannel channel : communicationChannels){
            if(channel.getPortSrc() == portSrc)
                return channel;
        }
        return null;
    }

    public CommunicationChannel createCommunication(int portSrc, String ip) throws SocketException, UnknownHostException {
        CommunicationChannel channel = new CommunicationChannel(portSrc,ip);
        channel.setObserver(this);
        communicationChannels.add(channel);
        return channel;
    }

    public void deleteCommunication(){
        throw new NotImplementedException();
    }

    public void send(){
        throw new NotImplementedException();
    }

    public void sendAll(String message){
        try {
            CommunicationPacket packet = new CommunicationPacket(CommunicationPacket.OP_CODE_DATA, 20, message);
            for (CommunicationChannel channel : communicationChannels) {
                channel.send(packet);
            }
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (IllegalOperationCodeException e) {
            e.printStackTrace();
        }

    }

    @Override
    public void update(Observable o, Object arg) {
        if(arg instanceof CommunicationPacket){
            CommunicationPacket packet = (CommunicationPacket) arg;
            if(packet.getOpCode() == CommunicationPacket.OP_CODE_DATA)
                this.notifyObservers(packet.getData());
        }
    }
}
