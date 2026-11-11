package r01f.model.security;

import java.util.ArrayList;
import java.util.Collection;
import java.util.stream.Collectors;

import r01f.oids.CommonOIDs.AppComponent;
import r01f.oids.SecurityIDs.SecurityRole;
import r01f.util.types.collections.CollectionUtils;

public interface HasSystemWideRoles {
/////////////////////////////////////////////////////////////////////////////////////////
//	
/////////////////////////////////////////////////////////////////////////////////////////
	public Collection<SecurityRoleInModule> getSystemWideRoles();

	public default void setSystemWideRoles(final Collection<SecurityRoleInModule> systemWideRoles) {
		throw new UnsupportedOperationException("setSystemWideRoles is not implemented");
	}

/////////////////////////////////////////////////////////////////////////////////////////
//
/////////////////////////////////////////////////////////////////////////////////////////
	public default boolean hasAnySystemWideRoleIn(final AppComponent module) {
		return CollectionUtils.hasData(this.getSystemWideRoles()) 
					? this.getSystemWideRoles()
					      .stream()
						  .anyMatch(role -> role.getAppComponent().is(module))
					: false;
	}
	public default boolean hasSystemWideRole(final AppComponent appComp,final SecurityRole role) {
		Collection<SecurityRole> principalRoles = this.getSystemWideRolesIn(appComp);
		return CollectionUtils.hasData(principalRoles)
					? principalRoles.stream()
								  	.anyMatch(r -> r.is(role))
					: false;
	}
	public default boolean hasSystemWideRoleIn(final AppComponent module,final SecurityRole aRole) {
		return CollectionUtils.hasData(this.getSystemWideRoles()) 
					? this.getSystemWideRoles()
						  .stream()
						  .anyMatch(role -> role.getAppComponent().is(module)
								   		 && role.getRole() != null
								   		 && role.getRole().is(aRole))
					: false;
	}
////////// --- system-wide roles in a module
	public default Collection<SecurityRole> getSystemWideRolesIn(final AppComponent module) {
		Collection<SecurityRoleInModule> rolesInModule = this.getSystemWideRolesInModuleIn(module);
		return CollectionUtils.hasData(rolesInModule)
					? rolesInModule.stream()
								   .map(SecurityRoleInModule::getRole)
								   .collect(Collectors.toList())
					: null;
	}
	public default Collection<SecurityRoleInModule> getSystemWideRolesInModuleIn(final AppComponent module) {
		return CollectionUtils.hasData(this.getSystemWideRoles()) ? this.getSystemWideRoles()
																			.stream()
																			.filter(role -> role.getAppComponent().is(module))
																			.collect(Collectors.toList())
																	  : null;
	}
////////// --- add	
	public default boolean addSystemWideRoleInModule(final AppComponent module,final SecurityRole role) {
		return this.addSystemWideRoleInModule(new SecurityRoleInModule(module,role));
	}
	public default boolean addSystemWideRoleInModule(final SecurityRoleInModule roleInModule) {
		if (roleInModule == null) return false;
		if (this.getSystemWideRoles() == null) this.setSystemWideRoles(new ArrayList<>());
		return !this.getSystemWideRoles().contains(roleInModule)
						? this.getSystemWideRoles().add(roleInModule)
						: false;
	}
	
////////// --- remove	
	public default boolean removeSystemWideRolesIn(final AppComponent module) {
		Collection<SecurityRoleInModule> rolesIn = this.getSystemWideRolesInModuleIn(module);
		return rolesIn != null ? this.getSystemWideRoles()
									 .removeAll(rolesIn) 
					 	       : false;
	}
	public default boolean removeSystemWideRoleIn(final AppComponent module,final SecurityRole role) {
		Collection<SecurityRoleInModule> rolesIn = this.getSystemWideRolesInModuleIn(module);
		return rolesIn != null ? this.getSystemWideRoles()
					 			     .remove(new SecurityRoleInModule(module,role)) 
					 		   : false;
	}

}