package dev.vepo.morphoboard.dashboards;

import java.util.Map;

public record KpiData(int total, Map<String, Integer> perStatus) { }
