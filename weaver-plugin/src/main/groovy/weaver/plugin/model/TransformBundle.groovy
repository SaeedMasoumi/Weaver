package weaver.plugin.model

import org.gradle.api.Project
import org.gradle.api.artifacts.Configuration
import weaver.plugin.javassist.WeaverClassPool
import weaver.plugin.util.Disposable

/**
 * @author Saeed Masoumi (saeed@6thsolution.com)
 */
interface TransformBundle extends Disposable{

    Project getProject()
    /**
     * Snapshot of manipulated classes.
     */
    File getOutputDir()

    Configuration getConfiguration()

    WeaverClassPool getClassPool()

    ClassLoader getRootClassLoader()

    Set<File> getClassFiles()
}
