package run.mone.mimeter.dashboard.bo.snapshot;

public enum SnapshotTypeEnum {

    SCENE_SNAPSHOT(1),
    DEBUG_SNAPSHOT(2);

    public int typeCode;

    SnapshotTypeEnum(int typeCode) {
        this.typeCode = typeCode;
    }

}
