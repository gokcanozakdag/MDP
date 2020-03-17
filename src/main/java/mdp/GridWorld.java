package mdp;

import com.google.common.collect.ImmutableMap;

import java.text.DecimalFormat;
import java.util.Map;

public class GridWorld extends AbstractMdp<GridWorld.Vector2D, GridWorld.Vector2D> {
    private static final double GAMMA = 0.9;
    private static int GRID_SIZE = 5;
    private static Map<Vector2D, Map<Vector2D, Double>> specialCaseDefinition = ImmutableMap.of(
            new Vector2D(0, 1), ImmutableMap.of(new Vector2D(4, 1), 10.0),
            new Vector2D(0, 3), ImmutableMap.of(new Vector2D(2, 3), 5.0)
    );

    public static void main(String[] args) {
        double[][] gridValue = new double[GRID_SIZE][GRID_SIZE];
        GridWorld gw = new GridWorld();
        for (int i = 0; i < GRID_SIZE; i++) {
            for (int j = 0; j < GRID_SIZE; j++) {
                gridValue[i][j] = gw.value(new Vector2D(i, j));
                System.out.print(new DecimalFormat("#.##").format(gridValue[i][j]) + ",");
            }
            System.out.println();
        }
    }

    public GridWorld() {
        super(GAMMA);
    }

    @Override
    ActionResult<Vector2D> takeAction(Vector2D currentState, Vector2D action) {
        if (isSpecialState(currentState)) {
            return handleSpecialState(currentState);
        }

        Vector2D maybeNextState = currentState.add(action);
        if (isInsideGrid(maybeNextState)) {
            return new ActionResult<Vector2D>(maybeNextState, 0);
        }

        return new ActionResult<Vector2D>(currentState, -1);
    }

    private boolean isInsideGrid(Vector2D v) {
        return v.x >= 0 && v.x < GRID_SIZE && v.y >=0 && v.y < GRID_SIZE;
    }

    @Override
    Map<Vector2D, Double> possibleActions(Vector2D state) {
        return ImmutableMap.of(
                new Vector2D(0, 1), 0.25,
                new Vector2D(1, 0), 0.25,
                new Vector2D(0, -1), 0.25,
                new Vector2D(-1, 0), 0.25
        );
    }

    private boolean isSpecialState(Vector2D coordinate) {
        return specialCaseDefinition.containsKey(coordinate);
    }

    private ActionResult<Vector2D> handleSpecialState(Vector2D coordinate) {
        Map.Entry<Vector2D, Double> stateRewardPair = specialCaseDefinition.get(coordinate).entrySet().iterator().next();
        return new ActionResult<Vector2D>(stateRewardPair.getKey(), stateRewardPair.getValue());
    }

    public static class Vector2D {
        final int x;
        final int y;

        Vector2D(int x, int y) {
            this.x = x;
            this.y = y;
        }

        public Vector2D add(Vector2D other) {
            return new Vector2D(x + other.x, y + other.y);
        }

        @Override
        public String toString() {
            return "x: " + x + ", y: " + y;
        }

        @Override
        public boolean equals(Object other) {
            if (other == null || other.getClass() != Vector2D.class) {
                return false;
            }

            Vector2D otherVector = (Vector2D) other;
            return x == otherVector.x && y == otherVector.y;
        }

        @Override
        public int hashCode() {
            return 31 * x + y;
        }
    }
}


