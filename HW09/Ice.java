// CS 540 HW 09
// Yien Xu
// yxu322@wisc.edu

import java.util.Random;

public class Ice {

    private static final int[] x = new int[2016 - 1855 + 1];
    private static final int[] y = new int[] {118, 151, 121, 96, 110, 117, 132, 104, 125, 118, 125,
        123, 110, 127, 131, 99, 126, 144, 136, 126, 91, 130, 62, 112, 99, 161, 78, 124, 119, 124,
        128, 131, 113, 88, 75, 111, 97, 112, 101, 101, 91, 110, 100, 130, 111, 107, 105, 89, 126,
        108, 97, 94, 83, 106, 98, 101, 108, 99, 88, 115, 102, 116, 115, 82, 110, 81, 96, 125, 104,
        105, 124, 103, 106, 96, 107, 98, 65, 115, 91, 94, 101, 121, 105, 97, 105, 96, 82, 116, 114,
        92, 98, 101, 104, 96, 109, 122, 114, 81, 85, 92, 114, 111, 95, 126, 105, 108, 117, 112, 113,
        120, 65, 98, 91, 108, 113, 110, 105, 97, 105, 107, 88, 115, 123, 118, 99, 93, 96, 54, 111,
        85, 107, 89, 87, 97, 93, 88, 99, 108, 94, 74, 119, 102, 47, 82, 53, 115, 21, 89, 80, 101,
        95, 66, 106, 97, 87, 109, 57, 87, 117, 91, 62, 65};

    private static int sum(int[] arr) {
        int sum = 0;
        for (int x : arr) {
            sum += x;
        }
        return sum;
    }

    private static double mean(int[] arr) {
        return (double) sum(arr) / arr.length;
    }

    private static double sd(int[] arr) {
        int n = arr.length;
        double mean = mean(arr);
        double sd = 0;
        for (double x : arr) {
            sd += Math.pow((x - mean), 2);
        }
        sd *= 1.0 / (n - 1);
        sd = Math.sqrt(sd);
        return sd;
    }

    private static double MSE(int[] x, int[] y, double beta0, double beta1) {
        int n = x.length;
        double MSE = 0;
        for (int i = 0; i < n; i++) {
            MSE += Math.pow(beta0 + beta1 * x[i] - y[i], 2);
        }
        MSE *= 1.0 / n;
        return MSE;
    }
    
    private static double MSE(double[] x, int[] y, double beta0, double beta1) {
        int n = x.length;
        double MSE = 0;
        for (int i = 0; i < n; i++) {
            MSE += Math.pow(beta0 + beta1 * x[i] - y[i], 2);
        }
        MSE *= 1.0 / n;
        return MSE;
    }

    private static double[] gradient(int[] x, int[] y, double beta0, double beta1) {
        int n = x.length;
        double grad0 = 0;
        double grad1 = 0;
        for (int i = 0; i < n; i++) {
            grad0 += beta0 + beta1 * x[i] - y[i];
            grad1 += (beta0 + beta1 * x[i] - y[i]) * x[i];
        }
        grad0 *= 2.0 / n;
        grad1 *= 2.0 / n;
        return new double[] {grad0, grad1};
    }
    
    private static double[] gradient(double[] x, int[] y, double beta0, double beta1) {
        int n = x.length;
        double grad0 = 0;
        double grad1 = 0;
        for (int i = 0; i < n; i++) {
            grad0 += beta0 + beta1 * x[i] - y[i];
            grad1 += (beta0 + beta1 * x[i] - y[i]) * x[i];
        }
        grad0 *= 2.0 / n;
        grad1 *= 2.0 / n;
        return new double[] {grad0, grad1};
    }

    private static double[] betaHat(int[] x, int[] y) {
        int n = x.length;
        double xBar = mean(x);
        double yBar = mean(y);
        double beta0 = 0;
        double beta1 = 0;
        double numerator = 0;
        double denominator = 0;
        for (int i = 0; i < n; i++) {
            numerator += (x[i] - xBar) * (y[i] - yBar);
            denominator += (x[i] - xBar) * (x[i] - xBar);
        }
        beta1 = numerator / denominator;
        beta0 = yBar - beta1 * xBar;
        return new double[] {beta0, beta1};
    }

