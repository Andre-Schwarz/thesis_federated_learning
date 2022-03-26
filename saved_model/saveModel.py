# for x in range(4000,5000):
#   print("train/airplane/%s.jpg" %x)
import pathlib
import os
import tensorflow as tf
import tensorflow_datasets as tfds
import matplotlib.pyplot as plt
import numpy as np


trainData = tf.keras.utils.image_dataset_from_directory(
    "./train/",
    seed=123,
    image_size=(32, 32),
    batch_size=32)

testData = tf.keras.utils.image_dataset_from_directory(
    "./tTest/",
    seed=123,
    image_size=(32, 32),
    batch_size=32)


# class_names = trainData.class_names
# print(class_names)

# for image_batch, labels_batch in trainData:
#   print(image_batch.shape)
#   print(labels_batch)
#   break


def process(image, label):
    image = tf.cast(image, tf.float32)
    return image, label


# ds = tf.keras.preprocessing.image_dataset_from_directory(IMAGE_DIR)
trainData = trainData.map(process)
testData = testData.map(process)

# (testImages, testLabels) = testData
# testData["data"]

def create_model():
    model = tf.keras.Sequential(
        [

            tf.keras.Input(shape=(32, 32, 3)),
            tf.keras.layers.Rescaling(1./255, offset=0.0),
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


model = create_model()
model.compile("adam", "sparse_categorical_crossentropy", metrics=["accuracy"])

model.fit(trainData, epochs=5)


cifar10 = tf.keras.datasets.cifar10
(trainImages, trainLabels), (testImages, testLabels) = cifar10.load_data()

loss, accuracy = model.evaluate(testImages, testLabels)
print("Model, accuracy: {:5.2f}%".format(100 * accuracy))

model.save('./saved_model/my_model.h5')
