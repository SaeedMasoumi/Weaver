package weaver.plugin.task

import org.gradle.api.DefaultTask
import org.gradle.api.file.FileCollection
import org.gradle.api.tasks.InputDirectory
import org.gradle.api.tasks.OutputDirectory
import org.gradle.api.tasks.TaskAction
import weaver.plugin.javassist.WeaverClassPool
import weaver.plugin.processor.ProcessorInstantiator

import static weaver.plugin.util.UrlUtils.normalizeDirectoryForClassLoader

/**
 * @author Saeed Masoumi (saeed@6thsolution.com)
 */
class TransformerTask extends DefaultTask {

    FileCollection classpath
    /**
     * A folder which .class files exist there.
     */
    @InputDirectory
    File classesDir

    /**
     * Snapshot of manipulated classes.
     */
    @OutputDirectory
    File outputDir

    String configurationName

    URLClassLoader classLoader
    WeaverClassPool pool
    ProcessorInstantiator processorInstantiator

    @TaskAction
    void startTransforming() {
        int time = System.currentTimeMillis()

        initResources()

        disposeResources()
        int duration = System.currentTimeMillis() - time
        log("[Weaver]: $name task takes $duration millis")
    }

    def initResources() {
        classLoader = initClassLoader()
        pool = createPool(classLoader)
        processorInstantiator = new ProcessorInstantiator(classLoader, project)
    }

    def disposeResources() {
        //closing all jar files that were opened by the classLoaders
        processorInstantiator.closeAllClassLoaders()
        classLoader.close()
        //detach all class paths
        pool.close()
    }


    /**
     * @return Returns all .class files from build directory.
     */
    Set<File> getClassFiles() {
        return project.fileTree(classesDir).matching {
            include '**/*.class'
        }.files
    }

    URLClassLoader initClassLoader() {
        def urls = []
        if (classpath)
            urls += classpath.collect { it.toURI().toURL() }
        if (classesDir)
            urls += normalizeDirectoryForClassLoader(classesDir)
        URLClassLoader classLoader = new URLClassLoader(urls as URL[], Thread.currentThread().contextClassLoader)
//        Thread.currentThread().contextClassLoader = classLoader
        return classLoader
    }

    def createPool(ClassLoader parentClassLoader) {
        pool = new WeaverClassPool(parentClassLoader, true)
        pool.childFirstLookup = true
        pool.appendClassPath(classpath)
        pool.appendClassPath(classesDir)
        return pool
    }

    void log(String message) {
        logger.info(message)
    }
}
