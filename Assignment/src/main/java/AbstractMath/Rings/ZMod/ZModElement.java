package AbstractMath.Rings.ZMod;

import AbstractMath.Rings.RingElement;

import java.util.Objects;

public class ZModElement<M extends Modulus> implements RingElement<ZModElement<M>> {
    private final int value;
    private final M modulus;

    public ZModElement(int value, M modulus) {
        this.modulus = Objects.requireNonNull(modulus, ErrorStrings.NULL_MODULUS.getMessage());
        int modulusValue = modulus.value();
        if (modulusValue <= 0 || modulusValue >= 1000) {
            throw new IllegalArgumentException(ErrorStrings.INVALID_MODULUS.getMessage());
        }
        if (value >= 1000) {
            throw new IllegalArgumentException(ErrorStrings.INVALID_VALUE.getMessage());
        }
        int remainder = value % modulusValue;
        if (remainder < 0) {
            remainder += modulusValue;
        }
        this.value = remainder;
    }

    public int getValue() {
        return value;
    }

    public int getModulus() {
        return modulus.value();
    }

    @Override
    public boolean hasSameType(Object other) {
        return other instanceof ZModElement<?> element
                && getClass() == element.getClass()
                && modulus.getClass() == element.modulus.getClass()
                && getModulus() == element.getModulus();
    }

    @Override
    public ZModElement<M> add(ZModElement<M> other) {
        requireSameModulus(other);
        return new ZModElement<>((value + other.value) % getModulus(), modulus);
    }

    @Override
    public ZModElement<M> negate() {
        return new ZModElement<>(-value, modulus);
    }

    @Override
    public ZModElement<M> multiply(ZModElement<M> other) {
        requireSameModulus(other);
        return new ZModElement<>((value * other.value) % getModulus(), modulus);
    }

    @Override
    public ZModElement<M> zero() {
        return new ZModElement<>(0, modulus);
    }

    @Override
    public ZModElement<M> one() {
        return new ZModElement<>(1 % getModulus(), modulus);
    }

    /**
     * Compares values within the same ring.
     *
     * @throws ClassCastException if the other object is null, has another element class,
     *         or uses another concrete modulus token class
     * @throws IllegalArgumentException if the other element has a different modulus
     */
    @Override
    public boolean equals(Object other) {
        if (!(other instanceof ZModElement<?> otherElement)) {
            throw new ClassCastException(ErrorStrings.INCOMPATIBLE_TYPE.getMessage());
        }
        if (otherElement.getModulus() != getModulus()) {
            throw new IllegalArgumentException(ErrorStrings.MODULUS_MISMATCH.getMessage());
        }
        if (!hasSameType(other)) {
            throw new ClassCastException(ErrorStrings.MODULUS_TYPE_MISMATCH.getMessage());
        }
        return value == otherElement.value;
    }

    @Override
    public int hashCode() {
        return Objects.hash(getClass(), modulus.getClass(), getModulus(), value);
    }

    private void requireSameModulus(ZModElement<M> other) {
        Objects.requireNonNull(other, ErrorStrings.NULL_OPERAND.getMessage());
        if (getModulus() != other.getModulus()) {
            throw new IllegalArgumentException(ErrorStrings.MODULUS_MISMATCH.getMessage());
        }
    }
}
