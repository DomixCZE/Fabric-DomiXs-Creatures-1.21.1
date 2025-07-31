package net.domixcze.domixscreatures.entity.client.hermit_crab;

public enum HermitCrabVariants {
    PINK(0, "pink"),
    BLUE(1, "blue");

    private static final HermitCrabVariants[] BY_ID = values();
    private final int id;
    private final String name;

    HermitCrabVariants(int id, String name) {
        this.id = id;
        this.name = name;
    }

    public int getId() {
        return id;
    }

    public String asString() {
        return name;
    }

    public static HermitCrabVariants byId(int id) {
        return id >= 0 && id < BY_ID.length ? BY_ID[id] : PINK;
    }

    public static HermitCrabVariants fromName(String name) {
        for (HermitCrabVariants color : values()) {
            if (color.name.equals(name)) {
                return color;
            }
        }
        return PINK;
    }
}