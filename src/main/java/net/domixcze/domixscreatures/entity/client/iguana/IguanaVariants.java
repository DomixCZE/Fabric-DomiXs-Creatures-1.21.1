package net.domixcze.domixscreatures.entity.client.iguana;

public enum IguanaVariants {
    GREEN(0, "green"),
    ALBINO(1, "albino"),
    MELANISTIC(2, "melanistic"),
    BLUE(3, "blue");

    private static final IguanaVariants[] BY_ID = values();
    private final int id;
    private final String name;

    IguanaVariants(int id, String name) {
        this.id = id;
        this.name = name;
    }

    public int getId() {
        return id;
    }

    public String asString() {
        return name;
    }

    public static IguanaVariants byId(int id) {
        return id >= 0 && id < BY_ID.length ? BY_ID[id] : GREEN;
    }

    public static IguanaVariants fromName(String name) {
        for (IguanaVariants variant : values()) {
            if (variant.name.equals(name)) {
                return variant;
            }
        }
        return GREEN;
    }
}