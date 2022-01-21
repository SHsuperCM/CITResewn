package shcm.shsupercm.fabric.citresewn.defaults.config;

import me.shedaniel.clothconfig2.api.ConfigBuilder;
import me.shedaniel.clothconfig2.api.ConfigCategory;
import me.shedaniel.clothconfig2.api.ConfigEntryBuilder;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.text.LiteralText;
import net.minecraft.text.TranslatableText;

public class CITResewnDefaultsConfigScreenFactory {
    public static Screen create(Screen parent) {
        CITResewnDefaultsConfig currentConfig = CITResewnDefaultsConfig.INSTANCE, defaultConfig = new CITResewnDefaultsConfig();

        ConfigBuilder builder = ConfigBuilder.create()
                .setParentScreen(parent)
                .setTitle(new TranslatableText("config.citresewn.defaults.title"))
                .setSavingRunnable(currentConfig::write);

        ConfigCategory category = builder.getOrCreateCategory(new LiteralText(""));
        ConfigEntryBuilder entryBuilder = builder.entryBuilder();



        return builder.build();
    }
}
