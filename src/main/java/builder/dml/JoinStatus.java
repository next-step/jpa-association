package builder.dml;

public enum JoinStatus {
    TRUE,
    FALSE;

    public boolean isTrue() {
        return this == TRUE;
    }
}
