/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package edu.gsgp.experiment.data;

import edu.gsgp.utils.Utils;

/**
 * @author Luiz Otavio Vilas Boas Oliveira
 * http://homepages.dcc.ufmg.br/~luizvbo/ 
 * luiz.vbo@gmail.com
 * Copyright (C) 20014, Federal University of Minas Gerais, Belo Horizonte, Brazil
 */
public class Instance {
    private static int IDCounter = 0;
    
    /** Instance input (one or more). */
    public double[] input;
    
    /** Instance output (only one). */
    public double output;

    /** Instance identifier. */
    public int id;

    /** Instance groups control flags */
    public int groups;

    /**
     * Constructor declaration
     * @param input Instance input 
     * @param output Instance output
     */
//    public Instance(double[] input, double output, int IDCounter) {
    public Instance(double[] input, double output) {
        this.input = input;
        this.output = output;
        id = IDCounter++;
    }

    public void addToGroup(int group) {
        if (belongsToGroup(group))
            return;

        this.groups |= Utils.toPowerOfTwo(group);
    }

    public void removeFromGroup(int group) {
        if (!belongsToGroup(group))
            return;

        this.groups ^= Utils.toPowerOfTwo(group);
    }

    public boolean belongsToGroup(int group) {
        return (this.groups & Utils.toPowerOfTwo(group)) > 0;
    }

    @Override
    public String toString() {
        StringBuilder out = new StringBuilder();
        for(int i = 0; i < input.length; i++){
            out.append(input[i]).append(",");
        }
        return out.toString() + output;
    }
}
