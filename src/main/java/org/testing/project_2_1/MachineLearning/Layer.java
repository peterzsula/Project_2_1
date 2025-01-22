package org.testing.project_2_1.MachineLearning;

/**
 * Represents a single layer in a neural network.
 * The layer contains a weight matrix, bias vector, and methods for forward propagation.
 * It uses the ReLU activation function for non-linear transformations.
 */
public class Layer {
    double[][] weights; // Weight matrix (outputSize x inputSize)
    double[] biases;    // Bias vector (size: outputSize)
    double[] outputs;   // Activations/output of the layer (size: outputSize)
    double[] inputs;    // Inputs to the layer (size: inputSize)

    /**
     * Constructs a Layer with the specified input and output sizes.
     * Initializes weights and biases to small random values.
     *
     * @param inputSize  The number of inputs to the layer.
     * @param outputSize The number of outputs from the layer.
     */
    public Layer(int inputSize, int outputSize) {
        weights = new double[outputSize][inputSize]; // Initialize weight matrix.
        biases = new double[outputSize]; // Initialize bias vector.
        outputs = new double[outputSize]; // Initialize output activations.
        inputs = new double[inputSize]; // Initialize input storage.
        initializeWeights(); // Populate weights with random values.
    }

    /**
     * Initializes the weight matrix with small random values.
     * This helps in breaking symmetry during training.
     */
    private void initializeWeights() {
        for (int i = 0; i < weights.length; i++) {
            for (int j = 0; j < weights[0].length; j++) {
                weights[i][j] = Math.random() * 0.01; // Assign small random weights.
            }
        }
    }

    /**
     * Performs a forward pass through the layer.
     * Computes the weighted sum of inputs plus biases and applies the ReLU activation function.
     *
     * @param input The input vector for the layer.
     * @return The output vector after applying the weights, biases, and activation function.
     */
    public double[] forward(double[] input) {
        this.inputs = input; // Store the input for potential use in backpropagation.
        for (int i = 0; i < weights.length; i++) {
            outputs[i] = biases[i]; // Start with the bias value for the neuron.
            for (int j = 0; j < weights[0].length; j++) {
                outputs[i] += weights[i][j] * input[j]; // Add weighted input.
            }
            outputs[i] = relu(outputs[i]); // Apply ReLU activation function.
        }
        return outputs; // Return the activations.
    }

    /**
     * Applies the ReLU (Rectified Linear Unit) activation function.
     * ReLU is defined as max(0, x), which introduces non-linearity.
     *
     * @param x The input value.
     * @return The activated value.
     */
    private double relu(double x) {
        return Math.max(0, x); // Return the maximum of 0 and the input.
    }

    /**
     * Computes the derivative of the ReLU function for backpropagation.
     * The derivative is 1 for positive inputs and 0 otherwise.
     *
     * @param x The input value.
     * @return The derivative of ReLU at the input value.
     */
    private double reluDerivative(double x) {
        return x > 0 ? 1 : 0; // Return 1 if x > 0, otherwise 0.
    }
}

