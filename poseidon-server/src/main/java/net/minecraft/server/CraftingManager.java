package net.minecraft.server;

import org.jspecify.annotations.Nullable;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

public class CraftingManager {

    private static final CraftingManager a = new CraftingManager();
    private List<CraftingRecipe> b = new ArrayList<>();

    public static final CraftingManager getInstance() {
        return a;
    }

    private CraftingManager() {
        (new RecipesTools()).a(this);
        (new RecipesWeapons()).a(this);
        (new RecipeIngots()).a(this);
        (new RecipesFood()).a(this);
        (new RecipesCrafting()).a(this);
        (new RecipesArmor()).a(this);
        (new RecipesDyes()).a(this);
        this.registerShapedRecipe(new ItemStack(Item.PAPER, 3), "###", '#', Item.SUGAR_CANE);
        this.registerShapedRecipe(new ItemStack(Item.BOOK, 1), "#", "#", "#", '#', Item.PAPER);
        this.registerShapedRecipe(new ItemStack(Block.FENCE, 2), "###", "###", '#', Item.STICK);
        this.registerShapedRecipe(new ItemStack(Block.JUKEBOX, 1), "###", "#X#", "###", '#', Block.WOOD, 'X', Item.DIAMOND);
        this.registerShapedRecipe(new ItemStack(Block.NOTE_BLOCK, 1), "###", "#X#", "###", '#', Block.WOOD, 'X', Item.REDSTONE);
        this.registerShapedRecipe(new ItemStack(Block.BOOKSHELF, 1), "###", "XXX", "###", '#', Block.WOOD, 'X', Item.BOOK);
        this.registerShapedRecipe(new ItemStack(Block.SNOW_BLOCK, 1), "##", "##", '#', Item.SNOW_BALL);
        this.registerShapedRecipe(new ItemStack(Block.CLAY, 1), "##", "##", '#', Item.CLAY_BALL);
        this.registerShapedRecipe(new ItemStack(Block.BRICK, 1), "##", "##", '#', Item.CLAY_BRICK);
        this.registerShapedRecipe(new ItemStack(Block.GLOWSTONE, 1), "##", "##", '#', Item.GLOWSTONE_DUST);
        this.registerShapedRecipe(new ItemStack(Block.WOOL, 1), "##", "##", '#', Item.STRING);
        this.registerShapedRecipe(new ItemStack(Block.TNT, 1), "X#X", "#X#", "X#X", 'X', Item.SULPHUR, '#', Block.SAND);
        this.registerShapedRecipe(new ItemStack(Block.STEP, 3, 3), "###", '#', Block.COBBLESTONE);
        this.registerShapedRecipe(new ItemStack(Block.STEP, 3, 0), "###", '#', Block.STONE);
        this.registerShapedRecipe(new ItemStack(Block.STEP, 3, 1), "###", '#', Block.SANDSTONE);
        this.registerShapedRecipe(new ItemStack(Block.STEP, 3, 2), "###", '#', Block.WOOD);
        this.registerShapedRecipe(new ItemStack(Block.LADDER, 2), "# #", "###", "# #", '#', Item.STICK);
        this.registerShapedRecipe(new ItemStack(Item.WOOD_DOOR, 1), "##", "##", "##", '#', Block.WOOD);
        this.registerShapedRecipe(new ItemStack(Block.TRAP_DOOR, 2), "###", "###", '#', Block.WOOD);
        this.registerShapedRecipe(new ItemStack(Item.IRON_DOOR, 1), "##", "##", "##", '#', Item.IRON_INGOT);
        this.registerShapedRecipe(new ItemStack(Item.SIGN, 1), "###", "###", " X ", '#', Block.WOOD, 'X', Item.STICK);
        this.registerShapedRecipe(new ItemStack(Item.CAKE, 1), "AAA", "BEB", "CCC", 'A', Item.MILK_BUCKET, 'B', Item.SUGAR, 'C', Item.WHEAT, 'E', Item.EGG);
        this.registerShapedRecipe(new ItemStack(Item.SUGAR, 1), "#", '#', Item.SUGAR_CANE);
        this.registerShapedRecipe(new ItemStack(Block.WOOD, 4), "#", '#', Block.LOG);
        this.registerShapedRecipe(new ItemStack(Item.STICK, 4), "#", "#", '#', Block.WOOD);
        this.registerShapedRecipe(new ItemStack(Block.TORCH, 4), "X", "#", 'X', Item.COAL, '#', Item.STICK);
        this.registerShapedRecipe(new ItemStack(Block.TORCH, 4), "X", "#", 'X', new ItemStack(Item.COAL, 1, 1), '#', Item.STICK);
        this.registerShapedRecipe(new ItemStack(Item.BOWL, 4), "# #", " # ", '#', Block.WOOD);
        this.registerShapedRecipe(new ItemStack(Block.RAILS, 16), "X X", "X#X", "X X", 'X', Item.IRON_INGOT, '#', Item.STICK);
        this.registerShapedRecipe(new ItemStack(Block.GOLDEN_RAIL, 6), "X X", "X#X", "XRX", 'X', Item.GOLD_INGOT, 'R', Item.REDSTONE, '#', Item.STICK);
        this.registerShapedRecipe(new ItemStack(Block.DETECTOR_RAIL, 6), "X X", "X#X", "XRX", 'X', Item.IRON_INGOT, 'R', Item.REDSTONE, '#', Block.STONE_PLATE);
        this.registerShapedRecipe(new ItemStack(Item.MINECART, 1), "# #", "###", '#', Item.IRON_INGOT);
        this.registerShapedRecipe(new ItemStack(Block.JACK_O_LANTERN, 1), "A", "B", 'A', Block.PUMPKIN, 'B', Block.TORCH);
        this.registerShapedRecipe(new ItemStack(Item.STORAGE_MINECART, 1), "A", "B", 'A', Block.CHEST, 'B', Item.MINECART);
        this.registerShapedRecipe(new ItemStack(Item.POWERED_MINECART, 1), "A", "B", 'A', Block.FURNACE, 'B', Item.MINECART);
        this.registerShapedRecipe(new ItemStack(Item.BOAT, 1), "# #", "###", '#', Block.WOOD);
        this.registerShapedRecipe(new ItemStack(Item.BUCKET, 1), "# #", " # ", '#', Item.IRON_INGOT);
        this.registerShapedRecipe(new ItemStack(Item.FLINT_AND_STEEL, 1), "A ", " B", 'A', Item.IRON_INGOT, 'B', Item.FLINT);
        this.registerShapedRecipe(new ItemStack(Item.BREAD, 1), "###", '#', Item.WHEAT);
        this.registerShapedRecipe(new ItemStack(Block.WOOD_STAIRS, 4), "#  ", "## ", "###", '#', Block.WOOD);
        this.registerShapedRecipe(new ItemStack(Item.FISHING_ROD, 1), "  #", " #X", "# X", '#', Item.STICK, 'X', Item.STRING);
        this.registerShapedRecipe(new ItemStack(Block.COBBLESTONE_STAIRS, 4), "#  ", "## ", "###", '#', Block.COBBLESTONE);
        this.registerShapedRecipe(new ItemStack(Item.PAINTING, 1), "###", "#X#", "###", '#', Item.STICK, 'X', Block.WOOL);
        this.registerShapedRecipe(new ItemStack(Item.GOLDEN_APPLE, 1), "###", "#X#", "###", '#', Block.GOLD_BLOCK, 'X', Item.APPLE);
        this.registerShapedRecipe(new ItemStack(Block.LEVER, 1), "X", "#", '#', Block.COBBLESTONE, 'X', Item.STICK);
        this.registerShapedRecipe(new ItemStack(Block.REDSTONE_TORCH_ON, 1), "X", "#", '#', Item.STICK, 'X', Item.REDSTONE);
        this.registerShapedRecipe(new ItemStack(Item.DIODE, 1), "#X#", "III", '#', Block.REDSTONE_TORCH_ON, 'X', Item.REDSTONE, 'I', Block.STONE);
        this.registerShapedRecipe(new ItemStack(Item.WATCH, 1), " # ", "#X#", " # ", '#', Item.GOLD_INGOT, 'X', Item.REDSTONE);
        this.registerShapedRecipe(new ItemStack(Item.COMPASS, 1), " # ", "#X#", " # ", '#', Item.IRON_INGOT, 'X', Item.REDSTONE);
        this.registerShapedRecipe(new ItemStack(Item.MAP, 1), "###", "#X#", "###", '#', Item.PAPER, 'X', Item.COMPASS);
        this.registerShapedRecipe(new ItemStack(Block.STONE_BUTTON, 1), "#", "#", '#', Block.STONE);
        this.registerShapedRecipe(new ItemStack(Block.STONE_PLATE, 1), "##", '#', Block.STONE);
        this.registerShapedRecipe(new ItemStack(Block.WOOD_PLATE, 1), "##", '#', Block.WOOD);
        this.registerShapedRecipe(new ItemStack(Block.DISPENSER, 1), "###", "#X#", "#R#", '#', Block.COBBLESTONE, 'X', Item.BOW, 'R', Item.REDSTONE);
        this.registerShapedRecipe(new ItemStack(Block.PISTON, 1), "TTT", "#X#", "#R#", '#', Block.COBBLESTONE, 'X', Item.IRON_INGOT, 'R', Item.REDSTONE, 'T', Block.WOOD);
        this.registerShapedRecipe(new ItemStack(Block.PISTON_STICKY, 1), "S", "P", 'S', Item.SLIME_BALL, 'P', Block.PISTON);
        this.registerShapedRecipe(new ItemStack(Item.BED, 1), "###", "XXX", '#', Block.WOOL, 'X', Block.WOOD);
        Collections.sort(this.b, new RecipeSorter(this));
        System.out.println(this.b.size() + " recipes");
    }

