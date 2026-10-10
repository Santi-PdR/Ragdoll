#!/usr/bin/env python3
"""Apply the narrow Create 0.5.x compatibility adaptation to the pinned Sable Forge source."""
import json
import sys
from pathlib import Path

root = Path(sys.argv[1])
props = root / "gradle.properties"
text = props.read_text(encoding="utf-8")
old_version = "version=2.0.5-port.1"
new_version = "version=2.0.5-port.6"
if text.count(old_version) != 1:
    raise SystemExit("Pinned Sable version marker changed; refusing an unreviewed patch")
props.write_text(text.replace(old_version, new_version), encoding="utf-8")

build = root / "forge/build.gradle"
text = build.read_text(encoding="utf-8")
old_range = "create_version_range: '[6.0.8,6.1.0)'"
new_range = "create_version_range: '[0,)'"
if text.count(old_range) != 1:
    raise SystemExit("Pinned Create version range changed; refusing an unreviewed patch")
build.write_text(text.replace(old_range, new_range), encoding="utf-8")

tools_build = root / "tools/build.ps1"
text = tools_build.read_text(encoding="utf-8")
old_tools_setting = "$includeTestTools = $Mode -in @('test', 'quick')"
new_tools_setting = "$includeTestTools = $Mode -eq 'test'"
if text.count(old_tools_setting) != 1:
    raise SystemExit("Pinned Sable quick-mode test-tools marker changed; refusing an unreviewed patch")
tools_build.write_text(text.replace(old_tools_setting, new_tools_setting), encoding="utf-8")

movement = root / "forge/src/port/java/dev/ryanhcode/sable/admitted/EntityMovementStateMixin.java"
text = movement.read_text(encoding="utf-8")
callback_import = "import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;\n"
callback_return_import = callback_import + "import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;\n"
if text.count(callback_import) != 1:
    raise SystemExit("Pinned Sable callback import marker changed; refusing an unreviewed patch")
text = text.replace(callback_import, callback_return_import)

old_method = """    /** @author RyanH @reason Take sub-level blocks into account. */
    @Overwrite
    public BlockState getFeetBlockState() {
        final Level currentLevel = this.level();
        if (this.feetBlockState == null || this.sable$trackingSubLevel != null) {
            this.feetBlockState = currentLevel.getBlockState(this.blockPosition);
            this.sable$inBlockStatePos = this.blockPosition;
            final Iterator<SubLevel> intersecting = Sable.HELPER
                    .getAllIntersecting(this.level, new BoundingBox3d(this.blockPosition)).iterator();
            while (this.feetBlockState.isAir() && intersecting.hasNext()) {
                final SubLevel subLevel = intersecting.next();
                final BlockPos localPos = BlockPos.containing(
                        subLevel.logicalPose().transformPositionInverse(this.position.add(0.0, 0.001, 0.0)));
                this.feetBlockState = currentLevel.getBlockState(localPos);
                this.sable$inBlockStatePos = localPos;
            }
        }
        return this.feetBlockState;
    }"""
new_method = """    @Inject(method = "getFeetBlockState", at = @At("HEAD"))
    private void sable$rememberFeetBlockStateRefresh(final CallbackInfoReturnable<BlockState> cir) {
        this.sable$refreshFeetBlockStateAfterVanilla =
                this.feetBlockState == null || this.sable$trackingSubLevel != null;
    }

    @Inject(method = "getFeetBlockState", at = @At("RETURN"), cancellable = true)
    private void sable$inspectSubLevelFeetBlockState(final CallbackInfoReturnable<BlockState> cir) {
        if (!this.sable$refreshFeetBlockStateAfterVanilla) {
            return;
        }
        this.sable$refreshFeetBlockStateAfterVanilla = false;
        this.sable$inBlockStatePos = this.blockPosition;
        if (this.feetBlockState != null && this.feetBlockState.isAir()) {
            final Level currentLevel = this.level();
            final Iterator<SubLevel> intersecting = Sable.HELPER
                    .getAllIntersecting(this.level, new BoundingBox3d(this.blockPosition)).iterator();
            while (this.feetBlockState.isAir() && intersecting.hasNext()) {
                final SubLevel subLevel = intersecting.next();
                final BlockPos localPos = BlockPos.containing(
                        subLevel.logicalPose().transformPositionInverse(this.position.add(0.0, 0.001, 0.0)));
                this.feetBlockState = currentLevel.getBlockState(localPos);
                this.sable$inBlockStatePos = localPos;
            }
        }
        cir.setReturnValue(this.feetBlockState);
    }"""
if text.count(old_method) != 1:
    raise SystemExit("Pinned Sable feet-block overwrite changed; refusing an unreviewed compatibility patch")
text = text.replace(old_method, new_method)
field_marker = "@Unique\n    private BlockPos sable$inBlockStatePos = BlockPos.ZERO;"
new_fields = "@Unique\n    private BlockPos sable$inBlockStatePos = BlockPos.ZERO;\n    @Unique\n    private boolean sable$refreshFeetBlockStateAfterVanilla;"
if text.count(field_marker) != 1:
    raise SystemExit("Pinned Sable in-block state field marker changed; refusing an unreviewed patch")
