package ru.itmo.mit.implementor;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.time.Duration;

public class ImplementorTest extends AbstractImplementorTest {
    /*
     * Here you can use for tests following methods:
     *    - checkInterfaceImplementationFromFolder
     *    - checkInterfaceImplementationFromStandardLibrary
     *    - checkAbstractClassImplementationFromFolder
     *    - checkAbstractClassImplementationFromStandardLibrary
     *
     * In each method you should use FQN.
     *
     * You can test implementor on any class/interface, that lays down
     *   in your main module (src/main/java).
     */

    public ImplementorTest() throws Exception {
        super();
    }

    // Uncomment, if you want to clean up your implementor output directory (tmp)
    @AfterAll
    public static void cleanUp() throws Exception {
        new ImplementorTest().deleteFolderContent(new File(OUTPUT_DIRECTORY), false);
    }

    @Test
    public void implementClassB() throws Exception {
        Assertions.assertTimeout(
                Duration.ofSeconds(5),
                () -> checkAbstractClassImplementationFromFolder("study.inheret.ClassB")
        );
    }

    @Test
    public void implementMyInterface() throws Exception {
        Assertions.assertTimeout(
                Duration.ofSeconds(5),
                () -> checkInterfaceImplementationFromFolder("study.MyInterface")
        );
    }

    @Test
    public void implementMyClass() throws Exception {
        Assertions.assertTimeout(
                Duration.ofSeconds(5),
                () -> checkAbstractClassImplementationFromFolder("study.MyClass")
        );
    }

    @Test
    public void implementComparable() throws Exception {
        Assertions.assertTimeout(
                Duration.ofSeconds(5),
                () -> checkInterfaceImplementationFromStandardLibrary("java.lang.Comparable")
        );
    }

    @Test
    public void implementCollectionUsingAbstractClass() throws Exception {
        Assertions.assertTimeout(
                Duration.ofSeconds(5),
                () -> checkAbstractClassImplementationFromFolder("study.CollectionUsingAbstractClass")
        );
    }

    @Test
    public void implementAbstractList() throws Exception {
        Assertions.assertTimeout(
                Duration.ofSeconds(5),
                () -> checkAbstractClassImplementationFromStandardLibrary("java.util.AbstractList")
        );
    }

    @Test
    public void testClassNotFoundException() {
        Assertions.assertThrows(
                ImplementorException.class,
                () -> checkAbstractClassImplementationFromFolder("non.existent.ClassName")
        );
    }

    @Test
    public void implementSynchronizedMethodClass() throws Exception {
        Assertions.assertTimeout(
                Duration.ofSeconds(5),
                () -> checkAbstractClassImplementationFromFolder("study.SynchronizedMethodClass")
        );
    }

    @Test
    public void implementVariousModifiersClass() throws Exception {
        Assertions.assertTimeout(
                Duration.ofSeconds(5),
                () -> checkAbstractClassImplementationFromFolder("study.VariousModifiersClass")
        );
    }

    @Test
    public void implementComplexNestedAbstractClass() throws Exception {
        Assertions.assertTimeout(
                Duration.ofSeconds(5),
                () -> checkAbstractClassImplementationFromFolder("study.ComplexNestedAbstractClass")
        );
    }

    @Test
    public void implementParameterizedInterface() throws Exception {
        Assertions.assertTimeout(
                Duration.ofSeconds(5),
                () -> checkInterfaceImplementationFromFolder("study.ParameterizedInterface")
        );
    }

    @Test
    public void implementParameterizedAbstractClass() throws Exception {
        Assertions.assertTimeout(
                Duration.ofSeconds(5),
                () -> checkAbstractClassImplementationFromFolder("study.ParameterizedAbstractClass")
        );
    }

    @Test
    public void implementExceptionThrowingInterface() throws Exception {
        Assertions.assertTimeout(
                Duration.ofSeconds(5),
                () -> checkInterfaceImplementationFromFolder("study.ExceptionThrowingInterface")
        );
    }

    @Test
    public void implementArrayReturningAbstractClass() throws Exception {
        Assertions.assertTimeout(
                Duration.ofSeconds(5),
                () -> checkAbstractClassImplementationFromFolder("study.ArrayReturningAbstractClass")
        );
    }

    @Test
    public void implementNestedInterface() throws Exception {
        Assertions.assertTimeout(
                Duration.ofSeconds(5),
                () -> checkInterfaceImplementationFromFolder("study.NestedInterface")
        );
    }

    @Test
    public void implementNestedAbstractClass() throws Exception {
        Assertions.assertTimeout(
                Duration.ofSeconds(5),
                () -> checkAbstractClassImplementationFromFolder("study.NestedAbstractClass")
        );
    }

    @Test
    public void implementPrimitiveAndObjectTypesInterface() throws Exception {
        Assertions.assertTimeout(
                Duration.ofSeconds(5),
                () -> checkInterfaceImplementationFromFolder("study.PrimitiveAndObjectTypesInterface")
        );
    }

    @Test
    public void implementVariousConstructorsAbstractClass() throws Exception {
        Assertions.assertTimeout(
                Duration.ofSeconds(5),
                () -> checkAbstractClassImplementationFromFolder("study.VariousConstructorsAbstractClass")
        );
    }

    @Test
    public void implementGenericCollectionInterface() throws Exception {
        Assertions.assertTimeout(
                Duration.ofSeconds(5),
                () -> checkInterfaceImplementationFromFolder("study.GenericCollectionInterface")
        );
    }
}
