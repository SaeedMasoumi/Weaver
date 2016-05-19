package weaver.test

/**
 * @author Saeed Masoumi (saeed@6thsolution.com)
 */

class Directories {
    static final File BUILD_ROOT = new File(getClass().getResource("/").toURI()).parentFile.parentFile
    static final File OUTPUT_DIR = new File(BUILD_ROOT, "functionalTest")
    static final File SAMPLE_PROJECT = new File(BUILD_ROOT, "resources/test/SampleProject")
    static final File SAMPLE_ANDROID_PROJECT = new File(BUILD_ROOT, "resources/test/SampleAndroidProject")
    static {
        if (!OUTPUT_DIR.exists())
            OUTPUT_DIR.mkdir()
    }
}
