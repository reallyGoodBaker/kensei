package top.rgb39.kensei_client.mixin;

import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.GuiGraphics;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import top.rgb39.kensei_client.KenseiClient;
import top.rgb39.kensei_client.KenseiListeners;
import top.rgb39.kensei_client.component.GuiRenderConfig;

@Mixin(Gui.class)
public class GuiMixin {

    @Shadow
    private int screenWidth;

    @Shadow
    private int screenHeight;

    @Inject(method = "render", at = @At("TAIL"))
    void handleGuiRender(GuiGraphics guiGraphics, float f, CallbackInfo ci) {
        GuiRenderConfig conf = (GuiRenderConfig) KenseiClient.clientApp.getSingletonComponent(GuiRenderConfig.class);
        conf.guiGraphics = guiGraphics;
        conf.f = f;
        conf.screenWidth = screenWidth;
        conf.screenHeight = screenHeight;
        KenseiListeners.GUI_RENDER.run();
    }
}
