package net.domixcze.domixscreatures.entity.client.freshwater_stingray;

public enum FreshwaterStingrayVariants {
    YELLOW(0, "yellow"),
    BLACK(1, "black");

    private static final FreshwaterStingrayVariants[] BY_ID = values();
    private final int id;
    private final String name;

    FreshwaterStingrayVariants(int id, String name) {
        this.id = id;
        this.name = name;
    }

    public int getId() {
        return id;
    }

    public String asString() {
        return name;
    }

    public static FreshwaterStingrayVariants byId(int id) {
        return id >= 0 && id < BY_ID.length ? BY_ID[id] : YELLOW;
    }

    public static FreshwaterStingrayVariants fromName(String name) {
        for (FreshwaterStingrayVariants variant : values()) {
            if (variant.name.equals(name)) {
                return variant;
            }
        }
        return YELLOW;
    }
}