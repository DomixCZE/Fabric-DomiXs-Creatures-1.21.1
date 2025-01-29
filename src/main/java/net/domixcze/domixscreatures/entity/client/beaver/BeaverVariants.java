package net.domixcze.domixscreatures.entity.client.beaver;

public enum BeaverVariants {
    BROWN(0, "brown"),
    ALBINO(1, "albino");

    private static final BeaverVariants[] BY_ID = values();
    private final int id;
    private final String name;

    BeaverVariants(int id, String name) {
        this.id = id;
        this.name = name;
    }

    public int getId() {
        return id;
    }

    public String asString() {
        return name;
    }

    public static BeaverVariants byId(int id) {
        return id >= 0 && id < BY_ID.length ? BY_ID[id] : BROWN;
    }

    public static BeaverVariants fromName(String name) {
        for (BeaverVariants variant : values()) {
            if (variant.name.equals(name)) {
                return variant;
            }
        }
        return BROWN;
    }
}