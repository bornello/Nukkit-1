package cn.nukkit.block;

import cn.nukkit.Server;
import cn.nukkit.level.Level;
import cn.nukkit.math.Vector3;
import cn.nukkit.utils.MainLogger;
import cn.nukkit.utils.Rail;

/**
 * Created by Snake1999 on 2016/1/11. 
 * Package cn.nukkit.block in project nukkit
 */
public class BlockRailPowered extends BlockRail {

    private final MainLogger debug = Server.getInstance().getLogger();

    public BlockRailPowered() {
        this(0);
        canBePowered = true;
    }

    public BlockRailPowered(int meta) {
        super(meta);
    }

    @Override
    public int getId() {
        return POWERED_RAIL;
    }

    @Override
    public String getName() {
        return "Powered Rail";
    }

    @Override
    public int onUpdate(int type) {
        super.onUpdate(type);
        if (type == Level.BLOCK_UPDATE_NORMAL) {
            computeRedstone();
            return Level.BLOCK_UPDATE_NORMAL;
        }
        return 0;
    }

    protected void computeRedstone() {
        boolean powered = level.isBlockIndirectlyGettingPowered(this) != 0;
        debug.debug("Powered Rail being compute");
        debug.debug("Powered Rail has Power Nearby: " + powered);

        powered = powered
                || checkSurrounding(this, true, 0)
                || checkSurrounding(this, false, 0);
        boolean hasUpdate = false;

        if (powered && (getDamage() & 0x8) == 0) {
            setActive(true);
            debug.debug("Powered Rail is Active");
            hasUpdate = true;
        } else if (!powered && (getDamage() & 0x8) != 0) {
            setActive(false);
            debug.debug("Powered Rail is In-Active");
            hasUpdate = true;
        }
        debug.debug("Powered Rail has Update: " + hasUpdate);

        if (hasUpdate) {
            int count = 1;

            level.updateAround(add(0, -1));
            switch (Rail.Orientation.byMetadata(getRealMeta())) {
                case ASCENDING_EAST:
                case ASCENDING_WEST:
                case ASCENDING_NORTH:
                case ASCENDING_SOUTH:
                    level.updateAround(add(0, 1));
                    count++;
                    break;
            }
            debug.debug("Rails that had updated: " + count);
        }
    }

    /**
     * Check the surrounding of the rail
     * 
     * @param pos       The rail position
     * @param powered   Are the rail has a power source nearby 
     * @param power     The count of the rail that had been counted
     * @return Boolean of the surrounding area. Where the powered rail on!
     */
    protected boolean checkSurrounding(Vector3 pos, boolean powered, int power) {
        if (power >= 8) {
            return false;
        } else {
            int metaLookup = getDamage() & 0x7;
            boolean hasPower = true;

            switch (metaLookup) {
                case 0:
                    if (powered) {
                        ++pos.z;
                    } else {
                        --pos.z;
                    }
                    break;

                case 1:
                    if (powered) {
                        --pos.x;
                    } else {
                        ++pos.x;
                    }
                    break;

                case 2:
                    if (powered) {
                        --pos.x;
                    } else {
                        ++pos.x;
                        ++pos.y;
                        hasPower = false;
                    }

                    metaLookup = 1;
                    break;

                case 3:
                    if (powered) {
                        --pos.x;
                        ++pos.y;
                        hasPower = false;
                    } else {
                        ++pos.x;
                    }

                    metaLookup = 1;
                    break;

                case 4:
                    if (powered) {
                        ++pos.z;
                    } else {
                        --pos.z;
                        ++pos.y;
                        hasPower = false;
                    }

                    metaLookup = 0;
                    break;

                case 5:
                    if (powered) {
                        ++pos.z;
                        ++pos.y;
                        hasPower = false;
                    } else {
                        --pos.z;
                    }

                    metaLookup = 0;
            }

            return flowIntoRail(pos, powered, power, metaLookup)
                    ? true : hasPower
                    && flowIntoRail(pos.add(0, -1), powered, power, metaLookup);
        }
    }

    protected boolean flowIntoRail(Vector3 pos, boolean powered, int power, int lockOver) {
        int metaPower = getDamage();
        int metaLookup = metaPower & 0x7;

        if (lockOver == 1
                && (metaLookup == 0 || metaLookup == 4 || metaLookup == 5)) {
            return false;
        }

        if (lockOver == 0
                && (metaLookup == 1 || metaLookup == 2 || metaLookup == 3)) {
            return false;
        }

        if ((metaPower & 0x8) != 0) {
            if (level.isBlockIndirectlyGettingPowered(pos) != 0) {
                return true;
            }
            
            // Check the next rail (could be powered somehow?)
            return checkSurrounding(pos, powered, power + 1);
        }

        return false;
    }
}
