# CIT Elytra
<h6>Note that features marked in <em>italics</em> are unique to CIT Resewn.</h6>

This page applies to CITs marked with `type=elytra`.

CIT Elytra replaces the texture of the elytra model when worn by an entity.

The replacement is a `png` texture, formatted like the vanilla elytra texture (`assets/minecraft/textures/entity/elytra.png`).

## Conditions

### Target
The CIT must match the elytra item in the chest slot for the entity that has its elytra's appearance changed.

### Overrides
The `items` condition is ignored. This CIT applies only to `minecraft:elytra`.


## Effects

| Key | Value Type        | Description | Default |
| --- | --- | --- | --- |
| `texture` | [**[Texture Asset](/cit/cit_base/#asset-resolution) <br> (`.png`)**]{Examples: &#10 texture=./my_textures/custom_elytra.png &#10 texture=assets/minecraft/citresewn/cit/elytras/cool_elytra &#10 texture=minecraft:entity/awesome_elytra.png|right} | Replaces the texture of the elytra with the resolved texture. | None |
