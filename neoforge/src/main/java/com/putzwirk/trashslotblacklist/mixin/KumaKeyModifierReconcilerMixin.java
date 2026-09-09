package com.putzwirk.trashslotblacklist.mixin;

import net.blay09.mods.kuma.api.Kuma;
import net.blay09.mods.kuma.api.KumaKeyMapping;
import net.blay09.mods.kuma.api.KeyModifiers;
import net.blay09.mods.kuma.api.ManagedKeyMapping;
import net.minecraft.client.KeyMapping;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(targets = "net.blay09.mods.kuma.NativeKeyModifierReconciler", remap = false)
public abstract class KumaKeyModifierReconcilerMixin {

    @Unique
    private static boolean trashslotblacklist$reconciling;

    @Inject(method = "reconcile", at = @At("HEAD"), cancellable = true, require = 0)
    private static void trashslotblacklist$onReconcile(KeyMapping mapping, CallbackInfo ci) {
        ci.cancel();

        if (trashslotblacklist$reconciling) {
            return;
        }

        if (!((Object) mapping instanceof KumaKeyMapping kuma) || !kuma.kuma$isManaged()) {
            return;
        }

        KeyModifiers nativeModifiers = Kuma.__getRuntime().getNativeKeyModifiers(mapping);
        if (nativeModifiers.isEmpty()) {
            return;
        }

        trashslotblacklist$reconciling = true;
        try {
            if (kuma.kuma$getModifiers().isEmpty()) {
                kuma.kuma$setModifiers(nativeModifiers);
                ManagedKeyMapping managed = kuma.kuma$getManagedKeyMapping();
                if (managed != null && managed.getStorage() != null) {
                    managed.getStorage().saveKeyMapping(managed);
                }
            }
            Kuma.__getRuntime().setNativeKeyModifiers(mapping, KeyModifiers.none());
        } finally {
            trashslotblacklist$reconciling = false;
        }
    }
}
