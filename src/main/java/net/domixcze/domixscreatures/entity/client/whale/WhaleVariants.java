package net.domixcze.domixscreatures.entity.client.whale;

public enum WhaleVariants {
    NORMAL(0, "brown"),
    ALBINO(1, "albino");

    private static final WhaleVariants[] BY_ID = values();
    private final int id;
    private final String name;

    WhaleVariants(int id, String name) {
        this.id = id;
        this.name = name;
    }

    public int getId() {
        return id;
    }

    public String asString() {
        return name;
    }

    public static WhaleVariants byId(int id) {
        return id >= 0 && id < BY_ID.length ? BY_ID[id] : NORMAL;
    }

    public static WhaleVariants fromName(String name) {
        for (WhaleVariants variant : values()) {
            if (variant.name.equals(name)) {
                return variant;
            }
        }
        return NORMAL;
    }
}