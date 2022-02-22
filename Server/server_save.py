from tensorflow.keras.applications import MobileNetV2
from typing import Any, Callable, Dict, List, Optional, Tuple

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
checkpoint_path = "./round-10-weights.npz"


def create_model():
    model = tf.keras.Sequential(
        [tf.keras.Input(shape=(32, 32, 3))],
    MobileNetV2(include_top=False, weights=None,
                    input_shape=(32, 32, 3), classes=10)
    )


#  model = MobileNetV2(include_top=True, weights=None,
#                     input_shape=(32, 32, 3), classes=10)



    # model = tf.keras.Sequential(
    #     [
    #         tf.keras.Input(shape=(32, 32, 3)),
    #         tf.keras.layers.Conv2D(6, 5, activation="relu"),
    #         tf.keras.layers.MaxPooling2D(pool_size=(2, 2)),
    #         tf.keras.layers.Conv2D(16, 5, activation="relu"),
    #         tf.keras.layers.Flatten(),
    #         tf.keras.layers.Dense(units=120, activation="relu"),
    #         tf.keras.layers.Dense(units=84, activation="relu"),
    #         tf.keras.layers.Dense(units=10, activation="softmax"),
    #     ]
    # )

    model.compile(optimizer='adam',
                  loss=tf.losses.SparseCategoricalCrossentropy(
                      from_logits=True),
                  metrics=[tf.metrics.SparseCategoricalAccuracy()])

    return model

def get_eval_fn(model):
    """Return an evaluation function for server-side evaluation."""

    # Load data and model here to avoid the overhead of doing it in `evaluate` itself
    (x_train, y_train), _ = tf.keras.datasets.cifar10.load_data()

    # Use the last 5k training examples as a validation set
    x_val, y_val = x_train[45000:50000], y_train[45000:50000]

    # The `evaluate` function will be called after every round
    def evaluate(weights: fl.common.Weights) -> Optional[Tuple[float, float]]:
        print(f"Going to evaluate these weights: {weights}")
        model.set_weights(weights)  # Update model with the latest parameters
        loss, accuracy = model.evaluate(x_val, y_val)
        print("Untrained model, accuracy: {:5.2f}%".format(100 * accuracy))
        return loss, accuracy

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

            # print(f"Saving round {rnd} parameters...")
            # # Convert `Parameters` to `List[np.ndarray]`
            # aggregated_weights: List[np.ndarray] = fl.common.parameters_to_weights(
            #     parameters)
            # np.savez(f"round-{rnd}-parameters.npz", *aggregated_weights)

            weights_results = [
                (self.parameters_to_weights(fit_res.parameters), fit_res.num_examples)
                for client, fit_res in results
            ]


            print(f"Saving round {rnd} weights...")
            np.savez(f"round-{rnd}-weights.npz",
                     *aggregate(weights_results))
        return parameters


def main() -> None:
    # Create strategy
    # strategy = fl.server.strategy.FedAvgAndroid(
    #     fraction_fit=1.0,
    #     fraction_eval=1.0,
    #     min_fit_clients=4,
    #     min_eval_clients=4,
    #     min_available_clients=4,
    #     eval_fn=None,
    #     on_fit_config_fn=fit_config,
    #     initial_parameters=None,
    # )

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
    fl.server.start_server("[::]:8999", config={"num_rounds": 20}, strategy=strategy)


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
