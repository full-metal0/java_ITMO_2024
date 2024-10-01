package study;

public interface NestedInterface {
    interface InnerInterface {
        void innerMethod();
    }
    void outerMethod();
}
