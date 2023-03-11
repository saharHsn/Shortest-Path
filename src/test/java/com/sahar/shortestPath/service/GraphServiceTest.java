package com.sahar.shortestPath.service;

import com.sahar.shortestPath.repository.NodeRepository;
import com.sahar.shortestPath.domain.entity.Node;
import com.sahar.shortestPath.domain.entity.Route;
import com.sahar.shortestPath.repository.RouteRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;

@ExtendWith(MockitoExtension.class)
class GraphServiceTest {

    @Mock
    NodeRepository nodeRepository;

    @Mock
    RouteRepository routeRepository;

    @InjectMocks
    GraphService graphService;
    private static final List<Node> NODE_LIST= new ArrayList<>();;
    private static final List<Route> ROUTE_LIST = new ArrayList<>();

    static {
        Node A = new Node("A", 0d);
        Node B = new Node("B", 0d);
        Node C = new Node("C", 0d);
        Node D = new Node("D", 0d);
        NODE_LIST.add(A);
        NODE_LIST.add(B);
        NODE_LIST.add(C);
        NODE_LIST.add(D);

        ROUTE_LIST.add(new Route(1L, A, B, 1, 0));
        ROUTE_LIST.add(new Route(2L, A, C, 2, 0));
        ROUTE_LIST.add(new Route(3L, B, D, 1, 0));
        ROUTE_LIST.add(new Route(4L, C, D, 3, 0));
    }

    @Test
    void initialize() {
    }

    @DisplayName("JUnit test for findShortestDistanceFromEarthToDest method")
    @Test
    void givenDestination_whenGetShortestDistance_thenReturnDistance() {

        // given
        given(nodeRepository.findAll()).willReturn(NODE_LIST);

        // given
        given(routeRepository.findAll()).willReturn(ROUTE_LIST);

        // given
        given(nodeRepository.findBySymbol("D")).willReturn(Optional.of(new Node("D", 0)));

        // when
        Double distance = graphService.findShortestDistance("D");

        // then
        assertThat(distance).isEqualTo(2);

    }

    @DisplayName("JUnit test for findShortestPath method")
    @Test
    void getShortestPath() {
        // given
        given(nodeRepository.findAll()).willReturn(NODE_LIST);

        // given
        given(routeRepository.findAll()).willReturn(ROUTE_LIST);

        //given
        given(nodeRepository.findBySymbol("D")).willReturn(Optional.of(new Node("D", 0)));

        // when
        LinkedList<String> shortestPath = graphService.findShortestPath("D");

        LinkedList<String> result = new LinkedList<>();
        result.add("D");
        result.add("B");
        result.add("A");
        // then
        assertThat(shortestPath).isEqualTo(result);
    }
}