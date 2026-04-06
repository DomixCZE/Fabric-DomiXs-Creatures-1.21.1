package net.domixcze.domixscreatures.entity.client.pigeon;

public enum PigeonVariants {
    GRAY(0, "gray"),
    LIGHT_GRAY(1, "light_gray"),
    WHITE(2, "white");

    private static final PigeonVariants[] BY_ID = values();
    private final int id;
    private final String name;

    PigeonVariants(int id, String name) {
        this.id = id;
        this.name = name;
    }

    public int getId() {
        return id;
    }

    public String asString() {
        return name;
    }

    public static PigeonVariants byId(int id) {
        return id >= 0 && id < BY_ID.length ? BY_ID[id] : GRAY;
    }

    public static PigeonVariants fromName(String name) {
        for (PigeonVariants variant : values()) {
            if (variant.name.equals(name)) {
                return variant;
            }
        }
        return GRAY;
    }
}
