package ar.com.kriche.minesweeper.controller;

import ar.com.kriche.minesweeper.domain.CellState;
import com.fasterxml.jackson.annotation.JsonInclude;

/**
 * @Author Kriche 2020
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
public class CellDTO {

    private Boolean mined;
    private Integer adjacentMines;
    private CellState state;

    public CellDTO(Boolean mined, Integer adjacentMines, CellState state) {
        this.mined = mined;
        this.adjacentMines = adjacentMines;
        this.state = state;
    }

    public Boolean getMined() {
        return mined;
    }

    public Integer getAdjacentMines() {
        return adjacentMines;
    }

    public CellState getState() {
        return state;
    }

}
