package com.bsoft.ambtsgrenzen.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class MultiPolygon extends Geometry {
    private double[][][][] coordinates;
}
