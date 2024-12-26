package com.moepus.createfluidstuffs.items;

import com.simibubi.create.foundation.utility.Components;
import com.simibubi.create.foundation.utility.Lang;
import net.minecraft.ChatFormatting;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import net.minecraftforge.common.capabilities.ForgeCapabilities;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.capability.IFluidHandlerItem;
import org.jetbrains.annotations.Nullable;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import java.util.List;

public class JarItem extends Item{
    public static final int capacity = 10;
    public JarItem(Properties pProperties) {
        super(pProperties);
    }

    @Override
    public ICapabilityProvider initCapabilities(ItemStack stack, @Nullable CompoundTag nbt)
    {
        return new JarFluidHandler(stack, capacity);
    }

    @Override
    @OnlyIn(value = Dist.CLIENT)
    public void appendHoverText(ItemStack stack, Level worldIn, List<Component> tooltip, TooltipFlag flagIn) {
        LazyOptional<IFluidHandlerItem> capability =
                stack.getCapability(ForgeCapabilities.FLUID_HANDLER_ITEM);
        if (!capability.isPresent())
        {
            super.appendHoverText(stack, worldIn, tooltip, flagIn);
            return;
        }
        FluidStack fluid = capability.orElse(null).getFluidInTank(0);
        if (fluid.isEmpty()) {
            tooltip.add(Components.translatable("createfluidstuffs.tooltips.empty").withStyle(ChatFormatting.GRAY));
        } else {
            Lang.fluidName(fluid).style(ChatFormatting.GOLD).addTo(tooltip);
        }
    }
}
