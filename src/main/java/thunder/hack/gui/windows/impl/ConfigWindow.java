package thunder.hack.gui.windows.impl;

import com.google.common.collect.Lists;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.util.InputUtil;
import net.minecraft.util.StringHelper;
import org.lwjgl.glfw.GLFW;
import thunder.hack.ThunderHack;
import thunder.hack.gui.clickui.ClickGUI;
import thunder.hack.gui.clickui.impl.SliderElement;
import thunder.hack.gui.font.FontRenderers;
import thunder.hack.gui.windows.WindowBase;
import thunder.hack.gui.windows.WindowsScreen;
import thunder.hack.modules.client.HudEditor;
import thunder.hack.utility.render.Render2DEngine;
import thunder.hack.utility.render.animation.AnimationUtility;

import java.awt.*;
import java.util.ArrayList;
import java.util.Objects;

import static thunder.hack.modules.Module.mc;
import static thunder.hack.modules.client.ClientSettings.isRu;

public class ConfigWindow extends WindowBase {
    private static ConfigWindow instance = new ConfigWindow();
    private ArrayList<ConfigPlate> configPlates = new ArrayList<>();
    private int listeningId = -1;
    private String search = "Search", addName = "Name";

    public ConfigWindow() {
        this(mc.getWindow().getScaledWidth() / 2f - 70, mc.getWindow().getScaledHeight() / 2f - 120, 200, 180);
    }

    public ConfigWindow(float x, float y, float width, float height) {
        super(x, y, width, height, "Config");
        refresh();
    }

    public static ConfigWindow get() {
        if (instance == null)
            instance = new ConfigWindow();
        instance.refresh();
        return instance;
    }

    @Override
    public void render(DrawContext context, int mouseX, int mouseY) {
        super.render(context, mouseX, mouseY);

        Color color = new Color(0xC5333333, true);
        Color color2 = new Color(0xC55B5B5B, true);
        Color hoveredColor = new Color(0xC5494949, true);
        int textColor = new Color(0xBDBDBD).getRGB();

        boolean hover1 = Render2DEngine.isHovered(mouseX, mouseY, getX() + getWidth() - 90, getY() + 3, 70, 10);

        Render2DEngine.drawRectWithOutline(context.getMatrices(), getX() + getWidth() - 90, getY() + 3, 70, 10, hover1 ? hoveredColor : color, color2);
        FontRenderers.sf_medium_mini.drawString(context.getMatrices(), search, getX() + getWidth() - 86, getY() + 7, new Color(0xD5D5D5).getRGB());

        if (configPlates.isEmpty()) {
            FontRenderers.sf_medium.drawCenteredString(context.getMatrices(), isRu() ? "Тут пока пусто" : "It's empty here yet",
                    getX() + getWidth() / 2f, getY() + getHeight() / 2f, new Color(0xBDBDBD).getRGB());
            return;
        }

        String blink = (System.currentTimeMillis() / 240) % 2 == 0 ? "" : "   <<<<";
        String blink2 = (System.currentTimeMillis() / 240) % 2 == 0 ? "" : "l";

        {
            // Name
            boolean hover2 = Render2DEngine.isHovered(mouseX, mouseY, getX() + 11, getY() + 19, getWidth() - 28, 11);
            Render2DEngine.drawRectWithOutline(context.getMatrices(), getX() + 11, getY() + 19, getWidth() - 28, 11, hover2 ? hoveredColor : color, color2);
            FontRenderers.sf_medium.drawString(context.getMatrices(), addName + (listeningId == -3 ? blink2 : "")
                    , getX() + 13, getY() + 23, new Color(0xBDBDBD).getRGB());

            // Add
            boolean hover5 = Render2DEngine.isHovered(mouseX, mouseY, getX() + getWidth() - 15, getY() + 19, 11, 11);
            Render2DEngine.drawRectWithOutline(context.getMatrices(), getX() + getWidth() - 15, getY() + 19, 11, 11, hover5 ? hoveredColor : color, color2);
            FontRenderers.categories.drawString(context.getMatrices(), "+", getX() + getWidth() - 12, getY() + 23, -1);
        }

        Render2DEngine.horizontalGradient(context.getMatrices(), getX() + 2, getY() + 33f, getX() + 2 + getWidth() / 2f - 2, getY() + 33.5f, Render2DEngine.injectAlpha(HudEditor.textColor.getValue().getColorObject(), 0), HudEditor.textColor.getValue().getColorObject());
        Render2DEngine.horizontalGradient(context.getMatrices(), getX() + 2 + getWidth() / 2f - 2, getY() + 33f, getX() + 2 + getWidth() - 4, getY() + 33.5f, HudEditor.textColor.getValue().getColorObject(), Render2DEngine.injectAlpha(HudEditor.textColor.getValue().getColorObject(), 0));

        int id = 0;
        for (ConfigPlate configPlate : configPlates) {
            id++;
            if ((int) (configPlate.offset + getY() + 50) + getScrollOffset() > getY() + getHeight() || configPlate.offset + getScrollOffset() + getY() < getY())
                continue;

            // Name
            Render2DEngine.drawRectWithOutline(context.getMatrices(), getX() + 11, configPlate.offset + getY() + 36 + getScrollOffset(), getWidth() - 52, 11, color, color2);
            FontRenderers.sf_medium.drawString(context.getMatrices(), configPlate.name() + (Objects.equals(configPlate.name() + ".th", ThunderHack.configManager.currentConfig.getName()) ? blink : "")
                    , getX() + 13, configPlate.offset + getY() + 40 + getScrollOffset(), textColor);

            // Load
            boolean hover3 = Render2DEngine.isHovered(mouseX, mouseY, getX() + getWidth() - 39, configPlate.offset + getY() + 36 + getScrollOffset(), 22, 11);
            Render2DEngine.drawRectWithOutline(context.getMatrices(), getX() + getWidth() - 39, configPlate.offset + getY() + 36 + getScrollOffset(), 22, 11, hover3 ? hoveredColor : color, color2);
            FontRenderers.sf_medium.drawString(context.getMatrices(), "Load", getX() + getWidth() - 37, configPlate.offset + getY() + 40 + getScrollOffset(), new Color(0xBDBDBD).getRGB());

            // Delete
            boolean hover5 = Render2DEngine.isHovered(mouseX, mouseY, getX() + getWidth() - 15, configPlate.offset + getY() + 36 + getScrollOffset(), 11, 11);
            Render2DEngine.drawRectWithOutline(context.getMatrices(), getX() + getWidth() - 15, configPlate.offset + getY() + 36 + getScrollOffset(), 11, 11, hover5 ? hoveredColor : color, color2);
            FontRenderers.icons.drawString(context.getMatrices(), "w", getX() + getWidth() - 15, configPlate.offset + getY() + 40 + getScrollOffset(), -1);
            FontRenderers.sf_medium_mini.drawString(context.getMatrices(), id + ".", getX() + 3, configPlate.offset + getY() + 41 + getScrollOffset(), textColor);
        }
    }

