package study;

public abstract class ComplexNestedAbstractClass {
    public abstract void outerMethod();

    public abstract class InnerAbstractClass {
        public abstract void innerMethod();
    }

    public interface InnerInterface {
        void innerInterfaceMethod();
    }

    public static class InnerClass {
        public void innerClassMethod() {}
    }
}
