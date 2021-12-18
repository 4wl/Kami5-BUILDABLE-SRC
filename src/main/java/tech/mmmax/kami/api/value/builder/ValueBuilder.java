package tech.mmmax.kami.api.value.builder;

import java.util.function.Consumer;
import tech.mmmax.kami.api.feature.Feature;
import tech.mmmax.kami.api.value.Value;

public class ValueBuilder<Type>
{
    Value<Type> value;

    public ValueBuilder() {
        this.value = new Value<Type>();
    }

    public ValueBuilder<Type> withDescriptor(final String name, final String tag) {
        this.value.setName(name);
        this.value.setTag(tag);
        return this;
    }

    public ValueBuilder<Type> withDescriptor(final String name) {
        this.value.setName(name);
        String camelCase = name.replace(" ", "");
        final char[] chars = camelCase.toCharArray();
        chars[0] = Character.toLowerCase(chars[0]);
        camelCase = new String(chars);
        this.value.setTag(camelCase);
        return this;
    }

    public ValueBuilder<Type> withValue(final Type value) {
        this.value.setValue(value);
        return this;
    }

    public ValueBuilder<Type> withAction(final Consumer<Value<Type>> action) {
        this.value.setAction(action);
        return this;
    }

    public ValueBuilder<Type> withRange(final Type min, final Type max) {
        this.value.setMin(min);
        this.value.setMax(max);
        return this;
    }

    public ValueBuilder<Type> withModes(final String... modes) {
        this.value.setModes(modes);
        return this;
    }

    public Value<Type> getValue() {
        return this.value;
    }

    public Value<Type> register(final Feature feature) {
        feature.getValues().add(this.value);
        return this.value;
    }
}