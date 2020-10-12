package ar.com.kriche.minesweeper.service.player;

import ar.com.kriche.minesweeper.domain.Player;
import ar.com.kriche.minesweeper.repository.PlayerRepository;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class PlayerService {

    private static final Log LOGGER = LogFactory.getLog(PlayerService.class);

    @Autowired
    private PlayerRepository playerRepo;

    public Player createPlayer(String userName) {
        LOGGER.info("creating player with user name: \"" + userName + "\".");
        if (playerRepo.existsByUserName(userName)) {
            throw new InvalidUserNameException("user name unavailable.");
        }
        Player player = new Player(userName);
        return playerRepo.save(player);
    }

    public Player getPlayerByUserName(String userName) {
        LOGGER.info("getting player by user name: \"" + userName + "\".");
        Player player = playerRepo.getPlayerByUserName(userName);
        if (player == null) {
            throw new PlayerNotFoundException("player not found.");
        }
        return player;
    }

}
