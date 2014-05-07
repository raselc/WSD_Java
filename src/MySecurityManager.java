import java.security.Permission;


public class MySecurityManager extends SecurityManager{
	public void checkPermission(Permission perm) {
        return;
    }
}
