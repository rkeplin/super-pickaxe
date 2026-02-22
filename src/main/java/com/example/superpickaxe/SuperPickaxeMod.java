package com.example.superpickaxe;

import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;

@Mod(SuperPickaxeMod.MODID)
public class SuperPickaxeMod {
    public static final String MODID = "superpickaxe";

    public SuperPickaxeMod() {
        IEventBus modEventBus = FMLJavaModLoadingContext.get().getModEventBus();

        ModItems.register(modEventBus);
    }
}
