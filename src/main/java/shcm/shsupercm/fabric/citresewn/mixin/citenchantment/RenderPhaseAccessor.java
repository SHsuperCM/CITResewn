package shcm.shsupercm.fabric.citresewn.mixin.citenchantment;

import net.minecraft.client.render.RenderPhase;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(RenderPhase.class)
public interface RenderPhaseAccessor {
    @Accessor("ARMOR_GLINT_SHADER") static RenderPhase.Shader ARMOR_GLINT_SHADER() { throw new RuntimeException(); }
    @Accessor("ARMOR_ENTITY_GLINT_SHADER") static RenderPhase.Shader ARMOR_ENTITY_GLINT_SHADER() { throw new RuntimeException(); }
    @Accessor("TRANSLUCENT_GLINT_SHADER") static RenderPhase.Shader TRANSLUCENT_GLINT_SHADER() { throw new RuntimeException(); }
    @Accessor("GLINT_SHADER") static RenderPhase.Shader GLINT_SHADER() { throw new RuntimeException(); }
    @Accessor("DIRECT_GLINT_SHADER") static RenderPhase.Shader DIRECT_GLINT_SHADER() { throw new RuntimeException(); }
    @Accessor("ENTITY_GLINT_SHADER") static RenderPhase.Shader ENTITY_GLINT_SHADER() { throw new RuntimeException(); }
    @Accessor("DIRECT_ENTITY_GLINT_SHADER") static RenderPhase.Shader DIRECT_ENTITY_GLINT_SHADER() { throw new RuntimeException(); }
    @Accessor("DISABLE_CULLING") static RenderPhase.Cull DISABLE_CULLING() { throw new RuntimeException(); }
    @Accessor("EQUAL_DEPTH_TEST") static RenderPhase.DepthTest EQUAL_DEPTH_TEST() { throw new RuntimeException(); }
    @Accessor("COLOR_MASK") static RenderPhase.WriteMaskState COLOR_MASK() { throw new RuntimeException(); }
    @Accessor("VIEW_OFFSET_Z_LAYERING") static RenderPhase.Layering VIEW_OFFSET_Z_LAYERING() { throw new RuntimeException(); }
    @Accessor("ITEM_TARGET") static RenderPhase.Target ITEM_TARGET() { throw new RuntimeException(); }
}
