# CIT Enchantment
<h6>Note that features marked in <em>italics</em> are unique to CIT Resewn.</h6>

This page applies to CITs marked with `type=enchantment`.

## This section is not complete! It is still being written :/

CIT Enchantment replaces the purple glint texture that appears on enchanted items.  
These glints can be layered, blended and even be placed on items without vanilla glints.

The glint is a `png` texture, formatted like the vanilla glint texture (`assets/minecraft/textures/misc/enchanted_item_glint.png`).

## Conditions

### Target
The CIT must match the item in that has its glint changed.

## Effects

| Key | Value Type                       | Description | Default |
| --- | --- | --- | --- |
| `texture` | [**[Texture Asset](/cit/cit_base/#asset-resolution) <br> (`.png`)**]{Examples: &#10 texture=./my_textures/custom_glint.png &#10 texture=assets/minecraft/citresewn/cit/ench/glint_3 &#10 texture=minecraft:misc/my_cool_enchanted_item_glint.png|right} | Replaces the texture of this glint layer with the resolved texture. | None |
| `layer` | Any whole number |  | `0` |
| `speed` | Any number |  | `1.0` |
| `rotation` | Any number |  | `0.0` |
| `duration` | Any number | Not implemented yet | `0.0` |
| `blend` | [Literal or Custom](#blending-functions) |  | `add` |
| *`useGlint`* | Boolean |  | `false` |
| *`blur`* | Boolean |  | `false` |
| *`r`* | Any positive number |  | `1.0` |
| *`g`* | Any positive number |  | `1.0` |
| *`b`* | Any positive number |  | `1.0` |
| *`a`* | Any positive number |  | `1.0` |


## Blending Functions

### Named Functions

| Function | Effect |
| --- | --- |
| `replace` | _ |
| *`glint`* | _ |
| `alpha` | _ |
| `add` | _ |
| `subtract` | _ |
| `multiply` | _ |
| `dodge` | _ |
| `burn` | _ |
| `screen` | _ |
| `overlay` | _ |

### Custom Function

_