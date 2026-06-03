package r01f.file;

import java.io.Serializable;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.google.common.base.Objects;

import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;
import r01f.debug.Debuggable;
import r01f.objectstreamer.annotations.MarshallField;
import r01f.objectstreamer.annotations.MarshallField.MarshallFieldAsXml;
import r01f.objectstreamer.annotations.MarshallType;
import r01f.util.types.Strings;

@MarshallType(as="filePermission")
@Accessors(prefix="_")
public class FilePermission
  implements Serializable,
  			 Debuggable {

	private static final long serialVersionUID = 3871233841722811189L;
/////////////////////////////////////////////////////////////////////////////////////////
//	POSIX permission style
/////////////////////////////////////////////////////////////////////////////////////////
	@MarshallField(as="user",whenXml=@MarshallFieldAsXml(attr=true))
	@Getter @Setter private FileAction _userAction = null;

	@MarshallField(as="group",whenXml=@MarshallFieldAsXml(attr=true))
	@Getter @Setter private FileAction _groupAction = null;

	@MarshallField(as="other",whenXml=@MarshallFieldAsXml(attr=true))
	@Getter @Setter private FileAction _otherAction = null;

	@MarshallField(as="sticky",whenXml=@MarshallFieldAsXml(attr=true))
	@Getter @Setter private boolean _stickyBit = false;
/////////////////////////////////////////////////////////////////////////////////////////
//	CONSTRUCTOR
/////////////////////////////////////////////////////////////////////////////////////////
	public FilePermission() {
		// default constructor
	}
	private static final Pattern UNIX_PERMISSION_PATTERN = Pattern.compile("([rwx-]{3})([rwx-]{3})([rwx-]{3})");
	public FilePermission(final FileAction userAction,
						  final FileAction groupAction,
						  final FileAction otherAction,
						  final boolean stickyBit) {
		_userAction = userAction;
		_groupAction = groupAction;
		_otherAction = otherAction;
		_stickyBit = stickyBit;
	}
	public FilePermission(final String unixPermissionStr) {
		Matcher m = UNIX_PERMISSION_PATTERN.matcher(unixPermissionStr);
		if (m.find()) {
			_userAction = FileAction.fromUnix(m.group(1));
			_groupAction = FileAction.fromUnix(m.group(2));
			_otherAction = FileAction.fromUnix(m.group(3));
			_stickyBit = false;
		} else {
			throw new IllegalArgumentException(Strings.customized("The string {} is not a valid unix-like permission string (user: rwx, group: rwx, other: rwx)",
																   unixPermissionStr));
		}
	}
/////////////////////////////////////////////////////////////////////////////////////////
//
/////////////////////////////////////////////////////////////////////////////////////////
	/**
	 * Creates a {@link FilePermission} object from a unix-like permission string
	 * as rw-rw-rwx
	 * @param str
	 * @return
	 */
	public static FilePermission createFromUNIXPermissionString(final String str) {
		return new FilePermission(str);
	}
/////////////////////////////////////////////////////////////////////////////////////////
//	EQUALS & HASHCODE
/////////////////////////////////////////////////////////////////////////////////////////
	@Override
	public boolean equals(final Object other) {
		if (other == null) return false;
		if (this == other) return true;
		if (!(other instanceof FilePermission) ) return false;

		FilePermission otherPerm = (FilePermission)other;
		return this.getUserAction() == otherPerm.getUserAction()
			&& this.getGroupAction() == otherPerm.getGroupAction()
			&& this.getOtherAction() == otherPerm.getOtherAction()
			&& this.isStickyBit() == otherPerm.isStickyBit();
	}
	@Override
	public int hashCode() {
		return Objects.hashCode(this);
	}
/////////////////////////////////////////////////////////////////////////////////////////
//
/////////////////////////////////////////////////////////////////////////////////////////
	@Override
	public CharSequence debugInfo() {
		return Strings.customized("user: {} / group: {} / other: {} / sticky: {}",
								  _userAction,_groupAction,_otherAction,
								  _stickyBit);
	}
}
