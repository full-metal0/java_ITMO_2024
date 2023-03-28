# Мультимножество

* Множество, допускающее включение одного и того же элемента по нескольку раз

# Требования

* Необходимо написать класс `HashMultiset`, реализующий интерфейс `Multiset` с учетом комментариев
* Стандартные операции коллекций (`add`/`contains`/`remove` и т.д.) должны работать за то же время, что и аналогичные методы в `java.util.HashSet`
* Java Stream API использовать нельзя
* Стандартные коллекции использовать можно

---

# Подробности

* множество должно позволять хранить `null`
* `remove` удаляет только одно вхождение объекта
* `size` возвращает общее количество вхождений
* В возвращаемом `Set<Entry<E>>` должны быть реализованы только `iterator` и `size`
* Все возвращаемые итераторы должны поддерживать методы `hasNext`/`next`/`remove`
* Все возвращаемые итераторы должны обходить элементы в порядке их добавления
* Порядок элементов в мультимножестве определяется по первому добавлению элемента
```
Multiset<String> set = new HashMultiset<String>(Arrays.asList("a", "b", "a"));
// Assume that println prints elements using `iterator()`
System.out.println(set) // ["a", "a", "b"]
System.out.println(set.elementSet()) // ["a", "b"]
System.out.println(set.entrySet()) // [("a", 2), ("b", 1)]
```
* Удаление элемента через `HashMultiset.iterator().remove()` удаляет только одно вхождение элемента
* `elementSet` и `entrySet` создают _view_ на исходное мультимножество
   * Добавление/удаление элемента в `HashMultiset` меняет `elementSet` и `entrySet`
   * Модифицирующие методы у `elementSet` и `entrySet` (`add`, `remove` и пр.) должны кидать `UnsupportedOperationException`
   * У итераторов `elementSet` и `entrySet` должен работать `remove`
      * Удаление элемента через такой итератор меняет исходную коллекцию и другие _view_
      * Удаление элемента через этот итератор удаляет все вхождения данного элемента в мультимножество
```
Multiset<String> set = new HashMultiset<>(Arrays.asList("a", "b", "a"));
Set<Multiset.Entry<String>> entrySet = set.entrySet();
Iterator<Entry<String>> iterator = entrySet.iterator();
iterator.next(); // ("a", 2)
iterator.remove();
System.out.println(set); // ["b"]
System.out.println(entrySet); // [("b", 1)]
```

---

# Примечания

* См. `LinkedHash*`
* См. `Abstract*`
* Реализации методов в `Abstract*` не всегда удовлетворяют требуемой асимптотике
* Некоторые реализации методов в `Abstract*` бросают `UnsupportedOperationException`
* Не забудьте про контракты функций коллекций и итераторов
* **В проекте есть тесты, которые можно и нужно запускать**
   * Для того, чтобы они заработали, необходимо раскомментировать строчку `HashMultisetTest.java:372`
