from numpy import arange, load
import json

import tensorflow as tf
from tensorflow import keras
from typing import Callable, Dict, List, Optional, Tuple, cast
from flwr.common import (
    EvaluateIns,
    EvaluateRes,
    FitIns,
    FitRes,
    Parameters,
    Scalar,
    Weights,
)
import numpy as np
from tensorflow.keras.applications import MobileNetV2
from tensorflow.keras import datasets, layers, models

def weights_to_parameters(weights: Weights) -> Parameters:
    """Convert NumPy weights to parameters object."""
    tensors = [ndarray_to_bytes(ndarray) for ndarray in weights]
    return Parameters(tensors=tensors, tensor_type="numpy.nda")

def ndarray_to_bytes(ndarray: np.ndarray) -> bytes:
    """Serialize NumPy array to bytes."""
    return cast(bytes, ndarray.tobytes())


data = load('./round-30-weights.npz', allow_pickle=True)
weights = data.f.arr_0

weights[0] = weights[0].reshape((5, 5, 3, 6))
weights[2] = weights[2].reshape((5, 5, 6, 16))
weights[4] = weights[4].reshape((1600, 120))
weights[6] = weights[6].reshape((120, 84))
weights[8] = weights[8].reshape((84, 10))



def create_model():
    model = tf.keras.Sequential(
        [
            tf.keras.Input(shape=(32, 32, 3)),
            tf.keras.layers.Conv2D(6, 5, activation="relu"),
            tf.keras.layers.MaxPooling2D(pool_size=(2, 2)),
            tf.keras.layers.Conv2D(16, 5, activation="relu"),
            tf.keras.layers.Flatten(),
            tf.keras.layers.Dense(units=120, activation="relu"),
            tf.keras.layers.Dense(units=84, activation="relu"),
            tf.keras.layers.Dense(units=10, activation="softmax"),
        ]
    )

    return model

# 
# Load data and model here to avoid the overhead of doing it in `evaluate` itself
(x_train, y_train), _ = tf.keras.datasets.cifar10.load_data()

    # Use the last 5k training examples as a validation set
x_val, y_val = x_train[45000:50000], y_train[45000:50000]

# 

# (train_images, train_labels), (test_images,
#                                test_labels) = datasets.cifar10.load_data()

# Normalize pixel values to be between 0 and 1
# train_images, test_images = train_images / 255.0, test_images / 255.0

# Create a basic model instance
model = create_model()


# for layer in model.layers:
#     print(layer.input_shape)


# model.load_weights(checkpoint_path)
model.set_weights(weights)
model.compile("adam", "sparse_categorical_crossentropy", metrics=["accuracy"])

# Evaluate the model
# loss, acc = model.evaluate(test_images, test_labels, verbose=2)
# print("Untrained model, accuracy: {:5.2f}%".format(100 * acc))


# Loads the weights

# Re-evaluate the model
# loss, acc = model.evaluate(
#     test_images, test_labels, verbose=1)
# print("Restored model, accuracy: {:5.2f}%".format(100 * acc))



# 


loss, acc = model.evaluate(x_val, y_val)
print("Restored model, accuracy: {:5.2f}%".format(100 * acc))
# 
