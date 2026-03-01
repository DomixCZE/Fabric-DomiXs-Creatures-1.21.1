package net.domixcze.domixscreatures.entity.client.termite;

public enum TermiteVariants {
    WORKER(0, "worker"),
    SOLDIER(1, "soldier"),
    QUEEN(2, "queen");

    private static final TermiteVariants[] BY_ID = values();
    private final int id;
    private final String name;

    TermiteVariants(int id, String name) {
        this.id = id;
        this.name = name;
    }

    public int getId() {
        return id;
    }

    public String asString() {
        return name;
    }

    public static TermiteVariants byId(int id) {
        return id >= 0 && id < BY_ID.length ? BY_ID[id] : WORKER;
    }

    public static TermiteVariants fromName(String name) {
        for (TermiteVariants variant : values()) {
            if (variant.name.equals(name)) {
                return variant;
            }
        }
        return WORKER;
    }
}