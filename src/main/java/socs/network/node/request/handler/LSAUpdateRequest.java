package socs.network.node.request.handler;

import socs.network.message.SOSPFPacket;
import socs.network.message.SOSPFType;
import socs.network.node.ClientSocketThread;
import socs.network.node.Link;
import socs.network.node.Router;
import socs.network.node.RouterStatus;
import socs.network.message.LSA;
import java.io.Serializable;
import java.util.Map;

public class LSAUpdateRequest implements Request, Serializable {
    private SOSPFPacket packet;

    public void send(ClientSocketThread clientThread) {
        Link link = clientThread.getLink();

        try {
            //initialize packet
            buildPacket(link);
            clientThread.getObjectOutputStream().writeObject(this);
            clientThread.getObjectOutputStream().flush();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    public void process(Request request, ClientSocketThread clientThread) {
        Link link = clientThread.getLink();
        if (link.getRouter1().getStatus() != RouterStatus.TWO_WAY && link.getRouter2().getStatus() != RouterStatus.TWO_WAY) {
            System.out.println("TWO_WAY connection is not established...\n");
            return;
        }

        //check if received packet is not LSAUpdate packet
        if (request.getPacket().sospfType != SOSPFType.LSAUPDATE) {
            System.out.println("Wrong packet type...\n");
            clientThread.updateLink(link);
            return;
        }

        try {
            // update router's database
            Router.getInstance().udpateLinkStateDatabase(packet.lsaArray);
                // multicasting
                //loop through the all ports, except the one from which the LSA was received
                for (ClientSocketThread port : Router.getInstance().getPorts()) {
                    if ( link.equals(port.getLink()) ||
                            request.getPacket().srcIP.equals(port.getLink().getRouter2().getSimulatedIPAddress())) {
                        continue;
                    }

                    port.getObjectOutputStream().writeObject(this);
                    port.getObjectOutputStream().flush();
                }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public SOSPFPacket getPacket() {
        return packet;
    }

    private void buildPacket(Link link) {
        packet = new SOSPFPacket();
        packet.sospfType = SOSPFType.LSAUPDATE;
        packet.srcProcessIP = Router.getInstance().getRouterDescription().getProcessIPAddress();
        packet.srcProcessPort = Router.getInstance().getRouterDescription().getProcessPortNumber();
        packet.srcIP = Router.getInstance().getRouterDescription().getSimulatedIPAddress();
        packet.dstIP = link.getRouter2().getSimulatedIPAddress();
        packet.routerID = link.getRouter2().getSimulatedIPAddress();
        packet.neighborID = Router.getInstance().getRouterDescription().getSimulatedIPAddress();

        //copy link state database to the packet
        for(Map.Entry<String, LSA> entry : Router.getInstance().getLinkStateDatabase().getStoreHashMap().entrySet()) {
            LSA lsa = entry.getValue();
            packet.lsaArray.add(lsa);
        }
    }
}