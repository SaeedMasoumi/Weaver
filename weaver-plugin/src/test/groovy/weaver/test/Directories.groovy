package weaver.test

/**
 * @author Saeed Masoumi (saeed@6thsolution.com)
 */

class Directories {
    static
    final File BUILD_ROOT = new File(getClass().getResource("/").toURI()).parentFile.parentFile
    static final File OUTPUT_DIR = new File(BUILD_ROOT, "functionalTest")
    static final File PROJECT_TEMPLATE = new File(BUILD_ROOT, "../src/test/project-template")
    static final File SAMPLE_PROJECT = new File(PROJECT_TEMPLATE, "SampleProject")
    static {
        if (!OUTPUT_DIR.exists())
            OUTPUT_DIR.mkdir()
    }
}
