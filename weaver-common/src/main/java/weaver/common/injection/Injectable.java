package weaver.common.injection;

/**
 * @author Saeed Masoumi (saeed@6thsolution.com)
 */
public interface Injectable<Parent> {
    Parent inject() throws Exception;
}
