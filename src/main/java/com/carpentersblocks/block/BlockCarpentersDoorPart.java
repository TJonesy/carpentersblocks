package com.carpentersblocks.block;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.common.util.ForgeDirection;
import com.carpentersblocks.CarpentersBlocks;
import com.carpentersblocks.data.HingePart;
import com.carpentersblocks.item.ItemCarpentersDoorPart;
import com.carpentersblocks.tileentity.TEBase;
import com.carpentersblocks.util.handler.ChatHandler;
import com.carpentersblocks.util.registry.BlockRegistry;
import com.carpentersblocks.util.registry.IconRegistry;
import com.carpentersblocks.util.registry.ItemRegistry;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public class BlockCarpentersDoorPart extends BlockCoverable {

    public final static String type[] = {"glassTall", "panel", "screenTall", "french", "hidden"};

    public BlockCarpentersDoorPart(Material material) {
        super(material);
    }

    /**
     * Determines whether the bottom-most hinge requires a solid block underneath it.
     *
     * @return the result
     */
    protected boolean requiresFoundation() {
        return false;
    }

    @Override
    /**
     * Alters texture connections
     */
    protected boolean onWrenchClick(TEBase TE, EntityPlayer entityPlayer, float hitX, float hitY, float hitZ) {

        System.out.println("Wrench hit: " + hitY);

        if (hitY > 0.5) {
            HingePart.setConnection(TE, HingePart.CONNECT_TOP);
        } else {
            HingePart.setConnection(TE, HingePart.CONNECT_BOTTOM);
        }
        return true;
    }

    @Override
    /**
     * Alters hinge side.
     */
    protected boolean onHammerLeftClick(TEBase TE, EntityPlayer entityPlayer) {
        int hinge = HingePart.getHinge(TE);

        setHingeSide(TE, hinge == HingePart.HINGE_LEFT ? HingePart.HINGE_RIGHT : HingePart.HINGE_LEFT);

        return true;
    }


    @Override
    /**
     * Alters hinge type and redstone behavior.
     */
    protected boolean onHammerRightClick(TEBase TE, EntityPlayer entityPlayer) {
        if (!entityPlayer.isSneaking()) {

            int temp = HingePart.getType(TE);

            if (++temp >= type.length) {
                temp = 0;
            }

            setHingeType(TE, temp);
            super.onHammerRightClick(TE, entityPlayer);
            return true;

        }
        if (entityPlayer.isSneaking()) {

            int rigidity = HingePart.getRigidity(TE) == HingePart.HINGED_NONRIGID ? HingePart.HINGED_RIGID : HingePart.HINGED_NONRIGID;

            setHingeRigidity(TE, rigidity);

            switch (rigidity) {
                case HingePart.HINGED_NONRIGID:
                    ChatHandler.sendMessageToPlayer("message.activation_wood.name", entityPlayer);
                    break;
                case HingePart.HINGED_RIGID:
                    ChatHandler.sendMessageToPlayer("message.activation_iron.name", entityPlayer);
            }

            return true;

        }

        return false;
    }

    @Override
    public boolean onBlockActivated(World world, int x, int y, int z, EntityPlayer entityPlayer, int side, float hitX, float hitY, float hitZ) {
        ItemStack itemStack = entityPlayer.getCurrentEquippedItem();
        if (
                itemStack != null &&
                itemStack.getItem() instanceof ItemCarpentersDoorPart && entityPlayer.isSneaking() &&
                ((ItemCarpentersDoorPart)itemStack.getItem()).onItemUse(itemStack, entityPlayer, world, x, y, z, side, hitX, hitY, hitZ)
        ) {
            return true;
        }
        return super.onBlockActivated(world, x, y, z, entityPlayer, side, hitX, hitY, hitZ);
    }

    @Override
    /**
     * Opens or closes hinge on right click.
     */
    protected void postOnBlockActivated(TEBase TE, EntityPlayer entityPlayer, int side, float hitX, float hitY, float hitZ, ActionResult actionResult) {
        if (!activationRequiresRedstone(TE)) {
            setHingeState(TE, HingePart.getState(TE) == HingePart.STATE_OPEN ? HingePart.STATE_CLOSED : HingePart.STATE_OPEN);
            actionResult.setAltered().setNoSound();
        }
    }

    /**
     * Returns whether hinge requires redstone activation.
     */
    private boolean activationRequiresRedstone(TEBase TE) {
        return HingePart.getRigidity(TE) == HingePart.HINGED_RIGID;
    }

    /**
     * Returns a list of hinge tile entities that make up either a single hinge or two connected double hinges.
     */
    private List<TEBase> getHingePieces(TEBase TE) {
        List<TEBase> list = new ArrayList<TEBase>();
        getHingePieces(TE, list);
        return list;
    }

    private void getHingePieces(TEBase TE, List<TEBase> list) {
        World world = TE.getWorldObj();

        int facing = HingePart.getFacing(TE);
        int hinge = HingePart.getHinge(TE);

        /* Add source hinge pieces */

        if(list.contains(TE))
            return;

        list.add(TE);



        /* Begin searching for and adding other neighboring pieces. */

        TEBase TE_YN = getTileEntity(world, TE.xCoord, TE.yCoord - 1, TE.zCoord);
        TEBase TE_YP = getTileEntity(world, TE.xCoord, TE.yCoord + 1, TE.zCoord);

        if (TE_YN != null && HingePart.getFacing(TE_YN) == facing && HingePart.getHinge(TE_YN) == hinge) {
            getHingePieces(TE_YN, list);
        }
        if (TE_YP != null && HingePart.getFacing(TE_YP) == facing && HingePart.getHinge(TE_YP) == hinge) {
            getHingePieces(TE_YP, list);
        }

        TEBase TE_ZN = getTileEntity(world, TE.xCoord, TE.yCoord, TE.zCoord - 1);
        TEBase TE_ZP = getTileEntity(world, TE.xCoord, TE.yCoord, TE.zCoord + 1);
        TEBase TE_XN = getTileEntity(world, TE.xCoord - 1, TE.yCoord, TE.zCoord);
        TEBase TE_XP = getTileEntity(world, TE.xCoord + 1, TE.yCoord, TE.zCoord);

        switch (facing) {
            case HingePart.FACING_XN:

                if (TE_ZN != null) {
                    if (facing == HingePart.getFacing(TE_ZN) && hinge == HingePart.HINGE_LEFT && HingePart.getHinge(TE_ZN) == HingePart.HINGE_RIGHT) {
                        getHingePieces(TE_ZN, list);
                    }
                }
                if (TE_ZP != null) {
                    if (facing == HingePart.getFacing(TE_ZP) && hinge == HingePart.HINGE_RIGHT && HingePart.getHinge(TE_ZP) == HingePart.HINGE_LEFT) {
                        getHingePieces(TE_ZP, list);
                    }
                }
                break;

            case HingePart.FACING_XP:

                if (TE_ZN != null) {
                    if (facing == HingePart.getFacing(TE_ZN) && hinge == HingePart.HINGE_RIGHT && HingePart.getHinge(TE_ZN) == HingePart.HINGE_LEFT) {
                        getHingePieces(TE_ZN, list);
                    }
                }
                if (TE_ZP != null) {
                    if (facing == HingePart.getFacing(TE_ZP) && hinge == HingePart.HINGE_LEFT && HingePart.getHinge(TE_ZP) == HingePart.HINGE_RIGHT) {
                        getHingePieces(TE_ZP, list);
                    }
                }
                break;

            case HingePart.FACING_ZN: {

                if (TE_XN != null) {
                    if (facing == HingePart.getFacing(TE_XN) && hinge == HingePart.HINGE_RIGHT && HingePart.getHinge(TE_XN) == HingePart.HINGE_LEFT) {
                        getHingePieces(TE_XN, list);
                    }
                }
                if (TE_XP != null) {
                    if (facing == HingePart.getFacing(TE_XP) && hinge == HingePart.HINGE_LEFT && HingePart.getHinge(TE_XP) == HingePart.HINGE_RIGHT) {
                        getHingePieces(TE_XP, list);
                    }
                }
                break;
            }
            case HingePart.FACING_ZP:

                if (TE_XN != null) {
                    if (facing == HingePart.getFacing(TE_XN) && hinge == HingePart.HINGE_LEFT && HingePart.getHinge(TE_XN) == HingePart.HINGE_RIGHT) {
                        getHingePieces(TE_XN, list);
                    }
                }
                if (TE_XP != null) {
                    if (facing == HingePart.getFacing(TE_XP) && hinge == HingePart.HINGE_RIGHT && HingePart.getHinge(TE_XP) == HingePart.HINGE_LEFT) {
                        getHingePieces(TE_XP, list);
                    }
                }
                break;

        }
    }

    @Override
    @SideOnly(Side.CLIENT)
    /**
     * Returns the bounding box of the wired rectangular prism to render.
     */
    public AxisAlignedBB getSelectedBoundingBoxFromPool(World world, int x, int y, int z) {
        if (world.getBlock(x, y, z).equals(this)) {
            setBlockBoundsBasedOnState(world, x, y, z);
        }

        return super.getSelectedBoundingBoxFromPool(world, x, y, z);
    }

    @Override
    /**
     * Returns a bounding box from the pool of bounding boxes (this means this box can change after the pool has been
     * cleared to be reused)
     */
    public AxisAlignedBB getCollisionBoundingBoxFromPool(World world, int x, int y, int z) {
        if (world.getBlock(x, y, z).equals(this)) {
            setBlockBoundsBasedOnState(world, x, y, z);
        }

        return super.getCollisionBoundingBoxFromPool(world, x, y, z);
    }

    @Override
    /**
     * Updates the blocks bounds based on its current state. Args: world, x, y, z
     */
    public void setBlockBoundsBasedOnState(IBlockAccess blockAccess, int x, int y, int z) {
        TEBase TE = getTileEntity(blockAccess, x, y, z);

        if (TE != null) {

            int facing = HingePart.getFacing(TE);
            int hinge = HingePart.getHinge(TE);
            boolean isOpen = HingePart.getState(TE) == HingePart.STATE_OPEN;

            float x_low = 0.0F;
            float z_low = 0.0F;
            float x_high = 1.0F;
            float z_high = 1.0F;

            switch (facing) {
                case HingePart.FACING_XN:
                    if (!isOpen) {
                        x_low = 0.8125F;
                    } else if (hinge == HingePart.HINGE_RIGHT) {
                        z_high = 0.1875F;
                    } else {
                        z_low = 0.8125F;
                    }
                    break;
                case HingePart.FACING_XP:
                    if (!isOpen) {
                        x_high = 0.1875F;
                    } else if (hinge == HingePart.HINGE_RIGHT) {
                        z_low = 0.8125F;
                    } else {
                        z_high = 0.1875F;
                    }
                    break;
                case HingePart.FACING_ZN:
                    if (!isOpen) {
                        z_low = 0.8125F;
                    } else if (hinge == HingePart.HINGE_RIGHT) {
                        x_low = 0.8125F;
                    } else {
                        x_high = 0.1875F;
                    }
                    break;
                case HingePart.FACING_ZP:
                    if (!isOpen) {
                        z_high = 0.1875F;
                    } else if (hinge == HingePart.HINGE_RIGHT) {
                        x_high = 0.1875F;
                    } else {
                        x_low = 0.8125F;
                    }
                    break;
            }

            setBlockBounds(x_low, 0.0F, z_low, x_high, 1.0F, z_high);

        }
    }

    /**
     * Cycle hinge state.
     * Will update all connecting hinge pieces.
     */
    public void setHingeState(TEBase TE, int state) {
        List<TEBase> hingePieces = getHingePieces(TE);
        for (TEBase piece : hingePieces) {
            HingePart.setState(piece, state, piece == TE);
        }
    }

    /**
     * Updates hinge type.
     * Will also update adjoining hinge piece.
     */
    public void setHingeType(TEBase TE, int type) {
        HingePart.setType(TE, type);
        updateAdjoiningPiece(TE);
    }

    /**
     * Set hinge rigidity.
     * Will update all connecting hinge pieces.
     */
    public void setHingeRigidity(TEBase TE, int rigidity) {
        List<TEBase> hingePieces = getHingePieces(TE);
        for (TEBase piece : hingePieces) {
            HingePart.setRigidity(piece, rigidity);
        }
    }

    /**
     * Updates hinge hinge side.
     * Will also update adjoining hinge piece.
     */
    public void setHingeSide(TEBase TE, int side) {
        HingePart.setHingeSide(TE, side);
        updateAdjoiningPiece(TE);
    }

    @Override
    /**
     * Lets the block know when one of its neighbor changes. Doesn't know which neighbor changed (coordinates passed are
     * their own) Args: x, y, z, neighbor blockID
     */
    public void onNeighborBlockChange(World world, int x, int y, int z, Block block) {
        if (!world.isRemote) {

            TEBase TE = getTileEntity(world, x, y, z);

            if (TE != null) {

                boolean isOpen = HingePart.getState(TE) == HingePart.STATE_OPEN;

                /*
                 * Create list of hinge pieces and check state of each so
                 * that they act as a single entity regardless of which
                 * hinge piece receives this event.
                 */

                boolean isPowered = false;
                List<TEBase> hingePieces = getHingePieces(TE);
                for (TEBase piece : hingePieces) {
                    if (piece != null) {
                        if (world.isBlockIndirectlyGettingPowered(piece.xCoord, piece.yCoord, piece.zCoord)) {
                            isPowered = true;
                        }
                    }
                }

                /* Set block open or closed. */

                if (block != null && block.canProvidePower() && isPowered != isOpen) {
                    setHingeState(TE, isOpen ? HingePart.STATE_CLOSED : HingePart.STATE_OPEN);
                }

            }

        }

        super.onNeighborBlockChange(world, x, y, z, block);
    }

    /**
     * Updates state, hinge and type for adjoining hinge piece.
     */
    private void updateAdjoiningPiece(TEBase TE) {
//        int state = HingePart.getState(TE);
//        int hinge = HingePart.getHinge(TE);
//        int type = HingePart.getType(TE);
//        int rigidity = HingePart.getRigidity(TE);
//
//        boolean isTop = HingePart.getPiece(TE) == HingePart.PIECE_TOP;
//
//        World world = TE.getWorldObj();
//
//        TEBase TE_adj;
//        if (isTop) {
//            TE_adj = (TEBase) world.getTileEntity(TE.xCoord, TE.yCoord - 1, TE.zCoord);
//        } else {
//            TE_adj = (TEBase) world.getTileEntity(TE.xCoord, TE.yCoord + 1, TE.zCoord);
//        }
//
//        if (TE_adj != null) {
//            HingePart.setState(TE_adj, state, false);
//            HingePart.setHingeSide(TE_adj, hinge);
//            HingePart.setType(TE_adj, type);
//            HingePart.setRigidity(TE_adj, rigidity);
//        }
    }


    @SideOnly(Side.CLIENT)
    @Override
    /**
     * When this method is called, your block should register all the icons it needs with the given IconRegister. This
     * is the only chance you get to register icons.
     */
    public void registerBlockIcons(IIconRegister iconRegister) {
        IconRegistry.icon_door_french_glass        = iconRegister.registerIcon(CarpentersBlocks.MODID + ":" + "door/door_french_glass");
        IconRegistry.icon_door_glass_middle        = iconRegister.registerIcon(CarpentersBlocks.MODID + ":" + "door/door_glass_tall_middle");
        IconRegistry.icon_door_french_glass_middle = iconRegister.registerIcon(CarpentersBlocks.MODID + ":" + "door/door_french_glass_middle");
    }

    @Override
    /**
     * Returns the ID of the items to drop on destruction.
     */
    public Item getItemDropped(int metadata, Random random, int par3) {
        return ItemRegistry.itemCarpentersDoorPart;
    }

    @Override
    @SideOnly(Side.CLIENT)
    /**
     * only called by clickMiddleMouseButton , and passed to inventory.setCurrentItem (along with isCreative)
     */
    public Item getItem(World world, int x, int y, int z) {
        return ItemRegistry.itemCarpentersDoorPart;
    }

    @Override
    /**
     * The type of render function that is called for this block
     */
    public int getRenderType() {
        return BlockRegistry.carpentersDoorPartRenderID;
    }

    @Override
    public ForgeDirection[] getValidRotations(World worldObj, int x, int y, int z) {
        ForgeDirection[] axises = {ForgeDirection.UP, ForgeDirection.DOWN};
        return axises;
    }

    @Override
    public boolean rotateBlock(World world, int x, int y, int z, ForgeDirection axis) {
        // to correctly support archimedes' ships mod:
        // if Axis is DOWN, block rotates to the left, north -> west -> south -> east
        // if Axis is UP, block rotates to the right:  north -> east -> south -> west

        TileEntity tile = world.getTileEntity(x, y, z);
        if (tile != null && tile instanceof TEBase) {
            TEBase cbTile = (TEBase) tile;
            int direction = HingePart.getFacing(cbTile);
            switch (axis) {
                case UP: {
                    switch (direction) {
                        case 0: {
                            HingePart.setFacing(cbTile, 1);
                            break;
                        }
                        case 1: {
                            HingePart.setFacing(cbTile, 2);
                            break;
                        }
                        case 2: {
                            HingePart.setFacing(cbTile, 3);
                            break;
                        }
                        case 3: {
                            HingePart.setFacing(cbTile, 0);
                            break;
                        }
                        default:
                            return false;
                    }
                    break;
                }
                case DOWN: {
                    switch (direction) {
                        case 0: {
                            HingePart.setFacing(cbTile, 3);
                            break;
                        }
                        case 1: {
                            HingePart.setFacing(cbTile, 0);
                            break;
                        }
                        case 2: {
                            HingePart.setFacing(cbTile, 1);
                            break;
                        }
                        case 3: {
                            HingePart.setFacing(cbTile, 2);
                            break;
                        }
                        default:
                            return false;
                    }
                    break;
                }
                default:
                    return false;
            }
            return true;
        }
        return false;
    }

}
