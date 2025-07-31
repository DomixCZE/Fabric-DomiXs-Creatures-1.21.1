package net.domixcze.domixscreatures.entity.client.hermit_crab;

public enum HermitCrabShapes {
    ROUND(0, "round"),
    POINTY(1, "pointy");

    private static final HermitCrabShapes[] BY_ID = values();
    private final int id;
    private final String name;

    HermitCrabShapes(int id, String name) {
        this.id = id;
        this.name = name;
    }

    public int getId() {
        return id;
    }

    public String asString() {
        return name;
    }

    public static HermitCrabShapes byId(int id) {
        return id >= 0 && id < BY_ID.length ? BY_ID[id] : ROUND;
    }

    public static HermitCrabShapes fromName(String name) {
        for (HermitCrabShapes shape : values()) {
            if (shape.name.equals(name)) {
                return shape;
            }
        }
        return ROUND;
    }
}