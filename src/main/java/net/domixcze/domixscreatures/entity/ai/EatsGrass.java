package net.domixcze.domixscreatures.entity.ai;

public interface EatsGrass {
    boolean isEating();
    void setEating(boolean eating);
    default boolean canEatGrass() {
        return true;
    }
    default void onEatGrass() {
    }
}