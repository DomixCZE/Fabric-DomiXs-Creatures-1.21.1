package net.domixcze.domixscreatures.entity.client.fire_salamander;

public enum FireSalamanderVariants {
    MAGMA(0, "magma"),
    OBSIDIAN(1, "obsidian");

    private static final FireSalamanderVariants[] BY_ID = values();
    private final int id;
    private final String name;

    FireSalamanderVariants(int id, String name) {
        this.id = id;
        this.name = name;
    }

    public int getId() {
        return id;
    }

    public String asString() {
        return name;
    }

    public static FireSalamanderVariants byId(int id) {
        return id >= 0 && id < BY_ID.length ? BY_ID[id] : MAGMA;
    }

    public static FireSalamanderVariants fromName(String name) {
        for (FireSalamanderVariants variant : values()) {
            if (variant.name.equals(name)) {
                return variant;
            }
        }
        return MAGMA;
    }
}