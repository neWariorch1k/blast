package com.game.blastred;

/**
 * Per-thread flag: "the entity whose armor is being rendered right now wears Blast Protection".
 * Set before the armor layer renders and cleared right after it.
 */
public final class BlastCache {
    private static final ThreadLocal<Boolean> CURRENT = ThreadLocal.withInitial(() -> false);

    private BlastCache() {
    }

    public static void push(boolean hasBlast) {
        CURRENT.set(hasBlast);
    }

    public static boolean get() {
        return CURRENT.get();
    }

    public static void clear() {
        CURRENT.set(false);
    }
}
