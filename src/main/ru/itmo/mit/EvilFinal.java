package ru.itmo.mit;

import java.io.IOException;

public class EvilFinal {
    static int i;
    int j, h;
    public void foo(){
        try {
            int i = 1;
            //
            throw new RuntimeException();
//            int j = 1999;
        } catch (RuntimeException exception) {
            throw new IOException();
        } finally {
            // ...
            throw new RuntimeException("some text");
        }
    }

    public static void main(String[] args) {
        var st = new EvilFinal();
        st.foo(); // ??
    }


}
