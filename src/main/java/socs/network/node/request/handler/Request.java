package socs.network.node.request.handler;

import socs.network.message.SOSPFPacket;
import socs.network.node.ClientSocketThread;
import socs.network.node.Link;
import socs.network.node.SocketServerThread;

import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

public interface Request {
    void send(ClientSocketThread clientThread);

    void process(Request request, ClientSocketThread clientThread);

    SOSPFPacket getPacket();
}
