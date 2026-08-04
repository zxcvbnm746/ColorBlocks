package com.colorblocks.client;

import com.colorblocks.ColorBlocksMod;
import com.colorblocks.ColorBlockEntity;
import net.minecraft.server.packs.PackType;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.RegisterBlockEntityRenderersEvent;
import net.neoforged.neoforge.event.AddPackFindersEvent;

@EventBusSubscriber(modid = ColorBlocksMod.MOD_ID, bus = EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
public class ClientSetup {

    @SubscribeEvent
    public static void registerRenderers(RegisterBlockEntityRenderersEvent event) {
        if (ColorBlocksMod.COLOR_BLOCK_ENTITY != null) {
            event.registerBlockEntityRenderer(
                    ColorBlocksMod.COLOR_BLOCK_ENTITY.get(),
                    ColorBlockEntityRenderer::new
            );
        }
    }

    @SubscribeEvent
    public static void onAddPackFinders(AddPackFindersEvent event) {
        if (event.getPackType() == PackType.CLIENT_RESOURCES) {
            // Resource pack will be added dynamically at runtime
            // For now, BlockEntityRenderer handles all rendering
        }
    }
}
