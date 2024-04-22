package top.rgb39.kensei_client.mixin;

import net.minecraft.client.CameraType;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.components.toasts.TutorialToast;
import net.minecraft.client.gui.screens.Overlay;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.advancements.AdvancementsScreen;
import net.minecraft.client.gui.screens.inventory.CreativeModeInventoryScreen;
import net.minecraft.client.gui.screens.inventory.InventoryScreen;
import net.minecraft.client.gui.screens.social.SocialInteractionsScreen;
import net.minecraft.client.tutorial.Tutorial;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.network.chat.Component;
import net.minecraft.network.protocol.game.ServerboundPlayerActionPacket;
import net.minecraft.world.InteractionHand;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import top.rgb39.ecs.plugin.EventWriter;
import top.rgb39.ecs.plugin.Events;
import top.rgb39.kensei_client.KenseiListeners;
import top.rgb39.kensei_client.system.KeyboardSystems;

import javax.annotation.Nullable;

import static top.rgb39.kensei_client.system.KeyboardSystems.registerKeyboardEvent;

@Mixin(Minecraft.class)
public abstract class MinecraftMixin {

    @Shadow
    protected abstract boolean isMultiplayerServer();

    @Final
    @Shadow
    private Tutorial tutorial;

    @Shadow
    private @Nullable Overlay overlay;

    @Shadow
    protected abstract void openChatScreen(String string);

    @Shadow
    private @Nullable TutorialToast socialInteractionsToast;

    @Final
    @Shadow
    private static Component SOCIAL_INTERACTIONS_NOT_AVAILABLE;

    @Shadow
    private int rightClickDelay;

    @Shadow
    protected abstract boolean startAttack();

    @Shadow
    protected abstract void startUseItem();

    @Shadow
    protected abstract void pickBlock();

    @Shadow
    protected abstract void continueAttack(boolean bl);

    @Unique
    private final EventWriter writer = Events.getEventWriter();

    @Unique
    private boolean bl3 = false;

    @Inject(method = "handleKeybinds", at = @At(value = "HEAD"), cancellable = true)
    void handleKeys(CallbackInfo ci) {
        final Minecraft self = Minecraft.getInstance();
        ci.cancel();
        KeyboardSystems.fireEvents(writer);
        continueAttack(self.screen == null && !bl3 && self.options.keyAttack.isDown() && self.mouseHandler.isMouseGrabbed());
    }

    @Inject(method = "onGameLoadFinished", at = @At("TAIL"))
    void registerNativeInputs(CallbackInfo ci) {
        final Minecraft self = Minecraft.getInstance();
        registerKeyboardEvent(
                self.options.keyTogglePerspective,
                (w, f) -> {
                    CameraType cameraType = self.options.getCameraType();
                    self.options.setCameraType(self.options.getCameraType().cycle());
                    if (cameraType.isFirstPerson() != self.options.getCameraType().isFirstPerson()) {
                        self.gameRenderer.checkEntityPostEffect(self.options.getCameraType().isFirstPerson() ? self.getCameraEntity() : null);
                    }
                    self.levelRenderer.needsUpdate();
                }
        );

        registerKeyboardEvent(
                self.options.keySmoothCamera,
                (w, f) -> self.options.smoothCamera = !self.options.smoothCamera
        );

        for(int i = 0; i < 9; ++i) {
            boolean bl = self.options.keySaveHotbarActivator.isDown();
            boolean bl2 = self.options.keyLoadHotbarActivator.isDown();
            int finalI = i;
            registerKeyboardEvent(
                    self.options.keyHotbarSlots[i],
                    (w, f) -> {
                        if (self.player.isSpectator()) {
                            self.gui.getSpectatorGui().onHotbarSelected(finalI);
                        } else if (!self.player.isCreative() || self.screen != null || !bl2 && !bl) {
                            self.player.getInventory().selected = finalI;
                        } else {
                            CreativeModeInventoryScreen.handleHotbarLoadOrSave(self, finalI, bl2, bl);
                        }
                    }
            );
        }

        registerKeyboardEvent(
                self.options.keySocialInteractions,
                (w, f) -> {
                    if (!isMultiplayerServer()) {
                        self.player.displayClientMessage(SOCIAL_INTERACTIONS_NOT_AVAILABLE, true);
                        self.getNarrator().sayNow(SOCIAL_INTERACTIONS_NOT_AVAILABLE);
                    } else {
                        if (socialInteractionsToast != null) {
                            tutorial.removeTimedToast(socialInteractionsToast);
                            socialInteractionsToast = null;
                        }

                        self.setScreen(new SocialInteractionsScreen());
                    }
                }
        );

        registerKeyboardEvent(
                self.options.keyInventory,
                (w, f) -> {
                    if (self.gameMode.isServerControlledInventory()) {
                        self.player.sendOpenInventory();
                    } else {
                        tutorial.onOpenInventory();
                        self.setScreen(new InventoryScreen(self.player));
                    }
                }
        );

        registerKeyboardEvent(
                self.options.keyAdvancements,
                (w, f) -> self.setScreen(new AdvancementsScreen(self.player.connection.getAdvancements()))
        );

        registerKeyboardEvent(
                self.options.keySwapOffhand,
                (w, f) -> {
                    if (!self.player.isSpectator()) {
                        self.getConnection().send(new ServerboundPlayerActionPacket(net.minecraft.network.protocol.game.ServerboundPlayerActionPacket.Action.SWAP_ITEM_WITH_OFFHAND, BlockPos.ZERO, Direction.DOWN));
                    }
                }
        );

        registerKeyboardEvent(
                self.options.keyDrop,
                (w, f) -> {
                    if (!self.player.isSpectator() && self.player.drop(Screen.hasControlDown())) {
                        self.player.swing(InteractionHand.MAIN_HAND);
                    }
                }
        );

        registerKeyboardEvent(
                self.options.keyChat,
                (w, f) -> {
                    openChatScreen("");
                }
        );

        registerKeyboardEvent(
                self.options.keyCommand,
                (w, f) -> {
                    if (self.screen == null && overlay == null)
                        openChatScreen("/");
                }
        );

        registerKeyboardEvent(
                self.options.keyPickItem,
                (w, f) -> pickBlock()
        );

        registerKeyboardEvent(
                self.options.keyUse,
                (w, f) -> startUseItem(),
                (w, f) -> {
                    if (self.player.isUsingItem()) {
                        self.gameMode.releaseUsingItem(self.player);
                    }
                }
        );

        registerKeyboardEvent(
                self.options.keyAttack,
                (w, f) -> bl3 |= startAttack()
        );

        KenseiListeners.CLIENT_READY.run();
    }
}
