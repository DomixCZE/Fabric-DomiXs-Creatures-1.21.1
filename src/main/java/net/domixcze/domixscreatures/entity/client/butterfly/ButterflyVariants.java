package net.domixcze.domixscreatures.entity.client.butterfly;

public enum ButterflyVariants {
    YELLOW(0, "black"),
    ORANGE(1, "orange"),
    WHITE(2, "white"),
    BLUE(3, "blue"),
    GREEN(4, "green"),
    BLACK(5, "black"),
    TERRACOTTA(6, "terracotta"),
    BROWN(7, "brown"),
    CRIMSON(8, "crimson"),
    PURPLE(9, "purple");

    private static final ButterflyVariants[] BY_ID = values();
    private final int id;
    private final String name;

    ButterflyVariants(int id, String name) {
        this.id = id;
        this.name = name;
    }

    public int getId() {
        return id;
    }

    public String asString() {
        return name;
    }

    public static ButterflyVariants byId(int id) {
        return id >= 0 && id < BY_ID.length ? BY_ID[id] : YELLOW;
    }

    public static ButterflyVariants fromName(String name) {
        for (ButterflyVariants variant : values()) {
            if (variant.name.equals(name)) {
                return variant;
            }
        }
        return YELLOW;
    }
}