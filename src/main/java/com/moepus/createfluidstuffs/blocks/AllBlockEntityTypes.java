package com.moepus.createfluidstuffs.blocks;

import com.moepus.createfluidstuffs.content.tank.MultiFluidTankBlockEntity;
import com.tterrag.registrate.util.entry.BlockEntityEntry;
import com.moepus.createfluidstuffs.content.tank.MultiFluidTankRender;

import static com.moepus.createfluidstuffs.CreateFluidStuffs.REGISTRATE;

public class AllBlockEntityTypes {
    public static final BlockEntityEntry<MultiFluidTankBlockEntity> MULTI_FLUID_TANK = REGISTRATE
            .blockEntity("multi_fluid_tank", MultiFluidTankBlockEntity::new)
            .validBlocks(AllBlocks.MULTI_FLUID_TANK)
            .renderer(() -> MultiFluidTankRender::new)
            .register();
    public static void register() {}

}
