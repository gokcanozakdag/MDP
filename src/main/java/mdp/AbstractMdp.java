package mdp;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public abstract class AbstractMdp<A, S> {
    private final double gamma;
    private static final int maxRecursion = 500;
    private List<Map<S, Double>> recursionIndexedCache;

    public AbstractMdp(double gamma) {
        this.gamma = gamma;
        this.recursionIndexedCache = IntStream.range(0, maxRecursion)
                .mapToObj(i -> new HashMap<S, Double>())
                .collect(Collectors.toList());
    }

    abstract ActionResult<S> takeAction(S currentState, A action);
    abstract Map<A, Double> possibleActions(S state);


    double value(S state) {
        return value(state, 0);
    }

    double value(S state, A action) {
        return value(state, action, 0);
    }

    private double value(S state, int recursionLevel) {
        if (recursionLevel >= maxRecursion) {
            return 0;
        }

        Map<S, Double> currentCache = getCache(recursionLevel);
        if (currentCache.containsKey(state)) {
            return currentCache.get(state);
        }

        double value = valueInternal(state, recursionLevel);
        currentCache.put(state, value);
        return value;

    }

    private Map<S, Double> getCache(int recursionLevel) {
        return recursionIndexedCache.get(recursionLevel);
    }

    private double value(S state, A action, int recursionLevel) {
        ActionResult<S> actionResult = takeAction(state, action);
        return value(actionResult.state(), recursionLevel) * gamma + actionResult.reward();
    }

    private double valueInternal(S state, int recursionLevel) {

        Map<A, Double> possibleActions = possibleActions(state);
        double value = 0;
        for (Map.Entry<A, Double> possibilityActionPair : possibleActions.entrySet()) {
            double possibility = possibilityActionPair.getValue();
            A action = possibilityActionPair.getKey();

            value += possibility * value(state, action, recursionLevel + 1);
        }

        return value;
    }

    public class ActionResult<S> {
        private final S state;
        private final double reward;

        public ActionResult(S nextState, double reward) {
            this.state = nextState;
            this.reward = reward;
        }

        S state() {
            return state;
        }

        double reward() {
            return reward;
        }

        @Override
        public String toString() {
            return "state: " + state + ", reward: " + reward;
        }
    }
}