    @Override
    public void mouseClicked(double mouseX, double mouseY, int button) {
        super.mouseClicked(mouseX, mouseY, button);

        if (Render2DEngine.isHovered(mouseX, mouseY, getX() + getWidth() - 90, getY() + 3, 70, 10)) {
            listeningId = -2;
            search = "";
        }

        if (Render2DEngine.isHovered(mouseX, mouseY, getX() + getWidth() - 15, getY() + 3, 10, 10))
            mc.setScreen(ClickGUI.getClickGui());

        boolean hoveringName = Render2DEngine.isHovered(mouseX, mouseY, getX() + 11, getY() + 19, getWidth() - 28, 11);
        boolean hoveringAdd = Render2DEngine.isHovered(mouseX, mouseY, getX() + getWidth() - 15, getY() + 19, 11, 11);

        if (hoveringName) {
            addName = "";
            listeningId = -3;
        }

        if (hoveringAdd && !addName.isEmpty()) {
            ThunderHack.configManager.save(addName);
            addName = "";
            refresh();
        }

        ArrayList<ConfigPlate> copy = Lists.newArrayList(configPlates);
        for (ConfigPlate configPlate : copy) {
            if ((int) (configPlate.offset + getY() + 50) + getScrollOffset() > getY() + getHeight())
                continue;

            boolean hoveringRemove = Render2DEngine.isHovered(mouseX, mouseY, getX() + getWidth() - 15, configPlate.offset + getY() + 36 + getScrollOffset(), 11, 11);
            boolean hoverLoad = Render2DEngine.isHovered(mouseX, mouseY, getX() + getWidth() - 39, configPlate.offset + getY() + 36 + getScrollOffset(), 22, 11);

            if(hoverLoad)
                ThunderHack.configManager.load(configPlate.name());

            if (hoveringRemove) {
                ThunderHack.configManager.delete(configPlate.name());
                refresh();
            }
        }
    }

    @Override
    public void keyPressed(int keyCode, int scanCode, int modifiers) {
        if (keyCode == GLFW.GLFW_KEY_F && (InputUtil.isKeyPressed(mc.getWindow().getHandle(), GLFW.GLFW_KEY_LEFT_CONTROL) || InputUtil.isKeyPressed(mc.getWindow().getHandle(), GLFW.GLFW_KEY_RIGHT_CONTROL))) {
            listeningId = -2;
            return;
        }

        if (listeningId != -1) {
            switch (keyCode) {
                case GLFW.GLFW_KEY_ENTER -> {
                    if (listeningId != -2)
                        listeningId = -1;
                }

                case GLFW.GLFW_KEY_ESCAPE -> {
                    if (listeningId == -2)
                        search = "Search";
                    listeningId = -1;
                    refresh();
                }

                case GLFW.GLFW_KEY_BACKSPACE -> {
                    if (listeningId == -2) {
                        search = SliderElement.removeLastChar(search);
                        refresh();
                        if (Objects.equals(search, "")) {
                            listeningId = -1;
                            search = "Search";
                        }
                        return;
                    }

                    if (listeningId == -3)
                        addName = SliderElement.removeLastChar(addName);
                }

                case GLFW.GLFW_KEY_SPACE -> {
                    if (listeningId == -2)
                        search = search + " ";
                }
            }
        }
    }

    @Override
    public void charTyped(char key, int keyCode) {
        if (StringHelper.isValidChar(key) && listeningId != -1) {
            if (listeningId == -2)
                search = search + key;

            if (listeningId == -3) {
                addName = addName + key;
            }

            refresh();
        }
    }

    private void refresh() {
        resetScroll();
        configPlates.clear();
        int id1 = 0;
        for (String s : ThunderHack.configManager.getConfigList())
            if (search.equals("Search") || search.isEmpty() || s.contains(search)) {
                configPlates.add(new ConfigPlate(id1, id1 * 20 + 8, s));
                id1++;
            }
    }

    private record ConfigPlate(int id, float offset, String name) {
    }
}
