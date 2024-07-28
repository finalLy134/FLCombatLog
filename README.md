# **FLCombatLog**

[SpigotMC Page](https://www.spigotmc.org/resources/flcombatlog.118428/)
```
Version: 1.1
Minecraft Version: 1.20.4, 1.20.6
Dependencies: Citizens
```

## **Overview**
FLCombatLog is a robust and efficient plugin designed to prevent combat logging on your Minecraft server. With FLCombatLog, when a player disconnects while in combat, an NPC representing their body will be spawned, giving their opponent a chance to claim victory and collect the loot.

## **Features**
- **Anti-Combat Log:** Prevents players from escaping combat by disconnecting.
- **NPC Spawn:** Uses the popular Citizens plugin to create an NPC of the player who logged out.
- **Lootable NPC:** The NPC can be killed within a configurable time frame, dropping the player's inventory.
- **Customizable Timer:** Set the duration for how long the NPC will remain before disappearing.
- **Lightweight and Efficient:** Minimal impact on server performance.

## **Commands**
- `/flcombatlog reload` - Reload the plugin configuration.

## **Permissions**
- `flcombatlog.reload` - Permission to reload the plugin configuration.

## **Configuration**
FLCombatLog offers a straightforward configuration file to adjust the plugin to your server's needs.

```yaml
# FLCombatLog Configuration
npc-lifetime-seconds: 30 # Time in seconds the NPC will stay alive after a player logs out.
combat-tag-duration: 10 # Time in seconds a player is considered in combat after taking damage.
```

## **Installation**

1. Download the latest version of Citizens and install it on your server.
2. Place the FLCombatLog.jar file into your server's plugins directory.
3. Restart your server to generate the configuration files.
4. Edit the configuration file located in the plugins/FLCombatLog folder to your liking.
5. Use /flcombatlog reload to apply any configuration changes without restarting.

## **How It Works**

1. Combat Tagging: When a player engages in combat, they are tagged.
2. Player Disconnects: If the tagged player logs out, an NPC replica is spawned at their location.
3. NPC Interaction: The remaining player has a configurable amount of time to kill the NPC.
4. Loot Drop: If the NPC is killed, it drops the original player's inventory.
5. NPC Expiry: If not killed within the timeframe, the NPC disappears, and the logged-out player is safe.

## **Why Choose FLCombatLog?**

Combat logging can ruin the competitive experience on PvP servers. FLCombatLog ensures fair play by giving players a chance to fight to the end, even if their opponent tries to escape by disconnecting. This plugin maintains the thrill and integrity of combat, providing a seamless and enjoyable experience for all players.

## **Showcase**
![Showcase Video](https://www.youtube.com/watch?v=yiPbv_6Wavs)

## **Support and Feedback**
For support, questions, or feedback, please join our Discord Community.

Download Now and keep your PvP battles fair and exciting with FLCombatLog!
