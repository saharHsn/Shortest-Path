package com.sahar.shortestPath.repository;

import com.sahar.shortestPath.domain.entity.Node;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.lang.NonNull;

import java.util.Optional;

public interface NodeRepository extends CrudRepository<Node, Long> {
    @Query("select n from Node n where n.symbol like ?1")
    Optional<Node> findBySymbol(@NonNull String symbol);
}
