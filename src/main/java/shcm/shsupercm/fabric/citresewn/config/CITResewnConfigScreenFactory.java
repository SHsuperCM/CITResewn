package shcm.shsupercm.fabric.citresewn.config;

import me.shedaniel.clothconfig2.api.ConfigBuilder;
import me.shedaniel.clothconfig2.api.ConfigCategory;
import me.shedaniel.clothconfig2.api.ConfigEntryBuilder;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;

import java.util.function.Function;

/**
 * Cloth Config integration to CIT Resewn's config
 * @see CITResewnConfig
 */
public class CITResewnConfigScreenFactory {
    /**
     * Used to get CIT Resewn - Defaults's Cloth Config implementation.
     */
    public static final String DEFAULTS_CONFIG_ENTRYPOINT = "citresewn-defaults:config_screen";

    /**
     * Creates a Cloth Config screen for the current active config instance.
     * @param parent parent to return to from the config screen
     * @return the config screen
     * @throws NoClassDefFoundError if Cloth Config is not present
     */
    public static Screen create(Screen parent) {
        CITResewnConfig currentConfig = CITResewnConfig.INSTANCE, defaultConfig = new CITResewnConfig();

        ConfigBuilder builder = ConfigBuilder.create()
                .setParentScreen(parent)
                .setTitle(Text.translatable("config.citresewn.title"))
                .setSavingRunnable(currentConfig::write);

        ConfigCategory category = builder.getOrCreateCategory(Text.empty());
        ConfigEntryBuilder entryBuilder = builder.entryBuilder();

        category.addEntry(entryBuilder.startBooleanToggle(Text.translatable("config.citresewn.enabled.title"), currentConfig.enabled)
                .setTooltip(Text.translatable("config.citresewn.enabled.tooltip"))
                .setSaveConsumer(newConfig -> {
                    if (currentConfig.enabled != newConfig) {
                        currentConfig.enabled = newConfig;
                        MinecraftClient.getInstance().reloadResources();
                    }
                })
                .setDefaultValue(defaultConfig.enabled)
                .build());

        if (FabricLoader.getInstance().isModLoaded("citresewn-defaults")) {
            class CurrentScreen { boolean prevToggle = false; } final CurrentScreen currentScreen = new CurrentScreen();
            category.addEntry(entryBuilder.startBooleanToggle(Text.translatable("config.citresewn-defaults.title"), false)
                    .setTooltip(Text.translatable("config.citresewn-defaults.tooltip"))

                    .setYesNoTextSupplier((b) -> {
                        if (b != currentScreen.prevToggle) {
                            //noinspection unchecked
                            MinecraftClient.getInstance().setScreen((Screen) FabricLoader.getInstance().getEntrypoints(DEFAULTS_CONFIG_ENTRYPOINT, Function.class).stream().findAny().orElseThrow().apply(create(parent)));

                            currentScreen.prevToggle = b;
                        }

                        return Text.translatable("config.citresewn.configure");
                    })
                    .build());
        }

        category.addEntry(entryBuilder.startBooleanToggle(Text.translatable("config.citresewn.mute_errors.title"), currentConfig.mute_errors)
                .setTooltip(Text.translatable("config.citresewn.mute_errors.tooltip"))
                .setSaveConsumer(newConfig -> currentConfig.mute_errors = newConfig)
                .setDefaultValue(defaultConfig.mute_errors)
                .build());

        category.addEntry(entryBuilder.startBooleanToggle(Text.translatable("config.citresewn.mute_warns.title"), currentConfig.mute_warns)
                .setTooltip(Text.translatable("config.citresewn.mute_warns.tooltip"))
                .setSaveConsumer(newConfig -> currentConfig.mute_warns = newConfig)
                .setDefaultValue(defaultConfig.mute_warns)
                .build());

        category.addEntry(entryBuilder.startIntSlider(Text.translatable("config.citresewn.cache_ms.title"), currentConfig.cache_ms / 50, 0, 5 * 20)
                .setTooltip(Text.translatable("config.citresewn.cache_ms.tooltip"))
                .setSaveConsumer(newConfig -> currentConfig.cache_ms = newConfig * 50)
                .setDefaultValue(defaultConfig.cache_ms / 50)
                .setTextGetter(ticks -> {
                    if (ticks <= 1)
                        return Text.translatable("config.citresewn.cache_ms.ticks." + ticks).formatted(Formatting.AQUA);

                    Formatting color = Formatting.DARK_RED;

                    if (ticks <= 40) color = Formatting.RED;
                    if (ticks <= 20) color = Formatting.GOLD;
                    if (ticks <= 10) color = Formatting.DARK_GREEN;
                    if (ticks <= 5) color = Formatting.GREEN;

                    return Text.translatable("config.citresewn.cache_ms.ticks.any", ticks).formatted(color);
                })
                .build());

        category.addEntry(entryBuilder.startBooleanToggle(Text.translatable("config.citresewn.broken_paths.title"), currentConfig.broken_paths)
                .setTooltip(Text.translatable("config.citresewn.broken_paths.tooltip"))
                .setSaveConsumer(newConfig -> currentConfig.broken_paths = newConfig)
                .setDefaultValue(defaultConfig.broken_paths)
                .requireRestart()
                .build());

        return builder.build();
    }
}
