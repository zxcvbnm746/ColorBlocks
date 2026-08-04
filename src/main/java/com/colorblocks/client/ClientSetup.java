package com.colorblocks.client;

import com.colorblocks.ColorBlocksMod;
import com.colorblocks.ColorBlockEntity;
import net.minecraft.server.packs.PackType;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.*;
import net.neoforged.neoforge.event.AddPackFindersEvent;

@EventBusSubscriber(modid = ColorBlocksMod.MOD_ID, bus = EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
public class ClientSetup {

    // Called lazily AFTER ColorBlocksMod constructor sets COLOR_BLOCK_ENTITY
    @SubscribeEvent
    public static void registerRenderers(RegisterBlockEntityRenderersEvent event) {
        // Access the field inside the method, not at annotation processing time
        var colorBlockEntity = ColorBlocksMod.COLOR_BLOCK_ENTITY;
        if (colorBlockEntity != null) {
            event.registerBlockEntityRenderers(
                    colorBlockEntity.get(),
                    context -> new ColorBlockEntityRenderer(context)
            );
        }
    }

    @SubscribeEvent
    public static void onAddPackFinders(AddPackFindersEvent event) {
        if (event.getPackType() == PackType.CLIENT_RESOURCES) {
            event.addPackFinder((profile, factory) -> new ColorBlockResourcePack());
        }
    }
}
