package tomeko.hylobby.plugins;

//? if forge {
/*import org.spongepowered.asm.mixin.Mixins;
import org.spongepowered.asm.mixin.connect.IMixinConnector;
import tomeko.hylobby.utils.Constants;
import zone.rong.mixinbooter.service.ModDiscoverer;

public class MixinConnector implements IMixinConnector {
    @Override
    public void connect() {
        if (ModDiscoverer.isModPresent(Constants.MOD_ID))
            Mixins.addConfiguration("mixins." + Constants.MOD_ID + ".json");
    }
}
*///?}
