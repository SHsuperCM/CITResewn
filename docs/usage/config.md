# Configuration

## Changing the Config

It is highly recommended that you use [Mod Menu](https://www.curseforge.com/minecraft/mc-mods/modmenu) 
and [Cloth Config](https://www.curseforge.com/minecraft/mc-mods/cloth-config) for ingame config integration.

If you have them installed, in the main menu/pause menu go to `Mods > CIT Resewn > Config`.  
If you do not, go to `%appdata%/.minecraft/config/` and edit `citresewn.json`.

## Config Values

| Config Name       | Description | Default Config Value |
| --- | --- | --- |
| `Enabled` <br>or `enabled` | Whether CIT Resewn should load CITs. | `true` |
| `Mute Errors` <br>or `mute_errors` | Should errors be muted in the logs. | `false` |
| `Mute Warns` <br>or `mute_warns` | Should errors be muted in the logs. | `false` |
| `Cache` <br>or `cache_ms` | How often should the cache be refreshed. <br>If set to 0, caching will be disabled. | `50`ms(every tick) |
| `Broken Paths` <br>or `broken_paths` | Should CIT Resewn be able to read broken folder/file names from CIT packs | `false` |