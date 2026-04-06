package net.domixcze.domixscreatures.recipe;

import net.domixcze.domixscreatures.DomiXsCreatures;
import net.domixcze.domixscreatures.component.ModDataComponents;
import net.domixcze.domixscreatures.item.ModItems;
import net.minecraft.item.ItemStack;
import net.minecraft.recipe.RecipeSerializer;
import net.minecraft.recipe.SpecialCraftingRecipe;
import net.minecraft.recipe.book.CraftingRecipeCategory;
import net.minecraft.recipe.input.CraftingRecipeInput;
import net.minecraft.registry.RegistryWrapper;
import net.minecraft.util.Identifier;
import net.minecraft.world.World;

public class CoatedQuillRecipe extends SpecialCraftingRecipe {

    public CoatedQuillRecipe(CraftingRecipeCategory category) {
        super(category);
    }

    @Override
    public boolean matches(CraftingRecipeInput input, World world) {
        if (input.getWidth() != 3 || input.getHeight() != 3) return false;

        for (int i = 0; i < input.getHeight(); i++) {
            for (int j = 0; j < input.getWidth(); j++) {
                ItemStack stack = input.getStackInSlot(j, i);
                if (stack.isEmpty()) return false;
                if (i == 1 && j == 1) { // center slot is venom
                    if (!stack.isOf(ModItems.KOMODO_DRAGON_VENOM_BOTTLE)) return false;
                } else { // all others must be quills
                    if (!stack.isOf(ModItems.QUILL)) return false;
                }
            }
        }

        return true;
    }

    @Override
    public ItemStack craft(CraftingRecipeInput input, RegistryWrapper.WrapperLookup wrapper) {
        ItemStack result = new ItemStack(ModItems.QUILL, 8);

        result.set(ModDataComponents.COATED_QUILL,
                new ModDataComponents.CoatedQuillComponent(
                        Identifier.of(DomiXsCreatures.MOD_ID, "bleeding"),
                        350,
                        0
                )
        );

        return result;
    }

    @Override
    public boolean fits(int width, int height) {
        return width >= 3 && height >= 3;
    }

    @Override
    public RecipeSerializer<?> getSerializer() {
        return ModRecipes.COATED_QUILL_RECIPE_SERIALIZER;
    }
}
