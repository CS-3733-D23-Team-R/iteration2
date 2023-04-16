package edu.wpi.teamR.mapdb;

import lombok.Getter;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Getter
public class UpdateAction {
    private final List<Pair<Method, Object[]>> updates;
    private final List<Pair<MapData, EditType>> undos;

    UpdateAction() {
        updates = new ArrayList<>();
        undos = new ArrayList<>();
    }

    void addUpdate(Method update, Object[] updateArgs, List<MapData> data, EditType editType) {
        updates.add(new Pair<>(update, updateArgs));
        for (MapData d : data) {
            undos.add(new Pair<>(d, editType));
        }
    }

    void addUpdate(Method update, Object[] updateArgs, MapData data, EditType editType) {
        updates.add(new Pair<>(update, updateArgs));
        undos.add(new Pair<>(data, editType));
    }

    List<Pair<Method, Object[]>> getUpdates() {
        return updates;
    }

    List<Pair<MapData, EditType>> getUndoData() {
        return undos;
    }
}
