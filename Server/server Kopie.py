from typing import Any, Callable, Dict, List, Optional, Tuple

import flwr as fl
import tensorflow as tf
import numpy as np


class SaveModelStrategy(fl.server.strategy.FedAvgAndroid):
    def aggregate_fit(
        self,
        rnd: int,
        results: List[Tuple[fl.server.client_proxy.ClientProxy, fl.common.FitRes]],
        failures: List[BaseException],
    ) -> Optional[fl.common.Weights]:
        weights = super().aggregate_fit(rnd, results, failures)
        if weights is not None:
            # Save weights
            print(f"Saving round {rnd} weights...")
            np.savez(f"round-{rnd}-weights.npz", *weights)
        return weights



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
