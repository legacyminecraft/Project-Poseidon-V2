package org.bukkit.event.player;

import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Player;
import org.bukkit.event.HandlerList;
import org.bukkit.inventory.ItemStack;

/**
 * Called when a player empties a bucket
 */
public class PlayerBucketEmptyEvent extends PlayerBucketEvent {

    private static final HandlerList HANDLER_LIST = new HandlerList();

    public PlayerBucketEmptyEvent(Player who, Block blockClicked, BlockFace blockFace, Material bucket, ItemStack itemInHand) {
        super(Type.PLAYER_BUCKET_EMPTY,  who, blockClicked, blockFace, bucket, itemInHand);
    }

    @Override
    public HandlerList getHandlers() {
        return HANDLER_LIST;
    }

    public static HandlerList getHandlerList() {
        return HANDLER_LIST;
    }
}
