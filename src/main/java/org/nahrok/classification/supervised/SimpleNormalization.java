package org.nahrok.classification.supervised;

import org.nahrok.classification.supervised.node.Node;

import java.util.*;

public class SimpleNormalization implements INormalization {
    @Override
    public void doNormalization(List<Node> nodes) {
        LinkedHashMap<Integer, List<Double>> columns = new LinkedHashMap<>();
        for (Node node : nodes) {
            for (int i = 0; i < node.getCoordinate().size(); i++) {
                columns.computeIfAbsent(i, k -> new ArrayList<>());
                columns.get(i).add(node.getCoordinate().get(i));
            }
        }
        LinkedHashMap<Integer, List<Double>> result = new LinkedHashMap<>();
        for (Map.Entry<Integer, List<Double>> map : columns.entrySet()) {
            List<Double> values = map.getValue();
            double min = values.stream().min(Comparator.naturalOrder()).orElse(0d);
            double max = values.stream().max(Comparator.naturalOrder()).orElse(0d);
            List<Double> newValues = new ArrayList<>();
            for (Double val : values) {
                double newVal = (min == max) ? 0 : (val - min) / (max - min);
                newValues.add(newVal);
            }
            result.put(map.getKey(), newValues);
        }

        System.out.println(result);

        int i = 0;
        for (Node node : nodes) {
            List<Double> val = new ArrayList<>();
            for (Map.Entry<Integer, List<Double>> e : result.entrySet()) {
                val.add(e.getValue().get(i));
            }
            node.setCoordinate(val);
            i++;
        }
    }
}
