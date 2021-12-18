package tech.mmmax.kami.api.management;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.InputEvent.KeyInputEvent;
import org.lwjgl.input.Keyboard;
import tech.mmmax.kami.api.binds.IBindable;

public class BindManager {

    public static BindManager INSTANCE;
    List bindables = new ArrayList();

    public BindManager() {
        MinecraftForge.EVENT_BUS.register(this);
    }

    public List getBindables() {
        return this.bindables;
    }

    public void setBindables(List bindables) {
        this.bindables = bindables;
    }

    @SubscribeEvent
    public void onKey(KeyInputEvent event) {
        if (Keyboard.getEventKeyState()) {
            Iterator iterator = this.getBindables().iterator();

            while (iterator.hasNext()) {
                IBindable bindable = (IBindable) iterator.next();

                if (bindable.getKey() == Keyboard.getEventKey()) {
                    bindable.onKey();
                }
            }

        }
    }
}
