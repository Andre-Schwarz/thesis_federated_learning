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

checkpoint_path = "./round-10-weights.npz"



def parameters_to_weights(self, parameters: Parameters) -> Weights:
    """Convert parameters object to NumPy weights."""
    return [self.bytes_to_ndarray(tensor) for tensor in parameters.tensors]

def create_model():
    model = MobileNetV2(include_top=True, weights=None,
                    input_shape=(32, 32, 3), classes=10)



    model.compile(optimizer='adam',
                  loss=tf.losses.SparseCategoricalCrossentropy(
                      from_logits=True),
                  metrics=[tf.metrics.SparseCategoricalAccuracy()])

    return model


(train_images, train_labels), (test_images,
                               test_labels) = datasets.cifar10.load_data()
train_images, test_images = train_images / 255.0, test_images / 255.0

model = create_model()

model.set_weights(checkpoint_path)
model.compile("adam", "sparse_categorical_crossentropy", metrics=["accuracy"])

loss, acc = model.evaluate(test_images, test_labels, verbose=2)
print("Restored model, accuracy: {:5.2f}%".format(100 * acc))
