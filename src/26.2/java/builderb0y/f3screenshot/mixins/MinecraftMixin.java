package builderb0y.f3screenshot.mixins;

import java.io.File;

import com.mojang.blaze3d.platform.InputConstants;
import com.mojang.blaze3d.platform.Window;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import net.minecraft.client.Minecraft;
import net.minecraft.client.Options;
import net.minecraft.network.chat.Component;
import net.minecraft.util.thread.ReentrantBlockableEventLoop;

import builderb0y.f3screenshot.F3Screenshot;

@Mixin(Minecraft.class)
public abstract class MinecraftMixin extends ReentrantBlockableEventLoop {

	@Shadow @Final public File gameDirectory;
	@Shadow @Final public Options options;

	public MinecraftMixin() {
		super(null, false);
	}

	@Shadow public abstract void showDebugChat(Component message);

	@Inject(method = "handleGlobalKeyPress", at = @At(value = "CONSTANT", args = "intValue=0", ordinal = 0), cancellable = true)
	private void f3screenshot_saveF3DataInstead(InputConstants.Key key, boolean controlDown, CallbackInfoReturnable<Boolean> callback) {
		if (this.options.keyScreenshot.matches(key)) {
			F3Screenshot.saveF3(new File(this.gameDirectory, "screenshots"), (Component message) -> this.execute(() -> this.showDebugChat(message)));
			callback.setReturnValue(true);
		}
	}
}