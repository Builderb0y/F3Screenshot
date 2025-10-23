package builderb0y.f3screenshot.mixins;

import java.io.File;
import java.util.function.Consumer;

import com.llamalad7.mixinextras.injector.v2.WrapWithCondition;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;

import net.minecraft.client.Keyboard;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gl.Framebuffer;
import net.minecraft.client.util.InputUtil;
import net.minecraft.text.Text;

import builderb0y.f3screenshot.F3Screenshot;

@Mixin(Keyboard.class)
@Environment(EnvType.CLIENT)
public class KeyboardMixin {

	@Shadow @Final private MinecraftClient client;

	@WrapWithCondition(method = "onKey", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/util/ScreenshotRecorder;saveScreenshot(Ljava/io/File;Lnet/minecraft/client/gl/Framebuffer;Ljava/util/function/Consumer;)V"))
	private boolean f3Screenshot_saveF3DataInstead(File gameDirectory, Framebuffer framebuffer, Consumer<Text> messageReceiver) {
		if (InputUtil.isKeyPressed(this.client.getWindow(), InputUtil.GLFW_KEY_F3)) {
			F3Screenshot.saveF3(new File(gameDirectory, "screenshots"), messageReceiver);
			return false;
		}
		return true;
	}
}