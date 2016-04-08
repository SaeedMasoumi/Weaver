package weaver.plugin

import org.gradle.api.Plugin
import org.gradle.api.Project
import weaver.plugin.task.PreLoaderTask

/**
 * @author Saeed Masoumi (saeed@6thsolution.com)
 */
class WeaverPlugin implements Plugin<Project> {

    static final WEAVER_PRE_LOADER_TASK = "weaverPreLoader"
    static final WEAVER_TRANSFORMER_TASK = "weaverTransformer"

    @Override
    void apply(Project project) {
        //Add weaver configuration
        project.configurations.create("weaver")
        //Add weaver extension
        project.extensions.create('weaver', WeaverExtension)
        addTasks project
    }

    private void addTasks(Project project) {
        project.task(WEAVER_PRE_LOADER_TASK, type: PreLoaderTask)
    }

    protected def getPreLoaderTask(Project project) {
        return project.tasks.getByName(WEAVER_PRE_LOADER_TASK)
    }
}
