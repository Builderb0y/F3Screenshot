package builderb0y.f3screenshot.mixins;

import java.io.File;
import java.util.function.Consumer;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.At.Shift;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import net.minecraft.client.texture.NativeImage;
import net.minecraft.client.util.ScreenshotRecorder;
import net.minecraft.text.Text;

import builderb0y.f3screenshot.F3Screenshot;

@Mixin(ScreenshotRecorder.class)
public class ScreenshotRecorderMixin {

	#if MC_VERSION >= MC_1_21_5

		@Inject(method = "method_22691", at = @At(value = "INVOKE", target = "java/util/function/Consumer.accept(Ljava/lang/Object;)V", shift = Shift.AFTER))
		private static void f3Screenshot_copyToClipboard(NativeImage nativeImage, File file, Consumer<Text> messageSender, CallbackInfo callback) {
			f3Screenshot_doCopyToClipboard(file, messageSender);
		}

	#else

		@Inject(method = "method_1661", at = @At(value = "INVOKE", target = "Ljava/util/function/Consumer;accept(Ljava/lang/Object;)V"))
		private static void f3Screenshot_copyToClipboard(NativeImage nativeImage, File file, Consumer<Text> messageSender, CallbackInfo callback) {
			f3Screenshot_doCopyToClipboard(file, messageSender);
		}

	#endif

	@Unique
	private static void f3Screenshot_doCopyToClipboard(File file, Consumer<Text> messageSender) {
		Throwable throwable = F3Screenshot.copyFile(file);
		if (throwable != null) {
			messageSender.accept(Text.translatableWithFallback(
				"f3screenshot.f3.no_success",
				"Failed to copy screenshot to your clipboard: %s",
				throwable
			));
		}
	}
}