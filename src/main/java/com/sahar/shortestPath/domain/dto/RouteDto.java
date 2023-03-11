package com.sahar.shortestPath.domain.dto;

import com.sahar.shortestPath.domain.entity.Route;
import lombok.*;

import java.io.Serial;
import java.io.Serializable;

/**
 * A DTO for the {@link Route} entity
 */
@AllArgsConstructor
@Setter
@Getter
@Builder
@Data
public final class RouteDto implements Serializable {
    @Serial
    private static final long serialVersionUID = 0L;
    private final Long id;
    private final NodeDto origin;
    private final NodeDto destination;
    private final double distance;
    private final double traffic;

}