package com.azati.warshipprocessing.util;

import com.azati.warshipprocessing.dto.SessionDTO;
import com.azati.warshipprocessing.entity.MapCell;
import com.azati.warshipprocessing.exception.NoSuchCellException;
import com.azati.warshipprocessing.repository.MapCellRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Comparator;

import static com.azati.warshipprocessing.util.VariableConstants.DROWN;
import static com.azati.warshipprocessing.util.VariableConstants.DROWN_VIEW;
import static com.azati.warshipprocessing.util.VariableConstants.GAME_MAP_CELLS_COUNT;
import static com.azati.warshipprocessing.util.VariableConstants.HIT;
import static com.azati.warshipprocessing.util.VariableConstants.HIT_VIEW;
import static com.azati.warshipprocessing.util.VariableConstants.MISS;
import static com.azati.warshipprocessing.util.VariableConstants.MISS_VIEW;
import static com.azati.warshipprocessing.util.VariableConstants.OK;
import static com.azati.warshipprocessing.util.VariableConstants.OK_VIEW;

@Service
@RequiredArgsConstructor
public class MapPrinter {

    private final MapCellRepository mapCellRepository;

    public void printMap(SessionDTO session) {
        System.out.println("\nVIEW RULES:");
        System.out.println("DROWN: " + DROWN_VIEW);
        System.out.println("HIT: " + HIT_VIEW);
        System.out.println("OK: " + OK_VIEW);
        System.out.println("MISS: " + MISS_VIEW + "\n");
        for (int k = 0; k < 2; k++) {
            System.out.print("Player: " + (k == 0 ? session.getFirstUserId() : session.getSecondUserId()));
            var cellList = mapCellRepository.findByUserIdAndSessionId(k == 0 ? session.getFirstUserId() : session.getSecondUserId(), session.getId())
                    .orElseThrow(NoSuchCellException::new);
            cellList.sort(Comparator.comparingInt(MapCell::getId));
            for (int i = 0; i < GAME_MAP_CELLS_COUNT; i++) {
                for (int j = 0; j < GAME_MAP_CELLS_COUNT; j++) {
                    var status = cellList.get(i * GAME_MAP_CELLS_COUNT + j).getStatus();
                    var statusView = switch (status) {
                        case HIT -> HIT_VIEW;
                        case MISS -> MISS_VIEW;
                        case DROWN -> DROWN_VIEW;
                        case OK -> OK_VIEW;
                        default -> throw new IllegalStateException("Unexpected value: " + status);
                    };
                    System.out.print(statusView + " ");
                }
                System.out.println();
            }
            System.out.println();
        }
    }
}
