package com.carpentersblocks.renderer;

import net.minecraft.item.ItemStack;
import net.minecraft.util.IIcon;
import net.minecraftforge.common.util.ForgeDirection;
import com.carpentersblocks.data.HingePart;
import com.carpentersblocks.util.registry.IconRegistry;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class BlockHandlerCarpentersDoorPart extends BlockHandlerBase {

    private boolean hingeLeft;
    private int facing;
    private int type;
    private int connections;

    /** Side block renders against. */
    protected ForgeDirection side;

    /** Whether block is in open state. */
    protected boolean isOpen;

    /** Bounds for glass or other type of pane. */
    protected static final double[][] paneBounds = new double[][] {
            { 0.0D, 0.09375D, 0.0D, 1.0D, 0.09375D, 1.0D },
            { 0.0D, 0.90625D, 0.0D, 1.0D, 0.90625D, 1.0D },
            { 0.0D, 0.0D, 0.09375D, 1.0D, 1.0D, 0.09375D },
            { 0.0D, 0.0D, 0.90625D, 1.0D, 1.0D, 0.90625D },
            { 0.09375D, 0.0D, 0.0D, 0.09375D, 1.0D, 1.0D },
            { 0.90625D, 0.0D, 0.0D, 0.90625D, 1.0D, 1.0D }
    };

    /**
     * Renders pane like glass or screen.
     */
    protected final void renderPartPane(IIcon icon, int x, int y, int z)
    {
        int dir = side.ordinal();
        renderBlocks.setRenderBounds(paneBounds[dir][0], paneBounds[dir][1], paneBounds[dir][2], paneBounds[dir][3], paneBounds[dir][4], paneBounds[dir][5]);
        renderPane(icon, x, y, z, side, true, true);
    }

    @Override
    public boolean shouldRender3DInInventory(int modelId)
    {
        return false;
    }

    @Override
    /**
     * Renders block at coordinates.
     */
    public void renderCarpentersBlock(int x, int y, int z)
    {
        renderBlocks.renderAllFaces = true;

        setParams();
        ItemStack itemStack = getCoverForRendering();

        switch (type) {
            case HingePart.TYPE_GLASS_TALL:
            case HingePart.TYPE_SCREEN_TALL:
                renderTypeTall(itemStack, x, y, z);
                break;
            case HingePart.TYPE_PANELS:
                renderTypePaneled(itemStack, x, y, z);
                break;
            case HingePart.TYPE_FRENCH_GLASS:
                renderTypeFrench(itemStack, x, y, z);
                break;
            case HingePart.TYPE_HIDDEN:
                renderTypeHidden(itemStack, x, y, z);
                break;
        }

        renderBlocks.renderAllFaces = false;
    }

    /**
     * Sets up commonly used fields.
     */
    private void setParams()
    {
        type = HingePart.getType(TE);
        connections = HingePart.getConnections(TE);
        hingeLeft = HingePart.getHinge(TE) == HingePart.HINGE_LEFT;
        isOpen = HingePart.getState(TE) == HingePart.STATE_OPEN;
        int facing = HingePart.getFacing(TE);
        this.facing = facing == HingePart.FACING_ZN ? 0 : facing == HingePart.FACING_ZP ? 1 : facing == HingePart.FACING_XN ? 2 : 3;

        ForgeDirection[][] extrapolatedSide = {
                { ForgeDirection.SOUTH, ForgeDirection.WEST, ForgeDirection.EAST },
                { ForgeDirection.NORTH, ForgeDirection.EAST, ForgeDirection.WEST },
                { ForgeDirection.EAST, ForgeDirection.SOUTH, ForgeDirection.NORTH },
                { ForgeDirection.WEST, ForgeDirection.NORTH, ForgeDirection.SOUTH }
        };

        side = extrapolatedSide[this.facing][!isOpen ? 0 : hingeLeft ? 1 : 2];
    }

    private void renderBorder(ItemStack itemStack, int x, int y, int z)
    {
        renderLeft(itemStack, x, y, z);
        renderRight(itemStack, x, y, z);

        if ((connections & HingePart.CONNECT_TOP) == 0) {
            renderTop(itemStack, x, y, z);
        }
        if ((connections & HingePart.CONNECT_BOTTOM) == 0) {
            renderBottom(itemStack, x, y, z);
        }
    }

    private void renderTop(ItemStack itemStack, int x, int y, int z)
    {
        renderBlockWithRotation(itemStack, x, y, z, 0.1875D, 0.8125D, 0.8125D, 0.8125D, 1.0D, 1.0D, side);
    }

    private void renderBottom(ItemStack itemStack, int x, int y, int z)
    {
        renderBlockWithRotation(itemStack, x, y, z, 0.1875D, 0.0D, 0.8125D, 0.8125D, 0.1875D, 1.0D, side);
    }

    private void renderLeft(ItemStack itemStack, int x, int y, int z)
    {
        renderBlockWithRotation(itemStack, x, y, z, 0.0D, 0.0D, 0.8125D, 0.1875D, 1.0D, 1.0D, side);
    }

    private void renderRight(ItemStack itemStack, int x, int y, int z)
    {
        renderBlockWithRotation(itemStack, x, y, z, 0.8125D, 0.0D, 0.8125D, 1.0D, 1.0D, 1.0D, side);
    }

    /**
     * Renders a French door at given coordinates.
     *
     * @param itemStack the {@link ItemStack}
     * @param x the x coordinate
     * @param y the y coordinate
     * @param z the z coordinate
     */
    private void renderTypeFrench(final ItemStack itemStack, final int x, final int y, final int z)
    {
        renderBorder(itemStack, x, y, z);

        renderBlockWithRotation(itemStack, x, y, z, 0.4375D, 0D, 0.875D, 0.5625D,1D, 0.9375D, side); //vertical


        IIcon icon;
        if (connections == (HingePart.CONNECT_TOP | HingePart.CONNECT_BOTTOM))
        {
            icon = IconRegistry.icon_door_french_glass_middle;
            renderBlockWithRotation(itemStack, x, y, z, 0.1875D, 0D, 0.875D, 0.8125D, 0.0625D, 0.9375D, side); //bottom horizontal
            renderBlockWithRotation(itemStack, x, y, z, 0.1875D, 0.9375D, 0.875D, 0.8125D, 1D, 0.9375D, side); //top horizontal
            renderBlockWithRotation(itemStack, x, y, z, 0.1875D, 0.4375D, 0.875D, 0.8125D, 0.5625D, 0.9375D, side); //horizontal
        }
        else if (connections == (HingePart.CONNECT_TOP))
        {
            icon = IconRegistry.icon_door_french_glass_bottom;
            renderBlockWithRotation(itemStack, x, y, z, 0.1875D, 0.9375D, 0.875D, 0.8125D, 1D, 0.9375D, side); //top horizontal
            renderBlockWithRotation(itemStack, x, y, z, 0.1875D, 0.5D, 0.875D, 0.8125D, 0.625D, 0.9375D, side); //horizontal
        }
        else if (connections == (HingePart.CONNECT_BOTTOM))
        {
            icon = IconRegistry.icon_door_french_glass_top;
            renderBlockWithRotation(itemStack, x, y, z, 0.1875D, 0D, 0.875D, 0.8125D, 0.0625D, 0.9375D, side); //bottom horizontal
            renderBlockWithRotation(itemStack, x, y, z, 0.1875D, 0.375D, 0.875D, 0.8125D, 0.5D, 0.9375D, side); //horizontal
        }
        else
        {
            icon = IconRegistry.icon_door_french_glass;
            renderBlockWithRotation(itemStack, x, y, z, 0.1875D, 0.4375D, 0.875D, 0.8125D, 0.5625D, 0.9375D, side); //horizontal
        }
        renderPartPane(icon, x, y, z);
    }

    /**
     * Renders a paneled door at given coordinates.
     *
     * @param itemStack the {@link ItemStack}
     * @param x the x coordinate
     * @param y the y coordinate
     * @param z the z coordinate
     */
    private void renderTypePaneled(ItemStack itemStack, int x, int y, int z)
    {
        renderBorder(itemStack, x, y, z);

        double pixel = 0.0625D;

        double top;
        double bottom;
        double left = 5*pixel;
        double right = 1-5*pixel;

        if ((connections & HingePart.CONNECT_TOP) == 0)
        {
            top = 1-5*pixel;
        }
        else
        {

            top = 1-2*pixel;
        }
        if ((connections & HingePart.CONNECT_BOTTOM) == 0)
        {
            bottom = 5*pixel;
        }
        else
        {

            bottom = 2*pixel;
        }


        renderBlockWithRotation(itemStack, x, y, z, 0D, 0.0D, 0.875D, 1D, 1D, 0.9375D, side); //fill
        renderBlockWithRotation(itemStack, x, y, z, left, bottom, 0.8125D, right, top, 1.0D, side); //middle
    }

    /**
     * Renders a tall screen or glass door at given coordinates.
     *
     * @param itemStack the {@link ItemStack}
     * @param x the x coordinate
     * @param y the y coordinate
     * @param z the z coordinate
     */
    private void renderTypeTall(final ItemStack itemStack, final int x, final int y, final int z)
    {
        renderBorder(itemStack, x, y, z);
        IIcon icon;
        if (type == HingePart.TYPE_SCREEN_TALL)
        {
            icon = IconRegistry.icon_door_screen_tall;
        }
        else
        {
            if (connections == (HingePart.CONNECT_TOP | HingePart.CONNECT_BOTTOM))
            {
                icon = IconRegistry.icon_door_glass_middle;
            }
            else if (connections == (HingePart.CONNECT_TOP))
            {
                icon = IconRegistry.icon_door_glass_tall_bottom;
            }
            else if (connections == (HingePart.CONNECT_BOTTOM))
            {
                icon = IconRegistry.icon_door_glass_tall_top;
            }
            else
            {
                icon = IconRegistry.icon_door_glass_top;
            }
        }
        renderPartPane(icon, x, y, z);

    }

    /**
     * Renders a hidden door at given coordinates.
     *
     * @param itemStack the {@link ItemStack}
     * @param x the x coordinate
     * @param y the y coordinate
     * @param z the z coordinate
     */
    private void renderTypeHidden(ItemStack itemStack, int x, int y, int z)
    {
        renderBlockWithRotation(itemStack, x, y, z, 0.0D, 0.0D, 0.8125D, 1.0D, 1.0D, 1.0D, side);
    }

}
