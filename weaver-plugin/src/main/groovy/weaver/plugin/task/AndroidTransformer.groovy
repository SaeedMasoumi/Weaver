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
import weaver.plugin.processor.ProcessorInvocator

import java.util.jar.JarFile

import static com.android.build.api.transform.QualifiedContent.*
import static weaver.plugin.util.UrlUtils.normalizeDirectoryForClassLoader

/**
 * @author Saeed Masoumi (saeed@6thsolution.com)
 */
class AndroidTransformer extends Transform {

    Project project
    Logger logger

    AndroidTransformer(Project project) {
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
        ProcessorInvocator invocator = new ProcessorInvocator(bundle)
        try {
            invocator.execute()
        } catch (all) {
            log "an error occurred during bytecode weaving [ $all.message ] "
        }
        invocator.dispose()
        bundle.dispose()
    }

    TransformBundle createTransformBundle(TransformInvocation transformInvocation) {
        TransformBundle bundle = new TransformBundleImp()
        bundle.setProject(project)
        bundle.setConfiguration(project.configurations.getByName(WeaverPlugin.WEAVER_CONFIGURATION))
        //
        URLClassLoader rootClassLoader = createClassLoader(transformInvocation.referencedInputs)
        bundle.setRootClassLoader(rootClassLoader)
        //creating javassist class pool
        WeaverClassPool pool = createClassPool(rootClassLoader, transformInvocation.inputs, transformInvocation.referencedInputs)
        appendBootClassPath(pool)
        bundle.setClassPool(pool)
        //
        bundle.setClassFiles(getClassFiles(transformInvocation.inputs))
        bundle.setOutputDir(getOutputFile(transformInvocation.outputProvider))
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
            urls += path
            log "Add boot class $path to root class loader."
        }
        return new URLClassLoader(urls as URL[], Thread.currentThread().contextClassLoader)
    }


    void appendBootClassPath(WeaverClassPool classPool) {
        project.android.bootClasspath.each {
            classPool.appendClassPath(it as File)
        }
    }

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

    private File getOutputFile(TransformOutputProvider outputProvider) {
        return outputProvider.getContentLocation(
                'weaver', getInputTypes(), getScopes(), Format.DIRECTORY)
    }

    void log(String message) {
        logger.info message
    }
}