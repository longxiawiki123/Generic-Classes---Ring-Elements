package AbstractMath.Rings.Structures.Polynomial;

import AbstractMath.Rings.RingElement;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/** An immutable polynomial with coefficients stored from constant to highest power. */
public final class Polynomial<T extends RingElement<T>> implements RingElement<Polynomial<T>> {
    private final ArrayList<T> coefficients;

    public Polynomial(List<T> coefficients) {
        this(initialDegree(coefficients), coefficients);
    }

    public Polynomial(int degree, List<T> coefficients) {
        validateDegree(degree);
        Objects.requireNonNull(coefficients, ErrorStrings.NULL_COEFFICIENTS.getMessage());
        if (coefficients.size() != degree + 1) {
            throw new IllegalArgumentException(ErrorStrings.COEFFICIENT_COUNT.getMessage());
        }
        this.coefficients = new ArrayList<>(coefficients);
        for (T coefficient : this.coefficients) {
            Objects.requireNonNull(coefficient, ErrorStrings.NULL_COEFFICIENT.getMessage());
        }
        T first = this.coefficients.get(0);
        for (T coefficient : this.coefficients) {
            if (!first.hasSameType(coefficient)) {
                throw new IllegalArgumentException(ErrorStrings.MIXED_COEFFICIENT_TYPES.getMessage());
            }
        }
        while (this.coefficients.size() > 1) {
            T last = this.coefficients.get(this.coefficients.size() - 1);
            if (!last.equals(last.zero())) {
                break;
            }
            this.coefficients.remove(this.coefficients.size() - 1);
        }
    }

    public static <T extends RingElement<T>> Builder<T> builder(int degree) {
        return new Builder<>(degree);
    }

    public int degree() {
        return coefficients.size() - 1;
    }

    public T coefficient(int degree) {
        if (degree < 0) {
            throw new IllegalArgumentException(ErrorStrings.NEGATIVE_TERM_DEGREE.getMessage());
        }
        if (degree >= coefficients.size()) {
            return coefficients.get(0).zero();
        }
        return coefficients.get(degree);
    }

    public T getValue(int degree) {
        return coefficient(degree);
    }

    @Override
    public boolean hasSameType(Object other) {
        return other instanceof Polynomial<?> polynomial
                && coefficients.get(0).hasSameType(polynomial.coefficients.get(0));
    }

    @Override
    public Polynomial<T> add(Polynomial<T> other) {
        Objects.requireNonNull(other, ErrorStrings.NULL_OPERAND.getMessage());
        int size = Math.max(coefficients.size(), other.coefficients.size());
        List<T> result = new ArrayList<>();
        for (int degree = 0; degree < size; degree++) {
            result.add(coefficient(degree).add(other.coefficient(degree)));
        }
        return new Polynomial<>(result);
    }

    @Override
    public Polynomial<T> negate() {
        List<T> result = new ArrayList<>();
        for (T coefficient : coefficients) {
            result.add(coefficient.negate());
        }
        return new Polynomial<>(result);
    }

    @Override
    public Polynomial<T> multiply(Polynomial<T> other) {
        Objects.requireNonNull(other, ErrorStrings.NULL_OPERAND.getMessage());
        long size = (long) coefficients.size() + other.coefficients.size() - 1;
        if (size > Integer.MAX_VALUE) {
            throw new IllegalArgumentException(ErrorStrings.INVALID_DEGREE.getMessage());
        }
        List<T> result = new ArrayList<>();
        for (int degree = 0; degree < size; degree++) {
            result.add(coefficients.get(0).zero());
        }
        for (int left = 0; left < coefficients.size(); left++) {
            for (int right = 0; right < other.coefficients.size(); right++) {
                T product = coefficients.get(left).multiply(other.coefficients.get(right));
                int degree = left + right;
                result.set(degree, result.get(degree).add(product));
            }
        }
        return new Polynomial<>(result);
    }

    @Override
    public Polynomial<T> zero() {
        return new Polynomial<>(List.of(coefficients.get(0).zero()));
    }

    @Override
    public Polynomial<T> one() {
        return new Polynomial<>(List.of(coefficients.get(0).one()));
    }

    @Override
    public boolean equals(Object other) {
        if (!hasSameType(other)) {
            throw new ClassCastException(ErrorStrings.INCOMPATIBLE_TYPE.getMessage());
        }
        Polynomial<?> polynomial = (Polynomial<?>) other;
        if (coefficients.size() != polynomial.coefficients.size()) {
            return false;
        }
        for (int degree = 0; degree < coefficients.size(); degree++) {
            if (!coefficients.get(degree).equals(polynomial.coefficients.get(degree))) {
                return false;
            }
        }
        return true;
    }

    @Override
    public int hashCode() {
        return coefficients.hashCode();
    }

    static void validateDegree(int degree) {
        if (degree < 0 || degree == Integer.MAX_VALUE) {
            throw new IllegalArgumentException(ErrorStrings.INVALID_DEGREE.getMessage());
        }
    }

    private static int initialDegree(List<?> coefficients) {
        Objects.requireNonNull(coefficients, ErrorStrings.NULL_COEFFICIENTS.getMessage());
        if (coefficients.isEmpty()) {
            throw new IllegalArgumentException(ErrorStrings.EMPTY_COEFFICIENTS.getMessage());
        }
        return coefficients.size() - 1;
    }
}
