package oo.homework11;

import com.oocourse.specs3.models.Path;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;

public class MyPath implements Path {
    private final int[] nodeList;
    private final ArrayList<Integer> tmpNodeList = new ArrayList<>();

    public MyPath(int... nodeList) {
        this.nodeList = nodeList;
        if (nodeList != null) {
            for (int i = 0; i < this.nodeList.length; i++) {
                if (i >= 0) {
                    tmpNodeList.add(nodeList[i]);
                }
            }
        }
    }

    // TODO : IMPLEMENT
    @Override
    public int compareTo(Path o) {
        int length = this.size();
        if (o != null && o instanceof MyPath) {
            if (o.size() < this.size()) {
                length = o.size();
            }
            for (int i = 0; i < length; i++) {
                if (o.getNode(i) < this.getNode(i)) {
                    return 1;
                } else if (o.getNode(i) > this.getNode(i)) {
                    return -1;
                }
            }
            if (this.size() > o.size()) {
                return 1;
            } else if (this.size() < o.size()) {
                return -1;
            } else {
                return 0;
            }
        } else {
            return 1;
        }      
    }

    @Override
    public Iterator<Integer> iterator() {
        return tmpNodeList.iterator();
    }

    @Override
    public int size() {
        if (this.nodeList == null) {
            return -1;
        } else {
            return this.nodeList.length;
        }
    }

    @Override
    public int getNode(int i) {
        if (i >= this.size() || i < 0) {
            return -1;
        } else {
            if (this.nodeList == null) {
                return -1;
            } else {
                if (i >= this.nodeList.length) {
                    return -1;
                } else {
                    return this.nodeList[i];
                }
            }
        }
    }

    @Override
    public boolean containsNode(int node) {
        if (this.nodeList == null) {
            return false;
        }
        for (int i = 0; i < this.nodeList.length; i++) {
            if (i >= 0) {
                if (this.nodeList[i] == node) {
                    return true;
                }
            }
        }
        return false;
    }

    @Override
    public int getDistinctNodeCount() {
        if (this.nodeList == null) {
            return -1;
        }
        HashMap<Integer, Integer> tmp = new HashMap<>();
        for (int i = 0; i < this.nodeList.length; i++) {
            if (i >= 0) {
                if (!tmp.containsKey(this.nodeList[i])) {
                    tmp.put(this.nodeList[i], 1);
                }
            }
        }
        return tmp.size();
    }

    @Override
    public boolean equals(Object obj) {
        if (obj != null) {
            if (obj != null && obj instanceof Path) {
                if (((Path) obj).size() == this.size()) {
                    for (int i = 0; i < this.size(); i++) {
                        if (i >= 0) {
                            if (((Path) obj).getNode(i) != this.getNode(i)) {
                                return false;
                            }
                        }
                    }
                    return true;
                } else {
                    return false;
                }
            } else {
                return false;
            }
        } else {
            return !(super.equals(obj));
        }
    }

    public int hashCode() {
        return Arrays.hashCode(nodeList);
    }

    @Override
    public boolean isValid() {
        if (this.size() >= 2) {
            return true;
        } else {
            return false;
        }
    }

    public int getUnpleasantValue(int nodeId) {
        return 0;
    }

}

