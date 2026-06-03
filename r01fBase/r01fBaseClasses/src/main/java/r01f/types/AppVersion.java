package r01f.types;

import java.util.Comparator;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
import lombok.experimental.Accessors;
import r01f.annotations.Immutable;
import r01f.guids.OID;
import r01f.guids.OIDBaseMutable;
import r01f.guids.VersionID;
import r01f.objectstreamer.annotations.MarshallType;
import r01f.patterns.Memoized;
import r01f.util.types.Strings;

@Immutable
@MarshallType(as="version")
@Accessors(prefix="_")
@NoArgsConstructor
public class AppVersion 
	 extends OIDBaseMutable<String>
  implements VersionID {

	private static final long serialVersionUID = 3935180518462516439L;
/////////////////////////////////////////////////////////////////////////////////////////
//  FIELDS
/////////////////////////////////////////////////////////////////////////////////////////
	private transient final Memoized<AppVersionInfo> _info = Memoized.using(() -> {
																	if (Strings.isNullOrEmpty(this.getId())) {
																		throw new IllegalArgumentException("NULL is NOT a valid version string");
																	}
																	AppVersionInfo outInfo = null;
																	Matcher m = APP_VERSION_PATTERN.matcher(this.getId());
																	if (m.find()) {
																		int major = Integer.parseInt(m.group(1));
																		int minor = Integer.parseInt(m.group(2));
																		int patch = Integer.parseInt(m.group(3));
																		String alias = m.groupCount() == 4 ? m.group(4) : null;
																		
																		outInfo = new AppVersionInfo(major,minor,patch,alias);
																	} else {
																		// throw new IllegalArgumentException(this.getId() + " is NOT a valid version strign (does NOT match " + APP_VERSION_PATTERN);
																		// do nothing... info will be null
																	}
																	return outInfo;
															 });
	@Accessors(prefix="_")
	@RequiredArgsConstructor
	public class AppVersionInfo {
		@Getter private final int _major;
		@Getter private final int _minor;
		@Getter private final int _patch;
		@Getter private final String _alias;
	}
/////////////////////////////////////////////////////////////////////////////////////////
//  BUILDER
/////////////////////////////////////////////////////////////////////////////////////////
	public AppVersion(final String appVersionStr) {
		super(appVersionStr);
	}
	public static AppVersion fromString(final String appVersionStr) {
		return AppVersion.valueOf(appVersionStr);
	}
	public static AppVersion from(final String appVersionStr) {
		return AppVersion.valueOf(appVersionStr);
	}
	public static AppVersion valueOf(final String appVersionStr) {
		return new DefaultAppVersionParser()
						.parse(appVersionStr);
	}
	public static boolean canBe(final String appVersionStr) {
		return new DefaultAppVersionParser()
						.canBe(appVersionStr);
	}
	public AppVersion(final int major,final int minor,final int patch) {
		this(major,minor,patch,
			 null);
	}
	public AppVersion(final int major,final int minor,final int patch,
					  final String alias) {
		super(Strings.isNOTNullOrEmpty(alias) ? Strings.customized("{}.{}.{}-{}",
																		  major,minor,patch,alias)
											  : Strings.customized("{}.{}.{}",
															 	   major,minor,patch));
	}
/////////////////////////////////////////////////////////////////////////////////////////
// 	PARSING
/////////////////////////////////////////////////////////////////////////////////////////
	private static final transient Pattern APP_VERSION_PATTERN = Pattern.compile("([0-9]+).([0-9]+).([0-9]+)(-[0-9a-zA-Z_])?");
	
	public interface AppVersionParser {
		public boolean canBe(final String appVersionStr);
		public AppVersion parse(final String appVersionStr);		
	}
	public static class DefaultAppVersionParser 
			 implements AppVersionParser {
		
		@Override
		public boolean canBe(final String appVersionStr) {
			if (Strings.isNullOrEmpty(appVersionStr)) {
				return false;
			}
			return APP_VERSION_PATTERN.matcher(appVersionStr).find();
		}
		@Override
		public AppVersion parse(final String appVersionStr) {
			AppVersion outAppVersion = new AppVersion(appVersionStr);
			return outAppVersion;
		}
	}
/////////////////////////////////////////////////////////////////////////////////////////
//	INFO ACCESS
/////////////////////////////////////////////////////////////////////////////////////////
	public int getMajor() {
		if (_info.get() == null) {
			throw new IllegalStateException(this.getId() + " does NOT match " + APP_VERSION_PATTERN + ": AppVersion#getMajor() cannot be used!");
		}
		return _info.get().getMajor();
	}
	public int getMinor() {
		if (_info.get() == null) {
			throw new IllegalStateException(this.getId() + " does NOT match " + APP_VERSION_PATTERN + ": AppVersion#getMinor() cannot be used!");
		}
		return _info.get().getMinor();
	}
	public int getPatch() {
		if (_info.get() == null) {
			throw new IllegalStateException(this.getId() + " does NOT match " + APP_VERSION_PATTERN + ": AppVersion#getPatch() cannot be used!");
		}
		return _info.get().getPatch();
	}
	public String getAlias() {
		if (_info.get() == null) {
			throw new IllegalStateException(this.getId() + " does NOT match " + APP_VERSION_PATTERN + ": AppVersion#getAlias() cannot be used!");
		}
		return _info.get().getAlias();
	}
/////////////////////////////////////////////////////////////////////////////////////////
//  
/////////////////////////////////////////////////////////////////////////////////////////
	public boolean hasSameMajorVersion(final AppVersion other) {
		if (other == null) {
			return false;
		}
		
		return this.getMajor() == other.getMajor();
	}
	public boolean hasGreaterMajorVersion(final AppVersion other) {
		return this.getMajor() > other.getMajor();
	}
	public boolean hasSameMinorVersion(final AppVersion other) {
		if (other == null) {
			return false;
		}
		
		return this.getMinor() == other.getMinor();
	}
	public boolean hasGreaterMinorVersion(final AppVersion other) {
		if (other == null) {
			return true;
		}
		
		return this.getMinor() > other.getMinor();
	}
	public boolean hasSamePatchLevel(final AppVersion other) {
		if (other == null) {
			return false;
		}
		
		return this.getPatch() == other.getPatch();
	}
	public boolean hasGreaterPathLevel(final AppVersion other) {
		if (other == null) {
			return true;
		}
		
		return this.getPatch() > other.getPatch();
	}
	public boolean hasSameAlias(final AppVersion other) {
		if (other == null) {
			return false;
		}
		
		return this.getAlias() != null ? this.getAlias().equals(other.getAlias())
									   : other.getAlias() != null ? false
											  					  : true;	// both null
	}
	public boolean isSameAs(final AppVersion other) {
		if (other == null) {
			return false;
		}
		
		if (_info.get() == null) {
			// a not-structured version > compare just the string
			return this.getId() == null ? other.getId() == null ? true	// both "null"
																: false	// this.getId() == null && other.getId() != null
										: this.getId().equals(other.getId());
		}
		return this.hasSameMajorVersion(other)
			&& this.hasSameMinorVersion(other)
			&& this.hasSamePatchLevel(other);
	}
	public boolean isGreaterThan(final AppVersion other) {
		if (other == null) {
			return true;
		}
		
		if (_info.get() == null) {
			return this.getId() == null ? other.getId() == null ? false	// both "null" = equals
																: false // this.getId() == null && other.getId() != null > noth greather
										: this.getId().compareTo(other.getId()) > 0 ? true
																					: false;										
		}
		
		if (this.hasGreaterMajorVersion(other)) {
			return true;
		}
		if (this.hasSameMajorVersion(other) 
		 && this.hasGreaterMinorVersion(other)) {
			return true;
		}
		if (this.hasSameMajorVersion(other)
		 && this.hasSameMinorVersion(other)
		 && this.hasGreaterPathLevel(other)) {
			return true;
		}
		return false;
	}
/////////////////////////////////////////////////////////////////////////////////////////
//	COMPARATOR
/////////////////////////////////////////////////////////////////////////////////////////
	public static transient Comparator<AppVersion> COMPARATOR = _createCompartor();
	
	protected static <V extends AppVersion> Comparator<V> _createCompartor() {
		return (v1, v2) -> {
					if (v1.isSameAs(v2)) {
						return 0;
					}
					if (v1.isGreaterThan(v2)) {
						return 1;
					}
					return -1;
				};
	}
/////////////////////////////////////////////////////////////////////////////////////////
//	VersionID
/////////////////////////////////////////////////////////////////////////////////////////	
	@Override
	public boolean isValid() {
		return super.isValid() && AppVersion.canBe(this.asString());
	}
	@Override
	public int compareTo(final OID o) {
		if (!(o instanceof AppVersion ov)) {
			return -1;
		}
		return COMPARATOR.compare(this,ov);
	}
}
