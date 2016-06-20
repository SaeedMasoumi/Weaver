package weaver.plugin.model

import org.gradle.api.Project
import org.gradle.api.artifacts.Configuration
import weaver.plugin.javassist.WeaverClassPool
import weaver.plugin.util.Disposable

/**
 * All needed data to instantiate processors and transform classes, {@link weaver.plugin.task.WeaverExec}
 * needs this bundle to be task independent.
 *
 * @author Saeed Masoumi (saeed@6thsolution.com)
 */
interface TransformBundle extends Disposable {

    Project getProject()

    File getOutputDir()

    Configuration getConfiguration()

    WeaverClassPool getClassPool()

    ClassLoader getRootClassLoader()

    Set<File> getClassFiles()
}
