package builderb0y.f3screenshot.mixins;

import java.io.File;
import java.util.function.Consumer;

import com.llamalad7.mixinextras.injector.v2.WrapWithCondition;
import com.mojang.blaze3d.pipeline.RenderTarget;
import com.mojang.blaze3d.platform.InputConstants;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.KeyboardHandler;
import net.minecraft.client.Minecraft;
import net.minecraft.network.chat.Component;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import builderb0y.f3screenshot.F3Screenshot;

@Mixin(KeyboardHandler.class)
@Environment(EnvType.CLIENT)
public class KeyboardMixin {

	@Shadow
	@Final
	private Minecraft minecraft;

	@Shadow
	private boolean usedDebugKeyAsModifier;

	@WrapWithCondition(method = "keyPress", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/Screenshot;grab(Ljava/io/File;Lcom/mojang/blaze3d/pipeline/RenderTarget;Ljava/util/function/Consumer;)V"))
	private boolean f3Screenshot_saveF3DataInstead(File gameDirectory, RenderTarget framebuffer, Consumer<Component> messageReceiver) {
		if (InputConstants.isKeyDown(this.minecraft.getWindow(), InputConstants.KEY_F3)) {
			F3Screenshot.saveF3(new File(gameDirectory, "screenshots"), messageReceiver);
			this.usedDebugKeyAsModifier = true;
			return false;
		}
		return true;
	}
}