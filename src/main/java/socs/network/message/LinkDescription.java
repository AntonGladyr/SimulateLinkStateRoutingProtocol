package socs.network.message;

import java.io.Serializable;

public class LinkDescription implements Serializable {
    public String linkID;
    public int portNum;
    public double tosMetrics;

    public String toString() {
        return linkID + "," + portNum + "," + tosMetrics;
    }

    @Override
    public boolean equals(Object obj) {
        LinkDescription link = (LinkDescription)obj;
        return linkID.equals(link.linkID);
    }
}
