package net.domixcze.domixscreatures.entity.client.owl;

public enum OwlVariants {
    BROWN(0, "brown"),
    GRAY(1, "gray"),
    WHITE(2, "white");

    private static final OwlVariants[] BY_ID = values();
    private final int id;
    private final String name;

    OwlVariants(int id, String name) {
        this.id = id;
        this.name = name;
    }

    public int getId() {
        return id;
    }

    public String asString() {
        return name;
    }

    public static OwlVariants byId(int id) {
        return id >= 0 && id < BY_ID.length ? BY_ID[id] : BROWN;
    }

    public static OwlVariants fromName(String name) {
        for (OwlVariants variant : values()) {
            if (variant.name.equals(name)) {
                return variant;
            }
        }
        return BROWN;
    }
}
