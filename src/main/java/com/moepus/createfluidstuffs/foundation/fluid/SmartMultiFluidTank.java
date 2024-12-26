package com.moepus.createfluidstuffs.foundation.fluid;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;
import net.minecraftforge.fluids.FluidStack;
import org.jetbrains.annotations.NotNull;
import net.minecraftforge.fluids.IFluidTank;
import net.minecraftforge.fluids.capability.IFluidHandler;

import java.util.OptionalInt;
import java.util.function.Consumer;
import java.util.function.Predicate;

public class SmartMultiFluidTank implements IFluidHandler, IFluidTank {

    private Consumer<FluidStack[]> updateCallback;
    int tanks;
    protected int capacity;
    protected Predicate<FluidStack> validator;

    @NotNull
    protected FluidStack[] multi_fluid;

    public SmartMultiFluidTank(int capacity, int tanks, Consumer<FluidStack[]> updateCallback) {
        this.capacity = capacity;
        this.validator = e -> true;
        this.updateCallback = updateCallback;
        this.tanks = tanks;
        multi_fluid = new FluidStack[tanks];
        resetTanks();
    }

    public SmartMultiFluidTank setCapacity(int capacity) {
        this.capacity = capacity;
        return this;
    }

    public SmartMultiFluidTank setValidator(Predicate<FluidStack> validator) {
        if (validator != null) {
            this.validator = validator;
        }
        return this;
    }

    @Override
    public int getFluidAmount() {
        int amount = 0;
        for (int i = 0; i < getTanks(); i++) {
            amount += multi_fluid[i].getAmount();
        }
        return amount;
    }

    public int getFluidAmount(int tank) {
        if (tank < getTanks())
            return multi_fluid[tank].getAmount();

        return 0;
    }

    public void resetTanks() {
        for (int i = 0; i < getTanks(); i++) {
            multi_fluid[i] = FluidStack.EMPTY;
        }
    }

    protected void onContentsChanged() {
        updateCallback.accept(getFluids());
    }

    public void setFluid(FluidStack stack) {
        for (int i = 1; i < tanks; i++) {
            multi_fluid[i] = FluidStack.EMPTY;
        }
        setFluid(0, stack);
    }

    public void setFluid(int tank, FluidStack stack) {
        if (tank < this.tanks) {
            multi_fluid[tank] = stack;
            updateCallback.accept(getFluids());
        }
    }

    public SmartMultiFluidTank readFromNBT(CompoundTag nbt) {
        for (int i = 0; i < getTanks(); i++) {
            if (!nbt.contains(Integer.toString(i), Tag.TAG_COMPOUND))
                break;
            CompoundTag fluid_nbt = nbt.getCompound(Integer.toString(i));
            FluidStack fluid = FluidStack.loadFluidStackFromNBT(fluid_nbt);
            setFluid(i, fluid);
        }
        return this;
    }

    public CompoundTag writeToNBT(CompoundTag nbt) {
        for (int i = 0; i < getTanks(); i++) {
            CompoundTag fluid_nbt = new CompoundTag();
            multi_fluid[i].writeToNBT(fluid_nbt);
            nbt.put(Integer.toString(i), fluid_nbt);
        }
        return nbt;
    }

    @Override
    public int getTanks() {
        return tanks;
    }

    @Override
    public @NotNull FluidStack getFluidInTank(int tank) {
        if (tank < getTanks()) {
            return multi_fluid[tank];
        }
        return FluidStack.EMPTY;
    }

    @Override
    public int getTankCapacity(int tank) {
        return getCapacity();
    }

    @Override
    public boolean isFluidValid(int tank, @NotNull FluidStack stack) {
        return isFluidValid(stack);
    }

    OptionalInt getFirstAvailableTank(FluidStack resource) {
        int first_empty = -1;
        for (int i = 0; i < getTanks(); i++) {
            if (multi_fluid[i].isEmpty() && first_empty < 0)
                first_empty = i;

            if (multi_fluid[i].isFluidEqual(resource))
                return OptionalInt.of(i);
        }
        if (first_empty >= 0)
            return OptionalInt.of(first_empty);
        return OptionalInt.empty();
    }

    @Override
    public int fill(FluidStack resource, FluidAction action) {
        if (resource.isEmpty() || !isFluidValid(resource)) {
            return 0;
        }
        if (getSpace() == 0)
            return 0;

        OptionalInt target_tank_opt = getFirstAvailableTank(resource);
        if (action.simulate()) {
            if (target_tank_opt.isEmpty()) return 0;
            return Math.min(getSpace(), resource.getAmount());
        }

        int target_tank = target_tank_opt.getAsInt();

        if (multi_fluid[target_tank].isEmpty()) {
            multi_fluid[target_tank] = new FluidStack(resource, Math.min(getSpace(), resource.getAmount()));
            onContentsChanged();
            return multi_fluid[target_tank].getAmount();
        }

        int filled = getSpace();

        if (resource.getAmount() < filled) {
            multi_fluid[target_tank].grow(resource.getAmount());
            filled = resource.getAmount();
        } else {
            multi_fluid[target_tank].grow(filled);
        }
        if (filled > 0)
            onContentsChanged();
        return filled;
    }

    @Override
    public @NotNull FluidStack drain(FluidStack resource, FluidAction action) {
        OptionalInt target_tank_opt = getFirstAvailableTank(resource);
        if (target_tank_opt.isEmpty()) return FluidStack.EMPTY;
        int target_tank = target_tank_opt.getAsInt();

        if (multi_fluid[target_tank].isEmpty()) return FluidStack.EMPTY;

        int drained = Math.min(multi_fluid[target_tank].getAmount(), resource.getAmount());
        FluidStack stack = new FluidStack(multi_fluid[target_tank], drained);

        if (action.execute() && drained > 0) {
            multi_fluid[target_tank].shrink(drained);
            onContentsChanged();
        }
        return stack;
    }

    @Override
    public @NotNull FluidStack drain(int maxDrain, FluidAction action) {
        for (int i = 0; i < getTanks(); i++) {
            if (!multi_fluid[i].isEmpty()) {
                int drained = Math.min(maxDrain, multi_fluid[i].getAmount());
                FluidStack stack = new FluidStack(multi_fluid[i], drained);
                if (action.execute() && drained > 0) {
                    multi_fluid[i].shrink(drained);
                    onContentsChanged();
                }
                return stack;
            }
        }
        return FluidStack.EMPTY;
    }

    @Override
    public @NotNull FluidStack getFluid() {
        for (int i = 0; i < getTanks(); i++) {
            if (!multi_fluid[i].isEmpty()) return multi_fluid[i];
        }
        return FluidStack.EMPTY;
    }

    public @NotNull FluidStack[] getFluids() {
        return multi_fluid;
    }

    @Override
    public int getCapacity() {
        return capacity;
    }

    @Override
    public boolean isFluidValid(FluidStack stack) {
        return validator.test(stack);
    }

    public boolean isEmpty() {
        for (int i = 0; i < getTanks(); i++) {
            if (!multi_fluid[i].isEmpty()) return false;
        }
        return true;
    }

    public int getSpace() {
        return Math.max(0, capacity - getFluidAmount());
    }
}
