package socs.network.node.request.handler;

import socs.network.message.SOSPFPacket;
import socs.network.node.ClientSocketThread;
import socs.network.node.Router;

import java.io.IOException;
import java.io.Serializable;

public class DisconnectRequest implements Request, Serializable {
    public void send(ClientSocketThread clientSocketThread) {
        try {
            clientSocketThread.getObjectOutputStream().writeObject(this);
            clientSocketThread.getObjectOutputStream().flush();
            clientSocketThread.disconnect();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void process(Request request, ClientSocketThread clientThread) {
        try {
            Router.getInstance().deleteLink(clientThread);
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
