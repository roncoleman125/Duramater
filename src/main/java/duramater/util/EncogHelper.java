package duramater.util;

import org.encog.ml.data.MLData;
import org.encog.ml.data.MLDataPair;
import org.encog.ml.data.MLDataSet;
import org.encog.ml.train.BasicTraining;
import org.encog.neural.networks.BasicNetwork;

public class EncogHelper {
    /** Maximum iterations to run */
    public final static long MAX_EPOCHS = 100000L;

    public final static boolean DEBUG = Boolean.parseBoolean(System.getProperty("debug","false"));

    /**
     * Reports training results.
     * @param trainingSet Training set of observations
     * @param network     Network
     */
    public static void report(MLDataSet trainingSet, BasicNetwork network) {
        System.out.println("Network training results:");

        int sz = trainingSet.size();
        if(sz == 0)
            return;

        MLDataPair first = trainingSet.get(0);

        // Output xs header
        System.out.printf("%4s ","#");

        int szInputs = first.getInputArray().length;
        for(int k=0; k < szInputs; k++)
            System.out.printf("%7s ","x"+(k+1));

        // Output ts (ideals) header
        int szOutputs = first.getIdealArray().length;
        for(int k=0; k < szOutputs; k++)
            System.out.printf("%7s ","t"+(k+1));

        // Output ys (actuals) header
        for(int k=0; k < szOutputs; k++)
            System.out.printf("%7s ","y"+(k+1));

        System.out.println();

        // Report inputs and ideals vs. outputs.
        int n = 1;
        for (MLDataPair pair : trainingSet) {
            System.out.printf("%4d ",n);

            final MLData inputs = pair.getInput();
            final MLData outputs = network.compute(inputs);

            final double input[] = inputs.getData();
            for(double d: input)
                System.out.printf("%7.4f ",d);

            final MLData ideals = pair.getIdeal();
            final double ideal[] = ideals.getData();
            for(double d: ideal)
                System.out.printf("%7.4f ",d);

            final double actual[] = outputs.getData();
            for(double d: actual)
                System.out.printf("%7.4f ",d);

            System.out.println("");

            n += 1;
        }
    }

    /**
     * Describes details of a network.
     * @param network Network
     */
    public static void describe(BasicNetwork network) {
        int layerCount = network.getLayerCount();

        int neuronCount = 0;
        for (int layerNum = 0; layerNum < layerCount; layerNum++) {
            // int neuronCount = network.calculateNeuronCount();  // Should work but doesn't appear to
            neuronCount += network.getLayerTotalNeuronCount(layerNum);
        }

        System.out.println("total layers: " + layerCount + " neurons: " + neuronCount);
        System.out.printf("%5s %5s %5s %10s\n", "layer", "from", "to", "wt");

        for (int layerNum = 0; layerNum < layerCount; layerNum++) {
            if (layerNum + 1 < layerCount) {
                // Nodes not including bias
                int fromNeuronCount = network.getLayerNeuronCount(layerNum);

                // Account for bias node
                if (network.isLayerBiased(layerNum))
                    fromNeuronCount += 1;

                int toNeuronCount = network.getLayerNeuronCount(layerNum + 1);

                for (int fromNeuron = 0; fromNeuron < fromNeuronCount; fromNeuron++) {
                    for (int toNeuron = 0; toNeuron < toNeuronCount; toNeuron++) {
                        double wt = network.getWeight(layerNum, fromNeuron, toNeuron);
                        System.out.printf("%5d %5d %5d %10.4f ", layerNum, fromNeuron, toNeuron, wt);
                        if (network.isLayerBiased(layerNum) && fromNeuron == fromNeuronCount - 1)
                            System.out.println("BIAS");
                        else
                            System.out.println("");
                    }
                }
            }
        }
    }

    /**
     * Logs statistics for each epoch.
     * @param epoch Epoch number
     * @param train Training results
     * @param done  True if the training is done
     */
    public static void log(int epoch, BasicTraining train, boolean done) {
        final int FREQUENCY = 100;

        // Report only the header
        if (epoch == 0)
            System.out.printf("%8s %6s\n", "epoch", "error");

        else if (epoch == 1 || (!done && (epoch % FREQUENCY) == 0)) {
            System.out.printf("%8d %6.4f\n", epoch, train.getError());
        }
        // Report only if we haven't just reported
        else if (done && (epoch % FREQUENCY) != 0)
            System.out.printf("%8d %6.4f\n", epoch, train.getError());

        if (epoch >= MAX_EPOCHS && done)
            System.out.println("--- DID NOT CONVERGE!");
    }
}

