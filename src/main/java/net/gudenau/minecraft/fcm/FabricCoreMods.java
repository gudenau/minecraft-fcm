package net.gudenau.minecraft.fcm;

import net.fabricmc.api.ModInitializer;
import net.fabricmc.loader.api.FabricLoader;
import net.fabricmc.loader.api.MappingResolver;
import net.gudenau.minecraft.fcm.api.AsmTarget;

@AsmTarget
public class FabricCoreMods implements ModInitializer{
    @Override
    public void onInitialize(){
        System.out.println("Breaking Fabric since 2020");
    }
}
