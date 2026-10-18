package tomeko.hylobby.mixins;

//? if 1.8.9 {
/*import net.minecraft.client.entity.EntityPlayerSP;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import tomeko.hylobby.event.ClientSendMessageEvents;

@Mixin(EntityPlayerSP.class)
abstract class EntityPlayerSPMixin {
    @Inject(method = "sendChatMessage", at = @At("HEAD"), cancellable = true)
    private void hylobby$allowCommand(String message, CallbackInfo ci) {
        if (message.startsWith("/") && !ClientSendMessageEvents.ALLOW_COMMAND.invoker().allowSendCommandMessage(message.substring(1))) {
            ci.cancel();
        }
    }
}
*///?}
