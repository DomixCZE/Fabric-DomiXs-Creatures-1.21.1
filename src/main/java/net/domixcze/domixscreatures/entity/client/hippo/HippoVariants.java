package net.domixcze.domixscreatures.entity.client.hippo;

public enum HippoVariants {
    NORMAL(0, "normal"),
    ALBINO(1, "albino");

    private static final HippoVariants[] BY_ID = values();
    private final int id;
    private final String name;

    HippoVariants(int id, String name) {
        this.id = id;
        this.name = name;
    }

    public int getId() {
        return id;
    }

    public String asString() {
        return name;
    }

    public static HippoVariants byId(int id) {
        return id >= 0 && id < BY_ID.length ? BY_ID[id] : NORMAL;
    }

    public static HippoVariants fromName(String name) {
        for (HippoVariants variant : values()) {
            if (variant.name.equals(name)) {
                return variant;
            }
        }
        return NORMAL;
    }
}