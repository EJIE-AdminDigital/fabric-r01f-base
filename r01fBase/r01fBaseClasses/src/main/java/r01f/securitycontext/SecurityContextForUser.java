package r01f.securitycontext;


import java.util.Optional;

import r01f.locale.Language;
import r01f.securitycontext.SecurityOIDs.UserOID;

public interface SecurityContextForUser {
	public UserOID getUserOid();
	public Optional<Language> getPrefLang();
	public String getDisplayName();
}
