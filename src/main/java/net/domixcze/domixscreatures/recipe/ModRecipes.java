package net.domixcze.domixscreatures.recipe;

import net.domixcze.domixscreatures.DomiXsCreatures;
import net.minecraft.recipe.RecipeSerializer;
import net.minecraft.recipe.RecipeType;
import net.minecraft.recipe.SpecialRecipeSerializer;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.util.Identifier;

public class ModRecipes {
    public static final RecipeSerializer<CoatedQuillRecipe> COATED_QUILL_RECIPE_SERIALIZER = Registry.register(
            Registries.RECIPE_SERIALIZER,
            Identifier.of(DomiXsCreatures.MOD_ID, "coated_quill"),
            new SpecialRecipeSerializer<>(CoatedQuillRecipe::new)
    );

    public static final RecipeType<CoatedQuillRecipe> COATED_QUILL_RECIPE_TYPE = Registry.register(
            Registries.RECIPE_TYPE,
            Identifier.of(DomiXsCreatures.MOD_ID, "coated_quill"),
            new RecipeType<CoatedQuillRecipe>() {
                @Override
                public String toString() {
                    return "coated_quill";
                }
            });

    public static void registerRecipes() {
        DomiXsCreatures.LOGGER.info("Registering Custom Recipes for " + DomiXsCreatures.MOD_ID);
    }
}
