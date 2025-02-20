import java.math.MathContext;
import java.math.RoundingMode;
import java.util.AbstractMap;
import java.util.ArrayList;
import java.math.BigDecimal;
import java.util.Map;

public class FibonacciGenerator {
    public static long recursiveFib(int n) {
        if (n <= 1){
            return n;
        }
        return recursiveFib(n - 1) + recursiveFib(n - 2);
    }

    public static long topDownDPFib(int n) {
        ArrayList<Long> list = new ArrayList<>();
        list.add(0L);
        list.add(1L);

        for (int i = 2; i < n + 1; i++) {
            list.add(list.get(i - 1) + list.get(i - 2));
        }

        return list.get(n);
    }

    public static long bottomUpDPFib(int n) {
        if (n <= 1) {
            return n;
        }

        long prev2 = 0, prev1 = 1, current = 0;

        for (int i = 2; i <= n; i++) {
            current = prev1 + prev2;
            prev2 = prev1;
            prev1 = current;
        }

        return current;
    }

    public static long matrixPowerFib(int n) {
        if (n == 0){
            return 0;
        }

        long[][] matrix = {
                {1, 1},
                {1, 0},
        };

        matrixToPower(matrix, n-1);

        return matrix[0][0];
    }

    public static void matrixToPower(long[][] matrix, int exp) {
        long[][] result = {{1, 0}, {0, 1}};

        while (exp > 0) {
            if (exp % 2 == 1) {
                multiply(result, matrix);
            }
            multiply(matrix, matrix);
            exp /= 2;
        }

        for (int i = 0; i < 2; i++) {
            System.arraycopy(result[i], 0, matrix[i], 0, 2);
        }
    }

    private static void multiply(long[][] A, long[][] B) {
        long a = A[0][0] * B[0][0] + A[0][1] * B[1][0];
        long b = A[0][0] * B[0][1] + A[0][1] * B[1][1];
        long c = A[1][0] * B[0][0] + A[1][1] * B[1][0];
        long d = A[1][0] * B[0][1] + A[1][1] * B[1][1];

        A[0][0] = a;
        A[0][1] = b;
        A[1][0] = c;
        A[1][1] = d;
    }

    public static long binetFormulaFib(int n) {
        double phi = 1 + Math.sqrt(5);
        double phi2 = 1 - Math.sqrt(5);

        return (long) ((Math.pow(phi, n) - Math.pow(phi2, n)) / (Math.pow(2, n) * Math.sqrt(5)));
    }

    public static long binetFormulaFib2(int n) {
        MathContext mc = new MathContext(50, RoundingMode.HALF_UP);

        BigDecimal sqrt5 = new BigDecimal(Math.sqrt(5), mc);

        BigDecimal phi = BigDecimal.ONE.add(sqrt5).divide(BigDecimal.valueOf(2), mc);
        BigDecimal phi2 = BigDecimal.ONE.subtract(sqrt5).divide(BigDecimal.valueOf(2), mc);

        BigDecimal phiN = phi.pow(n, mc);
        BigDecimal phi2N = phi2.pow(n, mc);

        BigDecimal fibN = phiN.subtract(phi2N).divide(sqrt5, mc);

        return fibN.setScale(0, RoundingMode.HALF_UP).longValue();
    }

    public static Map.Entry<Long, Long> fastDoublingFib(int n) {
        if (n == 0) {
            return new AbstractMap.SimpleEntry<>(0L, 1L);
        }

        Map.Entry<Long, Long> p = fastDoublingFib(n >> 1);
        long a = p.getKey();     // F_k
        long b = p.getValue();   // F_k+1

        // Compute F_2k and F_2k+1
        long c = a * (2 * b - a);
        long d = a * a + b * b;

        if ((n & 1) == 1) {
            return new AbstractMap.SimpleEntry<>(d, c + d);
        } else {
            return new AbstractMap.SimpleEntry<>(c, d);
        }
    }

    public static long fastDoublingFibHelper(int n) {
        Map.Entry<Long, Long> entry = fastDoublingFib(n);
        return entry.getKey();
    }

    public static long bitwiseDoublingFib(int n) {
        long a = 0, b = 1;
        for (int i = 31; i >= 0; i--) {
            long temp1 = a * (2 * b - a);
            long temp2 = a * a + b * b;
            if ((n & (1 << i)) != 0) {
                a = temp2;
                b = temp1 + temp2;
            } else {
                a = temp1;
                b = temp2;
            }
        }
        return a;
    }
}
