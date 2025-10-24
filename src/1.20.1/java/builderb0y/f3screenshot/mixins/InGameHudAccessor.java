package builderb0y.f3screenshot.mixins;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

import net.minecraft.client.gui.hud.DebugHud;
import net.minecraft.client.gui.hud.InGameHud;

@Mixin(InGameHud.class)
public interface InGameHudAccessor {

	//this getter was added in MC 1.20.2.
	@Accessor("debugHud")
	public abstract DebugHud f3Screenshot_getDebugHud();
}