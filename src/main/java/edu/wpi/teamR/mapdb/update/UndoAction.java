package edu.wpi.teamR.mapdb.update;

import edu.wpi.teamR.mapdb.MapData;
import lombok.Getter;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

public class UndoAction {
    @Getter
    private final List<UndoData> undos;

    public UndoAction() {
        undos = new ArrayList<>();
    }

    void addUpdate(List<? extends MapData> data) {
        for (MapData d : data) {
            undos.add(new UndoData(d, EditType.DELETION));
        }
    }
    void addUpdate(MapData data, EditType editType) {
        undos.add(new UndoData(data, editType));
    }
}
