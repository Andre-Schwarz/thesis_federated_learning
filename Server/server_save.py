from typing import Dict, List, Optional, Tuple
from numpy import arange, load
import flwr as fl
import tensorflow as tf
import numpy as np
from flwr.common.logger import log
from flwr.server.client_manager import ClientManager
from flwr.server.client_proxy import ClientProxy
from flwr.common import (
    EvaluateIns,
    EvaluateRes,
    FitIns,
    FitRes,
    Parameters,
    Scalar,
    Weights,
)
from flwr.server.strategy.aggregate import aggregate, weighted_loss_avg

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

    model.compile(optimizer='adam',
                  loss=tf.losses.SparseCategoricalCrossentropy(
                      from_logits=True),
                  metrics=[tf.metrics.SparseCategoricalAccuracy()])

    return model

def get_eval_fn(model):
    """Return an evaluation function for server-side evaluation."""

    cifar10 = tf.keras.datasets.cifar10
    (trainImages, trainLabels), (testImages, testLabels) = cifar10.load_data()

    trainImages = trainImages / 255
    testImages = testImages / 255

    # The `evaluate` function will be called after every round
    def evaluate(weights: fl.common.Weights) -> Optional[Tuple[float, float]]:
        
        weights[0] = weights[0].reshape((5, 5, 3, 6))
        weights[2] = weights[2].reshape((5, 5, 6, 16))
        weights[4] = weights[4].reshape((1600, 120))
        weights[6] = weights[6].reshape((120, 84))
        weights[8] = weights[8].reshape((84, 10))

        model.set_weights(weights)  # Update model with the latest parameters
        loss, accuracy = model.evaluate(testImages, testLabels)
        print("Model, accuracy: {:5.2f}%".format(100 * accuracy))
        return loss, {"accuracy": accuracy}

    return evaluate


# Load and compile model for server-side parameter evaluation
model = create_model()

class SaveModelStrategy(fl.server.strategy.FedAvgAndroid):
    def aggregate_fit(
        self,
        rnd: int,
        results: List[Tuple[ClientProxy, FitRes]],
        failures: List[BaseException],
    ) -> Tuple[Optional[Parameters], Dict[str, Scalar]]:
        parameters = super().aggregate_fit(rnd, results, failures)
        if parameters is not None:
            # Save weights
            print(f"Saving round {rnd} parameters...")
            np.savez(f"round-{rnd}-parameters.npz", *parameters)

            weights_results = [
                (self.parameters_to_weights(fit_res.parameters), fit_res.num_examples)
                for client, fit_res in results
            ]


            print(f"Saving round {rnd} weights...")
            np.savez(f"round-{rnd}-weights.npz",aggregate(weights_results))
        return parameters


def main() -> None:

    strategy = SaveModelStrategy(
        fraction_fit=1.0,
        fraction_eval=1.0,
        min_fit_clients=4,
        min_eval_clients=4,
        min_available_clients=4,
        # eval_fn=get_eval_fn(model),
        eval_fn=None,
        on_fit_config_fn=fit_config,
        initial_parameters=None,
    )

    # Start Flower server for four rounds of federated learning
    fl.server.start_server("[::]:8999", config={"num_rounds": 10}, strategy=strategy)


def fit_config(rnd: int):
    """Return training configuration dict for each round.

    Keep batch size fixed at 32, perform two rounds of training with one
    local epoch, increase to two local epochs afterwards.
    """
    config = {
        "batch_size": 32,
        "local_epochs": 5,
    }
    return config


if __name__ == "__main__":
    main()
