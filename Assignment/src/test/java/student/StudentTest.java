package student;

import AbstractMath.Rings.Structures.Matrix.Matrix;
import AbstractMath.Rings.Structures.Polynomial.Polynomial;
import AbstractMath.Rings.ZMod.Mod5;
import AbstractMath.Rings.ZMod.Modulus;
import AbstractMath.Rings.ZMod.ZModElement;

import java.util.List;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;

class StudentTest {
    @Test
    void addingTheNegationProducesTheZeroPolynomial() {
        Mod5 modulus = new Mod5();
        Polynomial<ZModElement<Mod5>> polynomial = new Polynomial<>(List.of(
                new ZModElement<>(2, modulus), new ZModElement<>(3, modulus)));

        Polynomial<ZModElement<Mod5>> result = polynomial.add(polynomial.negate());

        assertEquals(0, result.degree());
        assertEquals(0, result.coefficient(0).getValue());
        assertEquals(0, result.coefficient(10).getValue());
    }

    @Test
    void multiplicationCanReduceDegreeWhenLeadingCoefficientsMultiplyToZero() {
        Mod6 modulus = new Mod6();
        var left = new Polynomial<>(List.of(
                new ZModElement<>(1, modulus), new ZModElement<>(2, modulus)));
        var right = new Polynomial<>(List.of(
                new ZModElement<>(1, modulus), new ZModElement<>(3, modulus)));

        var product = left.multiply(right);

        assertEquals(1, product.degree());
        assertEquals(1, product.coefficient(0).getValue());
        assertEquals(5, product.coefficient(1).getValue());
        assertEquals(0, product.coefficient(2).getValue());
        assertEquals(1, left.degree());
        assertEquals(1, right.degree());
    }

    @Test
    void polynomialMultiplicationKeepsMatrixCoefficientsInOperandOrder() {
        var a = matrix(0, 1, 0, 0);
        var b = matrix(0, 0, 1, 0);
        var left = new Polynomial<>(List.of(a.zero(), a));
        var right = new Polynomial<>(List.of(b.zero(), b));

        var forward = left.multiply(right);
        var reverse = right.multiply(left);

        assertEquals(2, forward.degree());
        assertEquals(matrix(1, 0, 0, 0), forward.coefficient(2));
        assertEquals(matrix(0, 0, 0, 1), reverse.coefficient(2));
        assertNotEquals(forward, reverse);
        assertEquals(a.zero(), forward.coefficient(0));
        assertEquals(a.zero(), forward.coefficient(1));
        assertEquals(left, left.multiply(left.one()));
    }

    private static Matrix<ZModElement<Mod5>> matrix(int a, int b, int c, int d) {
        Mod5 modulus = new Mod5();
        return new Matrix<>(2, 2, List.of(
                List.of(new ZModElement<>(a, modulus), new ZModElement<>(b, modulus)),
                List.of(new ZModElement<>(c, modulus), new ZModElement<>(d, modulus))));
    }

    private record Mod6() implements Modulus {
        @Override
        public int value() {
            return 6;
        }
    }
}
