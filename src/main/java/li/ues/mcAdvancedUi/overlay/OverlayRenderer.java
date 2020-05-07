package li.ues.mcAdvancedUi.overlay;

import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.ChatScreen;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL12;

public class OverlayRenderer {
    public static float multiplier = 0.5f;

    protected static boolean hasLight;
    protected static boolean hasDepthTest;
    protected static boolean hasRescaleNormal;
    protected static boolean hasColorMaterial;
    protected static boolean depthMask;
    protected static int depthFunc;

    public static void renderOverlay() {
        MinecraftClient mc = MinecraftClient.getInstance();
        if ((mc.currentScreen != null && !(mc.currentScreen instanceof ChatScreen)) || mc.world == null)
            return;

        renderOverlay(TickHandler.instance().tooltip);
    }

    public static void renderOverlay(Tooltip tooltip) {
        if (TickHandler.instance().tooltip == null)
            return;

        MinecraftClient.getInstance().getProfiler().push("Waila Overlay");
        RenderSystem.pushMatrix();
        saveGLState();

        RenderSystem.scalef(multiplier, multiplier, 1.0F);

        RenderSystem.disableRescaleNormal();

        RenderSystem.disableLighting();
        RenderSystem.disableDepthTest();

        RenderSystem.enableBlend();
        RenderSystem.blendFunc(770, 771);
        tooltip.draw();
        RenderSystem.disableBlend();

        RenderSystem.enableRescaleNormal();

        loadGLState();
        RenderSystem.enableDepthTest();
        RenderSystem.popMatrix();
        MinecraftClient.getInstance().getProfiler().pop();
    }

    public static void saveGLState() {
        hasLight = GL11.glGetBoolean(GL11.GL_LIGHTING);
        hasDepthTest = GL11.glGetBoolean(GL11.GL_DEPTH_TEST);
        hasRescaleNormal = GL11.glGetBoolean(GL12.GL_RESCALE_NORMAL);
        hasColorMaterial = GL11.glGetBoolean(GL11.GL_COLOR_MATERIAL);
        depthFunc = GL11.glGetInteger(GL11.GL_DEPTH_FUNC);
        depthMask = GL11.glGetBoolean(GL11.GL_DEPTH_WRITEMASK);
        GL11.glPushAttrib(GL11.GL_CURRENT_BIT); // Leave me alone :(
    }

    public static void loadGLState() {
        RenderSystem.depthMask(depthMask);
        RenderSystem.depthFunc(depthFunc);
        if (hasLight)
            RenderSystem.enableLighting();
        else
            RenderSystem.disableLighting();

        if (hasDepthTest)
            RenderSystem.enableDepthTest();
        else
            RenderSystem.disableDepthTest();
        if (hasRescaleNormal)
            RenderSystem.enableRescaleNormal();
        else
            RenderSystem.disableRescaleNormal();
        if (hasColorMaterial)
            RenderSystem.enableColorMaterial();
        else
            RenderSystem.disableColorMaterial();

        RenderSystem.popAttributes();
    }
}