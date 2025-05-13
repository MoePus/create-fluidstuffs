package com.moepus.createfluidstuffs.content.tank;

import com.moepus.createfluidstuffs.foundation.fluid.SmartMultiFluidTank;
import com.mojang.blaze3d.vertex.PoseStack;
import com.simibubi.create.foundation.blockEntity.renderer.SafeBlockEntityRenderer;
import com.simibubi.create.foundation.fluid.FluidRenderer;

import net.createmod.catnip.animation.LerpedFloat;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.util.Mth;
import net.minecraftforge.fluids.FluidStack;

public class MultiFluidTankRender extends SafeBlockEntityRenderer<MultiFluidTankBlockEntity> {

    public MultiFluidTankRender(BlockEntityRendererProvider.Context context) {
    }

    @Override
    protected void renderSafe(MultiFluidTankBlockEntity be, float partialTicks, PoseStack ms, MultiBufferSource buffer,
                              int light, int overlay) {
        if (!be.isController())
            return;
        if (!be.window) {
            return;
        }
        if(be.tankInventory.isEmpty()){
            return;
        }

        LerpedFloat fluidLevel[] = be.getFluidLevel();
        if (fluidLevel == null)
            return;

        float capHeight = 1 / 4f;
        float tankHullWidth = 1 / 16f + 1 / 128f;
        float minPuddleHeight = 1 / 16f;
        float totalHeight = be.height - 2 * capHeight - minPuddleHeight;

        SmartMultiFluidTank tank = be.tankInventory;
        if (tank.isEmpty()) return;
        FluidStack fluidStacks[] = tank.getFluids();
        float accheight = 0;
        for (int i = 0; i < fluidStacks.length; i++) {
            FluidStack fluidStack = fluidStacks[i];
            if (fluidStack.isEmpty())
                continue;

            float level = fluidLevel[i].getValue(partialTicks);
            if (level < 1 / (512f * totalHeight))
                continue;
            float clampedLevel = Mth.clamp(level * totalHeight, 0, totalHeight);

            float xMin = tankHullWidth;
            float xMax = xMin + be.width - 2 * tankHullWidth;
            float yMin = totalHeight + capHeight + minPuddleHeight - clampedLevel;
            float yMax = yMin + clampedLevel;
            float zMin = tankHullWidth;
            float zMax = zMin + be.width - 2 * tankHullWidth;

            ms.pushPose();
            ms.translate(0, clampedLevel - totalHeight + accheight, 0);
            FluidRenderer.renderFluidBox(fluidStack.getFluid(), fluidStack.getAmount(), xMin, yMin, zMin, xMax, yMax, zMax,
                    buffer, ms, light, false, true, fluidStack.getTag());
            ms.popPose();

            accheight += clampedLevel;
        }
    }

    @Override
    public boolean shouldRenderOffScreen(MultiFluidTankBlockEntity be) {
        return be.isController();
    }

}