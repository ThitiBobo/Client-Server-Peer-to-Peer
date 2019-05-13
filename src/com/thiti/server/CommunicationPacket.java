package com.thiti.server;

import com.thiti.exception.IllegalOperationCodeException;

import java.nio.ByteBuffer;

public class CommunicationPacket {

    public static short OP_CODE_CONNECTION = 0;
    public static short OP_CODE_ACK = 1;
    public static short OP_CODE_DATA = 2;
    public static short OP_CODE_CLOSE = 3;
    public static short OP_CODE_ERROR = 4; // not implement

    private short _opCode;
    private int _portCommunication;
    private int _id;
    private String _data;
    private int _length;

    public short getOpCode() {
        return _opCode;
    }

    public int getPortCommunication() {
        return _portCommunication;
    }

    public int getId() {
        return _id;
    }

    public String getData() {
        return _data;
    }

    public int getLength() {
        return _length;
    }

    public void setOpCode(short opCode) throws IllegalOperationCodeException {
        if ( (opCode < 0) || (opCode > 3))
            throw new IllegalOperationCodeException("Operation Code does not exist");
        _opCode = opCode;
        _length = 6;
        if (_opCode == OP_CODE_CONNECTION)
            _length += 4;
        else if(_opCode == OP_CODE_DATA)
            _length += _data.length() * 2;
    }

    public void setPort(int port){
        if ((port < 0) || (port > 65535))
              throw new IllegalArgumentException();
        _portCommunication = port;
    }

    public void setId(int id){
        _id = id;
    }

    public void setData(String data){
        _data = data;
        _length = 6 + _data.length() * 2;
    }

    public CommunicationPacket(short opCode, int id, String data) throws IllegalOperationCodeException {
        setData(data);
        setOpCode(opCode);
        setId(id);

    }

    public CommunicationPacket(short opCode, int id, int portCommunication) throws IllegalOperationCodeException{
        this(opCode,id,"");
        setPort(portCommunication);
    }

    public CommunicationPacket(short opCode, int id) throws IllegalOperationCodeException{
        this(opCode,id,"");
    }

    public CommunicationPacket(byte[] datagramme) throws IllegalOperationCodeException {
        _data = "";
        ByteBuffer byteBuffer = ByteBuffer.wrap(datagramme);
        setOpCode(byteBuffer.getShort());
        setId(byteBuffer.getInt());
        if(_opCode == OP_CODE_CONNECTION)
            setPort(byteBuffer.getInt());
        else if(_opCode == OP_CODE_DATA){
            StringBuilder data = new StringBuilder();
            byteBuffer.position(6);
            boolean flag = true;
            while(byteBuffer.position() < byteBuffer.limit() && flag){
                char c = byteBuffer.getChar();
                if(c == '\0' )
                    flag = false;
                else
                    data.append(c);

            }
            setData(data.toString());
        }

    }

    public byte[] getByteArray(){
        ByteBuffer byteBuffer = ByteBuffer.allocate(_length);
        byteBuffer.putShort(_opCode);
        byteBuffer.putInt(_id);

        if (_opCode == OP_CODE_CONNECTION) {
            byteBuffer.putInt(_portCommunication);
        }
        else if(_opCode == OP_CODE_DATA){
            for (char c : _data.toCharArray())
                byteBuffer.putChar(c);
        }
        return byteBuffer.array();
    }

    @Override
    public String toString() {
        return "CommunicationPacket{" +
                "opCode=" + _opCode +
                ", portCommunication=" + _portCommunication +
                ", id=" + _id +
                ", data='" + _data + '\'' +
                ", length=" + _length +
                '}';
    }
}
