package net.domixcze.domixscreatures.entity.client.deer;

public enum DeerAntlerSize {
    NONE(0, "none"),
    SMALL(1, "small"),
    MEDIUM(2, "medium"),
    LARGE(3, "large");

    private static final DeerAntlerSize[] BY_ID = values();
    private final int id;
    private final String name;

    DeerAntlerSize(int id, String name) {
        this.id = id;
        this.name = name;
    }

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public static DeerAntlerSize byId(int id) {
        return id >= 0 && id < BY_ID.length ? BY_ID[id] : NONE;
    }

    public static DeerAntlerSize fromName(String name) {
        for (DeerAntlerSize variant : values()) {
            if (variant.name.equals(name)) {
                return variant;
            }
        }
        return NONE;
    }
}
