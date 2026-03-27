package builderb0y.f3screenshot.mixins;

import java.io.File;
import java.util.function.Consumer;
import net.minecraft.client.Screenshot;
import net.minecraft.network.chat.Component;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.At.Shift;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import builderb0y.f3screenshot.F3Screenshot;
import com.mojang.blaze3d.platform.NativeImage;

@Mixin(Screenshot.class)
public class ScreenshotRecorderMixin {

	@Inject(method = "method_22691", at = @At(value = "INVOKE", target = "java/util/function/Consumer.accept(Ljava/lang/Object;)V", shift = Shift.AFTER))
	private static void f3Screenshot_copyToClipboard(NativeImage nativeImage, File file, Consumer<Component> messageSender, CallbackInfo callback) {
		f3Screenshot_doCopyToClipboard(file, messageSender);
	}

	@Unique
	private static void f3Screenshot_doCopyToClipboard(File file, Consumer<Component> messageSender) {
		Throwable throwable = F3Screenshot.copyFile(file);
		if (throwable != null) {
			messageSender.accept(Component.translatableWithFallback(
				"f3screenshot.f3.no_success",
				"Failed to copy screenshot to your clipboard: %s",
				throwable
			));
		}
	}
}