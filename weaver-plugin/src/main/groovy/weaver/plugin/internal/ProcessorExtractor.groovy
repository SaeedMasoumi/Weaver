package weaver.plugin.internal

import org.gradle.api.Project
import weaver.processor.WeaverProcessor

/**
 * @author Saeed Masoumi (saeed@6thsolution.com)
 */
@Singleton
class ProcessorExtractor {

    static final PROCESSORS_PROP = "/META-INF/weaver/processors"

    private Project project
    private ClassLoader cl
    private ArrayList processorsClassNames

    public ProcessorExtractor with(Project project) {
        this.project = project
        return this
    }

    public ArrayList<WeaverProcessor> getProcessors() {
        load()
        return null
    }
    /**
     * Before transforming classes, {@code # cl} must be initialized because weaver plugin
     * needs to know {@link weaver.processor.WeaverProcessor} classes, So this method prepares them
     * by loading all classes and resources from .jar/.aar files that has been notated with 'weaver'
     * scope in dependencies.
     *
     */
    def load() {
        if (!project) throw new NullPointerException("Project instance is null!")
        if (cl) return
        Set<File> jarFiles = getAllJarFiles()
        loadJars(jarFiles)
        processorsClassNames = getAllProcessorsName(jarFiles)
    }

    /**
     * @return Returns All jar files from 'weaver' scope dependencies. For '.aar' bundles, which are the binary distribution of Android Libraries,
     * The jar extraction will include only <code>/classes.jar</cod> and <code>/libs/*.jar</code> (if exist).
     * <p>
     * See <a href="http://tools.android.com/tech-docs/new-build-system/aar-format">AAR Format</a> for more details.
     */
    def getAllJarFiles() {
        def jarFiles = []
        project.configurations.weaver.files.forEach {
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

    def loadJars(Set<File> jarFiles) {
        if (jarFiles) {
            def urls = jarFiles.collect() { it.toURI().toURL() }
            cl = new URLClassLoader(urls as URL[], Thread.currentThread().contextClassLoader)
            Thread.currentThread().contextClassLoader = cl
            jarFiles.forEach {
                project.logger.quiet("$name Task: $it.name has been added to classloader")
            }
        } else {
            cl = Thread.currentThread().contextClassLoader
        }
    }
    /**
     * Extracts {@code WeaverProcessor} classes from {@link #PROCESSORS_PROP} location.
     */
    def getAllProcessorsName(Set<File> jarFiles) {
        def weaverProcessors = []
        jarFiles.forEach {
            def zipTree = project.zipTree(it).matching {
                include PROCESSORS_PROP
            }
            if (zipTree.files) {
                File propFile = zipTree.singleFile
                if (propFile) {
                    Properties props = new Properties()
                    propFile.withInputStream {
                        props.load(it)
                    }
                    weaverProcessors.addAll(props.getProperty("weaverProcessors").split(",").collect())
                }
            }

        }
        return weaverProcessors
    }

    protected Class loadClass(String name) {
        return cl.loadClass(name)
    }

}
