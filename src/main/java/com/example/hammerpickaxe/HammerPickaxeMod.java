package com.example.hammerpickaxe;

import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;

@Mod(HammerPickaxeMod.MODID)
public class HammerPickaxeMod {
    public static final String MODID = "hammerpickaxe";

    public HammerPickaxeMod() {
        IEventBus modEventBus = FMLJavaModLoadingContext.get().getModEventBus();

        ModItems.register(modEventBus);
    }
}
