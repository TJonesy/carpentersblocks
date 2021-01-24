package com.carpentersblocks.util.registry;

import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraftforge.common.config.Configuration;
import com.carpentersblocks.item.ItemCarpentersBed;
import com.carpentersblocks.item.ItemCarpentersChisel;
import com.carpentersblocks.item.ItemCarpentersWrench;
import com.carpentersblocks.item.ItemCarpentersDoor;
import com.carpentersblocks.item.ItemCarpentersDoorPart;
import com.carpentersblocks.item.ItemCarpentersHammer;
import com.carpentersblocks.item.ItemCarpentersTile;
import cpw.mods.fml.common.event.FMLInitializationEvent;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import cpw.mods.fml.common.registry.GameRegistry;

public class ItemRegistry {

    public static Item itemCarpentersHammer;
    public static Item itemCarpentersChisel;
    public static Item itemCarpentersWrench;
    public static Item itemCarpentersDoor;
    public static Item itemCarpentersDoorPart;
    public static Item itemCarpentersBed;
    public static Item itemCarpentersTile;

    public static boolean enableHammer                  = true;
    public static boolean enableChisel                  = true;
    public static boolean enableWrench                  = true;
    public static boolean enableTile                    = true;
    public static int     itemCarpentersToolsUses       = 400;
    public static boolean itemCarpentersToolsDamageable = true;

    public static double itemHammerDamageChanceFromSlopes      = 0.75D;
    public static double itemHammerDamageChanceFromStairs      = 1.0D;
    public static double itemHammerDamageChanceFromCollapsible = 0.2D;

    private static int recipeQuantityTile = 12;

    /**
     * Registers item IDs.
     */
    public static void preInit(FMLPreInitializationEvent event, Configuration config)
    {
        enableHammer                  = config.get("items",            "Enable Hammer",                  enableHammer).getBoolean(enableHammer);
        enableChisel                  = config.get("items",            "Enable Chisel",                  enableChisel).getBoolean(enableChisel);
        enableWrench                  = config.get("items",            "Enable Wrench",                  enableWrench).getBoolean(enableWrench);
        enableTile                    = config.get("items",              "Enable Tile",                    enableTile).getBoolean(enableTile);
        itemCarpentersToolsUses       = config.get("items",        "Vanilla Tool Uses",       itemCarpentersToolsUses).getInt(itemCarpentersToolsUses);
        itemCarpentersToolsDamageable = config.get("items", "Vanilla Tools Damageable", itemCarpentersToolsDamageable).getBoolean(itemCarpentersToolsDamageable);

        recipeQuantityTile = config.get("recipe quantities", "Tile", recipeQuantityTile).getInt(recipeQuantityTile);

        itemHammerDamageChanceFromSlopes      = config.get("items",      "itemHammerDamageChanceFromSlopes",      itemHammerDamageChanceFromSlopes).getDouble(     itemHammerDamageChanceFromSlopes);
        itemHammerDamageChanceFromStairs      = config.get("items",      "itemHammerDamageChanceFromStairs",      itemHammerDamageChanceFromStairs).getDouble(     itemHammerDamageChanceFromStairs);
        itemHammerDamageChanceFromCollapsible = config.get("items", "itemHammerDamageChanceFromCollapsible", itemHammerDamageChanceFromCollapsible).getDouble(itemHammerDamageChanceFromCollapsible);

        registerItems();
    }

    public static void init(FMLInitializationEvent event)
    {
        registerRecipes();
    }

    private static void registerItems()
    {
        if (enableHammer) {
            itemCarpentersHammer = new ItemCarpentersHammer().setUnlocalizedName("itemCarpentersHammer");
            GameRegistry.registerItem(itemCarpentersHammer, "itemCarpentersHammer");
        }
        if (enableChisel) {
            itemCarpentersChisel = new ItemCarpentersChisel().setUnlocalizedName("itemCarpentersChisel");
            GameRegistry.registerItem(itemCarpentersChisel, "itemCarpentersChisel");
        }
        if (enableWrench) {
            itemCarpentersWrench = new ItemCarpentersWrench().setUnlocalizedName("itemCarpentersWrench");
            GameRegistry.registerItem(itemCarpentersWrench, "itemCarpentersWrench");
        }
        if (enableTile) {
            itemCarpentersTile = new ItemCarpentersTile().setUnlocalizedName("itemCarpentersTile");
            GameRegistry.registerItem(itemCarpentersTile, "itemCarpentersTile");
        }
        if (BlockRegistry.enableDoor) {
            itemCarpentersDoor = new ItemCarpentersDoor().setUnlocalizedName("itemCarpentersDoor");
            GameRegistry.registerItem(itemCarpentersDoor, "itemCarpentersDoor");
        }
        if (BlockRegistry.enableDoorPart) {
            itemCarpentersDoorPart = new ItemCarpentersDoorPart().setUnlocalizedName("itemCarpentersDoorPart");
            GameRegistry.registerItem(itemCarpentersDoorPart, "itemCarpentersDoorPart");
        }
        if (BlockRegistry.enableBed) {
            itemCarpentersBed = new ItemCarpentersBed().setUnlocalizedName("itemCarpentersBed");
            GameRegistry.registerItem(itemCarpentersBed, "itemCarpentersBed");
        }
    }

    private static void registerRecipes()
    {
        if (enableHammer) {
            GameRegistry.addRecipe(new ItemStack(itemCarpentersHammer, 1), new Object[] { "XX ", " YX", " Y ", 'X', Items.iron_ingot, 'Y', BlockRegistry.blockCarpentersBlock });
        }
        if (enableChisel) {
            GameRegistry.addRecipe(new ItemStack(itemCarpentersChisel, 1), new Object[] { "X", "Y", 'X', Items.iron_ingot, 'Y', BlockRegistry.blockCarpentersBlock });
        }
        if (enableWrench) {
            GameRegistry.addRecipe(new ItemStack(itemCarpentersWrench, 1),  new Object[] { "X X", " Y ", " Y ", 'X', Items.iron_ingot, 'Y', BlockRegistry.blockCarpentersBlock });
        }
        if (enableTile) {
            GameRegistry.addRecipe(new ItemStack(itemCarpentersTile, recipeQuantityTile), new Object[] { "XXX", "YYY", 'X', Blocks.hardened_clay, 'Y', BlockRegistry.blockCarpentersBlock });
        }
        if (BlockRegistry.enableDoor) {
            GameRegistry.addRecipe(new ItemStack(itemCarpentersDoor, BlockRegistry.recipeQuantityDoor), new Object[] { "XX", "XX", "XX", 'X', BlockRegistry.blockCarpentersBlock });
        }
        if (BlockRegistry.enableDoorPart) {
            GameRegistry.addRecipe(new ItemStack(itemCarpentersDoorPart, BlockRegistry.recipeQuantityDoorPart), new Object[] { "X", 'X', itemCarpentersDoor });

            if (BlockRegistry.enableDoor) {
                GameRegistry.addRecipe(new ItemStack(itemCarpentersDoor, 1), new Object[] { "X", "X", 'X', itemCarpentersDoorPart });
            }
        }
        if (BlockRegistry.enableBed) {
            GameRegistry.addRecipe(new ItemStack(itemCarpentersBed, BlockRegistry.recipeQuantityBed), new Object[] { "XXX", "YYY", 'X', Blocks.wool, 'Y', BlockRegistry.blockCarpentersBlock });
        }
    }

}
