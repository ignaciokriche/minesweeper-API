package ar.com.kriche.minesweeper.domain;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

/**
 * @Author Kriche 2020
 */
@Entity
@Table(name = "board_row")
public class BoardRow {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true)
    @OrderBy("id")
    private List<Cell> cells = new ArrayList<>();

    public List<Cell> getCells() {
        return cells;
    }

}
