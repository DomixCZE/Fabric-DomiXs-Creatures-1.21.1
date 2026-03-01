package net.domixcze.domixscreatures.entity.client.ant;

public enum AntVariants {
    WORKER(0, "worker"),
    SOLDIER(1, "soldier"),
    QUEEN(2, "queen");

    private static final AntVariants[] BY_ID = values();
    private final int id;
    private final String name;

    AntVariants(int id, String name) {
        this.id = id;
        this.name = name;
    }

    public int getId() {
        return id;
    }

    public String asString() {
        return name;
    }

    public static AntVariants byId(int id) {
        return id >= 0 && id < BY_ID.length ? BY_ID[id] : WORKER;
    }

    public static AntVariants fromName(String name) {
        for (AntVariants variant : values()) {
            if (variant.name.equals(name)) {
                return variant;
            }
        }
        return WORKER;
    }
}