package ar.com.kriche.minesweeper.domain;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

/**
 * @Author Kriche 2020
 */
@Entity
@Table(name = "player")
public class Player {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    private String userName;

    @OneToMany(cascade = CascadeType.ALL, mappedBy = "player", orphanRemoval = true)
    @OrderBy("id")
    private List<Game> games = new ArrayList<>();

    /**
     * For ORM only.
     */
    protected Player() {
    }

    public Player(String userName) {
        this.userName = userName;
    }

    public String getUserName() {
        return userName;
    }

    public List<Game> getGames() {
        return games;
    }

    public void addGame(Game game) {
        getGames().add(game);
    }

    @Override
    public String toString() {
        return getUserName();
    }

}
