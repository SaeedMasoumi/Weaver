package weaver.plugin.task

import javassist.CtClass
import org.gradle.api.DefaultTask
import org.gradle.api.file.FileCollection
import org.gradle.api.tasks.InputDirectory
import org.gradle.api.tasks.OutputDirectory
import org.gradle.api.tasks.TaskAction
import weaver.common.Processor
import weaver.common.WeaveEnvironment
import weaver.plugin.internal.javassist.WeaverClassPool
import weaver.plugin.internal.processor.ProcessorInvocationHandler
import weaver.plugin.internal.processor.WeaveEnvironmentImp

import static weaver.plugin.internal.util.UrlUtils.normalizeDirectoryForClassLoader

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

    URLClassLoader classLoader
    WeaverClassPool pool
    ProcessorInvocationHandler invocationHandler

    @TaskAction
    void startTransforming() {
        int time = System.currentTimeMillis()
        if (!outputDir.exists())
            outputDir.mkdir()
        initResources()
        def processors = invocationHandler.invokeProcessors(project.configurations.weaver)
        if (!processors) {
            debug("No processor found [transforming ignored]")
        }
        boolean successfulTransforming = true
        //weaving
        try {
            weaving(processors)
        } catch (all) {
            logger.quiet("Weaving exception :[ $all.message ]")
            successfulTransforming = false
        }
        setDidWork(successfulTransforming)
        closeResources()
        int duration = System.currentTimeMillis() - time
        logger.debug("$name : Weaving takes $duration millis")
    }

    def initResources() {
        classLoader = initClassLoader()
        pool = createPool(classLoader)
        invocationHandler = new ProcessorInvocationHandler(classLoader, project)
    }

    def closeResources() {
        //closing all jar files that were opened by the classloader
        invocationHandler.closeAllClassLoaders()
        classLoader.close()
        //detach all class paths
        pool.close()
    }

    void weaving(ArrayList<Processor> processors) {
        WeaveEnvironment env = new WeaveEnvironmentImp(project, pool)
        processors.each {
            it.init(env)
        }
        def classes = getClassesFiles()
        classes.each {
            CtClass ctClass = pool.get(it)
            processors.each {
                if (it.filter(ctClass)) {
                    ctClass.defrost()
                    it.transform(ctClass)
                    ctClass.writeFile(outputDir.path)

                }
            }
        }

    }

    /**
     * @return Returns all .class files from build directory.
     */
    Set<File> getClassesFiles() {
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

    void debug(String message) {
        logger.debug(message)
    }
}
