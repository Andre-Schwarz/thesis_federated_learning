import os
from tabnanny import check

import tensorflow as tf
from tensorflow import keras
from tensorflow.keras import datasets, layers, models
from tensorflow.keras.applications import MobileNetV2

from flwr.common import (
    EvaluateIns,
    EvaluateRes,
    FitIns,
    FitRes,
    Parameters,
    Scalar,
    Weights,
)
# print(tf.version.VERSION)


# checkpoint_path = "./weights/round-10-weights.npz"
checkpoint_path = "./round-10-weights.npz"
# Define a simple sequential model


def parameters_to_weights(self, parameters: Parameters) -> Weights:
    """Convert parameters object to NumPy weights."""
    return [self.bytes_to_ndarray(tensor) for tensor in parameters.tensors]

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

    model = MobileNetV2(include_top=True, weights=None,
                    input_shape=(32, 32, 3), classes=10)



    model.compile(optimizer='adam',
                  loss=tf.losses.SparseCategoricalCrossentropy(
                      from_logits=True),
                  metrics=[tf.metrics.SparseCategoricalAccuracy()])

    return model


(train_images, train_labels), (test_images,
                               test_labels) = datasets.cifar10.load_data()

# Normalize pixel values to be between 0 and 1
train_images, test_images = train_images / 255.0, test_images / 255.0

# Create a basic model instance
model = create_model()

# model.load_weights(checkpoint_path)
model.set_weights(checkpoint_path)
model.compile("adam", "sparse_categorical_crossentropy", metrics=["accuracy"])

# Evaluate the model
# loss, acc = model.evaluate(test_images, test_labels, verbose=2)
# print("Untrained model, accuracy: {:5.2f}%".format(100 * acc))


# Loads the weights

# Re-evaluate the model
loss, acc = model.evaluate(test_images, test_labels, verbose=2)
print("Restored model, accuracy: {:5.2f}%".format(100 * acc))
