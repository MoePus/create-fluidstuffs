/*
 * Copyright (c) Forge Development LLC and contributors
 * SPDX-License-Identifier: LGPL-2.1-only
 */

package com.moepus.createfluidstuffs.items;

import net.minecraft.world.item.ItemStack;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.core.Direction;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ForgeCapabilities;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.fluids.*;
import net.minecraftforge.fluids.capability.IFluidHandlerItem;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.UUID;

public class JarFluidHandler implements IFluidHandlerItem, ICapabilityProvider
{
    public static final String FLUID_NBT_KEY = "Fluid";
    public static final String UUID_NBT_KEY = "UUID";

    private final LazyOptional<IFluidHandlerItem> holder = LazyOptional.of(() -> this);

    @NotNull
    protected ItemStack container;
    protected int capacity;

    /**
     * @param container  The container itemStack, data is stored on it directly as NBT.
     * @param capacity   The maximum capacity of this fluid tank.
     */
    public JarFluidHandler(@NotNull ItemStack container, int capacity)
    {
        this.container = container;
        this.capacity = capacity;
    }

    @NotNull
    @Override
    public ItemStack getContainer()
    {
        return container;
    }

    @NotNull
    public FluidStack getFluid()
    {
        CompoundTag tagCompound = container.getTag();
        if (tagCompound == null || !tagCompound.contains(FLUID_NBT_KEY))
        {
            return FluidStack.EMPTY;
        }
        return FluidStack.loadFluidStackFromNBT(tagCompound.getCompound(FLUID_NBT_KEY));
    }

    protected void setFluid(FluidStack fluid)
    {
        if (!container.hasTag())
        {
            container.setTag(new CompoundTag());
        }

        CompoundTag fluidTag = new CompoundTag();
        fluid.writeToNBT(fluidTag);
        container.getTag().put(FLUID_NBT_KEY, fluidTag);
        container.getTag().putUUID(UUID_NBT_KEY, UUID.randomUUID());
    }

    @Override
    public int getTanks() {

        return 1;
    }

    @NotNull
    @Override
    public FluidStack getFluidInTank(int tank) {

        return getFluid();
    }

    @Override
    public int getTankCapacity(int tank) {

        return capacity;
    }

    @Override
    public boolean isFluidValid(int tank, @NotNull FluidStack stack) {

        return true;
    }

    @Override
    public int fill(FluidStack resource, FluidAction doFill)
    {
        if (resource.isEmpty() || !canFillFluidType(resource))
        {
            return 0;
        }

        FluidStack contained = getFluid();
        if (contained.isEmpty())
        {
            if(resource.getAmount() >= capacity)
            {
                if (doFill.execute())
                {
                    FluidStack filled = resource.copy();
                    filled.setAmount(capacity);
                    setFluid(filled);
                }
                return capacity;
            }
        }
        return 0;
    }

    @NotNull
    @Override
    public FluidStack drain(FluidStack resource, FluidAction action)
    {
        if (resource.isEmpty() || !resource.isFluidEqual(getFluid()))
        {
            return FluidStack.EMPTY;
        }
        return drain(resource.getAmount(), action);
    }

    @NotNull
    @Override
    public FluidStack drain(int maxDrain, FluidAction action)
    {
        if (maxDrain <= 0)
        {
            return FluidStack.EMPTY;
        }

        FluidStack contained = getFluid();
        if (contained.isEmpty() || !canDrainFluidType(contained))
        {
            return FluidStack.EMPTY;
        }

        if(maxDrain < capacity)
        {
            return FluidStack.EMPTY;
        }

        FluidStack drained = contained.copy();
        drained.setAmount(capacity);

        if (action.execute())
        {
            setContainerToEmpty();
        }

        return drained;
    }

    public boolean canFillFluidType(FluidStack fluid)
    {
        return true;
    }

    public boolean canDrainFluidType(FluidStack fluid)
    {
        return true;
    }

    /**
     * Override this method for special handling.
     * Can be used to swap out or destroy the container.
     */
    protected void setContainerToEmpty()
    {
        container.removeTagKey(FLUID_NBT_KEY);
        container.removeTagKey(UUID_NBT_KEY);
    }

    @Override
    @NotNull
    public <T> LazyOptional<T> getCapability(@NotNull Capability<T> capability, @Nullable Direction facing) {
        return ForgeCapabilities.FLUID_HANDLER_ITEM.orEmpty(capability, holder);
    }
}
