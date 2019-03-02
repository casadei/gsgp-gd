package edu.gsgp.experiment.data.classifiers;

import edu.gsgp.experiment.data.Classifier;
import edu.gsgp.experiment.data.Dataset;
import edu.gsgp.experiment.data.ExperimentalData;
import edu.gsgp.experiment.data.Instance;
import edu.gsgp.utils.MersenneTwister;
import edu.gsgp.utils.Utils;
import org.apache.commons.lang3.StringUtils;

import java.io.*;
import java.util.Scanner;

public class KMeans extends Classifier {
    private static String PATH = "/Users/casadei/dev/casadei/gsgp-mo/scripts/classifiers/kmeans.py";
    private static String PYTHON = "/Users/casadei/anaconda3/bin/python";

    @Override
    public void classify(MersenneTwister mersenneTwister, ExperimentalData experimentalData, int k) {
        Dataset training = experimentalData.getDataset(Utils.DatasetType.TRAINING);
        Dataset test = experimentalData.getDataset(Utils.DatasetType.TEST);

        try {
            ProcessBuilder pb = new ProcessBuilder(PYTHON, PATH, "-k", String.valueOf(k), "-tr",
                    String.valueOf(training.size()), "-ts", String.valueOf(test.size()), "-seed",
                    String.valueOf(123456));

            Process process = pb.start();

            OutputStream stdin = process.getOutputStream();
            InputStream stdout = process.getInputStream();
            InputStream stderr = process.getErrorStream();

            BufferedReader reader = new BufferedReader(new InputStreamReader(stderr));
            BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(stdin));

            Scanner scanner = new Scanner(stdout);

            write(training, test, writer);

            process.waitFor();

            while (scanner.hasNextLine()) {
                String[] line = scanner.nextLine().split(",");
                Dataset target = line[0].equals("TRAINING") ? training : test;

                target.get(Integer.parseInt(line[1])).addToGroup(Integer.parseInt(line[2]));
            }
        } catch (Exception ex) {
            System.out.println("Error " + ex.getMessage());
        }
    }

    private void write(Dataset training, Dataset test, BufferedWriter writer) throws IOException {
        for (Instance instance : training) {
            writer.write(StringUtils.join(instance.input, ',') + "\n");
        }

        for (Instance instance : test) {
            writer.write(StringUtils.join(instance.input, ',') + "\n");
        }

        writer.flush();
        writer.close();
    }
}