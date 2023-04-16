package edu.wpi.teamR.mapdb;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter(AccessLevel.PACKAGE)
public class DirectionArrow {
    String longname;
    int kioskID;
    Direction direction;

    DirectionArrow(String longname, int kioskID, Direction direction){
        this.longname = longname;
        this.kioskID = kioskID;
        this.direction = direction;
    }
}
