package com.sahar.shortestPath.service;

import com.sahar.shortestPath.exception.EntityNotFoundException;
import com.sahar.shortestPath.repository.NodeRepository;
import com.sahar.shortestPath.domain.entity.Node;
import com.sahar.shortestPath.domain.entity.Route;
import com.sahar.shortestPath.repository.RouteRepository;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.StreamSupport;

@Service
public class GraphService {
    private final Logger logger = LoggerFactory.getLogger(getClass());
    private final NodeRepository nodeRepository;
    private final RouteRepository routeRepository;
    List<Node> allNodes;
    Map<String, List<Node>> adjacentList;

    public GraphService(NodeRepository nodeRepository, RouteRepository routeRepository) {
        this.nodeRepository = nodeRepository;
        this.routeRepository = routeRepository;
    }

    public void initialize() {
        Iterable<Node> allNodesIt = nodeRepository.findAll();
        allNodes = StreamSupport.stream(allNodesIt.spliterator(), false).toList();

        // Initialize adjacency list for every node in the graph
        adjacentList = new HashMap<>();
        allNodes.forEach(node -> adjacentList.put(node.getSymbol(), new ArrayList<>()));

        //Make adjacency list
        Iterable<Route> allRouteIt = routeRepository.findAll();
        List<Route> allRoutes = StreamSupport.stream(allRouteIt.spliterator(), false).toList();
        allRoutes.forEach(route -> {
            Node destNode = route.getDestination();
            adjacentList.get(route.getOrigin().getSymbol()).add(new Node(destNode.getSymbol(), route.getDistance()));
        });
    }

    public double findShortestDistance(String destination) {
        Map<String, Double> stringDoubleMap = makeDistanceList("A", destination).getDistanceInfo();
        return stringDoubleMap.get(destination);
    }

    public LinkedList<String> findShortestPath(String destination) {
        return makeDistanceList("A", destination).getPathInfo();
    }

    private GraphResult makeDistanceList(String origin, String destination) {
        Optional<Node> destInDB = nodeRepository.findBySymbol(destination);
        if (destInDB.isEmpty()) {
            throw new EntityNotFoundException(Node.class, "symbol", destination);
        }
        initialize();
        Map<String, String> pred = new HashMap<>();
        Set<String> visited = new HashSet<>();
        int nodeSize = allNodes.size();
        PriorityQueue<Node> pQueue = new PriorityQueue<>(nodeSize, new Node());
        Map<String, Double> dist = new HashMap<>();

        for (Node node : allNodes) {
            dist.put(node.getSymbol(), Double.MAX_VALUE);
        }

        // first add source vertex to PriorityQueue
        pQueue.add(new Node(origin, 0d));

        // Distance to the source from itself is 0
        dist.put(origin, 0d);
        while (visited.size() != nodeSize) {
            // visitingNode is removed from PriorityQueue and has min distance
            String visitingNode = pQueue.remove().getSymbol();

            // add node to finalized list (visited)
            visited.add(visitingNode);
            graphAdjacentNodes(visitingNode, visited, dist, pQueue, pred);
        }
        // Print distance
        logger.info("Shortest path length is: " + dist.get(destination));

        LinkedList<String> shortestPath = findShortestPath(destination, pred);

        return new GraphResult(dist, shortestPath);
    }

    private void graphAdjacentNodes(String visitingNode,
                                    Set<String> visited,
                                    Map<String, Double> dist,
                                    PriorityQueue<Node> pQueue,
                                    Map<String, String> pred) {
        double edgeDistance;
        double newDistance;

        // process all neighbouring nodes of visitingNode
        int size = adjacentList.get(visitingNode).size();
        for (int i = 0; i < size; i++) {
            Node v = adjacentList.get(visitingNode).get(i);

            //  proceed only if current node is not in 'visited'
            if (!visited.contains(v.getSymbol())) {
                edgeDistance = v.getCost();
                newDistance = dist.get(visitingNode) + edgeDistance;

                // compare distances
                if (newDistance < dist.get(v.getSymbol()))
                    dist.put(v.getSymbol(), newDistance);
                pred.put(v.getSymbol(), visitingNode);
                // Add the current vertex to the PriorityQueue
                pQueue.add(v);
            }
        }
    }

    private LinkedList<String> findShortestPath(String destination, Map<String, String> pred) {
        LinkedList<String> path = new LinkedList<>();
        String crawl = destination;
        path.add(crawl);

        while (pred.containsKey(crawl)) {
            path.add(pred.get(crawl));
            crawl = pred.get(crawl);
        }

        // Print path
        logger.info("Path is ::");
        for (int j = path.size() - 1; j >= 0; j--) {
            logger.info(path.get(j) + " ");
        }
        return path;
    }

    @AllArgsConstructor
    @Getter
    class GraphResult {
        Map<String, Double> distanceInfo;
        LinkedList<String> pathInfo;
    }
}
