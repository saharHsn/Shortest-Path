package com.sahar.shortestPath.domain.entity;

import lombok.*;

import javax.persistence.*;
import java.util.Comparator;

@Setter
@Getter
@Builder
@AllArgsConstructor
@Entity
public class Node implements Comparator<Node> {
    @Id
    @GeneratedValue
    private Long id;
    @Column(unique = true, nullable = false)
    private String symbol;
    private String name;

    @Transient
    private double cost;

    public Node() {

    }

    public Node(String origin, double cost) {
        this.symbol = origin;
        this.cost = cost;
    }

    @Override
    public int compare(Node node1, Node node2) {
        return Double.compare(node1.cost, node2.cost);
    }
}
