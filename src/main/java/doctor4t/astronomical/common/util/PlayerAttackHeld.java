package doctor4t.astronomical.common.util;

public interface PlayerAttackHeld {
	default boolean astronomical$isHoldingAttack() {
		return false;
	}

	default void astronomical$setHoldingAttack(boolean attackHeld) {
	}
}
