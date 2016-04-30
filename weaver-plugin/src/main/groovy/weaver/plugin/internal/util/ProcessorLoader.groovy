package weaver.plugin.internal.util

import weaver.processor.WeaverProcessor

/**
 * This class is responsible to finds all dependencies which annotated with "weaver" scope,
 * then extract their processors and using a {@code ClassLoader} to loads classes
 * and makes them accessible for {@link weaver.plugin.task.JavassistTransformerTask}.
 *
 * @author Saeed Masoumi (saeed@6thsolution.com)
 */
class ProcessorLoader {

    static final PROCESSORS_PROP = "META-INF/weaver/processors"

    /**
     * @return Returns Instantiated {@code WeaverProcessor}s from {@link #PROCESSORS_PROP} location.
     */
    static List<WeaverProcessor> load(Set<File> files, List<String> names) {
        ClassLoader cl = initClassLoader(files)
        List<WeaverProcessor> processors = new ArrayList<>()
        names.each {
            processors.add(cl.loadClass(it).newInstance() as WeaverProcessor)
        }
        return processors
    }

    /**
     * Before transforming classes, {@code # cl} must be initialized because weaver plugin
     * needs to know about {@link weaver.processor.WeaverProcessor} classes, So this method prepares them
     * by loading all classes and resources from .jar/.aar files that has been notated with 'weaver'
     * scope in dependencies.
     *
     */
    static def initClassLoader(Set<File> jarFiles) {
        ClassLoader cl
        if (jarFiles) {
            def urls = jarFiles.collect() { it.toURI().toURL() }
            cl = new URLClassLoader(urls as URL[], Thread.currentThread().contextClassLoader)
            Thread.currentThread().contextClassLoader = cl
        } else {
            cl = Thread.currentThread().contextClassLoader
        }
        return cl
    }
}
