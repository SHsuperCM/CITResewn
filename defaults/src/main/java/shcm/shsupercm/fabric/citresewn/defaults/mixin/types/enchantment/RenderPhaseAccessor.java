package shcm.shsupercm.fabric.citresewn.defaults.mixin.types.enchantment;

import net.minecraft.client.render.RenderPhase;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(RenderPhase.class)
public interface RenderPhaseAccessor {
    /*? <1.21 {*/
    /*@Accessor("ARMOR_GLINT_PROGRAM") static RenderPhase.ShaderProgram ARMOR_GLINT_SHADER() { throw new RuntimeException(); }
    *//*?}*/
    @Accessor("ARMOR_ENTITY_GLINT_PROGRAM") static RenderPhase.ShaderProgram ARMOR_ENTITY_GLINT_SHADER() { throw new RuntimeException(); }
    @Accessor("TRANSLUCENT_GLINT_PROGRAM") static RenderPhase.ShaderProgram TRANSLUCENT_GLINT_SHADER() { throw new RuntimeException(); }
    @Accessor("GLINT_PROGRAM") static RenderPhase.ShaderProgram GLINT_SHADER() { throw new RuntimeException(); }
    /*? <1.21 {*/
    /*@Accessor("DIRECT_GLINT_PROGRAM") static RenderPhase.ShaderProgram DIRECT_GLINT_SHADER() { throw new RuntimeException(); }
    *//*?}*/
    @Accessor("ENTITY_GLINT_PROGRAM") static RenderPhase.ShaderProgram ENTITY_GLINT_SHADER() { throw new RuntimeException(); }
    @Accessor("DIRECT_ENTITY_GLINT_PROGRAM") static RenderPhase.ShaderProgram DIRECT_ENTITY_GLINT_SHADER() { throw new RuntimeException(); }
    @Accessor("DISABLE_CULLING") static RenderPhase.Cull DISABLE_CULLING() { throw new RuntimeException(); }
    @Accessor("EQUAL_DEPTH_TEST") static RenderPhase.DepthTest EQUAL_DEPTH_TEST() { throw new RuntimeException(); }
    @Accessor("COLOR_MASK") static RenderPhase.WriteMaskState COLOR_MASK() { throw new RuntimeException(); }
    @Accessor("VIEW_OFFSET_Z_LAYERING") static RenderPhase.Layering VIEW_OFFSET_Z_LAYERING() { throw new RuntimeException(); }
    @Accessor(/*? <1.20 {*//*"ITEM_TARGET"*//*?} else {*/"ITEM_ENTITY_TARGET"/*?}*/) static RenderPhase.Target ITEM_ENTITY_TARGET() { throw new RuntimeException(); }
}
