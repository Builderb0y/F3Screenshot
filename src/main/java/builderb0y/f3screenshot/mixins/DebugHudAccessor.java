package builderb0y.f3screenshot.mixins;

import java.util.List;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;
import org.spongepowered.asm.mixin.gen.Invoker;

import net.minecraft.client.gui.hud.DebugHud;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.hit.HitResult;
import net.minecraft.world.World;
import net.minecraft.world.chunk.WorldChunk;

@Mixin(DebugHud.class)
public interface DebugHudAccessor {

	#if MC_VERSION >= MC_1_21_9

		@Invoker("getClientChunk")
		public abstract WorldChunk f3Screenshot_getClientChunk();

		@Invoker("getChunk")
		public abstract WorldChunk f3Screenshot_getServerChunk();

		@Invoker("getWorld")
		public abstract World f3Screenshot_getWorld();

	#else

		@Invoker("getLeftText")
		public abstract List<String> f3Screenshot_getLeftText();

		@Invoker("getRightText")
		public abstract List<String> f3Screenshot_getRightText();

		@Accessor("blockHit")
		public abstract void f3Screenshot_setBlockHit(HitResult result);

		@Accessor("fluidHit")
		public abstract void f3Screenshot_setFluidHit(HitResult result);

	#endif
}