    public void registerShapedRecipe(ItemStack itemstack, Object... aobject) { // CraftBukkit - default -> public
        String s = "";
        int i = 0;
        int j = 0;
        int k = 0;

        if (aobject[i] instanceof String[]) {
            String[] astring = (String[]) aobject[i++];

            for (int l = 0; l < astring.length; ++l) {
                String s1 = astring[l];

                ++k;
                j = s1.length();
                s = s + s1;
            }
        } else {
            while (aobject[i] instanceof String) {
                String s2 = (String) aobject[i++];

                ++k;
                j = s2.length();
                s = s + s2;
            }
        }

        HashMap<Character, ItemStack> hashmap;

        for (hashmap = new HashMap<>(); i < aobject.length; i += 2) {
            Character character = (Character) aobject[i];
            ItemStack itemstack1 = null;

            switch (aobject[i + 1]) {
                case Item item -> itemstack1 = new ItemStack(item);
                case Block block -> itemstack1 = new ItemStack(block, 1, -1);
                case ItemStack itemStack -> itemstack1 = itemStack;
                default -> {}
            }

            hashmap.put(character, itemstack1);
        }

        @Nullable ItemStack[] aitemstack = new ItemStack[j * k];

        for (int i1 = 0; i1 < j * k; ++i1) {
            char c0 = s.charAt(i1);

            if (hashmap.containsKey(c0)) {
                aitemstack[i1] = hashmap.get(c0).cloneItemStack();
            } else {
                aitemstack[i1] = null;
            }
        }

        this.b.add(new ShapedRecipes(j, k, aitemstack, itemstack));
    }

    public void registerShapelessRecipe(ItemStack itemstack, Object... aobject) { // CraftBukkit - default -> public
        ArrayList<ItemStack> arraylist = new ArrayList<>();
        Object[] aobject1 = aobject;
        int i = aobject.length;

        for (int j = 0; j < i; ++j) {
            Object object = aobject1[j];

            if (object instanceof ItemStack) {
                arraylist.add(((ItemStack) object).cloneItemStack());
            } else if (object instanceof Item) {
                arraylist.add(new ItemStack((Item) object));
            } else {
                if (!(object instanceof Block)) {
                    throw new RuntimeException("Invalid shapeless recipy!");
                }

                arraylist.add(new ItemStack((Block) object));
            }
        }

        this.b.add(new ShapelessRecipes(itemstack, arraylist));
    }

    public @Nullable ItemStack craft(InventoryCrafting inventorycrafting) {
        for (int i = 0; i < this.b.size(); ++i) {
            CraftingRecipe craftingrecipe = this.b.get(i);

            if (craftingrecipe.a(inventorycrafting)) {
                return craftingrecipe.b(inventorycrafting);
            }
        }

        return null;
    }

    public List<CraftingRecipe> b() {
        return this.b;
    }
}
