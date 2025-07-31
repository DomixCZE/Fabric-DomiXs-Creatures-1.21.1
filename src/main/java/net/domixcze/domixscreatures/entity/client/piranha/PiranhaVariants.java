package net.domixcze.domixscreatures.entity.client.piranha;

public enum PiranhaVariants {
    BLUE(0, "blue"),
    GREEN(1, "green"),
    BLACK(2, "black");

    private static final PiranhaVariants[] BY_ID = values();
    private final int id;
    private final String name;

    PiranhaVariants(int id, String name) {
        this.id = id;
        this.name = name;
    }

    public int getId() {
        return id;
    }

    public String asString() {
        return name;
    }

    public static PiranhaVariants byId(int id) {
        return id >= 0 && id < BY_ID.length ? BY_ID[id] : BLUE;
    }

    public static PiranhaVariants fromName(String name) {
        for (PiranhaVariants variant : values()) {
            if (variant.name.equals(name)) {
                return variant;
            }
        }
        return BLUE;
    }
}