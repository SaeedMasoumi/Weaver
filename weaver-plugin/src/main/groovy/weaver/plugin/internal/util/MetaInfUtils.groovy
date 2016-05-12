package weaver.plugin.internal.util

import org.gradle.api.Project

/**
 * @author Saeed Masoumi (saeed@6thsolution.com)
 */
class MetaInfUtils {

    static final PROCESSORS_PROP = "META-INF/weaver/processors"

    /**
     * Retrieves processors name existing in META-INF
     */
    static List<String> extractProcessorsName(Project project, Set<File> dependencies) {
        //extract weaverProcessor
        List<String> names = new ArrayList<>()
        if (!dependencies)
            return names
        for (File it : dependencies) {
            if (!it.exists()) continue
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
