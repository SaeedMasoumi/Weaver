package weaver.plugin

import org.gradle.api.Plugin
import org.gradle.api.Project

/**
 * @author Saeed Masoumi (saeed@6thsolution.com)
 */
class WeaverPlugin implements Plugin<Project> {

    @Override
    void apply(Project project) {
        //Add weaver configuration
        project.configurations.create("weaver")
        //Add weaver extension
        project.extensions.create('weaver', WeaverExtension)
    }

    static addTask(Project project, String name, def type) {
        project.task(name, type: type)
    }

    static getTask(Project project, String name) {
        return project.tasks.getByName(name)
    }
}
