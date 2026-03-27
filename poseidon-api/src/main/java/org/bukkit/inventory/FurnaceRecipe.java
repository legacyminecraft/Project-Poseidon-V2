package org.bukkit.inventory;

import org.bukkit.Material;
import org.bukkit.material.MaterialData;
import org.jspecify.annotations.Nullable;

/**
 * Represents a smelting recipe.
 */
public class FurnaceRecipe implements Recipe {
    private ItemStack output;
    private @Nullable MaterialData ingredient;

    /**
     * Create a furnace recipe to craft the specified ItemStack.
     * @param result The item you want the recipe to create.
     * @param source The input material.
     */
    public FurnaceRecipe(ItemStack result, Material source) {
        this(result, source.getNewData((byte) 0));
        if (this.ingredient == null) {
            setInput(new MaterialData(source));
        }
    }

    /**
     * Create a furnace recipe to craft the specified ItemStack.
     * @param result The item you want the recipe to create.
     * @param source The input material.
     */
    public FurnaceRecipe(ItemStack result, @Nullable MaterialData source) {
        this.output = result;
        this.ingredient = source;
    }

    /**
     * Sets the input of this furnace recipe.
     * @param input The input material.
     * @return The changed recipe, so you can chain calls.
     */
    public FurnaceRecipe setInput(@Nullable MaterialData input) {
        this.ingredient = input;
        return this;
    }

    /**
     * Sets the input of this furnace recipe.
     * @param input The input material.
     * @return The changed recipe, so you can chain calls.
     */
    public FurnaceRecipe setInput(Material input) {
        setInput(input.getNewData((byte) 0));
        if (this.ingredient == null) {
            setInput(new MaterialData(input));
        }
        return this;
    }

    /**
     * Get the input material.
     * @return The input material.
     */
    public @Nullable MaterialData getInput() {
        return ingredient;
    }

    /**
     * Get the result of this recipe.
     * @return The resulting stack.
     */
    public ItemStack getResult() {
        return output;
    }
}
