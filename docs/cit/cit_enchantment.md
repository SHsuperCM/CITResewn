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
| `layer` | Any whole number | - | `0` |
| `speed` | Any number | Multiplier for the glint's scroll speed. | `1.0` |
| `rotation` | Any number | Rotates the texture and scroll direction by the given degrees. | `0.0` |
| `duration` | Any number | Amount of time in seconds to pause on this cit when the used method is `cycle`. | `0.0` |
| `blend` | [Literal or Custom](#blending-functions) | Sets the OpenGL blending function used to apply the texture. | `add` |
| *`useGlint`* | Boolean | Should the default enchantment glint show on the item. | `false` |
| *`blur`* | Boolean | Should the texture be blurred before being applied. | `false` |
| *`r`* | Any positive number | Multiplier for the texture's red component. | `1.0` |
| *`g`* | Any positive number | Multiplier for the texture's green component. | `1.0` |
| *`b`* | Any positive number | Multiplier for the texture's blue component. | `1.0` |
| *`a`* | Any positive number | Multiplier for the texture's alpha component. | `1.0` |


## Blending Functions

This section is very computer graphics and opengl heavy.

### Named Functions

| Function | Function |
| --- | --- |
| `replace` | Disables blending. |
| *`glint`* | `GL_SRC_COLOR` <br> `GL_ONE` <br> *\* Mimics the vanilla glint blending.* |
| `alpha` | `GL_SRC_ALPHA` <br> `GL_ONE_MINUS_SRC_ALPHA` |
| `add` | `GL_SRC_ALPHA` <br> `GL_ONE` |
| `subtract` | `GL_ONE_MINUS_DST_COLOR` <br> `GL_ZERO` |
| `multiply` | `GL_DST_COLOR` <br> `GL_ONE_MINUS_SRC_ALPHA` |
| `dodge` | `GL_ONE` <br> `GL_ONE` |
| `burn` | `GL_ZERO` <br> `GL_ONE_MINUS_SRC_COLOR` |
| `screen` | `GL_ONE` <br> `GL_ONE_MINUS_SRC_COLOR` |
| `overlay` | `GL_DST_COLOR` <br> `GL_SRC_COLOR` |

### Custom Function

Instead of using one of the named functions, you can create your own by specifying the 
parameters that are passed onto OpenGL's `blendFuncSeparate` method.

This takes either 4 or 2 parameters with the alpha factors defaulting to `GL_ZERO, GL_ONE`. <br>
The parameters are separated by spaces and can be specified by using either a decimal constant, 
a hexadecimal constant (with a `0x` prefix) or by a named GL11 constant name.

Example:
```properties
# Ways of specifying a multiply blending function
blend=multiply
blend=GL_DST_COLOR GL_ONE_MINUS_SRC_ALPHA
blend=774 771
blend=0x306 0x303
```