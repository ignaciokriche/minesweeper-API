package ar.com.kriche.minesweeper.controller;

import ar.com.kriche.minesweeper.domain.CellMark;
import com.fasterxml.jackson.annotation.JsonInclude;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class CellDTO {

    private Boolean mined;
    private Integer adjacentMines;
    private CellMark mark;

    public CellDTO(Boolean mined, Integer adjacentMines, CellMark mark) {
        this.mined = mined;
        this.adjacentMines = adjacentMines;
        this.mark = mark;
    }

    public Boolean getMined() {
        return mined;
    }

    public Integer getAdjacentMines() {
        return adjacentMines;
    }

    public CellMark getMark() {
        return mark;
    }

}
