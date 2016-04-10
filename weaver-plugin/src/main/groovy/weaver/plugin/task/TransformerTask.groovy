package weaver.plugin.task

import org.gradle.api.DefaultTask
import org.gradle.api.file.FileCollection
import org.gradle.api.tasks.TaskAction
import org.gradle.api.tasks.compile.JavaCompile

/**
 * @author Saeed Masoumi (saeed@6thsolution.com)
 */
abstract class TransformerTask extends DefaultTask {

    static final PROCESSORS_PROP = "/META-INF/weaver/processors.properties"

    private JavaCompile javaCompile
    private FileCollection classPath

    private ClassLoader cl
    private ArrayList processorsClassNames

    @TaskAction
    def doTransform() {
        loadClassLoader()
        transform()
    }

    /**
     * Before transforming classes, {@code # cl} must be initialized because weaver plugin
     * needs to know {@link weaver.processor.WeaverProcessor} classes, So this method prepares them
     * by loading all classes and resources from .jar/.aar files that has been notated with 'weaver'
     * scope in dependencies.
     *
     */
    def loadClassLoader() {
        Set<File> jarFiles = getAllJarFiles()
        loadJars(jarFiles)
        processorsClassNames = getAllProcessorsName(jarFiles)
    }

    abstract def transform()
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
            cl = new URLClassLoader(urls as URL[])
            jarFiles.forEach {
                project.logger.quiet("$name Task: $it.name has been added to classloader")
            }
        } else {
            cl = new URLClassLoader()
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

    public void setClassPathFileCollection(FileCollection classPathFileCollection) {
        this.classPath = classPathFileCollection
    }

    FileCollection getClassPathFileCollection() {
        return classPath
    }

    public void setJavaCompile(JavaCompile javaCompile) {
        this.javaCompile = javaCompile
    }

    JavaCompile getJavaCompile() {
        return javaCompile
    }

    protected ClassLoader getClassLoader() {
        return cl
    }

    protected Class loadClass(String name) {
        return cl.loadClass(name)
    }
}
