package com.lemby.silicon;

import com.lemby.silicon.client.render.RenderAzurite;
import com.lemby.silicon.client.render.RenderCoal;
import com.lemby.silicon.client.render.RenderSugar;
import com.lemby.silicon.entities.gems.EntityAzurite;
import com.lemby.silicon.entities.gems.EntityCoal;
import com.lemby.silicon.entities.gems.EntitySugar;
import com.lemby.silicon.entities.projectiles.BubbleEntity;
import com.lemby.silicon.init.RegistryHandler;
import com.lemby.silicon.init.SiliconBlocks;
import com.lemby.silicon.init.SiliconEntities;
import net.minecraft.block.Block;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.ItemRenderer;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.RenderTypeLookup;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererManager;
import net.minecraft.client.renderer.entity.SpriteRenderer;
import net.minecraft.entity.ai.attributes.GlobalEntityTypeAttributes;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.DeferredWorkQueue;
import net.minecraftforge.fml.InterModComms;
import net.minecraftforge.fml.client.registry.IRenderFactory;
import net.minecraftforge.fml.client.registry.RenderingRegistry;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.event.lifecycle.InterModEnqueueEvent;
import net.minecraftforge.fml.event.lifecycle.InterModProcessEvent;
import net.minecraftforge.fml.event.server.FMLServerStartingEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.stream.Collectors;

// The value here should match an entry in the META-INF/mods.toml file
@Mod(Silicon.MODID)
public class Silicon
{
    public static final String MODID = "silicon";

    public static final String VERSION = "1.0";
    // Directly reference a log4j logger.
    private static final Logger LOGGER = LogManager.getLogger();

    public Silicon() {
        FMLJavaModLoadingContext.get().getModEventBus().addListener(this::setup);
        FMLJavaModLoadingContext.get().getModEventBus().addListener(this::enqueueIMC);
        FMLJavaModLoadingContext.get().getModEventBus().addListener(this::processIMC);

        RegistryHandler.init();
        MinecraftForge.EVENT_BUS.register(this);
    }

    private void setup(final FMLCommonSetupEvent event)
    {
        DeferredWorkQueue.runLater(() -> {
            GlobalEntityTypeAttributes.put(SiliconEntities.SUGAR.get(), EntitySugar.setCustomAttributes().create());
            GlobalEntityTypeAttributes.put(SiliconEntities.AZURITE.get(), EntityAzurite.setCustomAttributes().create());
            GlobalEntityTypeAttributes.put(SiliconEntities.COAL.get(), EntityCoal.setCustomAttributes().create());
        });
        SiliconEntities.registerEntitiesToGempire();
    }

    public static class RenderBubbleFactory implements IRenderFactory<BubbleEntity> {
        @Override
        public EntityRenderer<? super BubbleEntity> createRenderFor(EntityRendererManager manager) {
            ItemRenderer itemRenderer = Minecraft.getInstance().getItemRenderer();
            return new SpriteRenderer<>(manager, itemRenderer);
        }
    }
    private void enqueueIMC(final InterModEnqueueEvent event)
    {
        // some example code to dispatch IMC to another mod
        InterModComms.sendTo("silicon", "helloworld", () -> { LOGGER.info("Hello world from the MDK"); return "Hello world";});
    }

    private void processIMC(final InterModProcessEvent event)
    {
        // some example code to receive and process InterModComms from other mods
        LOGGER.info("Got IMC {}", event.getIMCStream().
                map(m->m.getMessageSupplier().get()).
                collect(Collectors.toList()));
    }
    // You can use SubscribeEvent and let the Event Bus discover methods to call
    @SubscribeEvent
    public void onServerStarting(FMLServerStartingEvent event) {
        // do something when the server starts
        LOGGER.info("HELLO from server starting");
    }

    // You can use EventBusSubscriber to automatically subscribe events on the contained class (this is subscribing to the MOD
    // Event bus for receiving Registry Events)
    @Mod.EventBusSubscriber(bus=Mod.EventBusSubscriber.Bus.MOD)
    public static class RegistryEvents {
        @SubscribeEvent
        public static void onBlocksRegistry(final RegistryEvent.Register<Block> blockRegistryEvent) {
            // register a new block heref
            LOGGER.info("HELLO from Register Block");
        }
    }
}
