package weaver.test

/**
 * @author Saeed Masoumi (saeed@6thsolution.com)
 */

class Directories {
    public static final File BUILD_ROOT = new File(getClass().getResource("/").toURI()).parentFile.parentFile
    public static final File OUTPUT_DIR = new File(BUILD_ROOT, "functionalTest")
    public static final File CLASSES_DIR = new File(BUILD_ROOT, "resources/test/classes_dir")
}
