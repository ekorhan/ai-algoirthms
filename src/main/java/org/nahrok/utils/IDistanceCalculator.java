package org.nahrok.utils;

import org.nahrok.classification.supervised.node.Node;

public interface IDistanceCalculator {
    double calculateDistance(Node a, Node b);
}
