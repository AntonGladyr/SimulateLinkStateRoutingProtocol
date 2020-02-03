package socs.network.node.request.handler;

import socs.network.message.SOSPFPacket;
import socs.network.node.ClientSocketThread;
import socs.network.node.Link;
import socs.network.node.Router;

import java.io.IOException;
import java.io.ObjectOutputStream;
import java.io.Serializable;

public class DisconnectRequest implements Request, Serializable {
    public void send(ObjectOutputStream out, Link link, ClientSocketThread clientThread) {
        try {
            out.writeObject(this);
            out.flush();
            Router.getInstance().deleteLink(link);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void process(ObjectOutputStream out, Link link, Request request, ClientSocketThread clientThread) {
        try {
            Router.getInstance().deleteLink(link);
            clientThread.disconnect();
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("public void process() exception");
        }
    }

    public SOSPFPacket getPacket() {
        return null;
    }
}