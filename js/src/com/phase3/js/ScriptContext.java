package com.phase3.js;

/**
 * Arcus Service Framework
 * <p/>
 * User: cgh
 * Created: 6/14/13 9:30 PM
 * Copyright (c) 2012 IGT Inc.
 */
public class ScriptContext {
    private Object instanceObject;
    private String name;

    public ScriptContext() {
    }

    public ScriptContext(String name, Object instanceObject) {
        this.instanceObject = instanceObject;
        this.name = name;
    }

    public Object getInstanceObject() {
        return instanceObject;
    }

    public void setInstanceObject(Object instanceObject) {
        this.instanceObject = instanceObject;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
