# Introduction

## Class

### Path

the path class should inherit the interface `com.oocourse.specs3.models.Path`**。

```java
import com.oocourse.specs3.models.Path;

public class MyPath implements Path {
    // TODO : IMPLEMENT
}
```

interface definition

```java
package com.oocourse.specs3.models;

public interface Path extends Iterable<Integer>, Comparable<Path> {
    int size();

    int getNode(int index);

    boolean containsNode(int nodeId);

    int getDistinctNodeCount();

    boolean equals(Object obj);

    boolean isValid();

    int getUnpleasantValue(int nodeId);
}
```

注意：

* 本接口继承了`Iterable<Integer>`迭代器接口，所以**还需要实现迭代器方法`Iterator<Integer> iterator()`**。
* 本接口继承了`Comparable<Path>`偏序比较接口，所以**还需要实现偏序比较方法`int compareTo(Path o)`**。
* 关于迭代器接口、偏序比较接口相关的内容，后文有介绍

### RailwaySystem


```java
import com.oocourse.specs3.models.RailwaySystem;

public class MyRailwaySystem implements RailwaySystem {
    // TODO : IMPLEMENT
}
```


```java
package com.oocourse.specs3.models;

public interface RailwaySystem extends Graph {
    int getLeastTicketPrice(int fromNodeId, int toNodeId) throws NodeIdNotFoundException, NodeNotConnectedException;

    int getLeastTransferCount(int fromNodeId, int toNodeId) throws NodeIdNotFoundException, NodeNotConnectedException;

    int getUnpleasantValue(Path path, int fromIndex, int toIndex);

    int getLeastUnpleasantValue(int fromNodeId, int toNodeId) throws NodeIdNotFoundException, NodeNotConnectedException;

    int getConnectedBlockCount();
}

```

Graph：

```java
package com.oocourse.specs3.models;

public interface Graph extends PathContainer {
    boolean containsNode(int nodeId);

    boolean containsEdge(int fromNodeId, int toNodeId);

    boolean isConnected(int fromNodeId, int toNodeId) throws NodeIdNotFoundException;

    int getShortestPathLength(int fromNodeId, int toNodeId) throws NodeIdNotFoundException, NodeNotConnectedException;
}
```

PathContainer：

```java
package com.oocourse.specs3.models;

public interface PathContainer {
    int size();

    boolean containsPath(Path path);

    boolean containsPathId(int pathId);

    Path getPathById(int pathId) throws PathIdNotFoundException;

    int getPathId(Path path) throws PathNotFoundException;

    int addPath(Path path);

    int removePath(Path path) throws PathNotFoundException;

    boolean removePathById(int pathId) throws PathIdNotFoundException;

    int getDistinctNodeCount();
}
```

## Instruction



```java
import com.oocourse.specs3.AppRunner;

public class Main {
    public static void main(String[] args) throws Exception {
        AppRunner runner = AppRunner.newInstance(MyPath.class, MyRailwaySystem.class);
        runner.run(args);
    }
}
```



值得注意的是：

* 自己实现的Path类必须继承接口`com.oocourse.specs3.models.Path`，否则将无法传入。
* 自己实现的RailwaySystem类必须继承接口`com.oocourse.specs3.models.RailwaySystem`，否则将无法传入。
* **`Path`类必须实现构造函数`public Path(int[] nodeList)`**（或者`public Path(int... nodeList)`，实际上等价，后文会对此进行介绍），否则会出现报错。
* **`RailwaySystem`类必须实现构造函数`public RailwaySystem()`**，否则会出现报错。

## Appendix

### Interator


```java
import java.util.ArrayList;
import java.util.Iterator;

class TestList implements Iterable<Integer> {
    private final ArrayList<Integer> list;

    public TestList(ArrayList<Integer> list) {
        this.list = list;
    }

    @Override
    public Iterator<Integer> iterator() {
        return list.iterator();
    }
}
```

文件`TestMain.java`

```java
import java.util.ArrayList;

public class TestMain {
    public static void main(String[] args) throws Exception {
        ArrayList<Integer> originalList = new ArrayList<Integer>() {{
            add(2);
            add(3);
            add(5);
            add(7);
            add(19260817);
        }};
        TestList testList = new TestList(originalList);
        for (Integer integer : testList) {
            System.out.println(integer);
        }
    }
}
```

Result：

```
2
3
5
7
19260817
```


### Comparator



```java
public class TestInteger implements Comparable<TestInteger> {
    private final int value;

    TestInteger(int value) {
        this.value = value;
    }

    public int getValue() {
        return value;
    }

    @Override
    public int compareTo(TestInteger o) {
        // when this.value < o.value, return < 0
        // when this.value == o.value, return == 0
        // when this.value > o.value, return > 0
        return Integer.compare(this.value, o.value);
    }
}
```

`TestMain.java`

```java
import java.util.ArrayList;
import java.util.Collections;

public class TestMain {
    public static void main(String[] args) throws Exception {
        TestInteger testInteger1 = new TestInteger(1);
        TestInteger testInteger2 = new TestInteger(2);
        TestInteger testInteger3 = new TestInteger(3);
        System.out.println(String.format("#1 vs #2 : %s", testInteger1.compareTo(testInteger2)));
        System.out.println(String.format("#1 vs #1 : %s", testInteger1.compareTo(testInteger1)));
        System.out.println(String.format("#3 vs #2 : %s", testInteger3.compareTo(testInteger2)));

        ArrayList<TestInteger> testIntegers = new ArrayList<TestInteger>() {{
            add(new TestInteger(3));
            add(new TestInteger(7));
            add(new TestInteger(2));
            add(new TestInteger(5));
        }};
        Collections.sort(testIntegers);
        for (TestInteger testInteger : testIntegers) {
            System.out.println(testInteger.getValue());
        }
    }
}
```

Result：

```
#1 vs #2 : -1
#1 vs #1 : 0
#3 vs #2 : 1
2
3
5
7
```

