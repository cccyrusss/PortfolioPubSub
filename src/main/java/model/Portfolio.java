package model;

import java.util.List;

public class Portfolio {
    private List<Position> positions;
    private double nav;

    public Portfolio(List<Position> positions) {
        this.positions = positions;
    }

    public List<Position> getPositions() {
        return positions;
    }

    public double getNav() {
        return nav;
    }

    // Update market value of each position and compute NAV
    public void update() {
        for (Position position : positions) {
            position.setMarketValue(position.getProduct().getPrice() * position.getQuantity());
        }
        nav = positions.stream().map(position -> position.getProduct().getPrice()).reduce(0d, Double::sum);
    }
}
