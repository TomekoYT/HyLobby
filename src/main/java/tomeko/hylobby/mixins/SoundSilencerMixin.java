package tomeko.hylobby.mixins;

//? if 1.8.9 {
/*import net.minecraft.client.audio.ISound;
import net.minecraft.client.audio.SoundManager;
*///?} else {
import net.minecraft.client.resources.sounds.SoundInstance;
import net.minecraft.client.sounds.SoundEngine;
//?}
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
//? if 1.8.9
//import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
//? else
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import tomeko.hylobby.config.HyLobbyConfig;
import tomeko.hylobby.location.HypixelPackets;
import tomeko.hylobby.utils.Debug;

@Mixin(
        //? if 1.8.9
        //SoundManager.class
        //? else
        SoundEngine.class
)
abstract class SoundSilencerMixin {
    @Inject(
            method =
                    //? if 1.8.9
                    //"playSound",
                    //? else
                    "play",
            at = @At("HEAD"),
            cancellable = true
    )
    private void preventLobbyMusic(
            //? if 1.8.9
            //ISound instance, CallbackInfo ci
            //? else
            SoundInstance instance, CallbackInfoReturnable<SoundEngine.PlayResult> cir
    ) {
        if (!HypixelPackets.INSTANCE.getInLobby()) return;

        String path =
                //? if 1.8.9
                //instance.getSoundLocation().getResourcePath();
                //? else
                instance.getIdentifier().getPath();

        Debug.INSTANCE.log("Path: " + path + " <");

        //? if 1.8.9 {
        /*if ((!path.startsWith("ui.") && HyLobbyConfig.INSTANCE.getSilentLobby())
                || (path.startsWith("step.") && HyLobbyConfig.INSTANCE.getLobbyDisableSteppingSounds())
                || (path.startsWith("mob.slime") && HyLobbyConfig.INSTANCE.getLobbyDisableSlimeSounds())
                || (path.startsWith("mob.enderdragon") && HyLobbyConfig.INSTANCE.getLobbyDisableDragonSounds())
                || (path.startsWith("mob.wither") && HyLobbyConfig.INSTANCE.getLobbyDisableWitherSounds())
                || (path.equals("random.pop") && HyLobbyConfig.INSTANCE.getLobbyDisableItemPickupSounds())
                || (path.equals("random.orb") && HyLobbyConfig.INSTANCE.getLobbyDisableExperienceOrbSounds())
                || (path.equals("game.tnt.primed") && HyLobbyConfig.INSTANCE.getLobbyDisablePrimedTntSounds())
                || (path.equals("random.explode") && HyLobbyConfig.INSTANCE.getLobbyDisableExplosionSounds())
                || (path.equals("mob.chicken.plop") && HyLobbyConfig.INSTANCE.getLobbyDisableDeliveryManSounds())
                || (path.startsWith("note.") && HyLobbyConfig.INSTANCE.getLobbyDisableNoteBlockSounds())
                || (path.startsWith("fireworks.") && HyLobbyConfig.INSTANCE.getLobbyDisableFireworkSounds())
                || (path.equals("random.levelup") && HyLobbyConfig.INSTANCE.getLobbyDisableLevelupSounds())
                || ((path.equals("random.bow") || path.equals("random.bowhit")) && HyLobbyConfig.INSTANCE.getLobbyDisableArrowSounds())
                || (path.startsWith("mob.bat.") && HyLobbyConfig.INSTANCE.getLobbyDisableBatSounds())
                || (path.startsWith("fire.") && HyLobbyConfig.INSTANCE.getLobbyDisableFireSounds())
                || (path.startsWith("mob.endermen.") && HyLobbyConfig.INSTANCE.getLobbyDisableEndermanSounds())
                || ((path.equals("random.door_open") || path.equals("random.door_close")) && HyLobbyConfig.INSTANCE.getLobbyDisableDoorSounds())
                || (path.startsWith("portal.") && HyLobbyConfig.INSTANCE.getLobbyDisablePortalSounds())
        ) ci.cancel();
        *///?} else {
        if ((!path.startsWith("ui.") && HyLobbyConfig.INSTANCE.getSilentLobby())
                || (path.startsWith("block.") && path.endsWith(".step") && HyLobbyConfig.INSTANCE.getLobbyDisableSteppingSounds())
                || (path.startsWith("entity.slime.") && HyLobbyConfig.INSTANCE.getLobbyDisableSlimeSounds())
                || (path.startsWith("entity.ender_dragon.") && HyLobbyConfig.INSTANCE.getLobbyDisableDragonSounds())
                || (path.startsWith("entity.wither.") && HyLobbyConfig.INSTANCE.getLobbyDisableWitherSounds())
                || (path.equals("entity.item.pickup") && HyLobbyConfig.INSTANCE.getLobbyDisableItemPickupSounds())
                || (path.equals("entity.experience_orb.pickup") && HyLobbyConfig.INSTANCE.getLobbyDisableExperienceOrbSounds())
                || (path.equals("entity.tnt.primed") && HyLobbyConfig.INSTANCE.getLobbyDisablePrimedTntSounds())
                || (path.equals("entity.generic.explode") && HyLobbyConfig.INSTANCE.getLobbyDisableExplosionSounds())
                || (path.equals("entity.chicken.egg") && HyLobbyConfig.INSTANCE.getLobbyDisableDeliveryManSounds())
                || (path.startsWith("block.note_block.") && HyLobbyConfig.INSTANCE.getLobbyDisableNoteBlockSounds())
                || (path.startsWith("entity.firework_rocket.") && HyLobbyConfig.INSTANCE.getLobbyDisableFireworkSounds())
                || (path.equals("entity.player.levelup") && HyLobbyConfig.INSTANCE.getLobbyDisableLevelupSounds())
                || ((path.equals("entity.arrow.shoot") || path.equals("entity.arrow.hit") || path.equals("entity.arrow.hit_player")) && HyLobbyConfig.INSTANCE.getLobbyDisableArrowSounds())
                || (path.startsWith("entity.bat.") && HyLobbyConfig.INSTANCE.getLobbyDisableBatSounds())
                || (path.startsWith("block.fire.") && HyLobbyConfig.INSTANCE.getLobbyDisableFireSounds())
                || (path.startsWith("entity.enderman.") && HyLobbyConfig.INSTANCE.getLobbyDisableEndermanSounds())
                || ((path.equals("block.wooden_door.open") || path.equals("block.wooden_door.close") || path.equals("block.iron_door.open") || path.equals("block.iron_door.close")) && HyLobbyConfig.INSTANCE.getLobbyDisableDoorSounds())
                || (path.startsWith("block.portal.") && HyLobbyConfig.INSTANCE.getLobbyDisablePortalSounds())
        ) cir.setReturnValue(SoundEngine.PlayResult.NOT_STARTED);
        //?}
    }
}