package net.meetlounge.core.region;

public enum RegionFlag {

    PVP("pvp"),
    BUILD("build"),
    BREAK("break"),
    MOB_SPAWN("mob-spawn"),
    CROP_TRAMPLE("crop-trample"),
    FALL_DAMAGE("fall-damage");

    private final String id;

    RegionFlag(String id) {
        this.id = id;
    }

    public String id() {
        return id;
    }

    public static RegionFlag fromId(String id) {
        for (RegionFlag flag : values()) {
            if (flag.id.equalsIgnoreCase(id)) {
                return flag;
            }
        }
        return null;
    }
}