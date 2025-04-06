package net.domixcze.domixscreatures.entity.client.mud_golem;

public enum MudGolemVariants {
    NORMAL(0, "normal"),
    LOL(1, "lol");

    private static final MudGolemVariants[] BY_ID = values();
    private final int id;
    private final String name;

    MudGolemVariants(int id, String name) {
        this.id = id;
        this.name = name;
    }

    public int getId() {
        return id;
    }

    public String asString() {
        return name;
    }

    public static MudGolemVariants byId(int id) {
        return id >= 0 && id < BY_ID.length ? BY_ID[id] : NORMAL;
    }

    public static MudGolemVariants fromName(String name) {
        for (MudGolemVariants variant : values()) {
            if (variant.name.equals(name)) {
                return variant;
            }
        }
        return NORMAL;
    }
}