text = text.replace(field_marker, new_fields)
movement.write_text(text, encoding="utf-8")

fluid = root / "forge/src/port/java/dev/ryanhcode/sable/admitted/ForgeEntitySwimmingMixin.java"
text = fluid.read_text(encoding="utf-8")
old_fluid_entry = """    @Overwrite(remap = false)
    public void updateFluidHeightAndDoFluidPushing(final Predicate<FluidState> shouldUpdate) {"""
new_fluid_entry = """    @Inject(remap = false, method = "updateFluidHeightAndDoFluidPushing(Ljava/util/function/Predicate;)V", at = @At("HEAD"), cancellable = true)
    private void sable$updateFluidHeightAndDoFluidPushing(final Predicate<FluidState> shouldUpdate, final CallbackInfo ci) {"""
if text.count(old_fluid_entry) != 1:
    raise SystemExit("Pinned Sable fluid overwrite marker changed; refusing an unreviewed patch")
text = text.replace(old_fluid_entry, new_fluid_entry)
unloaded_guard = """        if (this.touchingUnloadedChunk()) {
            return;
        }
"""
sublevel_guard = unloaded_guard + """        final AABB intersectionBounds = this.getBoundingBox().deflate(0.001D);
        if (!Sable.HELPER.getAllIntersecting(this.level, new BoundingBox3d(intersectionBounds)).iterator().hasNext()) {
            return;
        }
"""
if text.count(unloaded_guard) != 1:
    raise SystemExit("Pinned Sable fluid unloaded-chunk guard changed; refusing an unreviewed patch")
text = text.replace(unloaded_guard, sublevel_guard)
fluid_end = """        if (calculations != null) {
            calculations.forEach((type, calculation) -> sable$applyFluidCalculation(forgeSelf, type, calculation));
        }
    }
"""
fluid_end_compat = """        if (calculations != null) {
            calculations.forEach((type, calculation) -> sable$applyFluidCalculation(forgeSelf, type, calculation));
        }
        ci.cancel();
    }
"""
if text.count(fluid_end) != 1:
    raise SystemExit("Pinned Sable fluid calculation end marker changed; refusing an unreviewed patch")
text = text.replace(fluid_end, fluid_end_compat)
fluid.write_text(text, encoding="utf-8")

level_policy = root / "forge/src/port/java/dev/ryanhcode/sable/mixin/plot/LevelBlockEntityTickPolicyMixin.java"
text = level_policy.read_text(encoding="utf-8")
imports_marker = "import dev.ryanhcode.sable.sublevel.plot.PlotBlockActivityPolicy;\n"
imports_replacement = """import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import dev.ryanhcode.sable.sublevel.plot.PlotBlockActivityPolicy;
"""
if text.count(imports_marker) != 1:
    raise SystemExit("Pinned Sable block-tick policy import marker changed; refusing an unreviewed patch")
text = text.replace(imports_marker, imports_replacement)
old_redirect = """    @Redirect(
            method = "tickBlockEntities()V",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/world/level/Level;shouldTickBlocksAt(Lnet/minecraft/core/BlockPos;)Z"
            )
    )
    private boolean sable$shouldTickPlotBlockEntity(final Level instance, final BlockPos pos) {
        return PlotBlockActivityPolicy.shouldProcess(instance, pos, instance.shouldTickBlocksAt(pos));
    }"""
new_wrap = """    @WrapOperation(
            method = "tickBlockEntities",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/world/level/Level;shouldTickBlocksAt(Lnet/minecraft/core/BlockPos;)Z"
            )
    )
    private boolean sable$shouldTickPlotBlockEntity(final Level instance, final BlockPos pos, final Operation<Boolean> original) {
        return PlotBlockActivityPolicy.shouldProcess(instance, pos, original.call(instance, pos));
    }"""
if text.count(old_redirect) != 1:
    raise SystemExit("Pinned Sable block-tick redirect marker changed; refusing an unreviewed patch")
text = text.replace(old_redirect, new_wrap)
text = text.replace("import org.spongepowered.asm.mixin.injection.Redirect;\\n", "")
level_policy.write_text(text, encoding="utf-8")

config = root / "forge/src/port/resources/sable-create.mixins.json"
data = json.loads(config.read_text(encoding="utf-8"))
if not data.get("mixins") or not data.get("client"):
    raise SystemExit("Expected the pinned Create/Flywheel mixin set; refusing an unreviewed patch")
data["mixins"] = []
data["client"] = []
config.write_text(json.dumps(data, indent=2) + "\n", encoding="utf-8")
print("Patched Sable to accept Create versions, skip Create/Flywheel 1.0 mixins, keep quick mode out of test-tools packaging, preserve vanilla movement/fluid hook points for Canary and Brutality, and retain Sable sublevel physics.")
