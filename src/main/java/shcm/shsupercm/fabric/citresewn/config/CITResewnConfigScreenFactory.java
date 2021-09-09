package shcm.shsupercm.fabric.citresewn.config;

import me.shedaniel.clothconfig2.api.ConfigBuilder;
import me.shedaniel.clothconfig2.api.ConfigCategory;
import me.shedaniel.clothconfig2.api.ConfigEntryBuilder;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.text.LiteralText;
import net.minecraft.text.TranslatableText;

public class CITResewnConfigScreenFactory {
    public static Screen create(Screen parent) {
        CITResewnConfig currentConfig = CITResewnConfig.INSTANCE(), defaultConfig = new CITResewnConfig();

        ConfigBuilder builder = ConfigBuilder.create()
                .setParentScreen(parent)
                .setTitle(new TranslatableText("config.citresewn.title"))
                .setSavingRunnable(currentConfig::write);

        ConfigCategory category = builder.getOrCreateCategory(new LiteralText(""));
        ConfigEntryBuilder entryBuilder = builder.entryBuilder();

        category.addEntry(entryBuilder.startBooleanToggle(new TranslatableText("config.citresewn.enabled.title"), currentConfig.enabled)
                .setTooltip(new TranslatableText("config.citresewn.enabled.tooltip"))
                .setSaveConsumer(newConfig -> {
                    if (currentConfig.enabled != newConfig) {
                        currentConfig.enabled = newConfig;
                        MinecraftClient.getInstance().reloadResources();
                    }
                })
                .setDefaultValue(defaultConfig.enabled)
                .build());

        category.addEntry(entryBuilder.startBooleanToggle(new TranslatableText("config.citresewn.mute_errors.title"), currentConfig.mute_errors)
                .setTooltip(new TranslatableText("config.citresewn.mute_errors.tooltip"))
                .setSaveConsumer(newConfig -> currentConfig.mute_errors = newConfig)
                .setDefaultValue(defaultConfig.mute_errors)
                .build());

        category.addEntry(entryBuilder.startBooleanToggle(new TranslatableText("config.citresewn.mute_warns.title"), currentConfig.mute_warns)
                .setTooltip(new TranslatableText("config.citresewn.mute_warns.tooltip"))
                .setSaveConsumer(newConfig -> currentConfig.mute_warns = newConfig)
                .setDefaultValue(defaultConfig.mute_warns)
                .build());

        category.addEntry(entryBuilder.startBooleanToggle(new TranslatableText("config.citresewn.broken_paths.title"), currentConfig.broken_paths)
                .setTooltip(new TranslatableText("config.citresewn.broken_paths.tooltip"))
                .setSaveConsumer(newConfig -> currentConfig.broken_paths = newConfig)
                .setDefaultValue(defaultConfig.broken_paths)
                .requireRestart()
                .build());

        return builder.build();
    }
}
