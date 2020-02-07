package socs.network.node.request.handler;

import socs.network.message.SOSPFPacket;
import socs.network.node.ClientSocketThread;


public interface Request {
    void send(ClientSocketThread clientThread);

    void process(Request request, ClientSocketThread clientThread);

    SOSPFPacket getPacket();
}
