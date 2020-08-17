package ru.natlex.geo;

import java.util.HashMap;

public enum TaskStatus {
    IN_PROGRESS(0),
    DONE(1),
    ERROR(2);


    private static HashMap<Integer, TaskStatus> map = new HashMap<Integer, TaskStatus>();

    static{
        for(TaskStatus s: TaskStatus.values()){
            map.put(s.index,s);
        }
    }
    public Integer index;
    TaskStatus(Integer index) {
        this.index = index;
    }

    public static TaskStatus valueOf(int value) {
        return (TaskStatus) map.get(value);
    }
}
