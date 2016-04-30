package weaver.plugin.internal.util

import com.android.build.api.transform.TransformException
import org.gradle.api.Project

/**
 * @author Saeed Masoumi (saeed@6thsolution.com)
 */
class MetaInfUtils {

    static final PROCESSORS_PROP = "META-INF/weaver/processors"

    static List<String> extractProcessorsName(Project project, Set<File> dependencies) {
        if (!dependencies) {
            throw new TransformException("TransformerTask ignored [No weaver processor specified]");
        }
        //extract weaverProcessor
        def names = []
        for (File it : dependencies) {
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
}
