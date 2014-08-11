package me.paulbgd.blocks.api.player;

import java.util.UUID;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import me.paulbgd.blocks.BlocksLanguage;
import me.paulbgd.blocks.api.block.Blocks;
import org.bukkit.Bukkit;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;

/**
 * Represents a player who can replicate blocks.
 */
@Data
public class BlocksPlayerData {

    private final UUID uuid;
    private final String name;

    @Getter
    @Setter
    private Block pointOne;
    @Getter
    @Setter
    private Block pointTwo;
    @Getter
    @Setter
    private Blocks clipboard;

    /**
     * Returns the player object from the uuid stored
     *
     * @return player object
     * @throws java.lang.IllegalArgumentException if the player is not online
     */
    public Player getPlayer() {
        Player player = Bukkit.getPlayer(this.uuid);
        if (player == null) {
            throw new IllegalArgumentException(String.format(BlocksLanguage.NOT_ONLINE, uuid, name));
        }
        return player;
    }

}
