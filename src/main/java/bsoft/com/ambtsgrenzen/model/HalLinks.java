package bsoft.com.ambtsgrenzen.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class HalLinks {
    private Link next;

    private Link prev;

    private Link self;

}
