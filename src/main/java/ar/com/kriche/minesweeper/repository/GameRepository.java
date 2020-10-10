package ar.com.kriche.minesweeper.repository;

import ar.com.kriche.minesweeper.domain.Game;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * the Spring JPA Repo for Game objects.
 *
 * @Author Kriche 2020
 */
public interface GameRepository extends JpaRepository<Game, Long> {
}
