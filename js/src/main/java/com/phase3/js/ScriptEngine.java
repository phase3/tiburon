package com.phase3.js;

import org.mozilla.javascript.*;

public class ScriptEngine {
    private Scriptable scope;

    public ScriptEngine() {
        init();
    }

    private void init() {
        Context context = Context.enter();
        this.scope = context.initStandardObjects();
        Context.exit();
    }
    public void registerContext(ScriptContext sc) {
        try {
            Context.enter();
            Object scopeObj = Context.javaToJS(sc.getInstanceObject(), scope);
            ScriptableObject.putProperty(scope, sc.getName(), scopeObj);
        } finally {
            Context.exit();
        }
    }
    public void registerFunction(ScriptFunction fcn) {
        try {
            Context.enter();
            Scriptable function = new FunctionObject(fcn.getName(), fcn.getClassMethod(), fcn.getInstanceThatHasMethod());
            scope.put(fcn.getName(), scope, function);
            ScriptableObject.defineProperty(scope, fcn.getName(), function, ScriptableObject.DONTENUM &ScriptableObject.PERMANENT & ScriptableObject.READONLY);
        } finally {
            Context.exit();
        }
    }
    public Object executeScript(String script, String executionName) {
        try {
            Context context = Context.enter();
            Object o = context.evaluateString(scope, script, executionName, 1, null);
            return o;
        } finally {
            Context.exit();
        }

    }
}
