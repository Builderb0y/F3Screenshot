package builderb0y.f3screenshot.mixins;

import net.minecraft.client.gui.components.DebugScreenOverlay;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.chunk.LevelChunk;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;

@Mixin(DebugScreenOverlay.class)
public interface DebugHudAccessor {

	@Invoker("getClientChunk")
	public abstract LevelChunk f3Screenshot_getClientChunk();

	@Invoker("getServerChunk")
	public abstract LevelChunk f3Screenshot_getServerChunk();

	@Invoker("getLevel")
	public abstract Level f3Screenshot_getWorld();
}