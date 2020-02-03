package socs.network.node.request.handler;

import socs.network.message.SOSPFPacket;
import socs.network.node.ClientSocketThread;
import socs.network.node.Link;

import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

public class LSAUpdateRequest implements Request {
    public void send(ObjectOutputStream out, Link link, ClientSocketThread clientThread) {

    }
    public void process(ObjectOutputStream out, Link link, Request request, ClientSocketThread clientThread) {

    }

    public SOSPFPacket getPacket() {
        return null;
    }
}
