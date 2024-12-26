package com.moepus.createfluidstuffs.blocks;

import com.moepus.createfluidstuffs.AllCreativeModeTabs;
import com.moepus.createfluidstuffs.content.tank.MultiFluidTankBlock;
import com.moepus.createfluidstuffs.content.tank.MultiFluidTankModel;
import com.moepus.createfluidstuffs.content.tank.MultiFluidTankItem;
import com.simibubi.create.content.fluids.tank.FluidTankGenerator;
import com.simibubi.create.foundation.data.AssetLookup;
import com.simibubi.create.foundation.data.CreateRegistrate;
import com.tterrag.registrate.util.entry.BlockEntry;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.world.level.block.Blocks;

import static com.moepus.createfluidstuffs.CreateFluidStuffs.REGISTRATE;
import static com.simibubi.create.foundation.data.TagGen.pickaxeOnly;

public class AllBlocks {
    static {
        REGISTRATE.setCreativeTab(AllCreativeModeTabs.BASE_CREATIVE_TAB);
    }

    public static final BlockEntry<MultiFluidTankBlock> MULTI_FLUID_TANK = REGISTRATE.block("multi_fluid_tank", MultiFluidTankBlock::regular)
            .initialProperties(() -> Blocks.IRON_BLOCK)
            .properties(p -> p.noOcclusion().isRedstoneConductor((p1, p2, p3) -> true))
            .transform(pickaxeOnly())
            .blockstate(new FluidTankGenerator()::generate)
            .onRegister(CreateRegistrate.blockModel(() -> MultiFluidTankModel::standard))
            .addLayer(() -> RenderType::cutoutMipped)
            .item(MultiFluidTankItem::new)
            .model(AssetLookup.customBlockItemModel("_", "block_single_window"))
            .build()
            .register();

    public static void register() {
    }
}
