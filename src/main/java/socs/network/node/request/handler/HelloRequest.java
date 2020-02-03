package socs.network.node.request.handler;

import socs.network.message.SOSPFPacket;
import socs.network.message.SOSPFType;
import socs.network.node.ClientSocketThread;
import socs.network.node.Link;
import socs.network.node.Router;
import socs.network.node.RouterStatus;
import java.io.ObjectOutputStream;
import java.io.Serializable;

//This class handles HELLO requests
public class HelloRequest implements Request, Serializable {
    private SOSPFPacket packet;

    //method for sending requests
    public void send(ObjectOutputStream out, Link link, ClientSocketThread clienThread) {
        //check if port already has TWO_WAY connection
        if (link.getRouter1().getStatus() == RouterStatus.TWO_WAY &&
                link.getRouter2().getStatus() == RouterStatus.TWO_WAY) {
//            System.out.printf("%s => %s already has TWO_WAY connection...\n>> ",
//                    link.getRouter1().getSimulatedIPAddress(),
//                    link.getRouter2().getSimulatedIPAddress());
            System.out.println("LSAUpdate");
        } else {
            try {
                //set INIT status for the sending port
                link.getRouter1().setStatus(RouterStatus.INIT);
                //initialize packet
                buildPacket(link);

                //write to socket
                System.out.printf("\n\tsend HELLO from %s to %s;\n\n",
                        Router.getInstance().getRouterDescription().getSimulatedIPAddress(),
                        link.getRouter2().getSimulatedIPAddress());
                out.writeObject(this);
                out.flush();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        clienThread.updateLink(link);
    }

    //method for handling received requests
    public void process(ObjectOutputStream out, Link link, Request request, ClientSocketThread clientThread) {
        try {
            //check if received packet is not HELLO packet
            if (request.getPacket().sospfType != SOSPFType.HELLO) {
                System.out.println("Wrong packet type...\n");
                clientThread.updateLink(link);
            }

            //if source router and destination router do not have INIT status
            //set the INIT status for the source router (current router)
            //send HELLO back
            if (link.getRouter1().getStatus() == null && link.getRouter2().getStatus() == null) {
                System.out.printf("\n\treceived HELLO from %s;\n", request.getPacket().neighborID);
                System.out.printf("\n\tset %s state to INIT;\n", request.getPacket().neighborID);
                link.getRouter1().setStatus(RouterStatus.INIT);
                link.getRouter1().setProcessIPAddress(Router.getInstance().getRouterDescription().getProcessIPAddress());
                link.getRouter1().setProcessPortNumber(Router.getInstance().getRouterDescription().getProcessPortNumber());
                link.getRouter1().setSimulatedIPAddress(Router.getInstance().getRouterDescription().getSimulatedIPAddress());
                link.getRouter2().setProcessIPAddress(request.getPacket().srcProcessIP);
                link.getRouter2().setProcessPortNumber(request.getPacket().srcProcessPort);
                link.getRouter2().setSimulatedIPAddress(request.getPacket().routerID);
                Router.getInstance().addLink(link);
                buildPacket(link);
                out.writeObject(this);
                out.flush();
            }
            else if (link.getRouter1().getStatus() == RouterStatus.INIT && link.getRouter2().getStatus() == null) {
                System.out.printf("\n\treceived HELLO from %s;\n", request.getPacket().neighborID);
                System.out.printf("\n\tset %s state to TWO_WAY;\n", request.getPacket().neighborID);
                link.getRouter1().setStatus(RouterStatus.TWO_WAY);
                link.getRouter2().setStatus(RouterStatus.TWO_WAY);
//                Router.getInstance().updateLink(link);
                buildPacket(link);
                out.writeObject(this);
                out.flush();
                //lsa update request
            }
            else if (link.getRouter1().getStatus() == RouterStatus.INIT &&
                    link.getRouter2().getStatus() == RouterStatus.INIT) {
                System.out.printf("\n\treceived HELLO from %s;\n", request.getPacket().neighborID);
                System.out.printf("\n\tset %s state to TWO_WAY;\n>>", request.getPacket().neighborID);
                link.getRouter1().setStatus(RouterStatus.TWO_WAY);
                link.getRouter2().setStatus(RouterStatus.TWO_WAY);
//                Router.getInstance().updateLink(link);
                //lsa update request
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
        clientThread.updateLink(link);
    }

    public SOSPFPacket getPacket() {
        return packet;
    }

    private void buildPacket(Link link) {
        packet = new SOSPFPacket();
        packet.sospfType = SOSPFType.HELLO;
        packet.srcProcessIP = Router.getInstance().getRouterDescription().getProcessIPAddress();
        packet.srcProcessPort = Router.getInstance().getRouterDescription().getProcessPortNumber();
        packet.srcIP = Router.getInstance().getRouterDescription().getSimulatedIPAddress();
        packet.dstIP = link.getRouter2().getSimulatedIPAddress();
        packet.routerID = link.getRouter2().getSimulatedIPAddress();
        packet.neighborID = Router.getInstance().getRouterDescription().getSimulatedIPAddress();
    }
}