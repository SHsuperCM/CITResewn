package shcm.shsupercm.fabric.citresewn.config;

import com.terraformersmc.modmenu.api.ConfigScreenFactory;
import com.terraformersmc.modmenu.api.ModMenuApi;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.NoticeScreen;
import net.minecraft.text.Text;

public class CITResewnModMenu implements ModMenuApi {
    @Override
    public ConfigScreenFactory<?> getModConfigScreenFactory() {
        if (FabricLoader.getInstance().isModLoaded("cloth-config2"))
            return new ClothConfigOpenImpl().getModConfigScreenFactory();

        return parent -> new NoticeScreen(() -> MinecraftClient.getInstance().setScreen(parent), Text.of("CIT Resewn"), Text.of("CIT Resewn requires Cloth Config to be able to show the config."));
    }

    private static class ClothConfigOpenImpl implements ModMenuApi {
        @Override
        public ConfigScreenFactory<?> getModConfigScreenFactory() {
            return CITResewnConfigScreenFactory::create;
        }
    }
}
