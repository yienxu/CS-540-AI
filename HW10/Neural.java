// CS 540 HW 10
// Yien Xu

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Scanner;

class Record {

    public double[] x;
    public double y;

    public Record(double x1, double x2, double y) {
        x = new double[2];
        x[0] = x1;
        x[1] = x2;
        this.y = y;
    }

}


public class Neural {

    private static double[] w;

    private static final String TRAIN = "hw2_midterm_A_train.txt";
    private static final String TEST = "hw2_midterm_A_test.txt";
    private static final String EVAL = "hw2_midterm_A_eval.txt";

    public static ArrayList<Record> readFromFile(String filename) {
        ArrayList<Record> records = new ArrayList<>();
        File file = new File(filename);
        try (Scanner fin = new Scanner(file)) {
            while (fin.hasNextLine()) {
                String line = fin.nextLine();
                String[] tokens = line.split("\\s+");
                double x1 = Double.parseDouble(tokens[0]);
                double x2 = Double.parseDouble(tokens[1]);
                double y = Double.parseDouble(tokens[2]);
                records.add(new Record(x1, x2, y));
            }
        } catch (FileNotFoundException e) {
            System.out.println("File " + filename + " is not found.");
            System.exit(1);
        }
        return records;
    }

    /**
     * @return Math.max(z, 0)
     */
    private static double ReLU(double z) {
        return Math.max(z, 0);
    }

    /**
     * @return 1.0 / (1 + Math.exp(-z))
     */
    private static double sigmoid(double z) {
        return 1.0 / (1 + Math.exp(-z));
    }

    /**
     * @return uA, vA, uB, vB, uC, vC
     */
    private static double[] eval(double[] x) {
        double uA = w[0] + w[1] * x[0] + w[2] * x[1];
        double vA = ReLU(uA);
        double uB = w[3] + w[4] * x[0] + w[5] * x[1];
        double vB = ReLU(uB);
        double uC = w[6] + w[7] * vA + w[8] * vB;
        double vC = sigmoid(uC);
        return new double[] {uA, vA, uB, vB, uC, vC};
    }

    /**
     * @return E, dEdvC, dEduC
     */
    private static double[] outputLayerDerivative(double[] values, double y) {
        double vC = values[5];
        double E = 0.5 * Math.pow(vC - y, 2);
        double dEdvC = vC - y;
        double dEduC = dEdvC * vC * (1 - vC);
        return new double[] {E, dEdvC, dEduC};
    }

    /**
     * @return dEdvA, dEduA, dEdvB, dEduB
     */
    private static double[] hiddenLayerDerivative(double[] values, double y) {
        double[] outputLayerDerivatives = outputLayerDerivative(values, y);
        double dEduC = outputLayerDerivatives[2];
        double dEdvA = w[7] * dEduC;
        double uA = values[0];
        double dEduA = uA >= 0 ? dEdvA : 0;
        double dEdvB = w[8] * dEduC;
        double uB = values[2];
        double dEduB = uB >= 0 ? dEdvB : 0;
        return new double[] {dEdvA, dEduA, dEdvB, dEduB};
    }

    /**
     * @return dEdw[0:8]
     */
    private static double[] weightDerivative(double[] x, double[] values, double y) {
        double[] outputLayerDerivatives = outputLayerDerivative(values, y);
        double[] hiddenLayerDerivatives = hiddenLayerDerivative(values, y);
        double[] dEdw = new double[w.length];
        dEdw[8] = values[3] * outputLayerDerivatives[2];
        dEdw[7] = values[1] * outputLayerDerivatives[2];
        dEdw[6] = outputLayerDerivatives[2];
        dEdw[5] = x[1] * hiddenLayerDerivatives[3];
        dEdw[4] = x[0] * hiddenLayerDerivatives[3];
        dEdw[3] = hiddenLayerDerivatives[3];
        dEdw[2] = x[1] * hiddenLayerDerivatives[1];
        dEdw[1] = x[0] * hiddenLayerDerivatives[1];
        dEdw[0] = hiddenLayerDerivatives[1];
        return dEdw;
    }

