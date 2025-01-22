package org.testing.project_2_1.MachineLearning;

/**
 * Represents a simple neural network with a hidden layer, a policy head, and a value head.
 * The network is designed for tasks requiring both policy (move probabilities) 
 * and value (win probability) outputs.
 */
public class NeuralNetwork {
    private Layer hiddenLayer; // The hidden layer of the network.
    private Layer policyHead;  // The policy head producing move probabilities.
    private Layer valueHead;   // The value head producing a single scalar output.

    /**
     * Constructs a NeuralNetwork with specified sizes for input, hidden, and policy layers.
     *
     * @param inputSize   The size of the input layer.
     * @param hiddenSize  The size of the hidden layer.
     * @param policySize  The size of the policy layer (number of possible moves).
     */
    public NeuralNetwork(int inputSize, int hiddenSize, int policySize) {
        hiddenLayer = new Layer(inputSize, hiddenSize); // Initialize hidden layer.
        policyHead = new Layer(hiddenSize, policySize); // Initialize policy head.
        valueHead = new Layer(hiddenSize, 1); // Initialize value head for scalar output.
    }

    /**
     * Performs a forward pass through the neural network.
     * The input passes through the hidden layer, then through the policy and value heads.
     * The policy output is normalized using the softmax function.
     *
     * @param input The input vector to the neural network.
     * @return An NNOutput object containing the policy (move probabilities) and value (win probability).
     */
    public NNOutput forward(double[] input) {
        // Pass input through the hidden layer.
        double[] hiddenOutput = hiddenLayer.forward(input);

        // Pass the hidden layer output to the policy head.
        double[] policyOutput = policyHead.forward(hiddenOutput);

        // Pass the hidden layer output to the value head and extract the scalar output.
        double valueOutput = valueHead.forward(hiddenOutput)[0];

        // Apply softmax to the policy output to get probabilities.
        policyOutput = softmax(policyOutput);

        // Return the outputs as an NNOutput object.
        return new NNOutput(policyOutput, valueOutput);
    }

    /**
     * Applies the softmax function to a vector of logits.
     * Softmax normalizes the logits into probabilities, ensuring they sum to 1.
     * A numerical stability trick is used to prevent overflow by subtracting the max logit.
     *
     * @param logits The input vector (logits).
     * @return A vector of probabilities corresponding to the input logits.
     */
    private double[] softmax(double[] logits) {
        // Find the maximum logit for numerical stability.
        double max = Double.NEGATIVE_INFINITY;
        for (double logit : logits) max = Math.max(max, logit);

        // Compute the exponential values and their sum.
        double sum = 0;
        double[] exp = new double[logits.length];
        for (int i = 0; i < logits.length; i++) {
            exp[i] = Math.exp(logits[i] - max); // Apply the stability trick.
            sum += exp[i];
        }

        // Normalize the exponential values to produce probabilities.
        for (int i = 0; i < logits.length; i++) {
            exp[i] /= sum;
        }

        return exp; // Return the normalized probabilities.
    }
}

/**
 * A helper class to hold the outputs of the neural network.
 * It contains the policy (probabilities for possible moves) and the value (win probability).
 */
class NNOutput {
    double[] policy; // Probabilities for each possible move.
    double value;    // The win probability (scalar value).

    /**
     * Constructs an NNOutput object with the given policy and value.
     *
     * @param policy The policy vector (move probabilities).
     * @param value  The value scalar (win probability).
     */
    public NNOutput(double[] policy, double value) {
        this.policy = policy;
        this.value = value;
    }
}
