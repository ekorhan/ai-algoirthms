package org.nahrok.utils;

import org.nahrok.classification.supervised.node.Node;

import java.util.ArrayList;
import java.util.List;

public class OklidDistanceCalculator implements IDistanceCalculator {
    @Override
    public double calculateDistance(Node a, Node b) {
        List<Double> aList = a.getCoordinate();
        List<Double> bList = b.getCoordinate();
        int size = aList.size();
        if (size != bList.size()) {
            System.out.println(aList.size());
            System.out.println(bList.size());
            System.out.println(aList);
            System.out.println(bList);
            throw new IllegalStateException("ERRROR");
        }
        double total = 0.0;
        for (int i = 0; i < size; i++) {
            total += Math.pow(aList.get(i) - bList.get(i), 2);
        }
        return Math.pow(total, 0.5);
    }
}
