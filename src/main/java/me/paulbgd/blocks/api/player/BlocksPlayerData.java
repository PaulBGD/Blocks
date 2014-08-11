/*
 * COPYRIGHT AND PERMISSION NOTICE
 *
 * Copyright (c) 2014, PaulBGD, <paul@paulbgd.me>.
 *
 * All rights reserved.
 *
 * Permission to use, copy, modify, and distribute this software for any purpose
 * with or without fee is hereby granted, provided that the above copyright
 * notice and this permission notice appear in all copies.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT OF THIRD PARTY RIGHTS. IN
 * NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM,
 * DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR
 * OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE
 * OR OTHER DEALINGS IN THE SOFTWARE.
 *
 * Except as contained in this notice, the name of a copyright holder shall not
 * be used in advertising or otherwise to promote the sale, use or other dealings
 * in this Software without prior written authorization of the copyright holder.
 */

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
