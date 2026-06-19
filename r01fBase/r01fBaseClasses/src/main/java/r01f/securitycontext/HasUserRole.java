package r01f.securitycontext;

import r01f.securitycontext.SecurityIDS.UserRole;

public interface HasUserRole {
	public UserRole getUserRole();
	public default void setUserRole(final UserRole role) {
		throw new UnsupportedOperationException();
	}
}