    private static double[] standardize(int[] arr) {
        int n = arr.length;
        double[] std = new double[n];
        double mean = mean(arr);
        double sd = sd(arr);
        for (int i = 0; i < n; i++) {
            std[i] = (x[i] - mean) / sd;
        }
        return std;
    }
    
    private static double[] SGD(double[] x, int[] y, double beta0, double beta1, int rand) {
        double grad0 = 2*(beta0 + beta1*x[rand] - y[rand]);
        double grad1 = 2*(beta0 + beta1*x[rand] - y[rand])*x[rand];
        return new double[] {grad0, grad1};
    }

    public static void main(String[] args) {
        for (int i = 1855; i <= 2016; i++) {
            x[i - 1855] = i;
        }
        int n = x.length;
        try {
            int flag = Integer.parseInt(args[0]);
            if (flag == 100) {
                for (int i = 0; i < n; i++) {
                    System.out.printf("%d %d\n", x[i], y[i]);
                }
            } else if (flag == 200) {
                System.out.printf("%d\n", n);
                System.out.printf("%.2f\n", mean(y));
                System.out.printf("%.2f\n", sd(y));
            } else if (flag == 300) {
                double beta0 = Double.parseDouble(args[1]);
                double beta1 = Double.parseDouble(args[2]);
                System.out.printf("%.2f\n", MSE(x, y, beta0, beta1));
            } else if (flag == 400) {
                double beta0 = Double.parseDouble(args[1]);
                double beta1 = Double.parseDouble(args[2]);
                double[] grad = gradient(x, y, beta0, beta1);
                System.out.printf("%.2f\n", grad[0]);
                System.out.printf("%.2f\n", grad[1]);
            } else if (flag == 500) {
                double eta = Double.parseDouble(args[1]);
                int time = Integer.parseInt(args[2]);
                double beta0 = 0;
                double beta1 = 0;
                for (int i = 1; i <= time; i++) {
                    double[] grad = gradient(x, y, beta0, beta1);
                    beta0 = beta0 - eta * grad[0];
                    beta1 = beta1 - eta * grad[1];
                    System.out.printf("%d %.2f %.2f %.2f\n", i, beta0, beta1,
                        MSE(x, y, beta0, beta1));
                }
            } else if (flag == 600) {
                double[] betaHat = betaHat(x, y);
                System.out.printf("%.2f %.2f %.2f\n", betaHat[0], betaHat[1],
                    MSE(x, y, betaHat[0], betaHat[1]));
            } else if (flag == 700) {
                double[] betaHat = betaHat(x, y);
                System.out.printf("%.2f\n", betaHat[0] + betaHat[1] * Integer.parseInt(args[1]));
            } else if (flag == 800) {
                double eta = Double.parseDouble(args[1]);
                int time = Integer.parseInt(args[2]);
                double[] stdX = standardize(x);
                double beta0 = 0;
                double beta1 = 0;
                for (int i = 1; i <= time; i++) {
                    double[] grad = gradient(stdX, y, beta0, beta1);
                    beta0 = beta0 - eta * grad[0];
                    beta1 = beta1 - eta * grad[1];
                    System.out.printf("%d %.2f %.2f %.2f\n", i, beta0, beta1,
                        MSE(stdX, y, beta0, beta1));
                }
            } else if (flag == 900) {
                Random random = new Random();
                double eta = Double.parseDouble(args[1]);
                int time = Integer.parseInt(args[2]);
                double[] stdX = standardize(x);
                double beta0 = 0;
                double beta1 = 0;
                for (int i = 1; i <= time; i++) {
                    double[] grad = SGD(stdX, y, beta0, beta1, random.nextInt(n));
                    beta0 = beta0 - eta * grad[0];
                    beta1 = beta1 - eta * grad[1];
                    System.out.printf("%d %.2f %.2f %.2f\n", i, beta0, beta1,
                        MSE(stdX, y, beta0, beta1));
                }
            } else {
                throw new UnsupportedOperationException();
            }
        } catch (Exception e) {
            System.out.println("Usage: java Ice FLAG [arg1 arg2]");
        }

    }

}
