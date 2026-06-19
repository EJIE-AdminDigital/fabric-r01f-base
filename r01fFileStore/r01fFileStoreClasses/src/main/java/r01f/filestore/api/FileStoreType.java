package r01f.filestore.api;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.experimental.Accessors;
import r01f.enums.EnumWithCode;

@Accessors(prefix="_")
@RequiredArgsConstructor(access=AccessLevel.PRIVATE)
public enum FileStoreType
 implements EnumWithCode<String,FileStoreType> {
	LOCAL("local"),
	HDFS("hdfs"),
	TEAM_SITE("team-site"),
	KEY_VALUE("key-value"),
	S3("s3");

	@Getter private final String _code;
	@Getter private final Class<String> _codeType = String.class;
}
