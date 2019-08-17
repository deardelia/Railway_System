package oo.homework11;

import com.oocourse.specs3.models.NodeIdNotFoundException;
import com.oocourse.specs3.models.NodeNotConnectedException;
import com.oocourse.specs3.models.Path;
import com.oocourse.specs3.models.PathIdNotFoundException;
import com.oocourse.specs3.models.PathNotFoundException;
import com.oocourse.specs3.models.RailwaySystem;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;

public class MyRailwaySystem extends MyGraph implements RailwaySystem {
    private HashMap<Integer, HashMap<Integer, Integer>> ticktMap;
    private HashMap<Integer, HashMap<Integer, Integer>> transferMap;
    private HashMap<Integer, HashMap<Integer, Integer>> unpleasantMap;
    private HashMap<Integer, HashMap<Integer, Integer>> shortestPathMap;
    private HashMap<Integer, ArrayList<Integer>> nodes;
    private HashMap<Integer, RestructNode> recordAdj;
    private HashMap<Path, Integer> pathList;
    private HashMap<Integer, Path> pidList;
    private ArrayList<Integer> list;
    private int id;

    public MyRailwaySystem() {
        ticktMap = new HashMap<>();
        transferMap = new HashMap<>();
        unpleasantMap = new HashMap<>();
        shortestPathMap = getShortestPathMap();
        nodes = getNodes();
        recordAdj = getRecordAdj();
        pathList = getPathList();
        list = getList();
        id = getId();
        pidList = getPidList();
    }

    public int addPath(Path path) {
        updatePathMatrix();
        if (path != null && path.isValid()) {
            int tmp = 0;
            if (!containsPath(path)) {
                list.add(id);
                pathList.put(path, id);
                pidList.put(id, path);
                addNodeEdge(path, id);
                id++;
            }
            tmp = pathList.get(path);
            return tmp;
        } else {
            return 0;
        }
    }

    @Override
    public int removePath(Path path) throws PathNotFoundException {
        updatePathMatrix();
        if (containsPath(path) && path != null && path.isValid()) {
            int result = -1;
            result = pathList.get(path);
            pathList.remove(path, result);
            pidList.remove(result, path);
            list.remove(new Integer(result));
            deleteNodeEdge(path, result);
            return result;
        } else {
            throw new PathNotFoundException(path);
        }
    }

    @Override
    public void removePathById(int pathId) throws PathIdNotFoundException {
        updatePathMatrix();
        if (containsPathId(pathId)) {
            Path certification = pidList.get(pathId);
            pidList.remove(pathId, certification);
            pathList.remove(certification, pathId);
            list.remove(new Integer(pathId));
            deleteNodeEdge(certification, pathId);
        } else {
            throw new PathIdNotFoundException(pathId);
        }
    }

    public void updatePathMatrix() {
        shortestPathMap.clear();
        ticktMap.clear();
        transferMap.clear();
        unpleasantMap.clear();
    }

    public int getLeastTicketPrice(int fromNodeId, int toNodeId)
            throws NodeIdNotFoundException, NodeNotConnectedException {
        if (!nodes.containsKey(fromNodeId)) {
            throw new NodeIdNotFoundException(fromNodeId);
        } else if (!nodes.containsKey(toNodeId)) {
            throw new NodeIdNotFoundException(toNodeId);
        } else if (!isConnected(fromNodeId, toNodeId)) {
            throw new NodeNotConnectedException(fromNodeId, toNodeId);
        } else {
            int result = -1;
            result = newShortestPath(ticktMap, 1, fromNodeId, toNodeId);
            if (result == -1) {
                throw new NodeNotConnectedException(fromNodeId, toNodeId);
            } else {
                return result;
            }
        }
    }

    public int getLeastTransferCount(int fromNodeId, int toNodeId)
            throws NodeIdNotFoundException, NodeNotConnectedException {
        if (!nodes.containsKey(fromNodeId)) {
            throw new NodeIdNotFoundException(fromNodeId);
        } else if (!nodes.containsKey(toNodeId)) {
            throw new NodeIdNotFoundException(toNodeId);
        } else if (!isConnected(fromNodeId, toNodeId)) {
            throw new NodeNotConnectedException(fromNodeId, toNodeId);
        } else {
            if (fromNodeId == toNodeId) {
                return 0;
            } else {
                int result = -1;
                result = newShortestPath(transferMap, 2, fromNodeId,
                        toNodeId);
                if (result == -1) {
                    throw new NodeNotConnectedException(fromNodeId, toNodeId);
                } else {
                    return result;
                }
            }
        }
    }

    public int getLeastUnpleasantValue(int fromNodeId, int toNodeId)
            throws NodeIdNotFoundException, NodeNotConnectedException {
        if (!nodes.containsKey(fromNodeId)) {
            throw new NodeIdNotFoundException(fromNodeId);
        } else if (!nodes.containsKey(toNodeId)) {
            throw new NodeIdNotFoundException(toNodeId);
        } else if (!isConnected(fromNodeId, toNodeId)) {
            throw new NodeNotConnectedException(fromNodeId, toNodeId);
        }
        if (fromNodeId == toNodeId) {
            return 0;
        } else {
            int result = -1;
            result = newShortestPath(unpleasantMap, 3, fromNodeId,
                    toNodeId);
            if (result == -1) {
                throw new NodeNotConnectedException(fromNodeId, toNodeId);
            } else {
                return result;
            }
        }
    }

