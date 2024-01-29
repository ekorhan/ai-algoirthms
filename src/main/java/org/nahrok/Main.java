package org.nahrok;

import org.nahrok.classification.supervised.KnnAlgorithm;
import org.nahrok.classification.supervised.SimpleNormalization;
import org.nahrok.classification.supervised.node.Node;
import org.nahrok.classification.supervised.node.PrepareNode;
import org.nahrok.utils.OklidDistanceCalculator;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.List;

public class Main {
    public static void main(String[] args) throws IOException {
        String fileLocation = "C:\\Users\\mekrh\\Documents\\workspace\\data\\knn_dataset.xlsx";
        FileInputStream file = new FileInputStream(new File(fileLocation));
        PrepareNode prepareNode = new PrepareNode();
        List<Node> nodes = prepareNode.createNodes(file);
        KnnAlgorithm knnAlgorithm = new KnnAlgorithm(3, new OklidDistanceCalculator(), nodes, new SimpleNormalization());
        knnAlgorithm.run(prepareNode);
        for (Node node : knnAlgorithm.getNodes()) {
            System.out.println(node.getName() + " -> " + node.getCoordinate() + " -|- class: " + node.getStatus());
        }
    }
}
