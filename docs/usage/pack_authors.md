# Resource Pack Authors

CITs are most commonly added to the game via resource packs, here's how you do that.

// This guide will assume you have a basic understanding of creating resource packs.

## Format

CITs are stored in `.properties` files. Poperties are a common file format where
each line stores a single property containing keys and values that are separated by `=` signs.  
Lines starting with `#` will be considered comments and ignored. 

Example:
```properties
# example_stick.properties

# This is an example of a properties file for a cit
type=item
items=stick
nbt.display.Name=Example Stick
texture=example_stick.png
```

You can read more about the `.properties` format [here](https://en.wikipedia.org/wiki/.properties).


## Structure

CIT Resewn will look for cits in the game's assets. For resource packs it will look in either:

`assets/minecraft/citresewn/cit/`..  
`assets/minecraft/optifine/cit/`..  
`assets/minecraft/mcpatcher/cit/`..  

Files and directories within the cit roots must follow standard resource pack formatting rules 
such as [snake_case](https://en.wikipedia.org/wiki/Snake_case) names and no special characters.  
The name `cit.properties` is reserved for [Global Properties](/cit/global).

Examples:
```html
My Cool Resourcepack.zip/assets/minecraft/citresewn/cit/example_stick.properties
My Cool Resourcepack.zip/assets/minecraft/citresewn/cit/example_stick.png
My Cool Resourcepack.zip/assets/minecraft/optifine/cit/emerald/sword/emerald_sword.properties
My Cool Resourcepack.zip/assets/minecraft/optifine/cit/emerald/sword/emerald_sword.json
My Cool Resourcepack.zip/assets/minecraft/optifine/cit/emerald/sword/emerald_sword.png
My Cool Resourcepack.zip/assets/minecraft/optifine/cit/obsidian_dust/obsidian_dust.properties
My Cool Resourcepack.zip/assets/minecraft/optifine/cit/obsidian_dust/obsidian_dust.png
```