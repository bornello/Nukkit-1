package cn.nukkit.entity.item;

import cn.nukkit.level.format.FullChunk;
import cn.nukkit.nbt.tag.CompoundTag;

public class EntityMinecartHopper extends EntityMinecartAbstract {

    public static final int NETWORK_ID = 96;

    public EntityMinecartHopper(FullChunk chunk, CompoundTag nbt) {
        super(chunk, nbt);
    }

    // TODO: 2016/12/18 inventory

    @Override
    public int getMineId() {
        return 5;
    }

    @Override
    public int getNetworkId() {
        return NETWORK_ID;
    }

}
