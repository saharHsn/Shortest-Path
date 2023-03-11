package com.sahar.shortestPath.domain.dto;

import com.sahar.shortestPath.domain.entity.Node;
import lombok.*;

import java.io.Serializable;

/**
 * A DTO for the {@link Node} entity
 */
@AllArgsConstructor
@Setter
@Getter
@Builder
@Data
public class NodeDto implements Serializable {
    private final Long id;
    private final String name;
    private final String symbol;
}