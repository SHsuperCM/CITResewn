# CIT Armor
<h6>Note that features marked in <em>italics</em> are unique to CIT Resewn.</h6>

This page applies to CITs marked with `type=armor`.

CIT Armor replaces the texture of the armor model when worn by an entity.

The replacement is a `png` texture, formatted like the vanilla armor textures (`assets/minecraft/textures/models/armor/`).

## Conditions

### Target
The CIT must match the armor item worn by the entity that has its armor's appearance changed.

### Overrides
The `items` condition may contain only armor items.


## Effects

| Key                             | Value Type        | Description | Default |
| --- | --- | --- | --- |
| `texture.`[**<ins>  layer  </ins>**]{Examples: &#10 texture.diamond_layer_1 &#10 texture.leather_layer_2_overlay &#10 texture.turtle_layer_1|right} | [**[Texture Asset](/cit/cit_base/#asset-resolution) <br> (`.png`)**]{Examples: &#10 texture.diamond_layer_1=./my_textures/awesome_diamond_1.png &#10 texture.leather_layer_2_overlay=assets/minecraft/citresewn/cit/armor/cool_armor_2_overlay &#10 texture.turtle_layer_1=minecraft:textures/models/armor/red_turtle_shell.png|right} | Replaces the texture of the armor layer with the resolved texture. The layer names are the names of the texture files that are being replaced. For a list of layers, see `assets/minecraft/textures/models/armor`. | None |
