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


data = load('./round-10-weights.npz', allow_pickle=True)
lst = data.f.arr_0

print(lst[8].shape)

# print(lst[0].reshape((5,5,3,6)).shape)
lst[0] = lst[0].reshape((5, 5, 3, 6))
lst[2] = lst[2].reshape((5, 5, 6, 16))
lst[4] = lst[4].reshape((1600, 120))
lst[6] = lst[6].reshape((120, 84))
lst[8] = lst[8].reshape((84, 10))


# # for item in lst:
# #     print(item)
# #     print(data[item])

# print(weights_to_parameters(weights=lst))


def create_model():
    # model = tf.keras.Sequential(
    #     [tf.keras.Input(shape=(32, 32, 3)),
    #      tf.keras.layers.Lambda(lambda x: x)],
    #     MobileNetV2(include_top=True, weights=None,
    #                 input_shape=(32, 32, 3), classes=10)
    # )

    # model = tf.keras.Sequential(
    #     MobileNetV2(include_top=True, weights=None,
    #                 input_shape=(32, 32, 3), classes=10)
    # )

    # model = MobileNetV2(include_top=False, weights=None,
    #                     input_shape=(32, 32, 3), classes=10)

    # model.compile(optimizer='adam',
    #               loss=tf.losses.SparseCategoricalCrossentropy(
    #                   from_logits=True),
    #               metrics=[tf.metrics.SparseCategoricalAccuracy()])


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

(train_images, train_labels), (test_images,
                               test_labels) = datasets.cifar10.load_data()

# Normalize pixel values to be between 0 and 1
# train_images, test_images = train_images / 255.0, test_images / 255.0

# Create a basic model instance
model = create_model()

# model.load_weights(checkpoint_path)
model.set_weights(lst)
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
