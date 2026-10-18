package tomeko.hylobby.mixins;

//? if 1.8.9 {
/*import net.minecraft.client.Minecraft;
*///?} else {
import com.mojang.blaze3d.platform.FramerateLimitTracker;
//?}
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import tomeko.hylobby.config.HyLobbyConfig;
import tomeko.hylobby.limbo.LimboLimiter;

//? if 1.8.9
//@Mixin(Minecraft.class)
//? else
@Mixin(FramerateLimitTracker.class)
abstract class LimboLimiterMixin {
    @Inject(
            method =
                    //? if 1.8.9
                    //"getLimitFramerate",
                    //? else
                    "getFramerateLimit",
            at = @At("HEAD"),
            cancellable = true
    )
    private void hylobby$limitFramerate(CallbackInfoReturnable<Integer> cir) {
        if (LimboLimiter.shouldLimitFramerate()) cir.setReturnValue(HyLobbyConfig.INSTANCE.getLimboFPS());
    }
}
