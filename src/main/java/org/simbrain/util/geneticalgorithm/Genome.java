package org.simbrain.util.geneticalgorithm;

import org.simbrain.util.math.SimbrainRandomizer;

/**
 * Contains a set of chromosomes, each of which contains a set of genes.
 *
 * @param <T> The Type of object (e.g. Network) encoded by this genome
 */
public abstract class Genome<T, G extends Genome<T, G>> {

    /**
     * A list of Chromosomes, indexed by a string description of their type
     */
    // private Map<String, Chromosome<?>> chromosomes = new HashMap<>();

    private SimbrainRandomizer randomizer;

    public abstract G crossOver(G other);

    public abstract void mutate();

    public abstract G copy();

    /**
     * Create an object using this genome. In a sense, create a phenotype.
     */
    public abstract T build();

    public SimbrainRandomizer getRandomizer() {
        return randomizer;
    }

    public void setRandomizer(SimbrainRandomizer randomizer) {
        this.randomizer = randomizer;
    }

    /**
     * Make a new randomizer based on the state of the parent's randomizer.
     *
     * @param randomizer the randomizer of the parent
     */
    public void inheritRandomizer(SimbrainRandomizer randomizer) {
        this.randomizer = new SimbrainRandomizer(randomizer.nextLong());
    }

    // public Map<String, Chromosome<?>> getChromosomes() {
    //     return chromosomes;
    // }
}