package org.nahrok.classification.supervised;

import lombok.Data;
import org.nahrok.classification.supervised.node.Node;
import org.nahrok.classification.supervised.node.PrepareNode;
import org.nahrok.utils.IDistanceCalculator;

import java.io.IOException;
import java.util.*;

@Data
public class KnnAlgorithm {
    private final int k;
    private final IDistanceCalculator distanceCalculator;
    private final List<Node> nodes;
    private final INormalization normalization;
    private List<Node> newNodes;

    public KnnAlgorithm(int k, IDistanceCalculator distanceCalculator, List<Node> nodes, INormalization normalization) {
        this.k = k;
        this.distanceCalculator = distanceCalculator;
        this.nodes = nodes;
        this.normalization = normalization;
    }

    public void run(PrepareNode prepareNode) {
        List<Node> willBeCalculatedNodes = getWillBeCalculatedNodes();
        normalization.doNormalization(nodes);
        for (Node newNode : willBeCalculatedNodes) {
            Map<Integer, List<Double>> statusAndDistance = new HashMap<>();
            double[] distanceArray = new double[k];
            Integer[] statusArray = new Integer[k];
            for (int i = 0; i < k; i++) {
                distanceArray[i] = Integer.MAX_VALUE;
                statusArray[i] = -1;
            }
            for (Node node : nodes) {
                if (Objects.equals(newNode.getName(), node.getName())) {
                    continue;
                }
                statusAndDistance.computeIfAbsent(node.getStatus(), k -> new ArrayList<>());
                double distance = distanceCalculator.calculateDistance(node, newNode);
                boolean isUpdated = false;
                for (int i = 0; i < k; i++) {
                    if (distance < distanceArray[i]) {
                        for (int j = k - 1; j >= i; j--) {
                            if (j == i) {
                                distanceArray[j] = distance;
                                statusArray[j] = node.getStatus();
                                isUpdated = true;
                                continue;
                            }
                            distanceArray[j] = distanceArray[j - 1];
                            statusArray[j] = statusArray[j - 1];
                            isUpdated = true;
                        }
                    }
                    if (isUpdated) {
                        break;
                    }
                }
            }
            Integer resultOfNewNode = getResultOfNewNode(statusArray);
            newNode.setStatus(resultOfNewNode);
            System.out.println("Node Name: " + newNode.getName() + ", resultOfNewNode: " + resultOfNewNode);
            for (Node node : nodes) {
                if (Objects.equals(node.getName(), newNode.getName())) {
                    node.setStatus(newNode.getStatus());
                    break;
                }
            }
        }
        try {
            prepareNode.write(Arrays.asList(2, 1));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private List<Node> getWillBeCalculatedNodes() {
        List<Node> result = new ArrayList<>();
        for (Node node : nodes) {
            if (node.getStatus() == null) {
                result.add(node);
            }
        }
        return result;
    }

    private static Integer getResultOfNewNode(Integer[] statusArray) {
        Map<Integer, Integer> scoreMap = new HashMap<>();
        for (Integer status : statusArray) {
            scoreMap.putIfAbsent(status, 0);
            scoreMap.put(status, scoreMap.get(status) + 1);
        }
        Integer max = Integer.MIN_VALUE;
        Integer resultOfNewNode = null;
        for (Map.Entry<Integer, Integer> map : scoreMap.entrySet()) {
            if (map.getValue() > max) {
                max = map.getValue();
                resultOfNewNode = map.getKey();
            }
        }
        return resultOfNewNode;
    }
}
