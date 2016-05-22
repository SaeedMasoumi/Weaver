package weaver.plugin

import org.gradle.util.ConfigureUtil

/**
 * @author Saeed Masoumi (saeed@6thsolution.com)
 */

public class WeaverExtension {

    AndroidConfiguration android = new AndroidConfiguration()

    def android(Closure closure) {
        ConfigureUtil.configure(closure, android)
    }

    class AndroidConfiguration {

    }
}
