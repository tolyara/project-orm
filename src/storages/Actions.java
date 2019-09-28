package storages;

public enum Actions {
    RESTRICT("RESTRICT"),
    CASCADE("CASCADE"),
    NOACTION("NO ACTION");

    private String value;

    Actions(String value){
        this.value = value;
    }

    @Override
    public String toString() {
        return value;
    }
}
