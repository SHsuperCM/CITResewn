package shcm.shsupercm.fabric.citresewn.config;

import com.terraformersmc.modmenu.api.ConfigScreenFactory;
import com.terraformersmc.modmenu.api.ModMenuApi;
import net.fabricmc.loader.api.FabricLoader;
import shcm.shsupercm.fabric.citresewn.OptionalCompat;

public class CITResewnModMenu implements ModMenuApi {
    @Override
    public ConfigScreenFactory<?> getModConfigScreenFactory() {
        return OptionalCompat.getModConfigScreenFactory()::apply;
    }
}
