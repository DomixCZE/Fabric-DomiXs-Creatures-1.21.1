package net.domixcze.domixscreatures.entity.client.moose;

import net.domixcze.domixscreatures.entity.client.deer.DeerVariants;

public enum MooseVariants {
    BROWN(0, "brown"),
    ALBINO(1, "albino");

    private static final MooseVariants[] BY_ID = values();
    private final int id;
    private final String name;

    MooseVariants(int id, String name) {
        this.id = id;
        this.name = name;
    }

    public int getId() {
        return id;
    }

    public String asString() {
        return name;
    }

    public static MooseVariants byId(int id) {
        return id >= 0 && id < BY_ID.length ? BY_ID[id] : BROWN;
    }

    public static MooseVariants fromName(String name) {
        for (MooseVariants variant : values()) {
            if (variant.name.equals(name)) {
                return variant;
            }
        }
        return BROWN;
    }
}