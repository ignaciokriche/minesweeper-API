package ar.com.kriche.minesweeper.domain;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

import static ar.com.kriche.minesweeper.domain.GameState.IN_PROGRESS;
import static ar.com.kriche.minesweeper.domain.GameState.PAUSED;

/**
 * Represents a minesweeper board.
 *
 * @Author Kriche 2020
 */
@Entity
@Table(name = "game")
public class Game {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    private int rowSize;
    private int columnSize;
    private int mines;
    private int availableFlags;
    private int revealedCells;
    private GameState state;
    @OneToOne(cascade = CascadeType.ALL, orphanRemoval = true)
    private SimpleTimeTracker timeTracker;
    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true)
    private List<BoardRow> board;
    @ManyToOne
    private Player player;

    /**
     * for ORM only.
     */
    protected Game() {
    }

    public Game(Player player, int rows, int columns, int mines) {
        this.player = player;
        this.rowSize = rows;
        this.columnSize = columns;
        this.mines = mines;
        this.availableFlags = mines;
        this.revealedCells = 0;
        this.board = new ArrayList<>(rows);
        this.state = IN_PROGRESS;
        this.timeTracker = new SimpleTimeTracker();
        // game on, start tracking time!
        timeTracker.start();
    }

    public Long getId() {
        return id;
    }

    public Player getPlayer() {
        return player;
    }

    public int getRowSize() {
        return rowSize;
    }

    public int getColumnSize() {
        return columnSize;
    }

    public int getMines() {
        return mines;
    }

    public int getAvailableFlags() {
        return availableFlags;
    }

    private void setAvailableFlags(int availableFlags) {
        this.availableFlags = availableFlags;
    }

    public void decreaseAvailableFlags() {
        setAvailableFlags(getAvailableFlags() - 1);
    }

    public void increaseAvailableFlags() {
        setAvailableFlags(getAvailableFlags() + 1);
    }

    public int getRevealedCells() {
        return revealedCells;
    }

    private void setRevealedCells(int revealedCells) {
        this.revealedCells = revealedCells;
    }

    public void increaseRevealedCells() {
        setRevealedCells(getRevealedCells() + 1);
    }

    public List<BoardRow> getBoard() {
        return board;
    }

    public Cell cellAt(int row, int column) {
        return board.get(row).getCells().get(column);
    }

    public void setState(GameState state) {
        this.state = state;
        if (state == IN_PROGRESS) {
            timeTracker.start();
        } else {
            timeTracker.stop();
        }
    }

    public GameState getState() {
        return state;
    }

    public boolean isInProgress() {
        return getState() == IN_PROGRESS;
    }

    public boolean isPaused() {
        return getState() == PAUSED;
    }

    public long getElapsedTimeMilliseconds() {
        return timeTracker.getAccumulatedTimeMilliseconds();
    }

    public Stream<CellCoordinate> getNeighbours(int cellRow, int cellColumn) {

        Stream.Builder<CellCoordinate> neighbours = Stream.builder();

        // for border cases:
        int lowerRow = Math.max(0, cellRow - 1);
        int upperRow = Math.min(getRowSize() - 1, cellRow + 1);
        int lowerColumn = Math.max(0, cellColumn - 1);
        int upperColumn = Math.min(getColumnSize() - 1, cellColumn + 1);

        for (int r = lowerRow; r <= upperRow; r++) {
            for (int c = lowerColumn; c <= upperColumn; c++) {
                if (r == cellRow && c == cellColumn) {
                    continue; // skip self
                }
                neighbours.accept(new CellCoordinate(r, c));
            }
        }

        return neighbours.build();
    }

    @Override
    public String toString() {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("id: ");
        stringBuilder.append(getId());
        stringBuilder.append('\n');
        getBoard().forEach(r -> {
            r.getCells().forEach(cell -> stringBuilder.append(cell));
            stringBuilder.append('\n');
        });
        return stringBuilder.toString();
    }

}
