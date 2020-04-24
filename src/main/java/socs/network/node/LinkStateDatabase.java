package socs.network.node;

import socs.network.message.LSA;
import socs.network.message.LinkDescription;

import java.util.*;

public class LinkStateDatabase {

    //linkID => LSAInstance
    HashMap<String, LSA> _store = new HashMap<String, LSA>();

    private RouterDescription rd = null;

    //private List<LSA> shortestPath = new ArrayList<>(); //TODO: fix

    public LinkStateDatabase(RouterDescription routerDescription) {
        rd = routerDescription;
        LSA l = initLinkStateDatabase();
            _store.put(l.linkStateID, l);
    }

    /**
     * output the shortest path from this router to the destination with the given IP address
     */
    public String getShortestPath(String destinationIP) {
//        LSA lsa = new LSA();
//        lsa.linkStateID = Router.getInstance().getRouterDescription().getSimulatedIPAddress();
//
//        LinkedList<LinkDescription> links = new LinkedList<LinkDescription>();
//        for (ClientSocketThread port: Router.getInstance().getPorts()) {
//            links.add(port.getLink());
//        }
//
//        lsa.links = links;
//        lsd._store.replace(
//                Router.getInstance().getRouterDescription().getSimulatedIPAddress(),
//                lsa
//        );
//
//        System.out.println(_store.toString());
//
//        Set<LSA> settledNodes = new HashSet<>();
//        Set<LSA> unsettledNodes = new HashSet<>();
//
//        unsettledNodes.add(lsa);
//
//        while (unsettledNodes.size() != 0) {
//            LSA currentNode = getLowestDistanceNode(unsettledNodes);
//            unsettledNodes.remove(currentNode);
//            for (LinkDescription link : currentNode.links) {
//                LSA adjacentNode = _store.get(link.linkID);
//                double edgeWeight = link.tosMetrics;
//                if (!settledNodes.contains(adjacentNode)) {
//                    calculateMinimumDistance(adjacentNode, edgeWeight, currentNode);
//                    unsettledNodes.add(adjacentNode);
//                }
//            }
//            settledNodes.add(currentNode);
//        }
//
//        StringBuilder sb = new StringBuilder();
//        return sb.toString();
        return "";
    }

    private LSA getLowestDistanceNode(Set<LSA> unsettledNodes) {
        LSA lowestDistanceNode = null;
        double lowestDistance = Double.MAX_VALUE;
        for (LSA node: unsettledNodes) {
            double nodeDistance = node.distance;
            if (nodeDistance < lowestDistance) {
                lowestDistance = nodeDistance;
                lowestDistanceNode = node;
            }
        }
        return lowestDistanceNode;
    }

    private static void CalculateMinimumDistance(LSA evaluationNode,
                                                 double edgeWeigh, LSA sourceNode) {
//        double sourceDistance = sourceNode.distance;
//        if (sourceDistance + edgeWeigh < evaluationNode.distance) {
//            evaluationNode.setDistance(sourceDistance + edgeWeigh);
//            LinkedList<LSA>shortestPath = new LinkedList<>(shortestPath);
//            shortestPath.add(sourceNode);
//            evaluationNode.setShortestPath(shortestPath);
//        }
    }

    //initialize the linkstate database by adding an entry about the router itself
    private LSA initLinkStateDatabase() {
        LSA lsa = new LSA();
        lsa.linkStateID = rd.simulatedIPAddress;
        lsa.lsaSeqNumber = Integer.MIN_VALUE;
        LinkDescription ld = new LinkDescription();
        ld.linkID = rd.simulatedIPAddress;
        ld.portNum = -1;
        ld.tosMetrics = 0;
        return lsa;
    }

    public HashMap<String, LSA> getStoreHashMap() {
        return _store;
    }

    public String toString() {
        StringBuilder sb = new StringBuilder();
        for (LSA lsa : _store.values()) {
            sb.append(lsa.linkStateID).append("(" + lsa.lsaSeqNumber + ")").append(":\t");
            for (LinkDescription ld : lsa.links) {
                sb.append(ld.linkID).append(",").append(ld.portNum).append(",").
                        append(ld.tosMetrics).append("\t");
            }
            sb.append("\n");
        }
        return sb.toString();
    }

}
