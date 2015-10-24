/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package gsgp.population;

import java.math.BigInteger;
import java.util.Arrays;
import gsgp.data.ExperimentDataset;
import gsgp.nodes.Node;

/**
 *
 * @author luiz
 */
public class GSGPIndividual extends Individual{
    private BigInteger numNodes;
    
    protected double[] tr_semantics;
    protected double[] ts_semantics;
    protected double tr_rmse;
    protected double ts_rmse;
    
    public GSGPIndividual(Node tree){
        super(tree);
    }
    
    public GSGPIndividual(Node tree, ExperimentDataset data) {
        this(tree, data, new BigInteger(tree.getNumNodes()+""));
    }
    
    public GSGPIndividual(Node tree, ExperimentDataset data, BigInteger numNodes) {
        super(tree);
        this.numNodes = numNodes;
        tr_semantics = new double[data.training.size()];
        ts_semantics = new double[data.test.size()];
    }
    
    public GSGPIndividual(ExperimentDataset data, BigInteger numNodes) {
        this(null, data, numNodes);
    }
    
    public double eval(double[] input){
        return tree.eval(input);
    }

    public BigInteger getNumNodes() {
        return numNodes;
    }

    public void setNumNodes(BigInteger numNodes) {
        this.numNodes = numNodes;
    }
    
    public void startNumNodes() {
        this.numNodes = new BigInteger(tree.getNumNodes()+"");
    }

    public void setTrRMSE(double tr_rmse) {
        this.tr_rmse = tr_rmse;
    }

    public void setTsRMSE(double ts_rmse) {
        this.ts_rmse = ts_rmse;
    }

    @Override
    public double getFitness() {
        return tr_rmse;
    }

    public double getTrRMSE() {
        return tr_rmse;
    }
    
    public double getTsRMSE() {
        return ts_rmse;
    }
    
//    @Override
//    public int compareTo(Individual o) {
//        if (getFitness() < o.getFitness()){
//            return -1;
//        }
//        if (getFitness() > o.getFitness()) {
//            return 1;
//        }
//        return 0;
//    }
    
    public GSGPIndividual clone(){
        GSGPIndividual newInd = new GSGPIndividual(tree.clone(null));
        newInd.numNodes = numNodes;
        newInd.tr_semantics = Arrays.copyOf(tr_semantics, tr_semantics.length);
        newInd.ts_semantics = Arrays.copyOf(ts_semantics, ts_semantics.length);
        newInd.tr_rmse = tr_rmse;
        newInd.ts_rmse = ts_rmse;
        return newInd;
    }

    @Override
    public boolean isBestSolution(double minError) {
        return getFitness() <= minError;
    }

    @Override
    public String toString() {
        return tree.toString(); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void setTree(Node randomSubtree) {
        this.tree = randomSubtree;
    }

    @Override
    public String getNumNodesAsString() {
        return numNodes.toString();
    }

    @Override
    public String getTrainingFitnessAsString() {
        return df.format(tr_rmse);
    }

    @Override
    public String getTestFitnessAsString() {
        return df.format(ts_rmse);
    }
    
    public void setTrSemantics(double[] tr_semantics) {
        this.tr_semantics = tr_semantics;
    }
    
    public void setTsSemantics(double[] newSemantics) {
        this.ts_semantics = newSemantics;
    }

    @Override
    public double[] getTrainingSemantics() {
        return tr_semantics;
    }
    
    public double[] getTestSemantics() {
        return ts_semantics;
    }
}
