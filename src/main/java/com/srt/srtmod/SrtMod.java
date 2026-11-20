package com.srt.srtmod;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.screen.v1.ScreenEvents;
import net.fabricmc.fabric.api.client.screen.v1.Screens;
import net.fabricmc.fabric.api.event.player.AttackEntityCallback;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.PauseScreen;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import net.minecraft.tags.ItemTags;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.item.Items;

public class SrtMod implements ClientModInitializer {
    public static boolean enabled = false;

    @Override
    public void onInitializeClient() {
        ScreenEvents.AFTER_INIT.register((client, screen, w, h) -> {
            if (screen instanceof PauseScreen) {
                Button b = Button.builder(Component.literal("Swap"),
                        btn -> client.setScreenAndShow(new SwapScreen(screen)))
                        .bounds(w / 2 - 102, h / 4 + 150, 204, 20).build();
                Screens.getButtons(screen).add(b);
            }
        });

        AttackEntityCallback.EVENT.register((player, level, hand, entity, hit) -> {
            if (!enabled || !level.isClientSide()) return InteractionResult.PASS;
            if (!player.getMainHandItem().is(ItemTags.AXES)) return InteractionResult.PASS;
            for (int i = 0; i < 9; i++) {
                if (player.getInventory().getItem(i).is(Items.MACE)) {
                    player.getInventory().setSelectedSlot(i);
                    break;
                }
            }
            return InteractionResult.PASS;
        });
    }

    public static class SwapScreen extends Screen {
        private final Screen parent;

        protected SwapScreen(Screen parent) {
            super(Component.literal("Swap"));
            this.parent = parent;
        }

        @Override
        protected void init() {
            addRenderableWidget(Button.builder(label(), btn -> {
                enabled = !enabled;
                btn.setMessage(label());
            }).bounds(width / 2 - 100, height / 2 - 10, 200, 20).build());

            addRenderableWidget(Button.builder(Component.literal("Done"),
                    btn -> Minecraft.getInstance().setScreenAndShow(parent))
                    .bounds(width / 2 - 100, height / 2 + 20, 200, 20).build());
        }

        private Component label() {
            return Component.literal("Axe & Mace Swap: " + (enabled ? "ON" : "OFF"));
        }

        @Override
        public void onClose() {
            Minecraft.getInstance().setScreenAndShow(parent);
        }
    }
}
