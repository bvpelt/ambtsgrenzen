package bsoft.com.ambtsgrenzen.model;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class HalLinks {
    private Link next;
    private Link prev;
    private Link self;

}
