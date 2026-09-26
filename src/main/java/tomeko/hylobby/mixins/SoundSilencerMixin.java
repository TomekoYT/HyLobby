package tomeko.hylobby.mixins;

//? if 1.8.9 {
/*import net.minecraft.client.audio.ISound;
import net.minecraft.client.audio.SoundManager;
*///?} else {
import net.minecraft.client.resources.sounds.Sound;
import net.minecraft.client.resources.sounds.SoundInstance;
import net.minecraft.client.sounds.SoundEngine;
//?}
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
//? if 1.8.9 {
//import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
//?} else {
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
//?}
import tomeko.hylobby.config.HyLobbyConfig;

@Mixin(
        //? if 1.8.9
        //SoundManager.class
        //? else
        SoundEngine.class
)
public abstract class SoundSilencerMixin {
    @Inject(
            method =
                    //? if 1.8.9
                    //"playSound",
                    //?else
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
        //? if 1.8.9
        //ISound sound = instance.getSound();
        //? else
        Sound sound = instance.getSound();

        if(sound == null) return;

        String path =
                //? if 1.8.9
                //sound.getSoundLocation().getResourcePath();
                //? else
                sound.getLocation().getPath();

        if ((!path.startsWith("ui.") && HyLobbyConfig.INSTANCE.getSilentLobby())
                || (path.endsWith(".step") && HyLobbyConfig.INSTANCE.getLobbyDisableSteppingSounds())
                || (path.startsWith("entity.slime") && HyLobbyConfig.INSTANCE.getLobbyDisableSlimeSounds())
                || (path.startsWith("entity.ender_dragon") && HyLobbyConfig.INSTANCE.getLobbyDisableDragonSounds())
                || (path.startsWith("entity.wither") && HyLobbyConfig.INSTANCE.getLobbyDisableWitherSounds())
                || (path.equals("entity.item.pickup") && HyLobbyConfig.INSTANCE.getLobbyDisableItemPickupSounds())
                || (path.equals("entity.experience_orb.pickup") && HyLobbyConfig.INSTANCE.getLobbyDisableExperienceOrbSounds())
                || (path.equals("entity.tnt.primed") && HyLobbyConfig.INSTANCE.getLobbyDisablePrimedTntSounds())
                || (path.equals("entity.generic.explode") && HyLobbyConfig.INSTANCE.getLobbyDisableExplosionSounds())
                || (path.equals("entity.chicken.egg") && HyLobbyConfig.INSTANCE.getLobbyDisableDeliveryManSounds())
                || (path.startsWith("block.note_block") && HyLobbyConfig.INSTANCE.getLobbyDisableNoteBlockSounds())
                || (path.startsWith("entity.firework_rocket") && HyLobbyConfig.INSTANCE.getLobbyDisableFireworkSounds())
                || (path.equals("entity.player.levelup") && HyLobbyConfig.INSTANCE.getLobbyDisableLevelupSounds())
                || (path.startsWith("entity.arrow") && HyLobbyConfig.INSTANCE.getLobbyDisableArrowSounds())
                || (path.startsWith("entity.bat") && HyLobbyConfig.INSTANCE.getLobbyDisableBatSounds())
                || (path.startsWith("block.fire") && HyLobbyConfig.INSTANCE.getLobbyDisableFireSounds())
                || (path.startsWith("entity.enderman") && HyLobbyConfig.INSTANCE.getLobbyDisableEndermanSounds())
                || (
                (path.startsWith("block.wooden_door")
                        || path.startsWith("block.wooden_trapdoor")
                        || path.startsWith("block.iron_door")
                        || path.startsWith("block.iron_trapdoor"))
                        && HyLobbyConfig.INSTANCE.getLobbyDisableDoorSounds())
                || (path.startsWith("block.portal") && HyLobbyConfig.INSTANCE.getLobbyDisablePortalSounds())
        ) {
            //? if 1.8.9
            //ci.cancel();
            //? else
            cir.setReturnValue(SoundEngine.PlayResult.NOT_STARTED);
        }
    }
}