package org.testing.project_2_1.MachineLearning;

public class Layer {
    double[][] weights; // Weight matrix
    double[] biases;    // Bias vector
    double[] outputs;   // Activations
    double[] inputs;    // Inputs to the layer

    public Layer(int inputSize, int outputSize) {
        weights = new double[outputSize][inputSize];
        biases = new double[outputSize];
        outputs = new double[outputSize];
        inputs = new double[inputSize];
        initializeWeights();
    }

    // Initialize weights randomly
    private void initializeWeights() {
        for (int i = 0; i < weights.length; i++) {
            for (int j = 0; j < weights[0].length; j++) {
                weights[i][j] = Math.random() * 0.01; // Small random weights
            }
        }
    }

    // Forward pass
    public double[] forward(double[] input) {
        this.inputs = input;
        for (int i = 0; i < weights.length; i++) {
            outputs[i] = biases[i];
            for (int j = 0; j < weights[0].length; j++) {
                outputs[i] += weights[i][j] * input[j];
            }
            outputs[i] = relu(outputs[i]); // Activation
        }
        return outputs;
    }

    // ReLU activation function
    private double relu(double x) {
        return Math.max(0, x);
    }

    // ReLU derivative (for backpropagation)
    private double reluDerivative(double x) {
        return x > 0 ? 1 : 0;
    }
}

