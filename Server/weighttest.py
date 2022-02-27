from numpy import arange, load

import tensorflow as tf

data = load('./round-10-weights.npz', allow_pickle=True)
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

(x_train, y_train), _ = tf.keras.datasets.cifar10.load_data()
x_val, y_val = x_train[45000:50000], y_train[45000:50000]

model = create_model()
model.set_weights(weights)
model.compile("adam", "sparse_categorical_crossentropy", metrics=["accuracy"])

loss, acc = model.evaluate(x_val/255.0, y_val)
print("Restored model, accuracy: {:5.2f}%".format(100 * acc))