    private static void SGD(double[] weightDerivatives, double eta) {
        for (int i = 0; i < w.length; i++) {
            w[i] = w[i] - eta * weightDerivatives[i];
        }
    }

    private static double setError(ArrayList<Record> records) {
        double setError = 0;
        for (Record record : records) {
            setError += Math.pow(eval(record.x)[5] - record.y, 2);
        }
        setError *= 0.5;
        return setError;
    }

    private static double accuracy(ArrayList<Record> records) {
        int numAccuratePredictions = 0;
        for (Record record : records) {
            int prediction = eval(record.x)[5] >= 0.5 ? 1 : 0;
            int label = (int) record.y;
            if (prediction == label) {
                numAccuratePredictions++;
            }
        }
        return (double) numAccuratePredictions / records.size();
    }

    private static void printResult(double[] values) {
        StringBuilder builder = new StringBuilder();
        for (double val : values) {
            builder.append(String.format("%.5f ", val));
        }
        System.out.println(builder.toString().trim());
    }

    public static void main(String[] args) {
        try {
            int flag = Integer.parseInt(args[0]);
            w = new double[9];
            for (int i = 0; i < w.length; i++) {
                w[i] = Double.parseDouble(args[i + 1]);
            }
            if (flag >= 100 && flag <= 500) {
                double y = Double.NaN;
                double eta = Double.NaN;
                double[] x = new double[2];
                for (int i = 0; i < x.length; i++) {
                    x[i] = Double.parseDouble(args[i + w.length + 1]);
                }
                // if 200 <= flag <= 500, initialize y
                if (flag >= 200) {
                    y = Double.parseDouble(args[w.length + x.length + 1]);
                }
                // if flag == 500, initialize eta
                if (flag == 500) {
                    eta = Double.parseDouble(args[w.length + x.length + 2]);
                }

                if (flag == 100) {
                    printResult(eval(x));
                } else if (flag == 200) {
                    printResult(outputLayerDerivative(eval(x), y));
                } else if (flag == 300) {
                    printResult(hiddenLayerDerivative(eval(x), y));
                } else if (flag == 400) {
                    printResult(weightDerivative(x, eval(x), y));
                } else if (flag == 500) {
                    printResult(w);
                    printResult(new double[] {outputLayerDerivative(eval(x), y)[0]});
                    SGD(weightDerivative(x, eval(x), y), eta);
                    printResult(w);
                    printResult(new double[] {outputLayerDerivative(eval(x), y)[0]});
                }
            } else { // if flag >= 600
                double eta = Double.parseDouble(args[w.length + 1]);
                ArrayList<Record> records = readFromFile(TRAIN);
                ArrayList<Record> evals = readFromFile(EVAL);
                ArrayList<Record> tests = readFromFile(TEST);
                int T = -1;
                if (flag >= 700) {
                    T = Integer.parseInt(args[w.length + 2]);
                }
                if (flag == 600) {
                    for (Record record : records) {
                        printResult(new double[] {record.x[0], record.x[1], record.y});
                        SGD(weightDerivative(record.x, eval(record.x), record.y), eta);
                        printResult(w);
                        printResult(new double[] {setError(evals)});
                    }
                } else if (flag == 700) {
                    for (int i = 0; i < T; i++) {
                        for (Record record : records) {
                            SGD(weightDerivative(record.x, eval(record.x), record.y), eta);
                        }
                        printResult(w);
                        printResult(new double[] {setError(evals)});
                    }
                } else if (flag == 800) {
                    int iter;
                    double prevSetError = Double.POSITIVE_INFINITY;
                    double setError = Double.NEGATIVE_INFINITY;
                    for (iter = 1; iter <= T; iter++) {
                        for (Record record : records) {
                            SGD(weightDerivative(record.x, eval(record.x), record.y), eta);
                        }
                        setError = setError(evals);
                        if (setError > prevSetError) {
                            break;
                        } else {
                            prevSetError = setError;
                        }
                    }
                    System.out.println(iter == T + 1 ? iter - 1 : iter);
                    printResult(w);
                    printResult(new double[] {setError});
                    printResult(new double[] {accuracy(tests)});
                }
            }
        } catch (Exception e) {
            System.out.println("Usage: java Neural FLAG [args]");
        }
    }

}
