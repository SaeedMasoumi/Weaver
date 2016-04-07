package weaver.plugin.task

import org.gradle.api.DefaultTask
import org.gradle.api.tasks.TaskAction
import weaver.plugin.classloader.WeaverClassLoader

/**
 * Loads classes and resources (at runtime) from .jar/.aar files that has been notated with 'weaver' scope in dependencies,
 * via <code>WeaverClassLoader</code>.
 * <p>
 * So with this approach, weaver plugin can find user's processors and execute them.
 *
 * @author Saeed Masoumi (saeed@6thsolution.com)
 */
class ClassLoaderTask extends DefaultTask {

    @TaskAction
    def resolveDependencies() {
        Set<File> jarFiles = getAllJarFiles()
        WeaverClassLoader.instance.load(jarFiles)
    }
    /**
     * @return Returns All jar files from 'weaver' scope dependencies. For '.aar' bundles, which are the binary distribution of Android Libraries,
     * only extracts <code>/classes.jar</cod> and <code>/libs/*.jar</code> (if exist)
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

}
