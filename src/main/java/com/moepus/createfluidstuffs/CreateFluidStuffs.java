package com.moepus.createfluidstuffs;

import com.moepus.createfluidstuffs.blocks.AllBlockEntityTypes;
import com.moepus.createfluidstuffs.blocks.AllBlocks;
import com.moepus.createfluidstuffs.items.AllItems;
import com.mojang.logging.LogUtils;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import org.slf4j.Logger;
import plus.dragons.createdragonlib.init.SafeRegistrate;
import plus.dragons.createdragonlib.lang.Lang;
import net.minecraft.network.chat.Component;

import static net.createmod.catnip.lang.LangBuilder.resolveBuilders;


// The value here should match an entry in the META-INF/mods.toml file
@Mod(CreateFluidStuffs.ID)
public class CreateFluidStuffs {

    // Define mod id in a common place for everything to reference
    public static final String ID = "createfluidstuffs";
    // Directly reference a slf4j logger
    private static final Logger LOGGER = LogUtils.getLogger();
    public static final SafeRegistrate REGISTRATE = new SafeRegistrate(ID);
    public static final Lang LANG = new Lang(ID);

    public CreateFluidStuffs() {
        IEventBus modEventBus = FMLJavaModLoadingContext.get().getModEventBus();
        IEventBus forgeEventBus = MinecraftForge.EVENT_BUS;

        // Register ourselves for server and other game events we are interested in
        MinecraftForge.EVENT_BUS.register(this);

        REGISTRATE.registerEventListeners(modEventBus);
        AllItems.register();
        AllBlocks.register();
        AllBlockEntityTypes.register();
        AllCreativeModeTabs.register(modEventBus);

        DistExecutor.unsafeRunWhenOn(Dist.CLIENT, () -> () -> ClientSetup.onCtorClient(modEventBus, forgeEventBus));
    }

    public static ResourceLocation asResource(String path) {
        return new ResourceLocation(ID, path);
    }
    public static MutableComponent translateDirect(String key, Object... args) {
        return Component.translatable(ID + "." + key, resolveBuilders(args));
    }
}
