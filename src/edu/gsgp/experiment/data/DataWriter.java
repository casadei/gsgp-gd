/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package edu.gsgp.experiment.data;

import edu.gsgp.utils.Statistics;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import edu.gsgp.utils.Statistics.StatsType;
import edu.gsgp.experiment.config.PropertiesManager;
import edu.gsgp.utils.Utils;
import org.apache.commons.lang3.StringUtils;

import java.io.IOException;

/**
 * @author Luiz Otavio Vilas Boas Oliveira
 * http://homepages.dcc.ufmg.br/~luizvbo/ 
 * luiz.vbo@gmail.com
 * Copyright (C) 20014, Federal University of Minas Gerais, Belo Horizonte, Brazil
 */
public class DataWriter {
    public synchronized static void writeResults(String outputPath,
                                    String outputPrefix, 
                                    Statistics statistic,
                                    int experimentId) throws Exception {
        StatsType writeableStats[] = {
                StatsType.BEST_OF_GEN_SIZE,
                StatsType.BEST_OF_GEN_TR_FIT,
                StatsType.BEST_OF_GEN_TS_FIT,
                StatsType.TRAINING_SEMANTICS,
                StatsType.TEST_SEMANTICS,
                StatsType.ELAPSED_TIME,
                StatsType.SMART_TRAINING_FITNESS,
                StatsType.SMART_TRAINING_SEMANTICS,
                StatsType.SMART_TRAINING_FEEDBACK,
                StatsType.SMART_TRAINING_SANITY,
                StatsType.SMART_TEST_FITNESS,
                StatsType.SMART_TEST_SEMANTICS,
                StatsType.SMART_TEST_FEEDBACK,
                StatsType.SMART_TEST_SANITY
        };

        for (StatsType type : writeableStats){
            writeOnFile(outputPath, outputPrefix, 
                    experimentId + "," + statistic.asWritableString(type) + "\n", type);
        }
    }
    
    public static void writeLoadedParameters(PropertiesManager parameters) throws Exception{
        writeOnFile(parameters.getOutputDir(),
                   parameters.getFilePrefix(),
                   parameters.getLoadedParametersString(), 
                   StatsType.LOADED_PARAMETERS);
    }
        
    public static void writeOutputs(String outputPath,
                                    String outputPrefix, 
                                    Statistics[] statsArray,
                                    ExperimentalData data) throws Exception{
        File outputDir = getOutputDir(outputPath);
        outputDir = new File(outputDir.getAbsolutePath()+ File.separator + outputPrefix);
        outputDir.mkdirs();
        BufferedWriter bw;
        bw = new BufferedWriter(new FileWriter(outputDir.getAbsolutePath()+ File.separator + "outputs.csv"));
//        bw.write(getDesiredOutputs(data));
        bw.write(getStatisticsFromArray(statsArray, StatsType.TRAINING_SEMANTICS));
        bw.close();
    }

    public static void writeGroups(PropertiesManager properties, ExperimentalData data, int execution) throws Exception {
        StringBuilder sb = new StringBuilder();
        sb.append(properties.getNumberOfObjectives());
        sb.append("\n");

        for (Utils.DatasetType dataType : Utils.DatasetType.values()) {
            for (Instance instance : data.getDataset(dataType)) {
                sb.append(dataType.toString() + ",");
                sb.append(instance.groups + ",");
                sb.append(StringUtils.join(instance.input, ','));
                sb.append("," + instance.output);
                sb.append("\n");
            }
        }

        writeOnFile(
                properties.getOutputDir(),
                properties.getFilePrefix(),
                sb.toString(),
                String.format(StatsType.GROUPS.getPath(), execution));
    }


    /**    /**
     * Write some information to the output file
     * @param outputPath Path to the directory where the output will be written
     * @param outputPrefix Name of the directory where the files will be written
     * @param info Information to be written
     * @param statsType Type of information
     * @throws NullPointerException The pathname was not found
     * @throws SecurityException If a required system property value cannot be accessed
     * @throws IOException If the file exists but is a directory rather than a regular
     * file, does not exist but cannot be created, or cannot be opened for any other reason
     */
    private static void writeOnFile(String outputPath,
                                    String outputPrefix,
                                    String info,
                                    StatsType statsType) throws NullPointerException, SecurityException, IOException {

        writeOnFile(outputPath, outputPrefix, info, statsType.getPath());
    }

    private static void writeOnFile(String outputPath,
                                    String outputPrefix,
                                    String info,
                                    String path) throws NullPointerException, SecurityException, IOException {

        File outputDir = getOutputDir(outputPath);
        outputDir = new File(outputDir.getAbsolutePath()+ File.separator + outputPrefix);
        outputDir.mkdirs();
        // Object to write results on file
        BufferedWriter bw;
        bw = new BufferedWriter(new FileWriter(outputDir.getAbsolutePath()+ File.separator + path, true));
        bw.write(info);
        bw.close();
    }

    /**
     * Selects the path to save output data.
     * @param outputPath Path to a directory to write the output data
     * @return File object pointing to the output directory
     */
    protected static File getOutputDir(String outputPath){
        File outputDir;
        if(!outputPath.equals("")){
            outputDir = new File(outputPath);
        }
        else{
            outputDir = new File(System.getProperty("user.dir"));
        }  
        return outputDir;
    }

    protected static String getStatisticsFromArray(Statistics[] statsArray, Statistics.StatsType type) {
        StringBuilder str = new StringBuilder();
        for(Statistics stats : statsArray){
            str.append(stats.asWritableString(type) + "\n");
        }
        return str.toString();
    }
}
