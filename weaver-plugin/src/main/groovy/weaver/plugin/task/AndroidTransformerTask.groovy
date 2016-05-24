package weaver.plugin.task

import com.android.SdkConstants
import com.android.build.api.transform.*
import com.google.common.collect.ImmutableSet
import com.google.common.collect.Sets
import groovy.io.FileType
import org.gradle.api.Project
import org.gradle.api.logging.Logger
import weaver.plugin.WeaverPlugin
import weaver.plugin.javassist.WeaverClassPool
import weaver.plugin.model.TransformBundle
import weaver.plugin.model.TransformBundleImp
import weaver.plugin.transform.TransformerDelegate

import java.util.jar.JarFile

import static com.android.build.api.transform.QualifiedContent.*
import static weaver.plugin.util.UrlUtils.normalizeDirectoryForClassLoader

/**
 * @author Saeed Masoumi (saeed@6thsolution.com)
 */
public class AndroidTransformerTask extends Transform {

    Project project
    Logger logger

    AndroidTransformerTask(Project project) {
        this.project = project
        this.logger = project.logger
    }

    @Override
    String getName() {
        return "Weaver"
    }

    @Override
    Set<ContentType> getInputTypes() {
        return ImmutableSet.<ContentType> of(DefaultContentType.CLASSES)
    }

    @Override
    Set<Scope> getScopes() {
        return Sets.immutableEnumSet(Scope.PROJECT)
    }

    @Override
    Set<Scope> getReferencedScopes() {
        return Sets.immutableEnumSet(Scope.EXTERNAL_LIBRARIES, Scope.PROJECT_LOCAL_DEPS,
                Scope.SUB_PROJECTS, Scope.SUB_PROJECTS_LOCAL_DEPS)
    }

    @Override
    boolean isIncremental() {
        return false
    }

    @Override
    void transform(TransformInvocation transformInvocation) throws TransformException, InterruptedException, IOException {
        TransformBundle bundle = createTransformBundle(transformInvocation)
        TransformerDelegate transformer = new TransformerDelegate(bundle)
        try {
            transformer.execute()
        } catch (all) {
            log "an error occurred during transformation [ $all.message ] "
        }
        transformer.dispose()
        bundle.dispose()
        log "ClassPool and ClassLoaders are disposed successfully"
    }

    TransformBundle createTransformBundle(TransformInvocation transformInvocation) {
        URLClassLoader rootClassLoader = createClassLoader(transformInvocation.referencedInputs)
        WeaverClassPool pool = createClassPool(rootClassLoader, transformInvocation.inputs, transformInvocation.referencedInputs)
        appendBootClassPath(pool)
        TransformBundle bundle = TransformBundleImp.builder()
                .project(project)
                .configuration(project.configurations.getByName(WeaverPlugin.WEAVER_CONFIGURATION))
                .rootClassLoader(rootClassLoader)
                .classPool(pool)
                .classFiles(getClassFiles(transformInvocation.inputs))
                .outputDir(getOutputFile(transformInvocation.outputProvider))
                .build()
        return bundle
    }

    URLClassLoader createClassLoader(Collection<TransformInput> referencedInputs) {
        def urls = []
        referencedInputs.each {
            it.directoryInputs.each {
                urls += normalizeDirectoryForClassLoader(it.file)
            }
            it.jarInputs.each {
                urls += it.file.toURI().toURL()
            }
        }
        //TODO check whether it's needed to add classes directory or not
        project.android.bootClasspath.each {
            String path = it.absolutePath
            urls += project.file(path).toURI().toURL()
            log "Add android boot class [$path] to root class loader."
        }
        return new URLClassLoader(urls as URL[], Thread.currentThread().contextClassLoader)
    }


    void appendBootClassPath(WeaverClassPool classPool) {
        project.android.bootClasspath.each {
            classPool.appendClassPath(it as File)
        }
    }

    /**
     * Creates a {@link javassist.ClassPool}. all class paths in source code,
     * dependencies, android boot class path and system class path will be appended to  class pool.
     *
     * @param classLoader Class loader of class pool.
     * @param inputs the source inputs provided by transform API.
     * @param referencedInputs the dependencies inputs provided by transform API.
     * @return returns a {@link WeaverClassPool}
     */
    private
    static WeaverClassPool createClassPool(URLClassLoader classLoader, Collection<TransformInput> inputs, Collection<TransformInput> referencedInputs) {
        WeaverClassPool pool = new WeaverClassPool(classLoader)
        pool.childFirstLookup = true
        pool.appendSystemPath()
        inputs.each {
            it.jarInputs.each {
                pool.appendClassPath(it.file)
            }
            it.directoryInputs.each {
                pool.appendClassPath(it.file)
            }
        }
        referencedInputs.each {
            it.jarInputs.each {
                pool.appendClassPath(it.file)
            }
            it.directoryInputs.each {
                pool.appendClassPath(it.file)
            }
        }
        return pool
    }

    /**
     * @param inputs the inputs provided by transform API.
     * @return Returns all .class files from project source set.
     */
    private static Set<File> getClassFiles(Collection<TransformInput> inputs) {
        Set<File> classFiles = new HashSet<File>()
        inputs.each {
            it.directoryInputs.each {
                it.file.eachFileRecurse(FileType.FILES) {
                    if (it.absolutePath.endsWith(SdkConstants.DOT_CLASS)) {
                        classFiles.add(it)
                    }
                }
            }
            it.jarInputs.each {
                def jarFile = new JarFile(it.file)
                jarFile.entries().findAll {
                    !it.directory && it.name.endsWith(SdkConstants.DOT_CLASS)
                }.each {
                    classFiles.add(new File(it.name))
                }
            }
        }
        return classFiles
    }

    /**
     * @param outputProvider
     * @return Returns a directory which will store transformed classes.
     */
    private File getOutputFile(TransformOutputProvider outputProvider) {
        return outputProvider.getContentLocation(
                'weaver', getInputTypes(), getScopes(), Format.DIRECTORY)
    }

    void log(String message) {
        logger.info message
    }
}