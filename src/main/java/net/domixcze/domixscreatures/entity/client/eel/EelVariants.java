package net.domixcze.domixscreatures.entity.client.eel;

public enum EelVariants {
    GREEN(0, "green"),
    YELLOW(1, "yellow"),
    ABYSS(2, "abyss");

    private static final EelVariants[] BY_ID = values();
    private final int id;
    private final String name;

    EelVariants(int id, String name) {
        this.id = id;
        this.name = name;
    }

    public int getId() {
        return id;
    }

    public String asString() {
        return name;
    }

    public static EelVariants byId(int id) {
        return id >= 0 && id < BY_ID.length ? BY_ID[id] : GREEN;
    }

    public static EelVariants fromName(String name) {
        for (EelVariants variant : values()) {
            if (variant.name.equals(name)) {
                return variant;
            }
        }
        return GREEN;
    }
}