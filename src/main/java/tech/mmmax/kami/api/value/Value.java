// 
// Decompiled by Procyon v0.5.36
// 

package tech.mmmax.kami.api.value;

import java.util.function.Consumer;

public class Value<Type>
{
    String name;
    String tag;
    Type min;
    Type max;
    String[] modes;
    boolean active;
    Consumer<Value<Type>> action;
    boolean enabled;
    Type value;
    
    public Value() {
        this.active = true;
    }
    
    public Type getValue() {
        return this.value;
    }
    
    public String getName() {
        return this.name;
    }
    
    public String getTag() {
        return this.tag;
    }
    
    public void setName(final String name) {
        this.name = name;
    }
    
    public void setTag(final String tag) {
        this.tag = tag;
    }
    
    public void setValue(final Type value) {
        this.value = value;
        if (this.action != null) {
            this.action.accept(this);
        }
    }
    
    public Type getMin() {
        return this.min;
    }
    
    public Type getMax() {
        return this.max;
    }
    
    public void setMin(final Type min) {
        this.min = min;
    }
    
    public void setMax(final Type max) {
        this.max = max;
    }
    
    public String[] getModes() {
        return this.modes;
    }
    
    public void setModes(final String[] modes) {
        this.modes = modes;
    }
    
    public Consumer<Value<Type>> getAction() {
        return this.action;
    }
    
    public void setAction(final Consumer<Value<Type>> action) {
        this.action = action;
    }
    
    public boolean isActive() {
        return this.active;
    }
    
    public void setActive(final boolean active) {
        this.active = active;
    }
}
