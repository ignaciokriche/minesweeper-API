package ar.com.kriche.minesweeper.service;

import ar.com.kriche.minesweeper.domain.Player;
import ar.com.kriche.minesweeper.repository.PlayerRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class PlayerService {

    @Autowired
    private PlayerRepository playerRepo;

    public Player createPlayer(String userName) {
        if (playerRepo.existsByUserName(userName)) {
            throw new IllegalArgumentException("user name unavailable.");
        }
        Player player = new Player(userName);
        return playerRepo.save(player);
    }

    public Player getPlayerByUserName(String userName) {
        Player player = playerRepo.getPlayerByUserName(userName);
        return player;
    }

}
