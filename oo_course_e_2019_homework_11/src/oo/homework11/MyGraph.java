package oo.homework11;

import com.oocourse.specs3.models.Graph;
import com.oocourse.specs3.models.NodeIdNotFoundException;
import com.oocourse.specs3.models.NodeNotConnectedException;
import com.oocourse.specs3.models.Path;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Stack;

public class MyGraph extends MyPathContainer implements Graph {
    private HashMap<Path, Integer> pathList;
    private HashMap<Integer, ArrayList<Integer>> nodes;
    private HashMap<String, Integer> repNodes;
    private ArrayList<Integer> list;
    private HashMap<Integer, RestructNode> recordAdj;
    private HashMap<String, RepRestructNode> repNodeClass;
    private HashMap<Integer, HashMap<Integer, Integer>> shortestPathMap;

    public MyGraph() {
        shortestPathMap = new HashMap<>();
        pathList = getPathList();
        nodes = getNodes();
        repNodes = getRepNodes();
        list = getList();
        recordAdj = getRecordAdj();
        repNodeClass = getRepNodeClass();
    }

    public HashMap<Integer, HashMap<Integer, Integer>> getShortestPathMap() {
        return shortestPathMap;
    }

    // TODO : IMPLEMENT
    public boolean containsNode(int nodeId) {
        Iterator<Path> iterator = pathList.keySet().iterator();
        while (iterator.hasNext()) {
            Path next = iterator.next();
            if (next.containsNode(nodeId)) {
                return true;
            }
        }
        return false;
    }

    public boolean containsEdge(int fromNodeId, int toNodeId) {
        if (recordAdj.containsKey(fromNodeId)) {
            RestructNode nodefrom = recordAdj.get(fromNodeId);
            if (nodefrom.getAdjEdges().containsKey(toNodeId)) {
                if (nodefrom.getAdjEdges().get(toNodeId) >= 1) {
                    return true;
                }
            }
        }
        return false;
    }

    public boolean isConnected(int fromNodeId, int toNodeId)
            throws NodeIdNotFoundException {
        if (!nodes.containsKey(fromNodeId)) {
            throw new NodeIdNotFoundException(fromNodeId);
        } else if (!nodes.containsKey(toNodeId)) {
            throw new NodeIdNotFoundException(toNodeId);
        } else if (fromNodeId == toNodeId) {
            return true;
        } else {
            HashMap<Integer, Boolean> flag = new HashMap<>();
            Stack<Integer> stack = new Stack<>();
            flag.put(fromNodeId, true);
            stack.push(fromNodeId);

            while (!stack.isEmpty()) {
                int k = stack.pop();

                if (!recordAdj.containsKey(k)) {
                    continue;
                }
                HashMap<Integer, Integer> integerHashMap
                        = recordAdj.get(k).getAdjEdges();
                Iterator<Integer> iterator = integerHashMap.keySet().iterator();
                while (iterator.hasNext()) {
                    int j = iterator.next();
                    if (j == toNodeId) {
                        return true;
                    }
                    if (!flag.containsKey(j)) {
                        flag.put(j, true);
                        stack.push(j);
                    }
                }
            }
        }
        return false;
    }

    public int getShortestPathLength(int fromNodeId, int toNodeId)
            throws NodeIdNotFoundException, NodeNotConnectedException {
        if (!nodes.containsKey(fromNodeId)) {
            throw new NodeIdNotFoundException(fromNodeId);
        } else if (!nodes.containsKey(toNodeId)) {
            throw new NodeIdNotFoundException(toNodeId);
        } else if (!isConnected(fromNodeId, toNodeId)) {
            throw new NodeNotConnectedException(fromNodeId, toNodeId);
        } else {
            int result = -1;
            result = newShortestPath(shortestPathMap, 0,
                    fromNodeId, toNodeId);
            if (result == -1) {
                throw new NodeNotConnectedException(fromNodeId, toNodeId);
            } else {
                return result;
            }
        }
    }

    public int judgeWhetherCal(HashMap<Integer, HashMap<Integer, Integer>>
                                       shortestMap, int fromNodeId,
                               int toNodeId) {
        int result = -1;
        if (shortestMap.size() > 0) {
            if (shortestMap.containsKey(fromNodeId)) {
                HashMap<Integer, Integer> tmpHashMap = shortestMap.
                        get(fromNodeId);
                if (tmpHashMap.containsKey(toNodeId)) {
                    result = tmpHashMap.get(toNodeId);
                    return result;
                }
            } else if (shortestMap.containsKey(toNodeId)) {
                HashMap<Integer, Integer> tmpHashMap = shortestMap.
                        get(toNodeId);
                if (tmpHashMap.containsKey(fromNodeId)) {
                    result = tmpHashMap.get(fromNodeId);
                    return result;
                }
            }
        }
        return result;
    }

