package cc.ifnot.java.design_patterns.singleton;

public class InitializingOnDemandHolderIdiomSt {

    private InitializingOnDemandHolderIdiomSt() {
    }

    public static InitializingOnDemandHolderIdiomSt getInstance() {
        return Holder.INSTANCE;
    }

    private static class Holder {
        private static InitializingOnDemandHolderIdiomSt INSTANCE = new InitializingOnDemandHolderIdiomSt();
    }
}
