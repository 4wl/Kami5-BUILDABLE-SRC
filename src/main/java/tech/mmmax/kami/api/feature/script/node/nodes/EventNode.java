package tech.mmmax.kami.api.feature.script.node.nodes;

import java.awt.Color;
import java.util.Map;
import tech.mmmax.kami.api.feature.script.node.Node;

public class EventNode extends Node {

    public EventNode(String name) {
        super(name);
    }

    public Object runNode(Map args) {
        return null;
    }

    public Color getColor() {
        return new Color(210, 96, 0);
    }
}
