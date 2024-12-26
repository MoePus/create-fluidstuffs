package com.moepus.createfluidstuffs.items;

import com.moepus.createfluidstuffs.AllCreativeModeTabs;
import com.tterrag.registrate.util.entry.ItemEntry;

import static com.moepus.createfluidstuffs.CreateFluidStuffs.REGISTRATE;

public class AllItems {
    static {
        REGISTRATE.setCreativeTab(AllCreativeModeTabs.BASE_CREATIVE_TAB);
    }
    public static final ItemEntry<JarItem> JAR =
            REGISTRATE.item("jar", JarItem::new)
                    .properties(p -> p.stacksTo(16))
                    .register();

    public static final ItemEntry<BucketItem> Bucket =
            REGISTRATE.item("bucket", BucketItem::new)
                    .properties(p -> p.stacksTo(1))
                    .register();

    public static void register(){
    }
}
