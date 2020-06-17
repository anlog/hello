package cc.ifnot.java.design_patterns.singleton;

public enum EnumSt {
    INSTANCE;

    @Override
    public String toString() {
        return getClass().getSimpleName() + hashCode();
    }
}
