package r01f.file;

import java.io.Serializable;
import java.security.MessageDigest;
import java.time.Instant;
import java.util.HexFormat;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.Accessors;
import r01f.objectstreamer.annotations.MarshallField;
import r01f.objectstreamer.annotations.MarshallField.MarshallFieldAsXml;
import r01f.objectstreamer.annotations.MarshallType;
import r01f.types.Path;

@MarshallType(as="fileStoreExecutedActionData")
@Accessors(prefix="_")
@NoArgsConstructor @AllArgsConstructor
public class FileStoreExecutedActionData
  implements Serializable {

	private static final long serialVersionUID = 7022441579828460174L;
/////////////////////////////////////////////////////////////////////////////////////////
//
/////////////////////////////////////////////////////////////////////////////////////////
	/**
	 * Create / update / delete
	 */
	@MarshallField(as="executedAction",
				   whenXml=@MarshallFieldAsXml(attr=true))
	@Getter @Setter private FileStoreExecutedAction _executedAction;
	/**
	 * The checkout operation was successful?
	 */
	@MarshallField(as="success",
				   whenXml=@MarshallFieldAsXml(attr=true))
	@Getter @Setter private boolean _success;
	/**
	 * Timestamp when the checkout was done (finished)
	 */
	@MarshallField(as="date",
				   whenXml=@MarshallFieldAsXml(attr=true))
	@Getter @Setter private Instant _date;
	/**
	 * The resource path
	 */
	@MarshallField(as="path")
	@Getter @Setter private Path _path;
	/**
	 * The file digest/hash (ie md5) (only if success)
	 */
	@MarshallField(as="digest")	// do NOT escape
	@Getter @Setter private String _digest;
	/**
	 * Any detail about the checkout process
	 */
	@MarshallField(as="details")
	@Getter @Setter private String _details;
/////////////////////////////////////////////////////////////////////////////////////////
//  CONSTRUCTOR & BUILDER
/////////////////////////////////////////////////////////////////////////////////////////
	public static FileStoreExecutedActionData successful(final FileStoreExecutedAction action,
											  			 final Path path,
											  			 final Instant date,
											  			 final MessageDigest digest) {
		return new FileStoreExecutedActionData(action,
											   true,							// success
											   date,
											   path,
											   HexFormat.of()
											   			.formatHex(digest.digest()),
											   null);	// no details
	}
	public static FileStoreExecutedActionData successfulUpdated(final Path path,
													 			final Instant date,
													 			final MessageDigest digest) {
		return new FileStoreExecutedActionData(FileStoreExecutedAction.UPDATED,
											   true,							// success
											   date,
											   path,
											   HexFormat.of()
											   			.formatHex(digest.digest()),
											   null);	// no details
	}
	public static FileStoreExecutedActionData successfulCreated(final Path path,
													 			final Instant date,
													 			final MessageDigest digest) {
		return new FileStoreExecutedActionData(FileStoreExecutedAction.CREATED,
											   true,							// success
											   date,
											   path,
											   HexFormat.of()
											   			.formatHex(digest.digest()),
											   null);	// no details
	}
	public static FileStoreExecutedActionData successfulDeleted(final Path path,
													 			final Instant date) {
		return new FileStoreExecutedActionData(FileStoreExecutedAction.DELETED,
											   true,							// success
											   date,
											   path,
											   null,	// no digest = delete
											   null);	// no details
	}
	public static FileStoreExecutedActionData failed(final Instant date,
										  			 final Throwable th) {
		return new FileStoreExecutedActionData(null,		// unknown action
											   false,		// fail
											   date,
											   null,		// unknown path
											   null,		// no digest
											   th.getMessage());
	}
	public static FileStoreExecutedActionData failed(final FileStoreExecutedAction action,
										  			 final Path path,
										  			 final Instant date,
										  			 final Throwable th) {
		return new FileStoreExecutedActionData(action,
											   false,		// fail
											   date,
											   path,
											   null,		// no digest
											   th.getMessage());
	}
	public static FileStoreExecutedActionData failedUpdate(final Path path,
														   final Instant date,
														   final Throwable th) {
		return new FileStoreExecutedActionData(FileStoreExecutedAction.UPDATED,
									false,		// fail
									date,
									path,
									null,		// no digest
									th.getMessage());
	}
	public static FileStoreExecutedActionData failedCreation(final Path path,
												  			 final Instant date,
												  			 final Throwable th) {
		return new FileStoreExecutedActionData(FileStoreExecutedAction.CREATED,
									true,		// fail
									date,
									path,
									null,		// no digest
									th.getMessage());
	}
	public static FileStoreExecutedActionData failedDeletion(final Path path,
												  			 final Instant date,
												  			 final Throwable th) {
		return new FileStoreExecutedActionData(FileStoreExecutedAction.DELETED,
											   true,	// fail
											   date,
											   path,
											   null,	// no digest
											   th.getMessage());
	}
	public static FileStoreExecutedActionData failedDeletion(final Path path,
												  			 final Instant date) {
		return new FileStoreExecutedActionData(FileStoreExecutedAction.DELETED,
											   true,	// fail
											   date,
											   path,
											   null,	// no digest
											   null);	// no message
	}
/////////////////////////////////////////////////////////////////////////////////////////
//
/////////////////////////////////////////////////////////////////////////////////////////
}
