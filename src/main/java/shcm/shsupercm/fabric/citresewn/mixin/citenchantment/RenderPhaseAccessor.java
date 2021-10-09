package shcm.shsupercm.fabric.citresewn.mixin.citenchantment;

import net.minecraft.client.render.RenderPhase;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(RenderPhase.class)
public interface RenderPhaseAccessor {
    @Accessor("GLINT_TRANSPARENCY") static RenderPhase.Transparency GLINT_TRANSPARENCY() { throw new RuntimeException(); }
    @Accessor("ARMOR_GLINT_SHADER") static RenderPhase.Shader ARMOR_GLINT_SHADER() { throw new RuntimeException(); }
    @Accessor("ARMOR_ENTITY_GLINT_SHADER") static RenderPhase.Shader ARMOR_ENTITY_GLINT_SHADER() { throw new RuntimeException(); }
    @Accessor("TRANSLUCENT_GLINT_SHADER") static RenderPhase.Shader TRANSLUCENT_GLINT_SHADER() { throw new RuntimeException(); }
    @Accessor("GLINT_SHADER") static RenderPhase.Shader GLINT_SHADER() { throw new RuntimeException(); }
    @Accessor("DIRECT_GLINT_SHADER") static RenderPhase.Shader DIRECT_GLINT_SHADER() { throw new RuntimeException(); }
    @Accessor("ENTITY_GLINT_SHADER") static RenderPhase.Shader ENTITY_GLINT_SHADER() { throw new RuntimeException(); }
    @Accessor("DIRECT_ENTITY_GLINT_SHADER") static RenderPhase.Shader DIRECT_ENTITY_GLINT_SHADER() { throw new RuntimeException(); }
    @Accessor("MIPMAP_BLOCK_ATLAS_TEXTURE") static RenderPhase.Texture MIPMAP_BLOCK_ATLAS_TEXTURE() { throw new RuntimeException(); }
    @Accessor("BLOCK_ATLAS_TEXTURE") static RenderPhase.Texture BLOCK_ATLAS_TEXTURE() { throw new RuntimeException(); }
    @Accessor("DEFAULT_TEXTURING") static RenderPhase.Texturing DEFAULT_TEXTURING() { throw new RuntimeException(); }
    @Accessor("GLINT_TEXTURING") static RenderPhase.Texturing GLINT_TEXTURING() { throw new RuntimeException(); }
    @Accessor("ENTITY_GLINT_TEXTURING") static RenderPhase.Texturing ENTITY_GLINT_TEXTURING() { throw new RuntimeException(); }
    @Accessor("ENABLE_LIGHTMAP") static RenderPhase.Lightmap ENABLE_LIGHTMAP() { throw new RuntimeException(); }
    @Accessor("DISABLE_LIGHTMAP") static RenderPhase.Lightmap DISABLE_LIGHTMAP() { throw new RuntimeException(); }
    @Accessor("ENABLE_OVERLAY_COLOR") static RenderPhase.Overlay ENABLE_OVERLAY_COLOR() { throw new RuntimeException(); }
    @Accessor("DISABLE_OVERLAY_COLOR") static RenderPhase.Overlay DISABLE_OVERLAY_COLOR() { throw new RuntimeException(); }
    @Accessor("ENABLE_CULLING") static RenderPhase.Cull ENABLE_CULLING() { throw new RuntimeException(); }
    @Accessor("DISABLE_CULLING") static RenderPhase.Cull DISABLE_CULLING() { throw new RuntimeException(); }
    @Accessor("ALWAYS_DEPTH_TEST") static RenderPhase.DepthTest ALWAYS_DEPTH_TEST() { throw new RuntimeException(); }
    @Accessor("EQUAL_DEPTH_TEST") static RenderPhase.DepthTest EQUAL_DEPTH_TEST() { throw new RuntimeException(); }
    @Accessor("LEQUAL_DEPTH_TEST") static RenderPhase.DepthTest LEQUAL_DEPTH_TEST() { throw new RuntimeException(); }
    @Accessor("ALL_MASK") static RenderPhase.WriteMaskState ALL_MASK() { throw new RuntimeException(); }
    @Accessor("COLOR_MASK") static RenderPhase.WriteMaskState COLOR_MASK() { throw new RuntimeException(); }
    @Accessor("DEPTH_MASK") static RenderPhase.WriteMaskState DEPTH_MASK() { throw new RuntimeException(); }
    @Accessor("NO_LAYERING") static RenderPhase.Layering NO_LAYERING() { throw new RuntimeException(); }
    @Accessor("POLYGON_OFFSET_LAYERING") static RenderPhase.Layering POLYGON_OFFSET_LAYERING() { throw new RuntimeException(); }
    @Accessor("VIEW_OFFSET_Z_LAYERING") static RenderPhase.Layering VIEW_OFFSET_Z_LAYERING() { throw new RuntimeException(); }
    @Accessor("MAIN_TARGET") static RenderPhase.Target MAIN_TARGET() { throw new RuntimeException(); }
    @Accessor("OUTLINE_TARGET") static RenderPhase.Target OUTLINE_TARGET() { throw new RuntimeException(); }
    @Accessor("TRANSLUCENT_TARGET") static RenderPhase.Target TRANSLUCENT_TARGET() { throw new RuntimeException(); }
    @Accessor("PARTICLES_TARGET") static RenderPhase.Target PARTICLES_TARGET() { throw new RuntimeException(); }
    @Accessor("WEATHER_TARGET") static RenderPhase.Target WEATHER_TARGET() { throw new RuntimeException(); }
    @Accessor("CLOUDS_TARGET") static RenderPhase.Target CLOUDS_TARGET() { throw new RuntimeException(); }
    @Accessor("ITEM_TARGET") static RenderPhase.Target ITEM_TARGET() { throw new RuntimeException(); }
}
