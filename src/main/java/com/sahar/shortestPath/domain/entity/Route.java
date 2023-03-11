package com.sahar.shortestPath.domain.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.ManyToOne;

@Setter
@Getter
@AllArgsConstructor
@Builder
@Entity
public class Route {
    @Id
    private Long id;

    @ManyToOne
    private Node origin;
    @ManyToOne
    private Node destination;

    private double distance;
    private double traffic;

    public Route() {
    }
}
