package oo.homework11;

import java.util.HashMap;

public class RestructNode {
    private int[] dist = {Integer.MAX_VALUE, Integer.MAX_VALUE,
            Integer.MAX_VALUE, Integer.MAX_VALUE};
    private RestructNode[] preNodes = {null, null, null, null};
    private HashMap<Integer, Integer> adjEdges;
    private int nodeId;

    public RestructNode(int id) {
        adjEdges = new HashMap<>();
        nodeId = id;
    }

    public int getNodeId() {
        return this.nodeId;
    }

    public void setPreNode(RestructNode x, int type) {
        preNodes[type] = x;
    }

    public int getDist(int type, Integer...sourceNode) {
        return dist[type];
    }

    public void setDist(int dist, int type) {
        this.dist[type] = dist;
    }

    public HashMap<Integer, Integer> getAdjEdges() {
        return adjEdges;
    }

    public int getAdjSize() {
        return adjEdges.size();
    }
}
