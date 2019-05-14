/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package edu.gsgp.experiment.data;

import edu.gsgp.experiment.data.Dataset;
import edu.gsgp.utils.Utils.DatasetType;

/**
 * @author Luiz Otavio Vilas Boas Oliveira
 * http://homepages.dcc.ufmg.br/~luizvbo/ 
 * luiz.vbo@gmail.com
 * Copyright (C) 20014, Federal University of Minas Gerais, Belo Horizonte, Brazil
 */
public class ExperimentalData {
    private Dataset training;
    private Dataset test;
    private Dataset validation;

    public ExperimentalData(){
        training = new Dataset();
        test = new Dataset();
        validation = new Dataset();
    }

    public ExperimentalData(Dataset training, Dataset test, Dataset validation) {
        this.training = training;
        this.test = test;
        this.validation = validation;
    }    
    
    /**
     * Return the dataset given by the dataType parameter
     * @param dataType Indicate what dataset to return
     * @return The selected dataset
     */
    public Dataset getDataset(DatasetType dataType){
        switch(dataType){
            case TEST:
                return test;
            case TRAINING:
                return training;
            case VALIDATION:
                return validation;
            default:
                return null;
        }
    }

    public void setDataset(Dataset dataset, DatasetType dataType) {
        switch(dataType){
            case TEST:
                test = dataset;
                break;
            case TRAINING:
                training = dataset;
                break;
        }
    }

    public ExperimentalData softClone() {
        return new ExperimentalData(training.softClone(), test.softClone(), validation.softClone());
    }
}
