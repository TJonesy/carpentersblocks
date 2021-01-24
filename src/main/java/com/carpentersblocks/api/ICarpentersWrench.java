package com.carpentersblocks.api;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.world.World;

public interface ICarpentersWrench {

    public void onWrenchUse(World world, EntityPlayer entityPlayer);

    public boolean canUseWrench(World world, EntityPlayer entityPlayer);

}
