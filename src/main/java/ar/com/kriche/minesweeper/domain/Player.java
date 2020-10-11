package ar.com.kriche.minesweeper.domain;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "player")
public class Player {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    private String userName;
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "player", orphanRemoval = true)
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

}
