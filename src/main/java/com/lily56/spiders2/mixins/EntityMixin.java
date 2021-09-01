package com.lily56.spiders2.mixins;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import net.minecraft.entity.Entity;
import net.minecraft.entity.MovementType;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import com.lily56.spiders2.common.entity.mob.IEntityMovementHook;
import com.lily56.spiders2.common.entity.mob.IEntityReadWriteHook;
import com.lily56.spiders2.common.entity.mob.IEntityRegisterDataHook;

@Mixin(Entity.class)
public abstract class EntityMixin implements IEntityMovementHook, IEntityReadWriteHook, IEntityRegisterDataHook {
	@Inject(method = "move(Lnet/minecraft/entity/MovementType;Lnet/minecraft/util/math/Vec3d;)V", at = @At("HEAD"), cancellable = true)
	private void onMovePre(MovementType type, Vec3d pos, CallbackInfo ci) {
		if(this.onMove(type, pos, true)) {
			ci.cancel();
		}
	}

	@Inject(method = "move(Lnet/minecraft/entity/MovementType;Lnet/minecraft/util/math/Vec3d;)V", at = @At("RETURN"))
	private void onMovePost(MovementType type, Vec3d pos, CallbackInfo ci) {
		this.onMove(type, pos, false);
	}

	@Override
	public boolean onMove(MovementType type, Vec3d pos, boolean pre) {
		return false;
	}

	@Inject(method = "getOnPosition()Lnet/minecraft/util/math/BlockPos;", at = @At("RETURN"), cancellable = true)
	private void onGetOnPosition(CallbackInfoReturnable<BlockPos> ci) {
		BlockPos adjusted = this.getAdjustedOnPosition(ci.getReturnValue());
		if(adjusted != null) {
			ci.setReturnValue(adjusted);
		}
	}

	@Override
	public BlockPos getAdjustedOnPosition(BlockPos onPosition) {
		return null;
	}

	@Inject(method = "canTriggerWalking()Z", at = @At("RETURN"), cancellable = true)
	private void onCanTriggerWalking(CallbackInfoReturnable<Boolean> ci) {
		ci.setReturnValue(this.getAdjustedCanTriggerWalking(ci.getReturnValue()));
	}

	@Override
	public boolean getAdjustedCanTriggerWalking(boolean canTriggerWalking) {
		return canTriggerWalking;
	}

	@Inject(method = "read(Lnet/minecraft/nbt/NbtCompound;)V", at = @At(
			value = "INVOKE",
			target = "Lnet/minecraft/entity/Entity;readAdditional(Lnet/minecraft/nbt/NbtCompound;)V",
			shift = At.Shift.AFTER
			))
	private void onRead(NbtCompound nbt, CallbackInfo ci) {
		this.onRead(nbt);
	}

	@Override
	public void onRead(NbtCompound nbt) { }

	@Inject(method = "writeWithoutTypeId(Lnet/minecraft/nbt/NbtCompound;)Lnet/minecraft/nbt/NbtCompound;", at = @At(
			value = "INVOKE",
			target = "Lnet/minecraft/entity/Entity;writeAdditional(Lnet/minecraft/nbt/NbtCompound;)V",
			shift = At.Shift.AFTER
			))
	private void onWrite(NbtCompound nbt, CallbackInfoReturnable<NbtCompound> ci) {
		this.onWrite(nbt);
	}

	@Override
	public void onWrite(NbtCompound nbt) { }

	@Shadow(prefix = "shadow$")
	private void shadow$registerData() { }

	@Redirect(method = "<init>*", at = @At(
			value = "INVOKE",
			target = "Lnet/minecraft/entity/Entity;registerData()V"
			))
	private void onRegisterData(Entity _this) {
		this.shadow$registerData();
		
		if(_this == (Object) this) {
			this.onRegisterData();
		}
	}

	@Override
	public void onRegisterData() { }
}
