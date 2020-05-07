package li.ues.mcAdvancedUi.overlay;

import com.google.common.base.Strings;
import com.mojang.text2speech.Narrator;
import net.minecraft.block.Blocks;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.ChatScreen;
import net.minecraft.container.AnvilContainer;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.enchantment.Enchantments;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.HungerManager;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;
import net.minecraft.text.*;
import net.minecraft.util.Formatting;
import net.minecraft.util.Hand;
import net.minecraft.util.Identifier;
import net.minecraft.util.hit.HitResult;
import net.minecraft.world.World;
import net.minecraft.world.level.storage.AnvilLevelStorage;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;


public class TickHandler {
    public static TickHandler INSTANCE = new TickHandler();

    public Tooltip tooltip = null;

    public void renderOverlay() {
        OverlayRenderer.renderOverlay();
    }

    private List<Text> itemLabel(ItemStack stack) {
        ArrayList<Text> lines = new ArrayList<>();

        lines.add(stack.toHoverableText());

        if (stack.isDamageable()) {
            lines.add(new LiteralText(
                    "Damage: " +
                            stack.getDamage() +
                            " / " +
                            stack.getMaxDamage()
            ));

            lines.add(new LiteralText(
                    "Repair Cost: " +
                            stack.getRepairCost() +
                            " => " +
                            AnvilContainer.getNextCost(stack.getRepairCost())
            ));
        }

        if (stack.hasEnchantments()) {
            Map<Enchantment, Integer> enchantments = EnchantmentHelper.getEnchantments(stack.getEnchantments());
            for (Map.Entry<Enchantment, Integer> entry : enchantments.entrySet()) {
                lines.add(
                        new TranslatableText(
                                entry.getKey().getTranslationKey()
                        ).append(
                                new LiteralText(" LVL: " + entry.getValue() + " / " + entry.getKey().getMaximumLevel())
                        )
                );
            }
        }

        return lines;
    }

    public void tickClient() {
        MinecraftClient client = MinecraftClient.getInstance();
        World world = client.world;
        PlayerEntity player = client.player;

        if (client.keyboard == null)
            return;

        if (world != null && player != null) {
            ItemStack mainHand = player.getStackInHand(Hand.MAIN_HAND);
            ItemStack offHand = player.getStackInHand(Hand.OFF_HAND);

            ArrayList<Text> texts = new ArrayList<>();

            int neededExperience = player.getNextLevelExperience();
            double currentExperience = Math.ceil(player.experienceProgress * neededExperience);

            texts.add(new LiteralText(
                    "Level: " +
                            player.experienceLevel +
                            " " +
                            currentExperience +
                            " / " +
                            neededExperience +
                            " " +
                            (player.experienceProgress * 100) +
                            "%"
            ));

            texts.add(new LiteralText(
                    "HP: " + player.getHealth() + " / " + player.getMaximumHealth()
            ));

            HungerManager hunger = player.getHungerManager();

            LiteralText hungerLabel = new LiteralText(
                    "Food: " + hunger.getFoodLevel() + " / 20 by " + hunger.getSaturationLevel()
            );

            if (!hunger.isNotFull()) {
                hungerLabel.setStyle(new Style().setColor(Formatting.GREEN));
            } else {
                if (hunger.getFoodLevel() < 7) {
                    hungerLabel.setStyle(new Style().setColor(Formatting.RED));
                }
            }

            texts.add(hungerLabel);

            texts.add(new LiteralText(""));

            if (!mainHand.isEmpty()) {
                texts.addAll(itemLabel(mainHand));
            }

            if (!mainHand.isEmpty() && !offHand.isEmpty()) {
                texts.add(new LiteralText(""));
            }

            if (!offHand.isEmpty()) {
                texts.addAll(itemLabel(offHand));
            }

            tooltip = new Tooltip(texts, true);
        }

    }

    public static TickHandler instance() {
        if (INSTANCE == null)
            INSTANCE = new TickHandler();
        return INSTANCE;
    }
}
