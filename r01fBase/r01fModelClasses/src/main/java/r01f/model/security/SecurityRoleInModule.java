package r01f.model.security;

import java.util.stream.Stream;

import lombok.Getter;
import r01f.aspects.interfaces.dirtytrack.ConvertToDirtyStateTrackable;
import r01f.debug.Debuggable;
import r01f.objectstreamer.annotations.MarshallField;
import r01f.objectstreamer.annotations.MarshallField.MarshallFieldAsXml;
import r01f.objectstreamer.annotations.MarshallType;
import r01f.oids.CommonOIDs.AppComponent;
import r01f.oids.SecurityIDs.SecurityRole;
import r01f.types.CanBeRepresentedAsString;
import r01f.util.types.Strings;
import r01f.util.types.collections.CollectionUtils;

@MarshallType(as="securityRoleInModule")
@ConvertToDirtyStateTrackable
public record SecurityRoleInModule(@MarshallField(as="appComponent",
				   							  	  whenXml=@MarshallFieldAsXml(attr=true))
							   	   @Getter AppComponent appComponent,
	
								   @MarshallField(as="role",
										   		  whenXml=@MarshallFieldAsXml(attr=true))
								   @Getter SecurityRole role)		 
   implements CanBeRepresentedAsString,
  			  Debuggable {
/////////////////////////////////////////////////////////////////////////////////////////
//	
/////////////////////////////////////////////////////////////////////////////////////////	
	@Override
	public String asString() {
		return Strings.customized("{}:{}",
								  this.appComponent,this.role);
	}
/////////////////////////////////////////////////////////////////////////////////////////
//	IS
/////////////////////////////////////////////////////////////////////////////////////////
	public boolean is(final AppComponent appComp,final SecurityRole role) {
		return this.appComponent.is(appComp)
			&& this.role.is(role);
	}
	public boolean is(final AppComponent appComp) {
		return this.appComponent.is(appComp);
	}
	public boolean is(final SecurityRoleInModule aRole) {
		return this.equals(aRole);
	}
	public boolean isNOT(final SecurityRoleInModule aRole) {
		return !this.is(aRole);
	}
	public boolean isContainedIn(final SecurityRoleInModule... roles) {
		if (CollectionUtils.isNullOrEmpty(roles)) return false;
		return Stream.of(roles)
					 .anyMatch(aRole -> aRole.is(this));
	}
	public boolean isNOTContainedIn(final SecurityRoleInModule... roles) {
		return !this.isContainedIn(roles);
	}
/////////////////////////////////////////////////////////////////////////////////////////
//	DEBUG
/////////////////////////////////////////////////////////////////////////////////////////	
	@Override
	public CharSequence debugInfo() {
		return this.asString();
	}
}
