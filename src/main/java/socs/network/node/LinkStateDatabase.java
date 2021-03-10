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

        // Trim input (just to be sure)
        destinationIP = destinationIP.trim();

        // Walking history (result arrays)
        ArrayList<LSA> nodes = new ArrayList<LSA>();
        ArrayList<LinkDescription> edges = new ArrayList<LinkDescription>();
        // Just to be sure, that we can pass dead end case
        ArrayList<LinkDescription> passedEdges = new ArrayList<LinkDescription>();
        // Current node from Router
        LSA currentNode = getCurrentLSA();

        // Going to "walk" till current node is not needed (destination)
        while (!currentNode.linkStateID.equals(destinationIP)) {
            // Find lightest edge
            LinkDescription lightestLink = null;
            for (LinkDescription link : currentNode.links) {
                // Pass, if the edge already used
                if (passedEdges.indexOf(link) != -1) {
                    continue;
                }
                // Initial link
                if (lightestLink == null) {
                    lightestLink = link;
                    continue;
                }

                // Compare link weight (tos)
                if (link.tosMetrics < lightestLink.tosMetrics) {
                    lightestLink = link;
                }
            }
            // In case of dead end, go back
            if (lightestLink == null) {
                currentNode = nodes.get(nodes.size()-1);
            }
            // Add current node to the path
            nodes.add(currentNode);
            // Save edge to the history and passed edges
            edges.add(lightestLink);
            passedEdges.add(lightestLink);
            // Switch current node to the next
            currentNode = _store.get(lightestLink.linkID);
            System.out.println(currentNode);
        }

        // Add final node to the path
        nodes.add(currentNode);

        // Output
        return formatPath(nodes, edges);
    }

    private LSA getCurrentLSA() {
        // Init
        LSA lsa = new LSA();
        // Set IP
        lsa.linkStateID = Router.getInstance().getRouterDescription().getSimulatedIPAddress();
        // Set Links
        for (ClientSocketThread port: Router.getInstance().getPorts()) {
            // Map link to link description
            Link link = port.getLink();
            LinkDescription linkDescription = new LinkDescription();
            linkDescription.linkID = link.getRouter2().simulatedIPAddress;
            linkDescription.portNum = link.getSocket().getPort();
            linkDescription.tosMetrics = link.getWeight();
            // Add link to the LSA
            lsa.links.add(linkDescription);
        }
        return lsa;
    }

    private String formatPath(ArrayList<LSA> nodes, ArrayList<LinkDescription> edges) {
        // Writer
        StringBuffer bf = new StringBuffer();
        // Iterate nodes
        for (int i = 0; i < nodes.size(); i++) {
            // Write address
            bf.append(nodes.get(i).linkStateID);
            // Write edge, if not the last
            if (i != edges.size()) {
                bf.append(" ->(" + edges.get(i).tosMetrics + ") ");
            }
        }
        // Build string
        return bf.toString();
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
