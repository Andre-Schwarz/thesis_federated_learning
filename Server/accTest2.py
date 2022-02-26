from tensorflow import keras
import tensorflow as tf

cifar10 = keras.datasets.cifar10
(trainImages, trainLabels), (testImages, testLabels) = cifar10.load_data()

trainImages = trainImages / 255
testImages = testImages / 255

# model = keras.Sequential(
#     [
#         keras.Input(shape=(32, 32, 3)),
#         keras.layers.Conv2D(6, 5, activation="relu"),
#         keras.layers.MaxPooling2D(pool_size=(2, 2)),
#         keras.layers.Conv2D(16, 5, activation="relu"),
#         keras.layers.Flatten(),
#         keras.layers.Dense(units=120, activation="relu"),
#         keras.layers.Dense(units=84, activation="relu"),
#         keras.layers.Dense(units=10, activation="softmax"),
#     ]
# )

model = keras.Sequential(
    [
        keras.Input(shape=(32, 32, 3)),
        keras.layers.Conv2D(6, 5, activation="relu"),
        keras.layers.MaxPooling2D(pool_size=(2, 2)),
        keras.layers.Conv2D(16, 5, activation="relu"),
        keras.layers.Flatten(),
        keras.layers.Dense(units=120, activation="relu"),
        keras.layers.Dense(units=84, activation="relu"),
        keras.layers.Dense(units=10, activation="softmax"),
    ]
)


model.compile(optimizer='adam',
              loss=tf.losses.SparseCategoricalCrossentropy(
                  from_logits=True),
              metrics=[tf.metrics.SparseCategoricalAccuracy()])

trainHistory = model.fit(
    trainImages,
    trainLabels,
    epochs=10,
    validation_data=(testImages, testLabels)
)


(loss, accuracy) = model.evaluate(testImages, testLabels)
print(loss)
print(accuracy)
