package com.carpentersblocks.item;

import net.minecraft.block.Block;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.MathHelper;
import net.minecraft.world.World;
import com.carpentersblocks.CarpentersBlocks;
import com.carpentersblocks.data.HingePart;
import com.carpentersblocks.tileentity.TEBase;
import com.carpentersblocks.util.BlockProperties;
import com.carpentersblocks.util.registry.BlockRegistry;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public class ItemCarpentersDoorPart extends ItemBlock {

    public ItemCarpentersDoorPart()
    {
        setMaxStackSize(64);
        setCreativeTab(CarpentersBlocks.creativeTab);
    }

    @SideOnly(Side.CLIENT)
    @Override
    public void registerIcons(IIconRegister iconRegister)
    {
        itemIcon = iconRegister.registerIcon(CarpentersBlocks.MODID + ":" + "panel");
    }

    @Override
    /**
     * Callback for item usage. If the item does something special on right clicking, it will have one of these. Return
     * True if something happen and false if it don't. This is for ITEMS, not BLOCKS
     */
    public boolean onItemUse(ItemStack itemStack, EntityPlayer entityPlayer, World world, int x, int y, int z, int side, float hitX, float hitY, float hitZ)
    {
        if (side == 1) {

            ++y;

            if (
                    y < 256                                                                                    &&
                    entityPlayer.canPlayerEdit(x, y, z, side, itemStack)                                       &&
                    world.isAirBlock(x, y, z)                                                                  &&
                    (
                      world.doesBlockHaveSolidTopSurface(world, x, y - 1, z)                                   ||
                      world.getBlock(x, y - 1, z).equals(BlockRegistry.blockCarpentersDoorPart)
                    )                                                                                          &&
                    placeBlock(world, BlockRegistry.blockCarpentersDoorPart, entityPlayer, itemStack, x, y, z)
                    )
            {
                int facing = MathHelper.floor_double((entityPlayer.rotationYaw + 180.0F) * 4.0F / 360.0F - 0.5D) & 3;


                TEBase TE = (TEBase) world.getTileEntity(x, y, z);

                /* Match door type and rigidity with adjacent type if possible. */

                TEBase TE_YN = world.getBlock(x, y - 1, z).equals(BlockRegistry.blockCarpentersDoorPart) ? (TEBase) world.getTileEntity(x, y - 1, z) : null;
                TEBase TE_XN = world.getBlock(x - 1, y, z).equals(BlockRegistry.blockCarpentersDoorPart) ? (TEBase) world.getTileEntity(x - 1, y, z) : null;
                TEBase TE_XP = world.getBlock(x + 1, y, z).equals(BlockRegistry.blockCarpentersDoorPart) ? (TEBase) world.getTileEntity(x + 1, y, z) : null;
                TEBase TE_ZN = world.getBlock(x, y, z - 1).equals(BlockRegistry.blockCarpentersDoorPart) ? (TEBase) world.getTileEntity(x, y, z - 1) : null;
                TEBase TE_ZP = world.getBlock(x, y, z + 1).equals(BlockRegistry.blockCarpentersDoorPart) ? (TEBase) world.getTileEntity(x, y, z + 1) : null;

                if (TE_YN != null) {
                    HingePart.setType(TE, HingePart.getType(TE_YN));
                    HingePart.setRigidity(TE, HingePart.getRigidity(TE_YN));
                    facing = HingePart.getFacing(TE_YN);
                    HingePart.setHingeSide(TE, HingePart.getHinge(TE_YN));
                } else if (TE_XN != null) {
                    HingePart.setType(TE, HingePart.getType(TE_XN));
                    HingePart.setRigidity(TE, HingePart.getRigidity(TE_XN));
                } else if (TE_XP != null) {
                    HingePart.setType(TE, HingePart.getType(TE_XP));
                    HingePart.setRigidity(TE, HingePart.getRigidity(TE_XP));
                } else if (TE_ZN != null) {
                    HingePart.setType(TE, HingePart.getType(TE_ZN));
                    HingePart.setRigidity(TE, HingePart.getRigidity(TE_ZN));
                } else if (TE_ZP != null) {
                    HingePart.setType(TE, HingePart.getType(TE_ZP));
                    HingePart.setRigidity(TE, HingePart.getRigidity(TE_ZP));
                }
                HingePart.setFacing(TE, facing);
                if (TE_YN == null) {
                    HingePart.setHingeSide(TE, getHingePoint(TE, BlockRegistry.blockCarpentersDoorPart));
                }

                BlockProperties.playBlockSound(world, new ItemStack(BlockRegistry.blockCarpentersDoorPart), x, y, z, false);

                if (!entityPlayer.capabilities.isCreativeMode && --itemStack.stackSize <= 0) {
                    entityPlayer.inventory.setInventorySlotContents(entityPlayer.inventory.currentItem, (ItemStack)null);
                }

                return true;
            }

        }

        return false;
    }

    /**
     * Returns a hinge point allowing double-doors if a matching neighboring door is found.
     * It returns the default hinge point if no neighboring doors are found.
     */
    private int getHingePoint(TEBase TE, Block block)
    {
        int facing = HingePart.getFacing(TE);
        HingePart.getHinge(TE);
        HingePart.getState(TE);

        World world = TE.getWorldObj();

        TEBase TE_ZN = world.getBlock(TE.xCoord, TE.yCoord, TE.zCoord - 1).equals(block) ? (TEBase) world.getTileEntity(TE.xCoord, TE.yCoord, TE.zCoord - 1) : null;
        TEBase TE_ZP = world.getBlock(TE.xCoord, TE.yCoord, TE.zCoord + 1).equals(block) ? (TEBase) world.getTileEntity(TE.xCoord, TE.yCoord, TE.zCoord + 1) : null;
        TEBase TE_XN = world.getBlock(TE.xCoord - 1, TE.yCoord, TE.zCoord).equals(block) ? (TEBase) world.getTileEntity(TE.xCoord - 1, TE.yCoord, TE.zCoord) : null;
        TEBase TE_XP = world.getBlock(TE.xCoord + 1, TE.yCoord, TE.zCoord).equals(block) ? (TEBase) world.getTileEntity(TE.xCoord + 1, TE.yCoord, TE.zCoord) : null;

        switch (facing)
        {
            case HingePart.FACING_XN:

                if (TE_ZP != null) {
                    if (facing == HingePart.getFacing(TE_ZP) && HingePart.getHinge(TE_ZP) == HingePart.HINGE_LEFT) {
                        return HingePart.HINGE_RIGHT;
                    }
                }

                break;
            case HingePart.FACING_XP:

                if (TE_ZN != null) {
                    if (facing == HingePart.getFacing(TE_ZN) && HingePart.getHinge(TE_ZN) == HingePart.HINGE_LEFT) {
                        return HingePart.HINGE_RIGHT;
                    }
                }

                break;
            case HingePart.FACING_ZN:

                if (TE_XN != null) {
                    if (facing == HingePart.getFacing(TE_XN) && HingePart.getHinge(TE_XN) == HingePart.HINGE_LEFT) {
                        return HingePart.HINGE_RIGHT;
                    }
                }

                break;
            case HingePart.FACING_ZP:

                if (TE_XP != null) {
                    if (facing == HingePart.getFacing(TE_XP) && HingePart.getHinge(TE_XP) == HingePart.HINGE_LEFT) {
                        return HingePart.HINGE_RIGHT;
                    }
                }

                break;
        }

        return HingePart.HINGE_LEFT;
    }

}
