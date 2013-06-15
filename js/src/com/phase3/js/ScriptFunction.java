package com.phase3.js;

import org.mozilla.javascript.*;

import java.lang.reflect.*;

/**
 * Arcus Service Framework
 * <p/>
 * User: cgh
 * Created: 6/14/13 9:34 PM
 * Copyright (c) 2012 IGT Inc.
 */
public class ScriptFunction {
    private Member classMethod;
    private String name;
    private Scriptable instanceThatHasMethod;

    public ScriptFunction(String name, Member classMethod, Scriptable instanceThatHasMethod) {
        this.classMethod = classMethod;
        this.name = name;
        this.instanceThatHasMethod = instanceThatHasMethod;
    }

    public Member getClassMethod() {
        return classMethod;
    }
    public String getName() {
        return name;
    }
    public Scriptable getInstanceThatHasMethod() {
        return instanceThatHasMethod;
    }
}
