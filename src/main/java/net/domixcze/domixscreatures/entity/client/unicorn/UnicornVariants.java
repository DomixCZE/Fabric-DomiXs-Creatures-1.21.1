package net.domixcze.domixscreatures.entity.client.unicorn;


public enum UnicornVariants {
    PINK(0, "pink"),
    BLUE(1, "blue"),
    YELLOW(2, "yellow"),
    GREEN(3, "green");

    private static final UnicornVariants[] BY_ID = values();
    private final int id;
    private final String name;

    UnicornVariants(int id, String name) {
        this.id = id;
        this.name = name;
    }

    public int getId() {
        return id;
    }

    public String asString() {
        return name;
    }

    public static UnicornVariants byId(int id) {
        return id >= 0 && id < BY_ID.length ? BY_ID[id] : PINK;
    }

    public static UnicornVariants fromName(String name) {
        for (UnicornVariants variant : values()) {
            if (variant.name.equals(name)) {
                return variant;
            }
        }
        return PINK;
    }
}