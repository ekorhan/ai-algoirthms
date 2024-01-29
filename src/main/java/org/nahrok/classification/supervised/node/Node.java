package org.nahrok.classification.supervised.node;

import lombok.Data;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Data
public class Node {

    //A node'u -> x->1.5, y->5.5, z -> 3.0 ...
    private String name;
    private List<Double> coordinate = new ArrayList<>();
    private Integer status;

    public Node(String key, List<Double> values, boolean hasStatus) {
        this.name = key;
        if (hasStatus) {
            this.coordinate = values.subList(0, values.size() - 1);
            Double status = values.subList(values.size() - 1, values.size()).get(0);
            if (Objects.nonNull(status)) {
                this.status = status.intValue();
            } else {
                this.status = null;
            }
        } else {
            this.coordinate = values;
        }
    }
}
