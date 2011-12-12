package com.metservice.kanban.charts;

import static org.hamcrest.number.IsCloseTo.closeTo;
import static org.junit.Assert.assertThat;
import org.junit.Test;
import com.metservice.kanban.charts.HueRebalancer;

public class HueRebalancerTest {

    @Test
    public void rebalancesHuesUsingTwoSegments() {
        HueRebalancer balancer = new HueRebalancer(0.00, 0.10, 1.00);

        assertThat(balancer.balance(0.00), closeTo(0.00, 0.0001));
        assertThat(balancer.balance(0.25), closeTo(0.05, 0.0001));
        assertThat(balancer.balance(0.50), closeTo(0.10, 0.0001));
        assertThat(balancer.balance(0.75), closeTo(0.55, 0.0001));
        assertThat(balancer.balance(1.00), closeTo(0.00, 0.0001));
        assertThat(balancer.balance(1.25), closeTo(0.05, 0.0001));
    }

    @Test
    public void balancesOutHuesUsingFourSegments() {
        HueRebalancer balancer = new HueRebalancer(0.20, 0.40, 0.50, 0.60, 1.00);

        assertThat(balancer.balance(0.125), closeTo(0.30, 0.0001));
        assertThat(balancer.balance(0.375), closeTo(0.45, 0.0001));
        assertThat(balancer.balance(0.625), closeTo(0.55, 0.0001));
        assertThat(balancer.balance(0.875), closeTo(0.80, 0.0001));
    }
}
