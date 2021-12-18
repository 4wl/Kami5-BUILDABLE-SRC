package tech.mmmax.kami.api.command;

import net.minecraftforge.common.MinecraftForge;

public abstract class Command {

    String name;
    String desc;
    String[] alias;

    public Command(String name, String desc, String[] alias) {
        this.name = name;
        this.desc = desc;
        this.alias = alias;
        MinecraftForge.EVENT_BUS.register(this);
    }

    public abstract void run(String[] astring);

    public String getName() {
        return this.name;
    }

    public String getDesc() {
        return this.desc;
    }

    public String[] getAlias() {
        return this.alias;
    }
}
