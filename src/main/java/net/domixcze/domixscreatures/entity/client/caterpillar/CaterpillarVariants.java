package net.domixcze.domixscreatures.entity.client.caterpillar;

public enum CaterpillarVariants {
    GREEN(0, "green"),
    BROWN(1, "brown");

    private static final CaterpillarVariants[] BY_ID = values();
    private final int id;
    private final String name;

    CaterpillarVariants(int id, String name) {
        this.id = id;
        this.name = name;
    }

    public int getId() {
        return id;
    }

    public String asString() {
        return name;
    }

    public static CaterpillarVariants byId(int id) {
        return id >= 0 && id < BY_ID.length ? BY_ID[id] : GREEN;
    }

    public static CaterpillarVariants fromName(String name) {
        for (CaterpillarVariants variant : values()) {
            if (variant.name.equals(name)) {
                return variant;
            }
        }
        return GREEN;
    }
}