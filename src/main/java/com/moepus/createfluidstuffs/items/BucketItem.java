package com.moepus.createfluidstuffs.items;

import com.simibubi.create.foundation.utility.CreateLang;
import net.minecraft.network.chat.Component;
import net.minecraft.ChatFormatting;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraftforge.common.capabilities.ForgeCapabilities;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.capability.IFluidHandler;
import net.minecraftforge.fluids.capability.IFluidHandlerItem;
import org.jetbrains.annotations.NotNull;
import net.minecraftforge.fluids.capability.templates.FluidHandlerItemStack;
import org.jetbrains.annotations.Nullable;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import java.util.List;

public class BucketItem extends Item {
    public static final int capacity = 2000;

    public BucketItem(Properties pProperties) {
        super(pProperties);
    }

    @Override
    public ICapabilityProvider initCapabilities(ItemStack stack, @Nullable CompoundTag nbt) {
        return new FluidHandlerItemStack(stack, capacity);
    }

    @Override
    @OnlyIn(value = Dist.CLIENT)
    public void appendHoverText(@NotNull ItemStack stack, Level worldIn, List<Component> tooltip, TooltipFlag flagIn) {
        LazyOptional<IFluidHandlerItem> capability =
                stack.getCapability(ForgeCapabilities.FLUID_HANDLER_ITEM);
        if (!capability.isPresent()) {
            super.appendHoverText(stack, worldIn, tooltip, flagIn);
            return;
        }
        FluidStack fluid = capability.orElse(null).getFluidInTank(0);
        if (fluid.isEmpty()) {
            tooltip.add(Component.translatable("createfluidstuffs.tooltips.empty").withStyle(ChatFormatting.GRAY));
        } else {
            CreateLang.fluidName(fluid).style(ChatFormatting.GOLD).addTo(tooltip);
            tooltip.add(Component.literal(Integer.toString(fluid.getAmount()) + "mB").withStyle(ChatFormatting.WHITE));
        }
    }

    @Override
    public InteractionResult useOn(UseOnContext pContext) {
        BlockEntity be = pContext.getLevel().getBlockEntity(pContext.getClickedPos());
        if (be == null)
            return InteractionResult.PASS;

        IFluidHandler blockFluidHandler = be.getCapability(ForgeCapabilities.FLUID_HANDLER, pContext.getClickedFace()).orElse(null);
        if (blockFluidHandler == null)
            return InteractionResult.PASS;

        ItemStack bucket = pContext.getItemInHand();
        IFluidHandlerItem bucketFluidHandler = bucket.getCapability(ForgeCapabilities.FLUID_HANDLER_ITEM).orElse(null);
        if (bucketFluidHandler == null)
            return InteractionResult.PASS;

        FluidStack bucketCanDrain = bucketFluidHandler.drain(capacity, IFluidHandler.FluidAction.SIMULATE);
        if (bucketCanDrain == FluidStack.EMPTY) { // drain from block
            FluidStack canDrain = blockFluidHandler.drain(capacity, IFluidHandler.FluidAction.SIMULATE);
            if (canDrain == FluidStack.EMPTY)
                return InteractionResult.PASS;

            canDrain = blockFluidHandler.drain(canDrain, IFluidHandler.FluidAction.EXECUTE);
            bucketFluidHandler.fill(canDrain, IFluidHandler.FluidAction.EXECUTE);
        } else {
            // fill in block
            int canFillAmount = blockFluidHandler.fill(bucketCanDrain, IFluidHandler.FluidAction.SIMULATE);
            if(canFillAmount == 0)
                return InteractionResult.PASS;

            bucketCanDrain.setAmount(canFillAmount);
            FluidStack toDrain = bucketFluidHandler.drain(bucketCanDrain, IFluidHandler.FluidAction.EXECUTE);
            blockFluidHandler.fill(toDrain, IFluidHandler.FluidAction.EXECUTE);
        }

        return InteractionResult.SUCCESS;
    }
}
