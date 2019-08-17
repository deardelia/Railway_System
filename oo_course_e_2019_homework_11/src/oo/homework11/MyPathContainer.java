package oo.homework11;

import com.oocourse.specs3.models.Path;
import com.oocourse.specs3.models.PathContainer;
import com.oocourse.specs3.models.PathIdNotFoundException;
import com.oocourse.specs3.models.PathNotFoundException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;

public class MyPathContainer implements PathContainer {
    private HashMap<Path, Integer> pathList;
    private HashMap<Integer, Path> pidList;
    private HashMap<Integer, ArrayList<Integer>> nodes;
    private HashMap<String, Integer> repNodes;
    private ArrayList<Integer> list;
    private HashMap<Integer, RestructNode> recordAdj;
    private HashMap<String, RepRestructNode> repNodeClass;
    private int id;

    public MyPathContainer() {
        pathList = new HashMap<>();
        pidList = new HashMap<>();
        nodes = new HashMap<>();
        repNodes = new HashMap<>();
        list = new ArrayList<>();
        recordAdj = new HashMap<>();
        repNodeClass = new HashMap<>();
        id = 1;
    }

    public ArrayList<Integer> getList() {
        return list;
    }

    public HashMap<Integer, ArrayList<Integer>> getNodes() {
        return nodes;
    }


    public HashMap<Integer, RestructNode> getRecordAdj() {
        return recordAdj;
    }

    public HashMap<Path, Integer> getPathList() {
        return pathList;
    }

    public HashMap<Integer, Path> getPidList() {
        return pidList;
    }

    public HashMap<String, Integer> getRepNodes() {
        return repNodes;
    }

    public HashMap<String, RepRestructNode> getRepNodeClass() {
        return repNodeClass;
    }

    public int getId() {
        return id;
    }

    public void addNodeEdge(Path path, int pathId) {
        Iterator<Integer> it = path.iterator();
        while (it.hasNext()) {
            int node = it.next();
            if (!nodes.containsKey(node)) {
                ArrayList<Integer> tmpArray = new ArrayList<>();
                tmpArray.add(pathId);
                nodes.put(node, tmpArray);
                RestructNode newnode = new RestructNode(node);
                recordAdj.put(node, newnode);
            } else {
                ArrayList<Integer> tmpArray = nodes.get(node);
                tmpArray.add(pathId);
                nodes.put(node, tmpArray);
            }
            String tmpS = node + "_" + pathId;
            repNodes.put(tmpS, pathId);
            RepRestructNode newRepNode = new RepRestructNode(tmpS);
            repNodeClass.put(tmpS, newRepNode);
        }
        addEdges(path, pathId);
    }

    public void addEdges(Path path, int pathId) {
        for (int i = 0; i < path.size(); i++) {
            int start = path.getNode(i);
            if (i + 1 < path.size()) {
                int end = path.getNode(i + 1);
                RestructNode nodefrom = recordAdj.get(start);
                RestructNode nodeto = recordAdj.get(end);
                if (nodefrom.getAdjEdges() != null
                        && nodefrom.getAdjEdges().containsKey(end)) {
                    int count = nodefrom.getAdjEdges().get(end) + 1;
                    nodefrom.getAdjEdges().put(end, count);
                } else {
                    nodefrom.getAdjEdges().put(end, 1);
                }
                if (nodeto.getAdjEdges() != null
                        && nodeto.getAdjEdges().containsKey(start)) {
                    int count = nodeto.getAdjEdges().get(start) + 1;
                    nodeto.getAdjEdges().put(start, count);
                } else {
                    nodeto.getAdjEdges().put(start, 1);
                }
                recordAdj.put(start, nodefrom);
                recordAdj.put(end, nodeto);
                RepRestructNode nodeRepfrom = repNodeClass.
                        get(start + "_" + pathId);
                RepRestructNode nodeRepto = repNodeClass.
                        get(end + "_" + pathId);
                if (nodeRepfrom.getAdjEdges() != null
                        && nodeRepfrom.getAdjEdges()
                        .containsKey(end + "_" + pathId)) {
                    int count = nodeRepfrom.getAdjEdges()
                            .get(end + "_" + pathId) + 1;
                    nodeRepfrom.getAdjEdges().put(end + "_" + pathId, count);
                } else {
                    nodeRepfrom.getAdjEdges().put(end + "_" + pathId, 1);
                }
                if (nodeRepto.getAdjEdges() != null
                        && nodeRepto.getAdjEdges()
                        .containsKey(start + "_" + pathId)) {
                    int count = nodeRepto.getAdjEdges()
                            .get(start + "_" + pathId) + 1;
                    nodeRepto.getAdjEdges().put(start + "_" + pathId, count);
                } else {
                    nodeRepto.getAdjEdges().put(start + "_" + pathId, 1);
                }
                repNodeClass.put(start + "_" + pathId, nodeRepfrom);
                repNodeClass.put(end + "_" + pathId, nodeRepto);
            }
        }
    }

