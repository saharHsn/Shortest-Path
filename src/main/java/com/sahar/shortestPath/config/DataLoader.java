package com.sahar.shortestPath.config;

import com.sahar.shortestPath.domain.dto.NodeDto;
import com.sahar.shortestPath.domain.dto.RouteDto;
import com.sahar.shortestPath.repository.NodeRepository;
import com.sahar.shortestPath.repository.RouteRepository;
import com.sahar.shortestPath.domain.entity.Node;
import com.sahar.shortestPath.domain.entity.Route;
import lombok.AllArgsConstructor;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import javax.transaction.Transactional;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Optional;

@AllArgsConstructor
@Component
public class DataLoader {
    private final Logger logger = LoggerFactory.getLogger(getClass());

    private final RouteRepository routeRepository;
    private final NodeRepository nodeRepository;

    @PostConstruct
    public void loadData() {
        InputStream dataFile = DataLoader.class.getResourceAsStream("/static/planets-data.xlsx");
        Workbook workbook;
        if (dataFile != null) {
            try {
                workbook = new XSSFWorkbook(dataFile);
                Sheet planet_names = workbook.getSheet("Planet Names");
                Iterator<Row> planet_name_rows = planet_names.iterator();
                List<NodeDto> nodeDtoList = makeNodes(planet_name_rows);
                saveNodes(nodeDtoList);

                Sheet routes = workbook.getSheet("Routes");
                Iterator<Row> route_rows = routes.iterator();
                List<RouteDto> routeDtoList = makeRoutes(route_rows);
                saveRoutes(routeDtoList);

                //TODO
                Sheet traffic = workbook.getSheet("Traffic");
                Iterator<Row> traffic_rows = traffic.iterator();

                workbook.close();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
    }

    private static List<NodeDto> makeNodes(Iterator<Row> planet_name_rows) {
        List<NodeDto> nodeDtoList = new ArrayList<>();
        while (planet_name_rows.hasNext()) {
            NodeDto.NodeDtoBuilder nodeDtoBuilder = NodeDto.builder();
            Row next = planet_name_rows.next();
            if (next.getRowNum() == 0) {
                continue;
            }
            Iterator<Cell> cellIterator = next.cellIterator();
            while (cellIterator.hasNext()) {
                Cell currentCell = cellIterator.next();
                int cellIndex = currentCell.getColumnIndex();
                switch (cellIndex) {
                    case 0 -> nodeDtoBuilder.symbol(currentCell.getStringCellValue());
                    case 1 -> nodeDtoBuilder.name(currentCell.getStringCellValue());
                    default -> {
                    }
                }
            }
            nodeDtoList.add(nodeDtoBuilder.build());
        }
        return nodeDtoList;
    }

    private static List<RouteDto> makeRoutes(Iterator<Row> route_rows) {
        List<RouteDto> routeDtoList = new ArrayList<>();

        while (route_rows.hasNext()) {
            RouteDto.RouteDtoBuilder routeDtoBuilder = RouteDto.builder();
            Row next = route_rows.next();
            if (next.getRowNum() == 0) {
                continue;
            }
            Iterator<Cell> cellIterator = next.cellIterator();
            while (cellIterator.hasNext()) {
                Cell currentCell = cellIterator.next();
                switch (currentCell.getColumnIndex()) {
                    case 0 -> routeDtoBuilder.id((long) currentCell.getNumericCellValue());
                    case 1 ->
                            routeDtoBuilder.origin(NodeDto.builder().symbol(currentCell.getStringCellValue()).build());
                    case 2 ->
                            routeDtoBuilder.destination(NodeDto.builder().symbol(currentCell.getStringCellValue()).build());
                    case 3 -> routeDtoBuilder.distance(currentCell.getNumericCellValue());
                    default -> {
                    }
                }
            }
            routeDtoList.add(routeDtoBuilder.build());
        }
        return routeDtoList;
    }

    @Transactional
    public void saveNodes(List<NodeDto> nodeDtoList) {
        nodeDtoList.stream().map(this::toNodeEntity).forEach(nodeRepository::save);
        logger.info("Loaded " + nodeDtoList.size() + " nodes");
    }

    @Transactional
    public void saveRoutes(List<RouteDto> routeDtoList) {
        routeDtoList.stream().map(this::toRouteEntity).forEach(routeRepository::save);
        logger.info("Loaded " + routeDtoList.size() + " routes");
    }

    private Node toNodeEntity(NodeDto dto) {
        return Node.builder().symbol(dto.getSymbol()).name(dto.getName()).build();
    }

    private Route toRouteEntity(RouteDto dto) {
        Optional<Node> originOpt = nodeRepository.findBySymbol(dto.getOrigin().getSymbol());
        Optional<Node> destinationOpt = nodeRepository.findBySymbol(dto.getDestination().getSymbol());
        if (originOpt.isEmpty() || destinationOpt.isEmpty()) {
            throw new RuntimeException("Node cannot be null!" + originOpt.orElse(null) + ": " + dto.getOrigin().getSymbol() + " \n"
                    + destinationOpt.orElse(null) + ":" + dto.getDestination().getSymbol());
        }
        return Route.builder().id(dto.getId()).origin(originOpt.orElse(null)).destination(destinationOpt.orElse(null)).distance(dto.getDistance()).build();
    }
}
