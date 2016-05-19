package weaver.plugin.model

import org.gradle.api.Project
import org.gradle.api.artifacts.Configuration
import weaver.plugin.javassist.WeaverClassPool

/**
 * @author Saeed Masoumi (saeed@6thsolution.com)
 */
class TransformBundleImp implements TransformBundle {

    Project project
    File outputDir
    Configuration configuration
    WeaverClassPool classPool
    URLClassLoader rootClassLoader
    Set<File> classFiles

    @Override
    Project getProject() {
        return project
    }

    @Override
    File getOutputDir() {
        return outputDir
    }

    @Override
    Configuration getConfiguration() {
        return configuration
    }

    @Override
    WeaverClassPool getClassPool() {
        return classPool
    }

    @Override
    URLClassLoader getRootClassLoader() {
        return rootClassLoader
    }

    @Override
    Set<File> getClassFiles() {
        return classFiles
    }

    @Override
    void dispose() {
        getRootClassLoader().close()
        getClassPool().close()
    }
}
