package com.sahar.shortestPath.controller;

import com.sahar.shortestPath.service.GraphService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.LinkedList;

@RestController
@RequestMapping("/graph")
public class GraphController {
    private final GraphService graphService;

    public GraphController(GraphService graphService) {
        this.graphService = graphService;
    }

    @GetMapping("/shortestPath/distance/{destination}")
    public ResponseEntity<Double> findShortestPathDistance(@PathVariable String destination){
        double shortestPath = graphService.findShortestDistance(destination);
        return new ResponseEntity<>(shortestPath, HttpStatus.OK);
    }

    @GetMapping("/shortestPath/path/{destination}")
    public ResponseEntity<LinkedList<String>> findShortestPath(@PathVariable String destination){
        LinkedList<String> shortestPath = graphService.findShortestPath(destination);
        return new ResponseEntity<>(shortestPath, HttpStatus.OK);
    }

}
