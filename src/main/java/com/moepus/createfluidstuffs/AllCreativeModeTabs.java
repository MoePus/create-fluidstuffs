package com.moepus.createfluidstuffs;

import com.moepus.createfluidstuffs.blocks.AllBlocks;
import com.moepus.createfluidstuffs.items.AllItems;
import net.minecraft.network.chat.Component;

import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.CreativeModeTabs;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber.Bus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.RegistryObject;

@EventBusSubscriber(bus = Bus.MOD)
public class AllCreativeModeTabs {
    private static final DeferredRegister<CreativeModeTab> REGISTER =
            DeferredRegister.create(Registries.CREATIVE_MODE_TAB, CreateFluidStuffs.ID);

    public static final RegistryObject<CreativeModeTab> BASE_CREATIVE_TAB = REGISTER.register("base",
            () -> CreativeModeTab.builder()
                    .title(Component.translatable("itemGroup.createfluidstuffs.base"))
                    .withTabsBefore(CreativeModeTabs.SPAWN_EGGS)
                    .icon(AllBlocks.MULTI_FLUID_TANK::asStack)
                    .displayItems((params, output) -> {
                        output.accept(AllBlocks.MULTI_FLUID_TANK);
                        output.accept(AllItems.Bucket);
                        output.accept(AllItems.JAR);
                    })
                    .build());

    public static void register(IEventBus modEventBus) {
        REGISTER.register(modEventBus);
    }
}