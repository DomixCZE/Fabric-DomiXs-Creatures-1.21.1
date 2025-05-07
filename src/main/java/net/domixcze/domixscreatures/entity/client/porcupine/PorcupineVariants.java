package net.domixcze.domixscreatures.entity.client.porcupine;

public enum PorcupineVariants {
    NORMAL(0, "normal"),
    ALBINO(1, "albino");

    private static final PorcupineVariants[] BY_ID = values();
    private final int id;
    private final String name;

    PorcupineVariants(int id, String name) {
        this.id = id;
        this.name = name;
    }

    public int getId() {
        return id;
    }

    public String asString() {
        return name;
    }

    public static PorcupineVariants byId(int id) {
        return id >= 0 && id < BY_ID.length ? BY_ID[id] : NORMAL;
    }

    public static PorcupineVariants fromName(String name) {
        for (PorcupineVariants variant : values()) {
            if (variant.name.equals(name)) {
                return variant;
            }
        }
        return NORMAL;
    }
}