    public int getConnectedBlockCount() {
        int block = 0;
        HashMap<Integer, Boolean> flag = new HashMap<>();
        Iterator<Integer> iterator = nodes.keySet().iterator();
        while (iterator.hasNext()) {
            int j = iterator.next();
            if (!flag.containsKey(j)) {
                dfs(j, flag);
                block++;
            }
        }
        return block;
    }

    public boolean containsPathSequence(Path[] pseq) {
        if (pseq != null) {
            for (int i = 0; i < pseq.length; i++) {
                Path tmpPath = pseq[i];
                if (!containsPath(tmpPath)) {
                    return false;
                }
            }
            return true;
        } else {
            return false;
        }
    }

    public  boolean isConnectedInPathSequence(Path[] pseq, int fromNodeId,
                                              int toNodeId)
            throws NodeIdNotFoundException, PathNotFoundException {
        if (!containsNode(fromNodeId)) {
            throw new NodeIdNotFoundException(fromNodeId);
        } else if (!containsNode(toNodeId)) {
            throw new NodeIdNotFoundException(toNodeId);
        } else if (!containsPathSequence(pseq)) {
            if (pseq == null) {
                throw new PathNotFoundException(null);
            } else {
                for (int i = 0; i < pseq.length; i++) {
                    Path tmpPath = pseq[i];
                    if (!containsPath(tmpPath)) {
                        throw new PathNotFoundException(tmpPath);
                    }
                }
            }
            return false;
        } else {
            if (pseq.length > 0) {
                ArrayList<Integer> arrayList = returnCurtickPath(pseq,
                        fromNodeId, toNodeId);
                if (arrayList.size() == pseq.length * 2) {
                    return true;
                } else {
                    return false;
                }
            } else {
                return false;
            }
        }
    }

    public ArrayList<Integer> returnCurtickPath(Path[] pseq, int fromNodeId,
                                                 int toNodeId) {
        ArrayList<Integer> arrayList = new ArrayList<>();
        if (pseq.length > 0) {
            Path firstPath = pseq[0];
            int j = 0;
            for (j = 0; j < firstPath.size(); j++) {
                if (firstPath.getNode(j) == fromNodeId) {
                    break;
                }
            }
            if (j >= firstPath.size()) {
                return arrayList;
            } else {
                arrayList.add(j);
            }
            for (int i = 0; i < pseq.length - 1; i++) {
                Path fatherPath = pseq[i];
                Path childPath = pseq[i + 1];
                int m = 0;
                int n = 0;
                for (m = 0; m < fatherPath.size(); m++) {
                    for (n = 0; n < childPath.size(); n++) {
                        if (fatherPath.getNode(m) == childPath.getNode(n)) {
                            arrayList.add(m);
                            arrayList.add(n);
                        }
                    }
                }
                if (m >= fatherPath.size() || n >= childPath.size()) {
                    return arrayList;
                }
            }

            Path lastPath = pseq[0];
            for (j = 0; j < lastPath.size(); j++) {
                if (lastPath.getNode(j) == toNodeId) {
                    break;
                }
            }
            if (j >= lastPath.size()) {
                return arrayList;
            } else {
                arrayList.add(j);
                return arrayList;
            }
        } else {
            return arrayList;
        }
    }

    public int getTicketPrice(Path[] pseq, int fromNodeId, int toNodeId) {
        int result = -1;
        if (containsNode(fromNodeId) && containsNode(toNodeId)
                && containsPathSequence(pseq)) {
            if (pseq.length > 0) {
                ArrayList<Integer> arrayList = returnCurtickPath(pseq,
                        fromNodeId, toNodeId);
                int sum = 0;
                if (arrayList.size() == pseq.length * 2) {
                    for (int j = 0; j < pseq.length; j++) {
                        sum += (arrayList.get(2 * j + 1) -
                                arrayList.get(2 * j));
                    }
                    sum += (pseq.length - 1) * 2;
                    result = sum;
                }
            }
        }
        return result;
    }

    public int getUnpleasantValue(Path path, int fromIndex, int toIndex) {
        int result = -1;
        return result;
    }

    public int getUnpleasantValue(Path[] pseq, int fromNodeId, int toNodeId) {
        int result = -1;
        return result;
    }

    private void dfs(int id, HashMap<Integer, Boolean> flag) {
        flag.put(id, true);
        RestructNode v = recordAdj.get(id);
        Iterator<Integer> iterator = v.getAdjEdges().keySet()
                .iterator();
        while (iterator.hasNext()) {
            int j = iterator.next();
            if (!flag.containsKey(j)) {
                dfs(j, flag);
            }
        }
    }

}
