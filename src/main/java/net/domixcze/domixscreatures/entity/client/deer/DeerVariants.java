package net.domixcze.domixscreatures.entity.client.deer;

public enum DeerVariants {
    BROWN(0, "brown"),
    ALBINO(1, "albino");

    private static final DeerVariants[] BY_ID = values();
    private final int id;
    private final String name;

    DeerVariants(int id, String name) {
        this.id = id;
        this.name = name;
    }

    public int getId() {
        return id;
    }

    public String asString() {
        return name;
    }

    public static DeerVariants byId(int id) {
        return id >= 0 && id < BY_ID.length ? BY_ID[id] : BROWN;
    }

    public static DeerVariants fromName(String name) {
        for (DeerVariants variant : values()) {
            if (variant.name.equals(name)) {
                return variant;
            }
        }
        return BROWN;
    }
}