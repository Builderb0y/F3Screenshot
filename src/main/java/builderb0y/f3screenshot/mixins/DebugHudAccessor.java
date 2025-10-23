package builderb0y.f3screenshot.mixins;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;

import net.minecraft.client.gui.hud.DebugHud;
import net.minecraft.world.World;
import net.minecraft.world.chunk.WorldChunk;

@Mixin(DebugHud.class)
public interface DebugHudAccessor {

	@Invoker("getClientChunk")
	public abstract WorldChunk f3Screenshot_getClientChunk();

	@Invoker("getChunk")
	public abstract WorldChunk f3Screenshot_getServerChunk();

	@Invoker("getWorld")
	public abstract World f3Screenshot_getWorld();
}