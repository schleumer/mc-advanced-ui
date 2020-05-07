package li.ues.mcAdvancedUi.overlay;

import com.google.common.collect.Lists;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.util.Window;
import net.minecraft.text.Text;

import java.awt.Dimension;
import java.awt.Rectangle;
import java.util.List;

public class Tooltip {

    private final MinecraftClient client;
    private final List<Line> lines;
    private final boolean showItem;
    private final Dimension totalSize;

    public Tooltip(List<Text> Texts, boolean showItem) {
        this.client = MinecraftClient.getInstance();
        this.lines = Lists.newArrayList();
        this.showItem = showItem;
        this.totalSize = new Dimension();

        computeLines(Texts);
    }

    public void draw() {
        Rectangle position = getPosition();

        for (Line line : lines) {
            client.textRenderer.drawWithShadow(line.getText().asFormattedString(), position.x, position.y, 0xffffff);
            position.y += line.size.height;
        }
    }

    public List<Line> getLines() {
        return lines;
    }

    public void computeLines(List<Text> Texts) {
        Texts.forEach(c -> {
            Dimension size = getLineSize(c, Texts);

            totalSize.setSize(Math.max(totalSize.width, size.width), totalSize.height + size.height);

            lines.add(new Line(c, size));
        });
    }

    private Dimension getLineSize(Text text, List<Text> texts) {
        return new Dimension(client.textRenderer.getStringWidth(text.asFormattedString()), client.textRenderer.fontHeight + 1);
    }

    public Rectangle getPosition() {
        Window window = MinecraftClient.getInstance().getWindow();
        double multiplier = 1 / OverlayRenderer.multiplier;

        return new Rectangle(
                0, // Center it
                (int) ((window.getScaledHeight() * multiplier) - totalSize.height),
                totalSize.width,
                totalSize.height
        );
    }

    public static class Line {

        private final Text Text;
        private final Dimension size;

        public Line(Text Text, Dimension size) {
            this.Text = Text;
            this.size = size;
        }

        public Text getText() {
            return Text;
        }

        public Dimension getSize() {
            return size;
        }
    }
}
