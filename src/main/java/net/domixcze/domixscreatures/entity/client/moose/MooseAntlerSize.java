package net.domixcze.domixscreatures.entity.client.moose;

public enum MooseAntlerSize {
    NONE(0, "none"),
    MEDIUM(1, "medium");

    private static final MooseAntlerSize[] BY_ID = values();
    private final int id;
    private final String name;

    MooseAntlerSize(int id, String name) {
        this.id = id;
        this.name = name;
    }

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public static MooseAntlerSize byId(int id) {
        return id >= 0 && id < BY_ID.length ? BY_ID[id] : NONE;
    }

    public static MooseAntlerSize fromName(String name) {
        for (MooseAntlerSize variant : values()) {
            if (variant.name.equals(name)) {
                return variant;
            }
        }
        return NONE;
    }
}