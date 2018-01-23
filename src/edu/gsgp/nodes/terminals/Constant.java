package edu.gsgp.nodes.terminals;

import edu.gsgp.nodes.Node;
import edu.gsgp.utils.MersenneTwister;

/**
 * Created by casadei on 20/10/17.
 */
public class Constant implements Terminal {
    private double value;
    private Node parent;
    private int parentArgPosition;

    public Constant(double value) {
        this.value = value;
    }


    @Override
    public int getArity() {
        return 0;
    }

    @Override
    public double eval(double[] inputs) {
        return value;
    }

    @Override
    public int getNumNodes() {
        return 1;
    }

    @Override
    public Node clone(Node parent) {
        Constant clone = new Constant(this.value);
        clone.setParent(parent, this.parentArgPosition);

        return clone;
    }

    @Override
    public Node getChild(int index) {
        return null;
    }

    @Override
    public Node getParent() {
        return this.parent;
    }

    @Override
    public void setParent(Node parent, int argPosition) {
        this.parent = parent;
        this.parentArgPosition = argPosition;

    }

    @Override
    public int getParentArgPosition() {
        return this.parentArgPosition;
    }

    @Override
    public Terminal softClone(MersenneTwister rnd) {
        return (Terminal)clone(this.parent);
    }
}
