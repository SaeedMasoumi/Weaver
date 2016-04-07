package weaver.plugin.task

import org.gradle.api.DefaultTask
import org.gradle.api.tasks.TaskAction
import weaver.plugin.classloader.WeaverClassLoader

/**
 * Before transforming classes, weaver plugin needs to know {@link weaver.processor.WeaverProcessor}
 * classes, So this task prepares them by loading all classes and resources from .jar/.aar files
 * that has been notated with 'weaver' scope in dependencies.
 * <p>
 * With this approach, weaver plugin has access to these processors at runtime.
 *
 * @author Saeed Masoumi (saeed@6thsolution.com)
 */
class PreLoaderTask extends DefaultTask {

    static final PROCESSORS_PROP = "/META-INF/weaver/processors.properties"

    @TaskAction
    def resolveDependencies() {
        Set<File> jarFiles = getAllJarFiles()
        WeaverClassLoader.instance.loadJars(jarFiles)
        println getAllProcessorsName(jarFiles)
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
    /**
     * Extracts {@code WeaverProcessor} classes from {@link #PROCESSORS_PROP} location.
     */
    def getAllProcessorsName(Set<File> jarFiles) {
        def weaverProcessors = []
        jarFiles.forEach {
            File propFile = project.zipTree(it).matching {
                include PROCESSORS_PROP
            }.singleFile

            if (propFile) {
                Properties props = new Properties()
                propFile.withInputStream {
                    props.load(it)
                }
                weaverProcessors.addAll(props.getProperty("weaverProcessors").split(",").collect())
            }

        }
        return weaverProcessors
    }

}
