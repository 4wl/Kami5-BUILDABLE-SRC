package tech.mmmax.kami.api.management;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import org.yaml.snakeyaml.DumperOptions;
import org.yaml.snakeyaml.Yaml;
import tech.mmmax.kami.api.config.ISavable;
import tech.mmmax.kami.impl.KamiMod;

public class SavableManager {

    public static final File MAIN_FOLDER = new File(System.getProperty("user.dir") + File.separator + KamiMod.NAME);
    public static SavableManager INSTANCE;
    Yaml yaml;
    final List savables = new ArrayList();

    public SavableManager() {
        if (!SavableManager.MAIN_FOLDER.exists()) {
            SavableManager.MAIN_FOLDER.mkdir();
        }

        DumperOptions options = new DumperOptions();

        options.setIndent(4);
        options.setPrettyFlow(true);
        options.setDefaultFlowStyle(DumperOptions.FlowStyle.BLOCK);
        this.yaml = new Yaml(options);
    }

    public List getSavables() {
        return this.savables;
    }

    public void load() {
        Iterator iterator = this.getSavables().iterator();

        while (iterator.hasNext()) {
            ISavable savable = (ISavable) iterator.next();

            try {
                File e = new File(SavableManager.MAIN_FOLDER.getAbsolutePath() + File.separator + savable.getDirName());

                if (!e.exists()) {
                    e.mkdirs();
                }

                File file = new File(SavableManager.MAIN_FOLDER.getAbsolutePath() + File.separator + savable.getDirName() + File.separator + savable.getFileName());

                if (!file.exists()) {
                    file.createNewFile();
                } else {
                    FileInputStream inputStream = new FileInputStream(file);
                    Map map = (Map) this.yaml.load((InputStream) inputStream);

                    savable.load(map);
                }
            } catch (Exception exception) {
                exception.printStackTrace();
            }
        }

    }

    public void save() throws IOException {
        System.out.println("Saving  your config");
        Iterator iterator = this.getSavables().iterator();

        while (iterator.hasNext()) {
            ISavable savable = (ISavable) iterator.next();
            File dir = new File(SavableManager.MAIN_FOLDER.getAbsolutePath() + File.separator + savable.getDirName());

            if (!dir.exists()) {
                dir.mkdirs();
            }

            File file = new File(SavableManager.MAIN_FOLDER.getAbsolutePath() + File.separator + savable.getDirName() + File.separator + savable.getFileName());

            if (!file.exists()) {
                file.createNewFile();
            }

            try {
                new BufferedWriter(new FileWriter(file));
                this.yaml.dump(savable.save(), new FileWriter(file));
            } catch (Throwable throwable) {
                throwable.printStackTrace();
            }
        }

    }
}
