package ar.com.kriche.minesweeper.repository;

import ar.com.kriche.minesweeper.domain.Player;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * the Spring JPA Repo for Player objects.
 *
 * @Author Kriche 2020
 */
public interface PlayerRepository extends JpaRepository<Player, String> {

    Player getPlayerByUserName(String userName);

    boolean existsByUserName(String userName);
}
