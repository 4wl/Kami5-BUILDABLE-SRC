package tech.mmmax.kami.api.management;

import java.util.ArrayList;
import java.util.List;

public class FeatureManager {

    public static FeatureManager INSTANCE;
    List features = new ArrayList();

    public List getFeatures() {
        return this.features;
    }

    public void setFeatures(List features) {
        this.features = features;
    }
}
