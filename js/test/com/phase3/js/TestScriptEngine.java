package com.phase3.js;

import org.junit.*;
import org.mozilla.javascript.*;

public class TestScriptEngine {
    @Test
    public void testScript() throws NoSuchMethodException {
        ScriptEngine sc = new ScriptEngine();
        sc.registerContext(new ScriptContext("out", System.out));

        FunctionClass fcnClass = new FunctionClass();
        sc.registerFunction(new ScriptFunction("upper", FunctionClass.class.getMethod("uppercase", String.class), fcnClass));

        sc.executeScript("out.println(upper('foo!'));", "myname");
    }

    class FunctionClass extends  ScriptableObject {
        public String uppercase (String s) {
            return s.toUpperCase();
        }

        @Override
        public String getClassName() {
            return this.getClassName();
        }
    }
}
