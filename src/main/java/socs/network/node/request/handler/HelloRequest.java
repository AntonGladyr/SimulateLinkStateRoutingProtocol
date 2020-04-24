package socs.network.node.request.handler;

import socs.network.message.SOSPFPacket;
import socs.network.message.SOSPFType;
import socs.network.node.ClientSocketThread;
import socs.network.node.Link;
import socs.network.node.Router;
import socs.network.node.RouterStatus;
import socs.network.message.LSA;

import java.io.Serializable;
import java.util.Vector;

//This class handles HELLO requests
public class HelloRequest implements Request, Serializable {
    private SOSPFPacket packet;
    private double linkWeight;

    //method for sending requests
    public void send(ClientSocketThread clientSocketThread) {
        //check if port already has TWO_WAY connection
        Link link = clientSocketThread.getLink();
        if (link.getRouter1().getStatus() == RouterStatus.TWO_WAY &&
                link.getRouter2().getStatus() == RouterStatus.TWO_WAY) {
            LSAUpdateRequest lsaUpdateRequest = new LSAUpdateRequest();
            lsaUpdateRequest.send(clientSocketThread); //send LSAupdate request
            return;
        }

        try {
            //set INIT status for the sending port
            link.getRouter1().setStatus(RouterStatus.INIT);
            //initialize packet
            buildPacket(link);
            //set link weight for the receiver
            linkWeight = link.getWeight();
            //write to socket
            System.out.printf("\n\tsend HELLO from %s to %s;\n\n",
                    Router.getInstance().getRouterDescription().getSimulatedIPAddress(),
                    link.getRouter2().getSimulatedIPAddress());
            clientSocketThread.getObjectOutputStream().writeObject(this);
            clientSocketThread.getObjectOutputStream().flush();
        } catch (Exception e) {
            e.printStackTrace();
        }

        clientSocketThread.updateLink(link);
    }

    //method for handling received requests
    public void process(Request request, ClientSocketThread clientSocketThread) {
        Link link = clientSocketThread.getLink();
        try {
            //check if received packet is not HELLO packet
            if (request.getPacket().sospfType != SOSPFType.HELLO) {
                System.out.println("Wrong packet type...\n");
                clientSocketThread.updateLink(link);
            }

            //if source router and destination router do not have INIT status
            //set the INIT status for the source router (current router)
            //send HELLO back

            // the remote end (R2) receives a HELLO message, set the status in the RouterDescriptionof R1 as an INIT,
            // then send HELLO to R1
            if (link.getRouter1().getStatus() == null && link.getRouter2().getStatus() == null) {
                System.out.printf("\n\treceived HELLO from %s;\n", request.getPacket().neighborID);
                System.out.printf("\n\tset %s state to INIT;\n", request.getPacket().neighborID);
                link.getRouter1().setStatus(RouterStatus.INIT);
                link.getRouter1().setProcessIPAddress(Router.getInstance().getRouterDescription().getProcessIPAddress());
                link.getRouter1().setProcessPortNumber(Router.getInstance().getRouterDescription().getProcessPortNumber());
                link.getRouter1().setSimulatedIPAddress(Router.getInstance().getRouterDescription().getSimulatedIPAddress());
                link.getRouter2().setStatus(RouterStatus.INIT);
                link.getRouter2().setProcessIPAddress(request.getPacket().srcProcessIP);
                link.getRouter2().setProcessPortNumber(request.getPacket().srcProcessPort);
                link.getRouter2().setSimulatedIPAddress(request.getPacket().routerID);
                //set link weight
                link.setWeight(linkWeight);
                Router.getInstance().addLink(clientSocketThread);

                // send response back
                buildPacket(link);
                clientSocketThread.getObjectOutputStream().writeObject(this);
                clientSocketThread.getObjectOutputStream().flush();
            }
            // R1 receives HELLO from R2, set the status of R1 as a TWO_WAY, send HELLO to R2
            else if (link.getRouter1().getStatus() == RouterStatus.INIT && link.getRouter2().getStatus() == null) {
                System.out.printf("\n\treceived HELLO from %s;\n", request.getPacket().neighborID);
                System.out.printf("\n\tset %s state to TWO_WAY;\n", request.getPacket().neighborID);
                link.getRouter1().setStatus(RouterStatus.TWO_WAY);
                link.getRouter2().setStatus(RouterStatus.TWO_WAY);

                // send response back
                buildPacket(link);
                clientSocketThread.getObjectOutputStream().writeObject(this);
                clientSocketThread.getObjectOutputStream().flush();

                // create link state advertise (LSA) containing information about its direct neighbors
                // and save it to the database
                Router.getInstance().createLSA();
                // Router broadcasts lsa update request to the all neighbors
                Router.getInstance().lsaUpdateBroadcast();
            }
            // R2 receives HELLO from R1, set status of R1 as a TWO_WAY
            else if (link.getRouter1().getStatus() == RouterStatus.INIT &&
                    link.getRouter2().getStatus() == RouterStatus.INIT) {
                System.out.printf("\n\treceived HELLO from %s;\n", request.getPacket().neighborID);
                System.out.printf("\n\tset %s state to TWO_WAY;\n", request.getPacket().neighborID);
                link.getRouter1().setStatus(RouterStatus.TWO_WAY);
                link.getRouter2().setStatus(RouterStatus.TWO_WAY);

                // create link state advertise (LSA) containing information about its direct neighbors
                // and save it to the database
                Router.getInstance().createLSA();
                // Router broadcasts lsa update request to the all neighbors
                Router.getInstance().lsaUpdateBroadcast();
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
        clientSocketThread.updateLink(link);
    }

    public SOSPFPacket getPacket() {
        return packet;
    }

    public double getLinkWeight() { return linkWeight; }

    private void buildPacket(Link link) {
        packet = new SOSPFPacket();
        packet.sospfType = SOSPFType.HELLO;
        packet.srcProcessIP = Router.getInstance().getRouterDescription().getProcessIPAddress();
        packet.srcProcessPort = Router.getInstance().getRouterDescription().getProcessPortNumber();
        packet.srcIP = Router.getInstance().getRouterDescription().getSimulatedIPAddress();
        packet.dstIP = link.getRouter2().getSimulatedIPAddress();
        packet.routerID = link.getRouter1().getSimulatedIPAddress();
        packet.neighborID = Router.getInstance().getRouterDescription().getSimulatedIPAddress();
    }
}