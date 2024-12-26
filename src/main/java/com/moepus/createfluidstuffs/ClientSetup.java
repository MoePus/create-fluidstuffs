package com.moepus.createfluidstuffs;

import com.moepus.createfluidstuffs.items.JarModel;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.ModelEvent;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(value = Dist.CLIENT, bus = Mod.EventBusSubscriber.Bus.MOD, modid = CreateFluidStuffs.ID)
public class ClientSetup
{
    public static void onCtorClient(IEventBus modEventBus, IEventBus forgeEventBus) {
        //modEventBus.addListener(ClientSetup::clientInit);
    }

    @SubscribeEvent
    public static void onRegisterGeometryLoaders(ModelEvent.RegisterGeometryLoaders event)
    {
        event.register("jar_model", JarModel.Loader.INSTANCE);
    }
}
