package com.cloudbees.hudson.plugins.folder.config;

import com.cloudbees.hudson.plugins.folder.health.FolderHealthMetric;
import com.cloudbees.hudson.plugins.folder.health.FolderHealthMetricDescriptor;
import hudson.Extension;
import hudson.init.InitMilestone;
import hudson.init.Initializer;
import hudson.util.DescribableList;
import jenkins.model.GlobalConfiguration;
import net.sf.json.JSONObject;
import org.jenkinsci.Symbol;
import org.kohsuke.stapler.DataBoundConstructor;
import org.kohsuke.stapler.DataBoundSetter;
import org.kohsuke.stapler.StaplerRequest;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Extension @Symbol("defaultFolderConfiguration")
public class AbstractFolderConfiguration extends GlobalConfiguration {

    private List<FolderHealthMetric> healthMetrics;

    @Nonnull
    public static AbstractFolderConfiguration get() {
        AbstractFolderConfiguration instance = GlobalConfiguration.all().get(AbstractFolderConfiguration.class);
        if (instance == null) {
            throw new IllegalStateException();
        }
        return instance;
    }

    @DataBoundConstructor
    public AbstractFolderConfiguration() {
        this.load();
        if(healthMetrics == null) {
            List<FolderHealthMetric> metrics = new ArrayList<>();
            for (FolderHealthMetricDescriptor d : FolderHealthMetricDescriptor.all()) {
                FolderHealthMetric metric = d.createDefault();
                if (metric != null) {
                    metrics.add(metric);
                }
            }
            setHealthMetrics(new DescribableList<FolderHealthMetric,
                    FolderHealthMetricDescriptor>(this, metrics));
        }
    }

    @Nonnull
    public List<FolderHealthMetric> getHealthMetrics() {
        return healthMetrics == null ? Collections.emptyList() : healthMetrics;
    }

    @DataBoundSetter
    public void setHealthMetrics(List<FolderHealthMetric> healthMetrics) {
        this.healthMetrics = healthMetrics;
        save();
    }

    @Override
    public boolean configure(StaplerRequest req, JSONObject json) {
        if(json.containsKey("healthMetrics")) {
            req.bindJSON(this, json);
            this.save();
        } else {
            this.setHealthMetrics(Collections.emptyList());
        }
        return true;
    }
}
