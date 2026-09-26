package tomeko.hylobby.config

//? if forge {
/*import cc.polyfrost.oneconfig.config.Config
import cc.polyfrost.oneconfig.config.annotations.*
import cc.polyfrost.oneconfig.config.core.OneColor as PolyColor
import cc.polyfrost.oneconfig.config.data.InfoType
import cc.polyfrost.oneconfig.config.data.Mod
import cc.polyfrost.oneconfig.config.data.ModType
*///?} else {
import org.polyfrost.oneconfig.api.config.v1.Config
import org.polyfrost.oneconfig.api.config.v1.annotations.*
//?}
//? if forge {
//import tomeko.hylobby.hud.BedwarsResourceDisplay
//?}
import tomeko.hylobby.utils.Constants

object HyLobbyConfig : Config(
    //? if forge {
    /*Mod(
        Constants.MOD_NAME,
        ModType.HYPIXEL,
        Constants.MOD_ICON
    ),
    "${Constants.MOD_ID}.json"
    *///?} else {
    "${Constants.MOD_ID}.json",
    Constants.MOD_ICON,
    Constants.MOD_NAME,
    Category.HYPIXEL
    //?}
) {
    //? if !forge {
    val DEPENDENCIES: List<Pair<String, List<String>>> = listOf(
    )
    //?}

    fun register() {
        //? if forge {
        //initialize()
        //?} else {
        preload()
        for ((condition, dependencies) in DEPENDENCIES) {
            for (dependency in dependencies) {
                addDependency(dependency, condition)
            }
        }
        //?}
    }

    //? if forge {
    //@Exclude
    //?}
    private const val CATEGORY_MIDDLE_CLICK_GUI_ITEMS = "Middle Click GUI Items"

    @Switch(
        //? if forge {
        //name =
        //?} else {
        title =
        //?}
        "Middle Click GUI Items",
        description = "Replace left click with middle click in GUIs in Hypixel lobbies",
        category = CATEGORY_MIDDLE_CLICK_GUI_ITEMS
    )
    var middleClickGUIItems = true


    private const val CATEGORY_SOUND_SILENCER = "Sound Silencer"

    @Switch(
        title = "Silent Lobby",
        description = "Prevent all sounds from playing when you are in a lobby.",
        category = CATEGORY_SOUND_SILENCER
    )
    var silentLobby = false

    @Switch(
        title = "Disable Stepping Sounds",
        description = "Remove sounds created by stepping.",
        category = CATEGORY_SOUND_SILENCER
    )
    var lobbyDisableSteppingSounds = false

    @Switch(
        title = "Disable Slime Sounds",
        description = "Remove sounds created by slimes.",
        category = CATEGORY_SOUND_SILENCER
    )
    var lobbyDisableSlimeSounds = false

    @Switch(
        title = "Disable Dragon Sounds",
        description = "Remove sounds created by dragons.",
        category = CATEGORY_SOUND_SILENCER
    )
    var lobbyDisableDragonSounds = false

    @Switch(
        title = "Disable Wither Sounds",
        description = "Remove sounds created by withers & wither skeletons.",
        category = CATEGORY_SOUND_SILENCER
    )
    var lobbyDisableWitherSounds = false

    @Switch(
        title = "Disable Item Pickup Sounds",
        description = "Remove sounds created by picking up an item.",
        category = CATEGORY_SOUND_SILENCER
    )
    var lobbyDisableItemPickupSounds = false

    @Switch(
        title = "Disable Experience Orb Sounds",
        description = "Remove sounds created by experience orbs.",
        category = CATEGORY_SOUND_SILENCER
    )
    var lobbyDisableExperienceOrbSounds = false

    @Switch(
        title = "Disable Primed TNT Sounds",
        description = "Remove sounds created by primed TNT.",
        category = CATEGORY_SOUND_SILENCER
    )
    var lobbyDisablePrimedTntSounds = false

    @Switch(
        title = "Disable Explosion Sounds",
        description = "Remove sounds created by explosions.",
        category = CATEGORY_SOUND_SILENCER
    )
    var lobbyDisableExplosionSounds = false

    @Switch(
        title = "Disable Delivery Man Sounds",
        description = "Remove sounds created by Delivery Man events.",
        category = CATEGORY_SOUND_SILENCER
    )
    var lobbyDisableDeliveryManSounds = false

    @Switch(
        title = "Disable Note Block Sounds",
        description = "Remove sounds created by note blocks.",
        category = CATEGORY_SOUND_SILENCER
    )
    var lobbyDisableNoteBlockSounds = false

    @Switch(
        title = "Disable Firework Sounds",
        description = "Remove sounds created by fireworks.",
        category = CATEGORY_SOUND_SILENCER
    )
    var lobbyDisableFireworkSounds = false

    @Switch(
        title = "Disable Levelup Sounds",
        description = "Remove sounds created by someone leveling up.",
        category = CATEGORY_SOUND_SILENCER
    )
    var lobbyDisableLevelupSounds = false

    @Switch(
        title = "Disable Arrow Sounds",
        description = "Remove sounds created by arrows.",
        category = CATEGORY_SOUND_SILENCER
    )
    var lobbyDisableArrowSounds = false

    @Switch(
        title = "Disable Bat Sounds",
        description = "Remove sounds created by bats.",
        category = CATEGORY_SOUND_SILENCER
    )
    var lobbyDisableBatSounds = false

    @Switch(
        title = "Disable Fire Sounds",
        description = "Remove sounds created by fire.",
        category = CATEGORY_SOUND_SILENCER
    )
    var lobbyDisableFireSounds = false

    @Switch(
        title = "Disable Enderman Sounds",
        description = "Remove sounds created by endermen.",
        category = CATEGORY_SOUND_SILENCER
    )
    var lobbyDisableEndermanSounds = false

    @Switch(
        title = "Disable Door Sounds",
        description = "Disable sounds caused by doors, trapdoors, and fence gates.",
        category = CATEGORY_SOUND_SILENCER
    )
    var lobbyDisableDoorSounds = false

    @Switch(
        title = "Disable Portal Sounds",
        description = "Disable sounds caused by nether portals.",
        category = CATEGORY_SOUND_SILENCER
    )
    var lobbyDisablePortalSounds = false


    //? if forge {
    //@Exclude
    //?}
    private const val CATEGORY_DEBUG = "Debug"

    @Info(
        //? if forge {
        //text =
        //?} else {
        title =
        //?}
        "Probably should stay disabled",
        //? if forge {
        //type = InfoType.WARNING,
        //?}
        category = CATEGORY_DEBUG
    )
    var debugModeInfo: Nothing? = null

    @Switch(
        //? if forge {
        //name =
        //?} else {
        title =
        //?}
        "Debug Mode",
        category = CATEGORY_DEBUG
    )
    var debugModeEnabled = false
}