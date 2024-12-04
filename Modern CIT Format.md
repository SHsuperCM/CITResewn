# Prelude
***
`//07:59 - 2024.11.25`

As snapshots are closing in for 1.21.4, it's become clear that the old CIT format is fragile and requires constant tweaking between versions.
The latest edition to this saga is snapshot [24w45a](https://www.minecraft.net/en-us/article/minecraft-snapshot-24w45a) which completely overhauls the definition of models for items, in a similar fashion to blockstates' model definitions.
Additionally, in Fabric's discord server while in discussion of how to port over FRAPI. Mojang dev boq has confirmed that the plan is to get rid of the `BakedModel` structure as it is no longer required, since all extra model data could be offloaded to the item model or the blockstate structure depending on what the model is used for. 

## Maintaining CIT Resewn
`//11:23 - 2024.11.27`

I love CIT Resewn, I love what it had done to modding as a whole. However, it's implementation has constantly been a pain point to me.
There is a lot of effort going towards maintaining support and compatibility with spec, which has unfortunately been kept by the hands of none other than sp614x.
To truly say CIT Resewn "supports legacy" in newer versions of the game, I must watch over and maintain compatibility with Optifine's dying environment. Optifine was once one of the greatest mods of its time, and then fell from glory once vanilla became as optimized as it is. Now all that's holding it together is a bunch of plasters slapped to let it keep living instead of stepping aside like it should've done and letting it rest in the era of Minecraft that needed it.
It no longer strives for better-than-vanilla performance, it is there purely for the features of the ancient MCPatcher and shaders. Both of which are no longer relevant in todays game.
<br><br>
Adding to this collection of additional game features are hooks and direct patches in the game code, replacing entire sections of it for the sake of performance. These changes create a hell hole of compatibility for anyone unlucky enough to have to mess with support for Optifine. Even worse, the code's hidden from the public because of some bullshit the guy keeps spitting about legal issues where it has become abysmally obvious that his pride wont let other, more modern developers, help him keep the mod from dying. Not even going to start about the complete lack of communication from the dev, having to be forced to look at community discussions in order to not get the new item components changes wrong. 
<br><br>
Now with CIT Resewn, not only do I have to stay tied to the shit show that is Optifine, now you also gotta add Mojang.
Across the years Mojang has earned the humorous title of "mojank" in response to the amount of bad practices used by the team. Now this is very unfair to compare to as their situation is a lot different than most dev teams. They have been running with changing people for a decade, they are updating code that is almost as old and plagued by management and non-developers' decision making. The code in most of vanilla has been deemed "jank". 
I have to give credit, they are getting better! but of course that comes with a large amount of refactors and changes. These days the changes are often and often very positively received. Which brings me to the next topic.

## The New "Item Model" Format
`//09:21 - 2024.11.25`

To summarize, much like block states, the models of (specifically) items are now decided through a data driven json that is in the resourcepack for each item id. 
The "item model" json goes through and chooses the relevant model based on the actual item stack by applying defined conditions.
Because this mechanism relies on the actual item stack, it essentially replaces item overrides and as such it has been removed from the model format, further driving the removal of the `BakedModel`'s reason to still exist.

### The Format's Problems
Personally I think that the new format is fantastic! It greatly expands what resourcepack makers could accomplish.
However it still suffers from the exact same pain points that drive the (or at the very least, my) reason to use a dedicated CIT format. 
#### Item Stack Data Conditions
Much like the ol' overrides, switching out the model depending on current info or stack info stops when you need to check something that was not intentionally and explicitly given support. 
<br><br>
Let's take the most common usage of CIT, giving items custom models based on custom item names.
You can check if the item property `minecraft:has_component` to see if the item has a custom name component.
However, you cannot check the actual name of the item because.. it's not a property!
<br><br>
For the sake of it let's say there was a new `minecraft:name` itemstack property that returned a string of the item's plaintext name.
This is great! you can now select a different model if the item has a custom name!
<br><br>
But.. this server gives me items with custom names that have special colors, I dont want to let any player get that model if it's a fake of that item. 
Okay, let's add explicit support for checking formatting. Now you can easily make sure the item name is formatted correctly.
<br><br>
Let's add another common use case of changing based on enchantments.
Again, you need a new explicit support.
<br><br>
This can be solved with actual general purpose component checks that dont rely on the actual type of the component.
There is already a codec that lets a predicate use abstract internals of a component without needing to understand the component.
Of course, this is less performant, but it provides a fallback for the dozens of niche things that are possible with CIT and not in vanilla.
#### Breaks with Layering
Again, exactly like it was with model overrides, you cannot layer more than one condition without breaking the ones from previously selected packs.
<br><br>
Say Pack A wants to change sticks that have the unbreaking enchantment.
If the stick does not have this enchantment, well.. fall back to being a normal stick of course.
<br><br>
Then the user chooses to enable Pack B which promises to "change sticks named 'Diamond' to look like diamonds!".
They enable both packs and of course, only the feature from the top pack works. 
This is because the item model json completely replaces its counterparts in the resourcepacks below. You can only have one or the other.
<br><br>
This problem plagues most of the resourcepack ecosystem which discourages the use of one of the more powerful features of enabling multiple packs.
<br><br>
Easily solved in this case by applying conditions dynamically from multiple packs with separate files. This likely requires a custom format that does not rely on the json file being named after the item id.
#### One Item Type At a Time 
Want to apply a custom rule for multiple item types? too bad. Duplicate the same file for each item id you want.
<br><br>
Again solved by using a custom format that does not use the json file name to specify the item type. 
Using a custom format could easily let the user target condition based models for multiple items. 

# Solving a Niche
***
`//12:15 - 2024.11.27`

I do not believe that this is my "duty" or something I have to do for the community, I dont rely on money the project makes, I do not give a shit about what happens if I let go. No, what I do is I look at issues and solve them out of my own amusement and intrigue in the project. This is the only thing driving this document, and the project as a whole.
<br><br>
Somehow there are a lot of people that want the functionality and at the same time the people that understand it and know to utilize it are only within a very small niche of the Minecraft community. I believe the reason for this was the popularization of Optifine which brought to light MCPatcher's features. This then led to resourcepack makers seeing the potential and uses of CIT and added a substantial amount of resource packs of neat features that rely on it. Players of course ignorantly choose to add packs and mods to their instance and seeing that their promised functionality relies on something that is not in vanilla.
<br><br>
Finding a sufficient solution here requires much thought and it cannot rely on things that are alien and difficult to add to the base game. The more different and unique the implementation is, the harder it is to maintain across versions. Keeping it simple enough to the point of seeing it as a possible vanilla implementation is the way to go.
<br><br>
But there is this other aspect of the problem at hand. The overbearing amount of old CIT based resourcepacks that players expect full support for. I do not believe anyone could ever achieve full support for things this old, but I do believe a mitigation can be made. Not one where the old format is accepted, but one where it becomes simple and seamless to port an old pack to the new format. Potentially even while loading an out of date pack, hidden from the end user if successful. This will only be possible with a new format that completely replaces the functionality that the old format had. Functionality can only be extended, never taken away.
If I manage to modernize the format to fit the modern game, while also keeping it not alien for updating pack developers, I believe it could work.

# Modern CIT
***
Okay, let's get to the meat of this document. Not all of the "whys" and starting the whole "hows".
This is my proposed idea of a new CIT. I will most likely be wanting to implement it myself and building CIT Resewn atop it, for that CIT Resewn will have to be updated to a new major version v2, more on that later.
## Adapt to Item Components
`//12:46 - 2024.11.28`

It appears that every item behavior is moving towards being controlled by components. Probably in preparation for the datafication of item definitions. 
I had the thought of doing the same with the format. 
<br><br>
Because components are built with datafication in mind, they get the love that all data driven objects get with the power of codecs. Them being encodable to json through codecs is a very powerful way to expand the scope of cosmetic itemstack modification beyond item models and armor textures as it allows the user to arbitrarily replace ***any*** aspect of an itemstack and its definition on the client. As long as there is a component for it, you can modify it.
<br><br>
This implementation approach creates two new problems.
### Client Sided Component Changes
Or rather a sort of "ghost" component map, computed on the fly based on CIT conditions. Having a safe way to apply extra component changes on top of the current stack's component map is crucial to achieve this solution. 
There's some stuff' I should look at before proceeding. One of which is owolib's "derived item components" API, which supposedly allows applying additional components on the condition that a criteria is met within the current set of components. The implementation of this system is of interest to me.
Do they add real components to the nbt of the itemstack? Are they "ghost" components like the ones I describe? Are there any major issues with its usage?

`//10:45 - 2024.11.30`

Currently in the process of testing some theories and I'll move some shit here.
<br><br>
Reading some stuff in the code and looking at some other similar implementations I believe the best approach would be to store the added changes in `MergedComponendMap` which is used by instances of `ItemStack`. 
<br><br>
Looking at the direct usages of components in `ItemStack` I see
For reading components:
```
ItemStack#contains     -> getComponents().contains
ItemStack#get          -> getComponents().get
ItemStack#getOrDefault -> getComponents().getOrDefault
```

This means that whenever the stack should be queried for a component value, it has to pass through `getComponents()`. For visual, non persistent, changes I can update the component map from that method as it is the most common intersection that happens right before the value is used for non storage or map comparison purposes while still having access to the entire surrounding `ItemStack`.
<br><br>
For writing components:
```
ItemStack#init                    | map = 
ItemStack#set                     | map.set
ItemStack#remove                  | map.remove
ItemStack#applyChanges            | map.applyChanges
ItemStack#applyUnvalidatedChanges | map.applyChanges
ItemStack#applyComponentsFrom     | map.setAll
```

As for writing, except for `ItemStack` initialization, all of the writing methods pass through methods in the map that would modify it. Those methods all, in some part, must pass through `MergedComponentMap#onWrite`. `onWrite` appears to be a safe guard for modifying component maps' changed elements where it would copy the changed components map before modifying so that the map instances would not cause component modification -based duping.
This is ***useful*** because I can safely guarantee that the `onWrite` method is always called if the component map is modified, which in turn lets me invalidate the CIT result for it to be updated on the next time the component map gets queried(ie with the stack's `getComponents`).
<br><br>
As for the stack's initializer, it appears it is private and the only users of it always provide a new instance of `MergedComponentMap`. Even when supplied with existing instances, they are copied into new ones before that constructor is used. By having the `MergedComponentMap` be invalidated by default, I can avoid adding an extra mixin to invalidate the map when that constructor is used.
Seeing as most of the logic eventually happens in `MergedComponentMap`, I believe it is the best spot to store and apply CIT. The only mixin I would need in `ItemStack` would be to update the CIT from its `ItemStack#getComponents`. That logic of course could then also be delegated into the component map with ducking.

 `//15:03 - 2024.11.30`
 
Implementation went surprisingly well. I have created a ghost component map that applies additional components on top of the real components while all being localized to the client.
<br><br>
One flaw I did not account for was that the client could potentially have a server thread(in singleplayer), meaning that if I have a client only mixin, it could still mess with game logic on the server thread if the mixins apply there as well. Now of course, logically this shouldnt matter but I really did not like it.
I opted to create a `ThreadLocal` that simply blocks the functionality outside of the render thread. This is the analogous as checking `world.isClient`.
<br><br>
In other news, the server just does not care if I have additional components registered on the client, not even in creative where it'd have to potentially sync the components up to the server. (well it might care but if it did I dont believe my implementation actually tries sending the unknown components upwards)
Brings me neatly to the next section.
### Additional Client Sided Component Data
`//13:09 - 2024.11.28`

After solving adding additional ghost components, I still would need room to add more functionality beyond the components that exist in vanilla.
A few examples are the glint texture, or even the planned armor custom model format I've had in the drawing board for years. 
<br><br>
Asking around and looking at fapi, I stumbled upon [FabricMC/Fabric#1179](https://github.com/FabricMC/fabric/pull/1179) which implied that in theory I could simply register extra client sided components and the server would just not be bothered by them simply existing in the client's registry. If I register an extra component, I could apply it as a ghost on top of the item and then provide it with whatever functionality I need. 
<br><br>
For example, the immediate one that's needed would be `citresewn:glint` which, when combined with `minecraft:enchantment_glint_override` could recreate the functionality of the old `enchantment` CIT type. It would contain a pointer to the glint texture and whatever other parameters needed to customize the item's glint.

`//16:29 - 2024.11.30`

From testing(both on a vanilla server and a fabric api server) it appears that my implementation of the client sided component changes allows for components of types that are registered only on the client. Also confirms that clients with additionally registered components can safely join servers that apply fabric registry sync.
### Runtime Dynamic Fast Changing Conditions
`//09:05 - 2024.12.03`

There are some conditions that dont rely on fundamental changes in the stack's data, but rather dependent on supplied values based on the current state of the game. As a few examples, there is item use time, bow pull amount, time of day for clocks, player's direction for compass, etc..
The issue with this type of data is that conditions that check it would not be refreshed as there is no clear way to notify the item about changes. In the past, this has been solved in CIT by invalidating the cache every so often to allow the conditions to run again. In optifine there even isn't a cache which could tank performance. I would've implemented these conditions as item override types if that system was not as clunky as it was as it was made exactly for that use case.
In 1.21.4 however, things have changed. Snapshot 24w45a's item models now allow you to select the model based on runtime conditions that are not necessarily tied to the item's composition. 
<br><br>
I believe that this is the appropriate place to handle such cases, and as such I have decided that CITs would not be allowed to check these types of conditions where the checked information is not on the stack itself(i.e. components, count and item type). This also lets me get rid of the cache invalidation process which should fair a lot better for performance on lower end systems.
Speaking of performance, one of the neat things this also allows is to memoize the item stack to resolve CITs as the conditions can only check data that is included in the stack's hash(of course, while also needing to clear it on pack reloads).
## Pack Format
`//11:36 - 2024.12.04`

How a new CIT format would look like is an interesting question. If anything I might as well forgo the entirety of the old format to allow for room to see what is needed and what is relevant in modern minecraft. 
<br><br>
Let's start with the elephant in the room and that is `.properties`, goddamn after working so much with it I hate this format with a passion. You are either forced to reimplement it to add slightly more complex features or you are forced to use the default java implementation for it. It looks like MCPatcher just took it and ran with it, not realizing how simplified the format is and not seeing that it is being utilized for the wrong use case.
No, obviously I dont want to mess with that thing. Let's pivot to something modern, you know, the thing that almost every piece of data uses?
### JSON
Modern minecraft is made up of an insane amount of JSON files. They have a ton of support internally and they are way better for most "configuration" stuff in data driving. Speaking of internally supported, minecraft these days is built on top of DFU's `Codec`s. I have no doubt that defining the new format using codecs is a much better approach and use of my time.
### File Structure

### Broken Paths
No.
### Modern Conditions

### Applied Effects

## Legacy2Modern Conversion
`//09:23 - 2024.12.03`

As I said, I would like to keep support with older resourcepacks. This is what drives the use of CIT Resewn over other alternatives in my opinion.
However I have decided a while ago that I would not like to interpret the old CIT format directly in modern CIT, as that would create a lot of messy code and hard to decipher issues. Instead I feel that the best solution going forward is to separately read the old format in a separate mod, where I would be free to update it at a less urgent pace than the main mod. Then, it would either inject CITs into the runtime or, I would personally rather generating a modern version of the older resourcepack on the fly, to match the new format and also allow pack publishers to update with ease.
### Runtime Resourcepacks
The problem with directly interpreting legacy packs is that a modern equivalent would need to have more files than in the original, such as the item asset.
So stealing the idea directly from ARRP and similar, I thought it would be smarter to generate modern packs at runtime, either letting the player convert the pack or doing so automatically when the old legacy pack is being loaded.
## The CIT Resewn Repository

### Version 2

### Multiversion

### The Legacy Converter


# Obviously this document is not finished, I just wanted it to be out there for now
