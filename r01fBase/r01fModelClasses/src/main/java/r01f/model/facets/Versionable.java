package r01f.model.facets;


import java.time.Instant;

import r01f.guids.VersionIndependentOID;
import r01f.guids.VersionOID;
import r01f.model.VersionInfo;

public interface Versionable {
/////////////////////////////////////////////////////////////////////////////////////////
//  HasVersionableFacet
/////////////////////////////////////////////////////////////////////////////////////////
	public static interface HasVersionableFacet
				    extends ModelObjectFacet {

		public Versionable asVersionable();

		public VersionInfo getVersionInfo();
		public void setVersionInfo(VersionInfo versionInfo);
	}
/////////////////////////////////////////////////////////////////////////////////////////
//
/////////////////////////////////////////////////////////////////////////////////////////
	public VersionIndependentOID getVersionIndependentOid();
	public VersionOID getVersionOid();

	public Instant getStartOfUseDate();
	public void setStartOfUseDate(Instant date);

	public Instant getEndOfUseDate();
	public void setEndOfUseDate(Instant date);

	public VersionOID getNextVersion();
	public void setNextVersion(VersionOID nextVersion);
	/**
	 * @return true if version is the current version in use
	 */
	public boolean isActive();
	/**
	 * @return true the version is not the current version in use
	 */
	public boolean isNotActive();
	/**
	 * @return the version is not currently activated (another version is the current version until this
	 * 		   version is finished)
	 */
	public boolean isDraft();
	/**
	 * Activates the version
	 * @param activationDate
	 */
	public void activate(Instant activationDate);
	/**
	 * Overrides this version with another which becomes the NEXT version
	 * @param otherVersion
	 * @param otherVersionStartOfUseDate
	 */
	public void overrideBy(VersionOID otherVersion,
    					   Instant otherVersionStartOfUseDate);
}
