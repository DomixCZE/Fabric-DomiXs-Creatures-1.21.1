package net.domixcze.domixscreatures.entity.client.mole;

public enum MoleVariants {
    BLACK(0, "black"),
    ALBINO(1, "albino");

    private static final MoleVariants[] BY_ID = values();
    private final int id;
    private final String name;

    MoleVariants(int id, String name) {
        this.id = id;
        this.name = name;
    }

    public int getId() {
        return id;
    }

    public String asString() {
        return name;
    }

    public static MoleVariants byId(int id) {
        return id >= 0 && id < BY_ID.length ? BY_ID[id] : BLACK;
    }

    public static MoleVariants fromName(String name) {
        for (MoleVariants variant : values()) {
            if (variant.name.equals(name)) {
                return variant;
            }
        }
        return BLACK;
    }
}