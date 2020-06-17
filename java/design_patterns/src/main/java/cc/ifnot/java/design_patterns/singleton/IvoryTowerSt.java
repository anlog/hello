package cc.ifnot.java.design_patterns.singleton;

public class IvoryTowerSt {
    private static IvoryTowerSt mInstance = new IvoryTowerSt();

    private IvoryTowerSt() {
    }

    public static IvoryTowerSt getInstance() {
        return mInstance;
    }

}
