package com.colorblocks.client;

import com.colorblocks.ColorBlocksMod;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.RegisterBlockEntityRenderersEvent;

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
}