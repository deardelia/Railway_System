package oo.homework11;

import java.util.HashMap;

public class RepRestructNode {
    private int[] dist = {Integer.MAX_VALUE, Integer.MAX_VALUE,
            Integer.MAX_VALUE, Integer.MAX_VALUE};
    private RepRestructNode[] preNodes = {null, null, null, null};
    private HashMap<String, Integer> adjEdges;
    private String nodeId;

    public RepRestructNode(String id) {
        adjEdges = new HashMap<>();
        nodeId = id;
    }

    public String getNodeId() {
        return this.nodeId;
    }

    public int getPureId() {
        String[]  strs = nodeId.split("_");
        return Integer.parseInt(strs[0]);
    }

    public void setPreNode(RepRestructNode x, int type) {
        preNodes[type] = x;
    }

    public int getDist(int type) {
        return dist[type];
    }

    public void setDist(int dist, int type) {
        this.dist[type] = dist;
    }

    public HashMap<String, Integer> getAdjEdges() {
        return adjEdges;
    }
}