    public void deleteNodeEdge(Path path, int pathId) {
        HashMap<Integer, RestructNode> tmpRecordAdj = new HashMap<>();
        HashMap<String, RepRestructNode> tmpRepRecord = new HashMap<>();
        Iterator<Integer> it = path.iterator();
        while (it.hasNext()) {
            int node = it.next();
            if (nodes.containsKey(node)) {
                ArrayList<Integer> tmpArray = nodes.get(node);
                if (tmpArray.size() <= 1) {
                    nodes.remove(node, tmpArray);
                    tmpRecordAdj.put(node, recordAdj.get(node));
                } else {
                    tmpArray.remove((Integer) pathId);
                    nodes.put(node, tmpArray);
                }
                String tmpS = node + "_" + pathId;
                RepRestructNode tmpClass = repNodeClass.get(tmpS);
                tmpRepRecord.put(tmpS, tmpClass);
            }
        }
        deteleEdges(path, pathId);
        for (Integer i : tmpRecordAdj.keySet()) {
            recordAdj.remove(i, tmpRecordAdj.get(i));
        }
        for (String s : tmpRepRecord.keySet()) {
            repNodeClass.remove(s, tmpRepRecord.get(s));
        }
    }

    public void deteleEdges(Path path, int pathId) {
        for (int i = 0; i < path.size(); i++) {
            int start = path.getNode(i);
            if (i + 1 < path.size()) {
                int end = path.getNode(i + 1);
                RestructNode nodefrom = recordAdj.get(start);
                RestructNode nodeto = recordAdj.get(end);
                if (nodefrom.getAdjEdges().containsKey(end)) {
                    int count = nodefrom.getAdjEdges().get(end);
                    if (count <= 1) {
                        nodefrom.getAdjEdges().remove(end, count);
                    } else {
                        nodefrom.getAdjEdges().put(end, count - 1);
                    }
                }
                if (nodeto.getAdjEdges().containsKey(start)) {
                    int count = nodeto.getAdjEdges().get(start);
                    if (count <= 1) {
                        nodeto.getAdjEdges().remove(start, count);
                    } else {
                        nodeto.getAdjEdges().put(start, count - 1);
                    }
                }
                RepRestructNode nodeRepfrom = repNodeClass
                        .get(start + "_" + pathId);
                RepRestructNode nodeRepto = repNodeClass
                        .get(end + "_" + pathId);
                if (nodeRepfrom.getAdjEdges().containsKey(end + "_" + pathId)) {
                    int count = nodeRepfrom.getAdjEdges()
                            .get(end + "_" + pathId);
                    if (count <= 1) {
                        nodeRepfrom.getAdjEdges()
                                .remove(end + "_" + pathId, count);
                    } else {
                        nodeRepfrom.getAdjEdges()
                                .put(end + "_" + pathId, count - 1);
                    }
                }
                if (nodeRepto.getAdjEdges().containsKey(start + "_" + pathId)) {
                    int count = nodeRepto.getAdjEdges()
                            .get(start + "_" + pathId);
                    if (count <= 1) {
                        nodeRepto.getAdjEdges()
                                .remove(start + "_" + pathId, count);
                    } else {
                        nodeRepto.getAdjEdges()
                                .put(start + "_" + pathId, count - 1);
                    }
                }
                recordAdj.put(start, nodefrom);
                recordAdj.put(end, nodeto);
                repNodeClass.put(start + "_" + pathId, nodeRepfrom);
                repNodeClass.put(end + "_" + pathId, nodeRepto);
            }
        }
    }

    // TODO : IMPLEMENT
    @Override
    public int size() {
        return new Integer(list.size());
    }

    @Override
    public boolean containsPath(Path path) {
        if (path != null) {
            if (pathList.containsKey(path)) {
                return true;
            } else {
                return false;
            }
        } else {
            return false;
        }
    }

    public boolean containsPathId(int pathId) {
        if (pidList.containsKey(pathId)) {
            return true;
        } else {
            return false;
        }
    }


    @Override
    public Path getPathById(int pathId) throws PathIdNotFoundException {
        if (!containsPathId(pathId)) {
            throw new PathIdNotFoundException(pathId);
        } else {
            return pidList.get(pathId);
        }
    }

    @Override
    public int getPathId(Path path) throws PathNotFoundException {
        if (containsPath(path) && path != null && path.isValid()) {
            int tmp = 0;
            tmp = pathList.get(path);
            return new Integer(tmp);
        } else {
            throw new PathNotFoundException(path);
        }
    }

    @Override
    public int addPath(Path path) {
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

    @Override
    public int getDistinctNodeCount() {
        return nodes.size();
    }
}



