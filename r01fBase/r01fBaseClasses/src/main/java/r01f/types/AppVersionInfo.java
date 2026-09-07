package r01f.types;

import lombok.Getter;

public record AppVersionInfo(@Getter int major,
							 @Getter int minor,
							 @Getter int patch,
							 @Getter String alias) {
}
