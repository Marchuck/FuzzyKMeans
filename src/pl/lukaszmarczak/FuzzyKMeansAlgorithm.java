package pl.lukaszmarczak;

import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by lukasz on 09.04.2016.
 */
public class FuzzyKMeansAlgorithm {

    public static final double verySmallPositiveNumber = 1 / Math.pow(10, 10);

    public static class ClusterCenter {
        public ClusterCenter() {
            this(0);
        }

        public ClusterCenter(double value) {
            this.value = value;
        }

        public double value;

        @Override
        public boolean equals(Object obj) {
            return obj instanceof ClusterCenter && Double.compare(((ClusterCenter) obj).value, value) == 0;
        }


        @Override
        public int hashCode() {
            return (int) (31 * Double.doubleToLongBits(value));
        }
    }

    public static synchronized FuzzyKMeansAlgorithm create() {
        return new FuzzyKMeansAlgorithm();
    }

    private FuzzyKMeansAlgorithm() {
    }

    public boolean isValid(double[][] u) {
        return Math.ceil(sumU(u)) == 1L;
    }

    public double sumU(double[][] u) {
        double size = 0L;
        for (double[] anU : u)
            for (double anAnU : anU) size += anAnU;
        return size;
    }

    public double multiplyUwithX(double[][] u, double[] X) {
        double size = 0L;
        for (int i = 0; i < u.length; i++)
            for (double uRow : u[i]) size += uRow * X[i];
        return size;
    }

    public void runAlgorithm() {
        //step 1
        List<ClusterCenter> clusterCenters = new ArrayList<>();
        clusterCenters.add(new ClusterCenter());
        int p = 1;
        int m = 2;
        double u[][] = createDummyU();
        double X[] = createDummyU()[1];
        double d[][] = createDummyU();
        ClusterCenter previousCluster = clusterCenters.get(0);
        ClusterCenter currentCluster = null;

        //step 2
        while (!clustersMetricStopCondition(currentCluster, previousCluster)) {
            for (int j = 0; j < u.length; j++) {
                for (int i = 0; i < u[j].length; i++) {
                    if (isDTooSmall(d[j][i])) {
                        u[j][i] = 1;
                    } else {
                        double fixedDPower = 1 / (m - 1);
                        double dPowered = Math.pow(d[j][i], fixedDPower);
                        double sumPowered = calculateSumPowered(d[i], fixedDPower);
                        double result = 1 / (dPowered * sumPowered);
                        u[j][i] = result;
                    }
                }
            }
            //step 3
            clusterCenters.add(calculateNewCluster(u, X, m));
            int currentClusterSize = clusterCenters.size();
            if (currentClusterSize > 1) {
                previousCluster = clusterCenters.get(currentClusterSize - 2);
                currentCluster = clusterCenters.get(currentClusterSize - 1);
            }
        }
        for (ClusterCenter c : clusterCenters) {
            log("Next cluster: " + c.value);
        }
    }

    private boolean clustersMetricStopCondition(@Nullable ClusterCenter c1, @Nullable ClusterCenter c2) {
        if (c1 == null || c2 == null) return false;
        double value = c1.value - c2.value;
        if (value < 0) value = -value;
        return value < verySmallPositiveNumber;
    }


    private ClusterCenter calculateNewCluster(double[][] u, double[] X, int m) {
        double a = multiplyUwithX(u, X);
        double b = sumU(u);
        double value = a / b;
        return new ClusterCenter(value);
    }

    private boolean isDTooSmall(double d) {
        return d < verySmallPositiveNumber;
    }

    private double calculateSumPowered(double[] di, double m) {
        double sum = 0;
        for (double aDi : di) sum += Math.pow((1 / aDi), m);
        return sum;
    }

    public double calculateCoefficientJ(double[][] u, double[][] d) {
        if (!isValid(u)) throw new IllegalArgumentException("Invalid argument");
        //        if (u.length!=d.length) throw new IllegalArgumentException("Given u = "+u.length+" got d = "+d.length);
        double J = 0L;
        for (int j = 0; j < u.length; j++) {
            for (int i = 0; i < d.length; i++) {
                double result = u[j][i] * d[j][i];
                J += result;
            }
        }
        return J;
    }

    public double[][] createDummyU() {
        double factor = 1 / 6f;
        double u[][] = new double[][]{
                new double[]{factor, factor, 0},
                new double[]{0, 2 * factor, 0},
                new double[]{factor, 0, factor},
        };
        return u;
    }

    public int[][] createEmpty(int capacity) {
        int matrix[][] = new int[capacity][capacity];
        return matrix;
    }

    public static void log(String s) {
        System.out.println(s);
    }

    public int[][] fillRows(int[]... rows) {
        int actualLength = rows[0].length;
        for (int j = 0; j < rows.length; j++) {
            int nextLength = rows[j].length;
            if (nextLength != actualLength) throw new IllegalArgumentException("Invalid argument for " + actualLength +
                    " got " + nextLength);
        }
        int[][] out = new int[actualLength][rows.length];
        for (int i = 0; i < rows.length; i++) {
            System.arraycopy(rows[i], 0, out[i], 0, rows[i].length);
        }
        return out;
    }

    public static void main(String[] args) {
        FuzzyKMeansAlgorithm fkm = new FuzzyKMeansAlgorithm();
        fkm.runAlgorithm();
    }
}
