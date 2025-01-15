package org.testing.project_2_1.MachineLearning;

public class NeuralNetwork {
    Layer hiddenLayer;
    Layer policyHead;
    Layer valueHead;

    public NeuralNetwork(int inputSize, int hiddenSize, int policySize) {
        hiddenLayer = new Layer(inputSize, hiddenSize);
        policyHead = new Layer(hiddenSize, policySize);
        valueHead = new Layer(hiddenSize, 1); // Single value output
    }

    // Forward pass
    public NNOutput forward(double[] input) {
        double[] hiddenOutput = hiddenLayer.forward(input);
        double[] policyOutput = policyHead.forward(hiddenOutput);
        double valueOutput = valueHead.forward(hiddenOutput)[0]; // Scalar

        // Apply softmax to policy output
        policyOutput = softmax(policyOutput);

        return new NNOutput(policyOutput, valueOutput);
    }

    // Softmax activation for policy
    private double[] softmax(double[] logits) {
        double max = Double.NEGATIVE_INFINITY;
        for (double logit : logits) max = Math.max(max, logit);

        double sum = 0;
        double[] exp = new double[logits.length];
        for (int i = 0; i < logits.length; i++) {
            exp[i] = Math.exp(logits[i] - max); // Stability trick
            sum += exp[i];
        }
        for (int i = 0; i < logits.length; i++) {
            exp[i] /= sum;
        }
        return exp;
    }
}

// Helper class to hold outputs
class NNOutput {
    double[] policy;  // Probabilities of moves
    double value;     // Win probability

    public NNOutput(double[] policy, double value) {
        this.policy = policy;
        this.value = value;
    }

    
}
