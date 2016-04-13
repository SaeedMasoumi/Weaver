package weaver.plugin.internal

import org.gradle.api.Project
import weaver.processor.WeaverProcessor

/**
 * @author Saeed Masoumi (saeed@6thsolution.com)
 */
class ProcessorLoader {

    static final PROCESSORS_PROP = "META-INF/weaver/processors"

    private Project project
    private Set<File> dependencies
    private ClassLoader cl
    private Set<File> jarFiles

    public ProcessorLoader(Project project, Set<File> dependencies) {
        this.project = project
        this.dependencies = dependencies
    }

    /**
     * @return Returns Instantiated {@code WeaverProcessor}s from {@link #PROCESSORS_PROP} location.
     */
    public ArrayList<WeaverProcessor> getProcessors() {
        def names = getProcessorsName(load())
        def processors = []
        names.forEach { String name ->
            processors.add(loadClass(name))
        }
        return processors
    }

    /**
     *
     * @return First Finds all jar dependencies in weaver scope from configurations container
     * and then initializes classloader, finally returns all jar dependencies.
     */
    def load() {
        if (!project) throw new NullPointerException("Project instance is null!")
        jarFiles = getAllJarFiles()
        initClassLoader(jarFiles)
        return jarFiles
    }

    /**
     * @return Returns All jar files from 'weaver' scope dependencies. For '.aar' bundles, which are the binary distribution of Android Libraries,
     * The jar extraction will include only <code>/classes.jar</cod> and <code>/libs/*.jar</code> (if exist).
     * <p>
     * See <a href="http://tools.android.com/tech-docs/new-build-system/aar-format">AAR Format</a> for more details.
     */
    def getAllJarFiles() {
        def jarFiles = []
        dependencies.forEach {
            if (it.name.endsWith(".jar")) {
                jarFiles.add(it)
            } else if (it.name.endsWith(".aar")) {
                project.zipTree(it).matching {
                    include '**/*.jar'
                    exclude 'lint.jar'
                }.files.forEach {
                    jarFiles.add(it)
                }
            }
        }
        return jarFiles
    }

    /**
     * Before transforming classes, {@code # cl} must be initialized because weaver plugin
     * needs to know about {@link weaver.processor.WeaverProcessor} classes, So this method prepares them
     * by loading all classes and resources from .jar/.aar files that has been notated with 'weaver'
     * scope in dependencies.
     *
     */
    def initClassLoader(Set<File> jarFiles) {
        if (jarFiles) {
            def urls = jarFiles.collect() { it.toURI().toURL() }
            cl = new URLClassLoader(urls as URL[], Thread.currentThread().contextClassLoader)
            Thread.currentThread().contextClassLoader = cl
        } else {
            cl = Thread.currentThread().contextClassLoader
        }
    }
    /**
     * Extracts {@code WeaverProcessor} classes from {@link #PROCESSORS_PROP} location.
     */
    def getProcessorsName(Set<File> jarFiles) {
        def names = []
        jarFiles.forEach {
            def prop = project.zipTree(it).matching {
                include PROCESSORS_PROP
            }
            if (prop.files) {
                def propFile = prop.singleFile
                if (propFile) {
                    propFile.eachLine {
                        names.add(it)
                    }
                }
            }
        }

        return names
    }

    WeaverProcessor loadClass(String name) {
        return cl.loadClass(name).newInstance() as WeaverProcessor
    }

}
