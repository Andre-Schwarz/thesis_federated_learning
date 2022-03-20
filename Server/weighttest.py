from numpy import arange, load
from keras.models import load_model
from typing import Dict, List, Optional, Tuple, cast

import tensorflow as tf
import numpy as np
from flwr.common import (
    EvaluateIns,
    EvaluateRes,
    FitIns,
    FitRes,
    Parameters,
    Scalar,
    Weights,
)



def ndarray_to_bytes(ndarray: np.ndarray) -> bytes:
    """Serialize NumPy array to bytes."""
    return cast(bytes, ndarray.tobytes())


def weights_to_parameters(weights: Weights) -> Parameters:
    """Convert NumPy weights to parameters object."""
    tensors = [ndarray_to_bytes(ndarray) for ndarray in weights]
    return Parameters(tensors=tensors, tensor_type="numpy.nda")

data = load('./round-1-weights.npz', allow_pickle=True)
weights = data.f.arr_0

print(weights[0].shape)

# model = load_model('../saved_model/saved_model/my_model.h5')
# weights2 = model.get_weights()



# weights[0] = weights[0].reshape((5, 5, 3, 6))
# weights[2] = weights[2].reshape((5, 5, 6, 16))
# weights[4] = weights[4].reshape((1600, 120))
# weights[6] = weights[6].reshape((120, 84))
# weights[8] = weights[8].reshape((84, 10))


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


cifar10 = tf.keras.datasets.cifar10
(trainImages, trainLabels), (testImages, testLabels) = cifar10.load_data()


model = create_model()
model.set_weights(weights)
model.compile("adam", "sparse_categorical_crossentropy", metrics=["accuracy"])

loss, acc = model.evaluate(testImages/255, testLabels)
print("Restored model, accuracy: {:5.2f}%".format(100 * acc))
