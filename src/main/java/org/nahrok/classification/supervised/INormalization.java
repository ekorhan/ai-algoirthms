package org.nahrok.classification.supervised;

import org.nahrok.classification.supervised.node.Node;

import java.util.List;

public interface INormalization {
    void doNormalization(List<Node> nodes);
}
