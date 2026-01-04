package net.domixcze.domixscreatures.entity.client.rat;

public enum RatVariants {
    BLACK(0, "black"),
    GRAY(1, "gray"),
    WHITE(2, "white"),
    DARK_BROWN(3, "dark_brown"),
    BROWN(4, "brown"),
    BROWN_WHITE(5, "brown_white"),
    GRAY_WHITE(6, "gray_white"),
    WHITE_BLACK(7, "white_black"),
    WHITE_BROWN(8, "white_brown"),
    WHITE_BLACK_SPOTS(9, "white_black_spots");

    private static final RatVariants[] BY_ID = values();
    private final int id;
    private final String name;

    RatVariants(int id, String name) {
        this.id = id;
        this.name = name;
    }

    public int getId() {
        return id;
    }

    public String asString() {
        return name;
    }

    public static RatVariants byId(int id) {
        return id >= 0 && id < BY_ID.length ? BY_ID[id] : BLACK;
    }

    public static RatVariants fromName(String name) {
        for (RatVariants variant : values()) {
            if (variant.name.equals(name)) {
                return variant;
            }
        }
        return BLACK;
    }
}