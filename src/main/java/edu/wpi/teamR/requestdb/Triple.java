package edu.wpi.teamR.requestdb;

import lombok.Getter;
import lombok.Setter;

@Getter @Setter
public class Triple<A, B, C> {
    private A left;
    private B middle;
    private C right;

    Triple(A left, B middle, C right){
        this.left = left;
        this.middle = middle;
        this.right = right;
    }
}
