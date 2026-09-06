package com.putzwirk.trashslotblacklist.mixin;

import net.blay09.mods.kuma.api.KeyModifiers;
import net.blay09.mods.kuma.api.KumaKeyMapping;
import net.blay09.mods.kuma.api.ManagedKeyMapping;
import net.minecraft.client.KeyMapping;
import net.neoforged.neoforge.client.settings.KeyModifier;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(targets = "net.blay09.mods.kuma.NativeKeyModifierReconciler", remap = false)
public abstract class KumaKeyModifierReconcilerMixin {

    @Inject(method = "reconcile", at = @At("HEAD"), cancellable = true, require = 0)
    private static void trashslotblacklist$onReconcile(KeyMapping mapping, CallbackInfo ci) {
        ci.cancel();

        if (!((Object) mapping instanceof KumaKeyMapping kuma) || !kuma.kuma$isManaged()) {
            return;
        }

        KeyModifiers nativeModifiers = trashslotblacklist$fromNeoForge(mapping.getKeyModifier());
        if (nativeModifiers.isEmpty() || !kuma.kuma$getModifiers().isEmpty()) {
            return;
        }

        kuma.kuma$setModifiers(nativeModifiers);
        ManagedKeyMapping managed = kuma.kuma$getManagedKeyMapping();
        if (managed != null && managed.getStorage() != null) {
            managed.getStorage().saveKeyMapping(managed);
        }
    }

    private static KeyModifiers trashslotblacklist$fromNeoForge(KeyModifier modifier) {
        if (modifier == KeyModifier.CONTROL) {
            return KeyModifiers.of(net.blay09.mods.kuma.api.KeyModifier.CONTROL);
        }
        if (modifier == KeyModifier.SHIFT) {
            return KeyModifiers.of(net.blay09.mods.kuma.api.KeyModifier.SHIFT);
        }
        if (modifier == KeyModifier.ALT) {
            return KeyModifiers.of(net.blay09.mods.kuma.api.KeyModifier.ALT);
        }
        return KeyModifiers.none();
    }}