    public int newShortestPath(HashMap<Integer, HashMap<Integer, Integer>>
                                        shortestMap, int type, int fromNodeId,
                                int toNodeId) {
        int result = judgeWhetherCal(shortestMap, fromNodeId, toNodeId);
        if (result != -1) {
            return result;
        }
        HashSet<String> flag = new HashSet<>();
        HashMap<String, Integer> dist = new HashMap<>();
        HashMap<Integer, Integer> tmpPath = new HashMap<>();
        ArrayList<Integer> fromNodePath = nodes.get(fromNodeId);
        for (String s : repNodes.keySet()) {
            dist.put(s, Integer.MAX_VALUE);
        }
        for (Integer path : fromNodePath) {
            String tmpS = fromNodeId + "_" + path;
            RepRestructNode curFromNode = repNodeClass.get(tmpS);
            curFromNode.setDist(0, type);
            flag.add(tmpS);
            dist.put(tmpS, 0);
            List<String> tmpNeighbors = updateFromNodeAdj(tmpS);
            for (String tmp : tmpNeighbors) {
                dist.put(tmp, getCurrentEge(type, tmpS, tmp));
                RepRestructNode neighbor = repNodeClass.get(tmp);
                tmpPath.put(neighbor.getPureId(), dist.get(tmp));
            }
        }
        tmpPath.put(fromNodeId, 0);
        for (int i = 1; i < repNodes.size(); i++) {
            String index = fromNodeId + "_";
            int mincost = Integer.MAX_VALUE;
            for (String j : repNodes.keySet()) {
                if (!flag.contains(j) && dist.get(j) < mincost) {
                    mincost = dist.get(j);
                    index = j;
                }
            }
            if (index.equals(fromNodeId + "_")) {
                continue;
            }
            flag.add(index);
            updateNeighbors(index, type, dist, flag, tmpPath);
        }
        shortestMap.put(fromNodeId, tmpPath);
        if (shortestMap.get(fromNodeId).containsKey(toNodeId)) {
            result = shortestMap.get(fromNodeId).get(toNodeId);
        }
        return result;
    }

    public void updateNeighbors(String index, int type,
                                HashMap<String, Integer> dist,
                                HashSet<String> flag,
                                HashMap<Integer, Integer> tmpPath) {
        List<String> neighbors = getAdj(index);
        for (String s : neighbors) {
            int current = dist.get(index) + getCurrentEge(type, index, s);
            if (!flag.contains(s) && (dist.get(s) > current)) {
                dist.put(s, current);
            }
            RepRestructNode neighbor = repNodeClass.get(s);
            if (tmpPath.containsKey(neighbor.getPureId())) {
                int last = tmpPath.get(neighbor.getPureId());
                if (dist.get(s) < last) {
                    tmpPath.put(neighbor.getPureId(), dist.get(s));
                }
            } else {
                tmpPath.put(neighbor.getPureId(), dist.get(s));
            }
        }
    }

    public List<String> updateFromNodeAdj(String source) {
        List<String> neighbors = new ArrayList<>();
        RepRestructNode sourceNode = repNodeClass.get(source);
        Iterator<String> iterator = sourceNode.getAdjEdges().keySet()
                .iterator();
        while (iterator.hasNext()) {
            String tmpId = iterator.next();
            if (tmpId.equals(source)) {
                continue;
            }
            neighbors.add(tmpId);
        }
        return neighbors;
    }

    public List<String> getAdj(String v) {
        List<String> neighbors = new ArrayList<>();
        RepRestructNode sourceNode = repNodeClass.get(v);
        Iterator<String> iterator = sourceNode.getAdjEdges().keySet()
                .iterator();
        while (iterator.hasNext()) {
            String tmpId = iterator.next();
            if (tmpId.equals(v)) {
                continue;
            }
            neighbors.add(tmpId);
        }

        int pureId = sourceNode.getPureId();
        for (Integer i : nodes.get(pureId)) {
            String s = pureId + "_" + i;
            if (s.equals(v)) {
                continue;
            }
            neighbors.add(s);
        }
        return neighbors;
    }

    public int getCurrentEge(int type, String start, String end) {
        int result = 1;
        RepRestructNode startNode = repNodeClass.get(start);
        RepRestructNode endNode = repNodeClass.get(end);
        if (type == 0) {
            if (startNode.getPureId() == endNode.getPureId()) {
                result = 0;
            }
        } else if (type == 1) {
            if (startNode.getPureId() == endNode.getPureId()) {
                result = 2;
            } else {
                result = 1;
            }
        } else if (type == 2) {
            if (startNode.getPureId() == endNode.getPureId()) {
                result = 1;
            } else {
                result = 0;
            }
        } else if (type == 3) {
            if (startNode.getPureId() == endNode.getPureId()) {
                result = 32;
            } else {
                result = calculateUnHappy(startNode.getPureId(),
                        endNode.getPureId());
            }
        }
        return result;
    }

    public int functionBasic(int x) {
        int f = (x % 5 + 5) % 5;
        return f;
    }

    public int calculateUnHappy(int fromNodeId, int toNodeId) {
        int f1 = functionBasic(fromNodeId);
        int f2 = functionBasic(toNodeId);
        int dim = 0;
        if (f1 > f2) {
            dim = 2 * f1 - 1;
        } else {
            dim = 2 * f2 - 1;
        }
        return 2 << dim;
    }
}

