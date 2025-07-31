package net.domixcze.domixscreatures.entity.client.betta_fish;

public enum BettaFishVariants {
    BLUE(0, "blue"),
    RED(1, "red"),
    WHITE(2, "white"),
    LIGHT_PINK(3, "light_pink"),
    BLUE_WHITE(4, "blue_white"),
    RED_WHITE(5, "red_white"),
    PINK_WHITE(6, "pink_white"),
    GREEN_YELLOW(7, "green_yellow"),
    YELLOW(8, "yellow"),
    RED_YELLOW(9, "red_yellow"),
    YELLOW_WHITE(10, "yellow_white"),
    BLACK(11, "black"),
    BLACK_RED(12, "black_red"),
    BLUE_RED(13, "blue_red");

    private static final BettaFishVariants[] BY_ID = values();
    private final int id;
    private final String name;

    BettaFishVariants(int id, String name) {
        this.id = id;
        this.name = name;
    }

    public int getId() {
        return id;
    }

    public String asString() {
        return name;
    }

    public static BettaFishVariants byId(int id) {
        return id >= 0 && id < BY_ID.length ? BY_ID[id] : BLUE;
    }

    public static BettaFishVariants fromName(String name) {
        for (BettaFishVariants variant : values()) {
            if (variant.name.equals(name)) {
                return variant;
            }
        }
        return BLUE;
    }
}