package me.davidml16.aparkour.utils;

import me.davidml16.aparkour.data.WalkableBlock;

import java.util.List;

public class WalkableBlocksUtil {

    public static boolean noContainsWalkable(List<WalkableBlock> walkableBlocks, String id, byte data) {
        for(WalkableBlock walkableBlock : walkableBlocks) {
            if(id == "stone_slab" && data == walkableBlock.getData() && walkableBlock.getId() == "smooth_stone_slab") return false;
            if(id == "wooden_slab" && data == walkableBlock.getData() && walkableBlock.getId() == "oak_slab") return false;
            if(walkableBlock.isDirectional() && walkableBlock.getId() == id) return false;
            if(walkableBlock.getId() == id && walkableBlock.getData() == data) return false;
            if(walkableBlock.isSkull() && WalkableBlock.skullIDs.contains(id)) return false;
        }
        return true;
    }

    public static WalkableBlock getWalkableBlock(List<WalkableBlock> walkableBlocks, String id, byte data) {
        for(WalkableBlock walkableBlock : walkableBlocks) {
            if(walkableBlock.getId() == id && walkableBlock.getData() == data) return walkableBlock;
        }
        return null;
    }

}
