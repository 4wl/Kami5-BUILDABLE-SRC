package tech.mmmax.kami.api.config;

import java.util.Map;

public interface ISavable {

    void load(Map map);

    Map save();

    String getFileName();

    String getDirName();
}
