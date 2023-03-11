package com.sahar.shortestPath.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sahar.shortestPath.service.GraphService;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import java.util.LinkedList;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


@WebMvcTest(GraphController.class)
class GraphControllerTest {
    @Autowired
    MockMvc mockMvc;
    @Autowired
    ObjectMapper mapper;
    @MockBean
    GraphService graphService;


    @Test
    void findShortestPathDistance() throws Exception {
        Mockito.when(graphService.findShortestDistance("B")).thenReturn(0.44);

        mockMvc.perform(MockMvcRequestBuilders
                        .get("/graph/shortestPath/distance/B")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
    }

    @Test
    void findShortestPath() throws Exception {
        LinkedList<String> path = new LinkedList<>();
        path.add("J");
        path.add("k");
        path.add("A");
        Mockito.when(graphService.findShortestPath("B")).thenReturn(path);

        mockMvc.perform(MockMvcRequestBuilders
                        .get("/graph/shortestPath/path/B")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
    }
}