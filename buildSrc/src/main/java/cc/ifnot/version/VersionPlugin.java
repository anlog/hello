package cc.ifnot.version;

import org.gradle.api.Plugin;
import org.gradle.api.Project;

import cc.ifnot.libs.utils.Lg;

class VersionPlugin implements Plugin<Project> {
    @Override
    public void apply(Project target) {
        Lg.d("project: %s applied version plugin", target.getName());
    }
}
