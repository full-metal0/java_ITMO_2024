package study;

import java.util.List;

public interface GenericCollectionInterface<T> {
    List<T> getList();
    void setList(List<T> list);
}
