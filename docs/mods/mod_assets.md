# Mod Assets

*This feature requires [Fabric API](https://www.curseforge.com/minecraft/mc-mods/fabric-api)
to be installed to work, specifically `fabric-resource-loader-v0`.*

*If you didn't know, your mod jar is basically a resourcepack!*

Example:

Let's say the mod "Gold Sticks" wants to add replacement textures for multiple gold sticks. 
The gold stick is identified by `goldsticks:golden_stick`.  
Now, the mod resources have the following file structure:
```html
src/main/resources/assets/goldsticks/models/item/golden_stick.json
src/main/resources/assets/goldsticks/textures/item/golden_stick.png

src/main/resources/assets/goldsticks/citresewn/multiple_golden_sticks_2.properties
src/main/resources/assets/goldsticks/citresewn/multiple_golden_sticks_2.png
```

Now, `multiple_golden_sticks_2.properties` contains the following CIT:
```properties
type=item
items=goldsticks:golden_stick
stackSize=2-
texture=multiple_golden_sticks_2.png
```

If CIT Resewn is present(and Fabric API), it will load that cit from the mod jar.
And if you have 2 or more golden sticks, the `multiple_golden_sticks_2.png` texture will 
be used instead of `golden_stick.png`!