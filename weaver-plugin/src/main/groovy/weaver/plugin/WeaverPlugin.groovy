package weaver.plugin

import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.ProjectConfigurationException

import java.lang.reflect.Method

/**
 * @author Saeed Masoumi (saeed@6thsolution.com)
 */

class WeaverPlugin implements Plugin<Project> {

    @Override
    void apply(Project project) {
        //First find the type of current project.
        PluginType pluginType = findPluginType { String it -> project.plugins.findPlugin(it) }
        //Add weaver configuration
        project.configurations.create("weaver").extendsFrom(project.configurations.compile)
        //Add weaver extension
        project.extensions.create('weaver', WeaverExtension)

        project.afterEvaluate {
            Set<File> jarFiles = project.configurations.weaver.files
            println jarFiles
            File propFile = project.zipTree(jarFiles.getAt(0)).matching {
                include 'META-INF/weaver.properties'
            }.singleFile
//
//            propFile.readLines().forEach {
//                String className = it
//                URL url = jarFile.toURI().toURL();
//                URL[] urls = [url]
//                ClassLoader classLoader = new URLClassLoader(urls);
//                Class<?> instanceClass = classLoader.loadClass(className)
//                Object instance = instanceClass.newInstance()
//                Method method = instanceClass.getDeclaredMethod("doStuff")
//                method.invoke(instance)
//                println(instance)
//            }
        }
    }

    private static PluginType findPluginType(Closure hasPlugin) {
        if (hasPlugin("com.android.application") || hasPlugin("android") ||
                hasPlugin("com.android.test")) {
            return PluginType.ANDROID
        } else if (hasPlugin("com.android.library") || hasPlugin("android-library")) {
            return PluginType.ANDROID_LIB
        } else if (hasPlugin("java")) {
            return PluginType.JAVA
        } else {
            throw new ProjectConfigurationException("The android/android-library/java plugin must be applied to the project", null)
        }
    }
}
