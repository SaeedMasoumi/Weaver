package weaver.plugin.processor

import org.gradle.api.Project
import org.gradle.api.artifacts.Configuration
import org.gradle.api.artifacts.Dependency
import weaver.common.Processor
import weaver.plugin.model.TransformBundle
import weaver.plugin.util.Disposable

import static weaver.plugin.util.DependencyManager.jarToURL
import static weaver.plugin.util.MetaInfUtils.extractProcessorsName

/**
 * This class is responsible to instantiate all processors in weaver or testWeaver scopes.
 *
 * @author Saeed Masoumi (saeed@6thsolution.com)
 */
class ProcessorInstantiator implements Disposable {

    Project project
    Configuration configuration
    ClassLoader parentClassLoader
    def dependenciesCL = []

    public ProcessorInstantiator(TransformBundle bundle) {
        project = bundle.project
        configuration = bundle.configuration
        parentClassLoader = bundle.rootClassLoader
    }

    /**
     *
     * It get all dependencies from given configuration and creates a {@link ClassLoader} for each dependency
     * and its subset dependencies and then instantiates {@link Processor}s belonging to the current dependency via created classloader.
     * <p/>
     * This way allows weaver to deal with different versions of processors.

     * @param configuration Given configuration for getting dependencies
     * @return Returns instantiated processors.
     */
    public ArrayList<Processor> instantiate() {
        def processors = []
        configuration.dependencies.each { Dependency dependency ->
            //get all dependencies including current and its subset
            Set<File> dependencies = configuration.files(dependency)
            def processorsName = extractProcessorsName(project, dependencies)
            if (processorsName && processorsName.size() > 0) {
                def urls = jarToURL(dependencies)
                URLClassLoader dependencyClassLoader = new URLClassLoader(urls as URL[], parentClassLoader)
                processorsName.each {
                    processors += dependencyClassLoader.loadClass(it).newInstance() as Processor
                }
                dependenciesCL += dependencyClassLoader
            }
        }
        return processors
    }

    void closeAllClassLoaders() {
        dependenciesCL.each {
            it.close()
        }
    }

    @Override
    void dispose() {
        closeAllClassLoaders()
    }
}
