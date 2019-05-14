package edu.gsgp.experiment.data.classifiers;

import edu.gsgp.experiment.config.PropertiesManager;
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
    private static String PATH = "classifiers/kmeans.py";

    @Override
    public void classify(PropertiesManager properties, MersenneTwister mersenneTwister, ExperimentalData experimentalData, int k) {
        Dataset training = experimentalData.getDataset(Utils.DatasetType.TRAINING);
        Dataset test = experimentalData.getDataset(Utils.DatasetType.TEST);
        Dataset validation = experimentalData.getDataset(Utils.DatasetType.VALIDATION);

        try {
            ProcessBuilder pb = new ProcessBuilder(
                    properties.getPathToPython(),
                    properties.getPathToScripts() + PATH,
                    "-k",
                    String.valueOf(k),
                    "-tr",
                    String.valueOf(training.size()),
                    "-ts",
                    String.valueOf(test.size()),
                    "-vt",
                    String.valueOf(validation.size()),
                    "-seed",
                    String.valueOf(properties.getSeed())
            );

            Process process = pb.start();

            OutputStream stdin = process.getOutputStream();
            InputStream stdout = process.getInputStream();
            InputStream stderr = process.getErrorStream();

            BufferedReader reader = new BufferedReader(new InputStreamReader(stderr));
            BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(stdin));

            Scanner scanner = new Scanner(stdout);

            write(training, test, validation, writer);

            boolean stop = false;
            while (!stop) {
                while (!scanner.hasNext()) {
                    process.wait(1500);
                }

                String line = scanner.nextLine();
                if (line.startsWith("<<EOF")) {
                    stop = true;
                }  else {
                    String[] parts = line.split(",");

                    Dataset target = null;
                    if (parts[0].equals("TRAINING"))
                        target = training;
                    else if (parts[0].equals("TEST"))
                        target = test;
                    else
                        target = validation;

                    target.get(Integer.parseInt(parts[1])).addToGroup(Integer.parseInt(parts[2]));
                }
            }
        } catch (Exception ex) {
            System.out.println("Error " + ex.getMessage());
        }
    }

    private void write(Dataset training, Dataset test, Dataset validation, BufferedWriter writer) throws IOException {
        for (Instance instance : training) {
            writer.write(StringUtils.join(instance.input, ',') + "\n");
        }

        for (Instance instance : test) {
            writer.write(StringUtils.join(instance.input, ',') + "\n");
        }

        for (Instance instance : validation) {
            writer.write(StringUtils.join(instance.input, ',') + "\n");
        }

        writer.flush();
        writer.close();
    }
}