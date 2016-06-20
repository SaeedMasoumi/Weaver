package weaver.plugin.task

import org.gradle.api.DefaultTask
import org.gradle.api.file.FileCollection
import org.gradle.api.tasks.InputDirectory
import org.gradle.api.tasks.OutputDirectory
import org.gradle.api.tasks.TaskAction
import weaver.plugin.javassist.WeaverClassPool
import weaver.plugin.model.TransformBundle
import weaver.plugin.model.TransformBundleImp

import static weaver.plugin.util.UrlUtils.normalizeDirectoryForClassLoader

/**
 * @author Saeed Masoumi (saeed@6thsolution.com)
 */
class TransformerTask extends DefaultTask {

    FileCollection classpath

    /**
     * A folder which contains all .class files.
     */
    @InputDirectory
    File classesDir

    /**
     * Snapshot of manipulated classes.
     */
    @OutputDirectory
    File outputDir

    String configurationName

    @TaskAction
    void startTransforming() {
        int time = System.currentTimeMillis()

        TransformBundle bundle = createTransformBundle()
        WeaverExec transformer = new WeaverExec(bundle)
        try {
            transformer.execute()
        } catch (all) {
            log "an error occurred during bytecode weaving [ $all.message ] "
        }
        transformer.dispose()
        bundle.dispose()
        int duration = System.currentTimeMillis() - time
        log("[Weaver]: $name task takes $duration millis")
    }

    TransformBundle createTransformBundle() {
        URLClassLoader classLoader = createClassLoader()
        def bundle = TransformBundleImp.builder()
                .project(project)
                .configuration(project.configurations.getByName(configurationName))
                .classFiles(getClassFiles())
                .rootClassLoader(classLoader)
                .classPool(createPool(classLoader))
                .outputDir(outputDir)
                .build()
        bundle
    }

    /**
     * @return Returns all .class files from build directory.
     */
    Set<File> getClassFiles() {
        return project.fileTree(classesDir).matching {
            include '**/*.class'
        }.files
    }

    URLClassLoader createClassLoader() {
        def urls = []
        if (classpath)
            urls += classpath.collect { it.toURI().toURL() }
        if (classesDir)
            urls += normalizeDirectoryForClassLoader(classesDir)
        URLClassLoader classLoader = new URLClassLoader(urls as URL[], Thread.currentThread().contextClassLoader)
        classLoader
    }

    WeaverClassPool createPool(ClassLoader parentClassLoader) {
        WeaverClassPool pool = new WeaverClassPool(parentClassLoader)
        pool.childFirstLookup = true
        pool.appendClassPath(classpath)
        pool.appendClassPath(classesDir)
        pool
    }

    void log(String message) {
        logger.info(message)
    }
}
