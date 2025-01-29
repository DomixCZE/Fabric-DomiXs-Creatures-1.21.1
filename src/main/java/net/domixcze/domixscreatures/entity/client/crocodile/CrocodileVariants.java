package net.domixcze.domixscreatures.entity.client.crocodile;

public enum CrocodileVariants {
    NORMAL(0, "normal"),
    ALBINO(1, "albino");

    private static final CrocodileVariants[] BY_ID = values();
    private final int id;
    private final String name;

    CrocodileVariants(int id, String name) {
        this.id = id;
        this.name = name;
    }

    public int getId() {
        return id;
    }

    public String asString() {
        return name;
    }

    public static CrocodileVariants byId(int id) {
        return id >= 0 && id < BY_ID.length ? BY_ID[id] : NORMAL;
    }

    public static CrocodileVariants fromName(String name) {
        for (CrocodileVariants variant : values()) {
            if (variant.name.equals(name)) {
                return variant;
            }
        }
        return NORMAL;
    }
}