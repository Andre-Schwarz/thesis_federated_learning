from keras.models import load_model
import tensorflow as tf


model = load_model('./saved_model/my_model.h5')

cifar10 = tf.keras.datasets.cifar10
(trainImages, trainLabels), (testImages, testLabels) = cifar10.load_data()

loss, accuracy = model.evaluate(testImages, testLabels)
print("Model, accuracy: {:5.2f}%".format(100 * accuracy))
