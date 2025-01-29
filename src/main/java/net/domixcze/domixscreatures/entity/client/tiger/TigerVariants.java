package net.domixcze.domixscreatures.entity.client.tiger;

public enum TigerVariants {
    NORMAL(0, "normal"),
    ALBINO(1, "albino"),
    DREAM(2, "dream"),
    ALBINO_DREAM(3, "albino_dream");

    private static final TigerVariants[] BY_ID = values();
    private final int id;
    private final String name;

    TigerVariants(int id, String name) {
        this.id = id;
        this.name = name;
    }

    public int getId() {
        return id;
    }

    public String asString() {
        return name;
    }

    public static TigerVariants byId(int id) {
        return id >= 0 && id < BY_ID.length ? BY_ID[id] : NORMAL;
    }

    public static TigerVariants fromName(String name) {
        for (TigerVariants variant : values()) {
            if (variant.name.equals(name)) {
                return variant;
            }
        }
        return NORMAL;
    }
}