package net.domixcze.domixscreatures.entity.client.arapaima;

public enum ArapaimaVariants {
    GREEN_RED(0, "green_red"),
    BLACK(1, "black"),
    BLACK_RED(2, "black_red");

    private static final ArapaimaVariants[] BY_ID = values();
    private final int id;
    private final String name;

    ArapaimaVariants(int id, String name) {
        this.id = id;
        this.name = name;
    }

    public int getId() {
        return id;
    }

    public String asString() {
        return name;
    }

    public static ArapaimaVariants byId(int id) {
        return id >= 0 && id < BY_ID.length ? BY_ID[id] : GREEN_RED;
    }

    public static ArapaimaVariants fromName(String name) {
        for (ArapaimaVariants variant : values()) {
            if (variant.name.equals(name)) {
                return variant;
            }
        }
        return GREEN_RED;
    }
}