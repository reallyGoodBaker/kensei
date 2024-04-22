package top.rgb39.kensei_client.component;

import net.minecraft.client.gui.GuiGraphics;
import top.rgb39.ecs.annotation.Component;

@Component(singleton = true)
public class GuiRenderConfig {
    public GuiGraphics guiGraphics;
    public float f;
    public int screenWidth;
    public int screenHeight;
}
