package tech.mmmax.kami.api.feature.script.node;

import java.awt.Color;
import java.util.HashMap;
import java.util.Map;

public abstract class Node {

    String name;
    Map inputs = new HashMap();

    public Node(String name) {
        this.name = name;
    }

    public abstract Object runNode(Map map);

    public abstract Color getColor();

    public String getName() {
        return this.name;
    }
}
