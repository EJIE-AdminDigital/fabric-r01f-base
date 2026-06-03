package r01f.file.util;

import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

import com.google.common.base.Objects;
import com.google.common.collect.Sets;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.Accessors;
import r01f.debug.Debuggable;
import r01f.file.FileProperties;
import r01f.objectstreamer.annotations.MarshallField;
import r01f.objectstreamer.annotations.MarshallField.MarshallFieldAsXml;
import r01f.objectstreamer.annotations.MarshallFrom;
import r01f.objectstreamer.annotations.MarshallType;
import r01f.securitycontext.SecurityIDS.LoginID;
import r01f.types.CanBeRepresentedAsString;
import r01f.types.Path;
import r01f.util.types.Strings;
import r01f.util.types.collections.CollectionUtils;

/**
 * {@link FolderContentReporter} diff model objects
 */
@MarshallType(as="folderContentReportDiff")
@Accessors(prefix="_")
@NoArgsConstructor @AllArgsConstructor
public class FolderContentReportDiff
  implements Serializable,
		 	 Debuggable {
	private static final long serialVersionUID = -6592740535253495605L;
	
/////////////////////////////////////////////////////////////////////////////////////////
//	FIELDS
/////////////////////////////////////////////////////////////////////////////////////////
	@MarshallField(as="newOrUpdated",
				   whenXml=@MarshallFieldAsXml(collectionElementName="newItem"))
	@Getter @Setter private Set<FolderContentReportItem> _newOrUpdated;
	
	@MarshallField(as="deleted",
				   whenXml=@MarshallFieldAsXml(collectionElementName="deletedItem"))
	@Getter @Setter private Set<FolderContentReportItem> _deleted;
	
	@MarshallField(as="untouched",
				   whenXml=@MarshallFieldAsXml(collectionElementName="untouchedItem"))
	@Getter @Setter private Set<FolderContentReportItem> _untouched;
/////////////////////////////////////////////////////////////////////////////////////////
//	CONSTRUCTOR & BUILDER
/////////////////////////////////////////////////////////////////////////////////////////
	/**
	 * Creates a diff from two reports
	 * @param r1 - old report
	 * @param r2 - current (primary) report
	 * @return
	 */
	public static FolderContentReportDiff createFrom(final Set<FolderContentReportItem> r1,final Set<FolderContentReportItem> r2) {
		Set<FolderContentReportItem> newOrUpdated = r1 != null && r2 != null ? Sets.difference(r2,r1)			// not in r1 (can be an update or a new element)
																			 : r2 != null ? Sets.newHashSet(r2)	// r1 is assumed to be null (all new)
																					 	  : null;				// r2 is assumed to be null (deleted) or both are null

																			 
		Set<FolderContentReportItem> deleted = r1 != null && r2 != null ? Sets.difference(r1,r2)				// still in r1 but NOT in r2
																		: r1 != null ? Sets.newHashSet(r1)		// r2 is assumed to be null (all deleted)
																					 : null;					// r1 is assumed to be null (new) or both are null
		// Comparison from r1 to r2 will return all elements in r1 are differen from r2:
		//  - Some FolderContet are in r1 and not in r2: those must be deleted
		//  - Some FolderContet are different: don't delete these
		if (r1 != null && r2 != null && deleted != null) {
			deleted = new HashSet<>(deleted); // You cannot use removeIf with Set returned by Sets.difference (SetView)
			for (FolderContentReportItem currentFromR2:r2) {
				deleted.removeIf(currentFromDeletedSet->(currentFromDeletedSet.getPath().is(currentFromR2.getPath())));
			}
		}
		Set<FolderContentReportItem> unchanged = r1 != null && r2 != null ? Sets.intersection(r1,r2)			// both in r1 and r2
																		  : null;
		return new FolderContentReportDiff(newOrUpdated,
										   deleted,
										   unchanged);
	}
/////////////////////////////////////////////////////////////////////////////////////////
//	
/////////////////////////////////////////////////////////////////////////////////////////
	public int getNewOrUpdatedCount() {
		return _newOrUpdated != null ? _newOrUpdated.size() : 0;
	}
	public Set<Path> getNewOrUpadtedPaths() {
		if (CollectionUtils.isNullOrEmpty(_newOrUpdated)) return Sets.newLinkedHashSet();
		return _newOrUpdated.stream()
							.map(item -> item.getPath())
							.collect(Collectors.toSet());
	}
	public int getDeletedCount() {
		return _deleted != null ? _deleted.size() : 0;
	}
	public Set<Path> getDeletedPaths() {
		if (CollectionUtils.isNullOrEmpty(_deleted)) return Sets.newLinkedHashSet();
		return _deleted.stream()
					   .map(item -> item.getPath())
					   .collect(Collectors.toSet());
	}
	public int getUntouchedCount() {
		return _untouched != null ? _untouched.size() : 0;
	}
	public Set<Path> getUntouchedPaths() {
		if (CollectionUtils.isNullOrEmpty(_untouched)) return Sets.newLinkedHashSet();
		return _untouched.stream()
						 .map(item -> item.getPath())
						 .collect(Collectors.toSet());
	}
	
	@Override
	public CharSequence debugInfo() {
		return Strings.customized("{} new or updated / {} deleted / {} unchanged",
								  _newOrUpdated != null ? _newOrUpdated.size() : 0,
								  _deleted != null ? _deleted.size() : 0,
								  _untouched != null ? _untouched.size() : 0);
	}
/////////////////////////////////////////////////////////////////////////////////////////
//	
/////////////////////////////////////////////////////////////////////////////////////////
	/**
	 * Merges this diff report with another returning a NEW diff report object
	 * @param other
	 * @return
	 */
	public FolderContentReportDiff mergedWith(final FolderContentReportDiff other) {
		if (other == null) return this;
		
		Set<FolderContentReportItem> newOrUpdated = this.getNewOrUpdated() != null && other.getNewOrUpdated() != null
														? Sets.union(this.getNewOrUpdated(),other.getNewOrUpdated())
														: this.getNewOrUpdated() != null ? this.getNewOrUpdated()
																						 : other.getNewOrUpdated();
		Set<FolderContentReportItem> deleted = this.getDeleted() != null && other.getDeleted() != null
														? Sets.union(this.getDeleted(),other.getDeleted())
														: this.getDeleted() != null ? this.getDeleted()
																					: other.getDeleted();
		Set<FolderContentReportItem> untouched = this.getUntouched() != null && other.getUntouched() != null
														? Sets.union(this.getUntouched(),other.getUntouched())
														: this.getUntouched() != null ? this.getUntouched()
																					  : other.getUntouched();
		return new FolderContentReportDiff(newOrUpdated,deleted,untouched);
	}
/////////////////////////////////////////////////////////////////////////////////////////
//	
/////////////////////////////////////////////////////////////////////////////////////////	
	@MarshallType(as="folderContentReportItem")
	@Accessors(prefix="_")
	public static class FolderContentReportItem 
			 implements CanBeRepresentedAsString {
	
		private static final long serialVersionUID = 255494264159950729L;
	
		////////// Fields
		@MarshallField(as="type",
					   whenXml=@MarshallFieldAsXml(attr=true))
		@Getter private final FolderContentReportItemType _type;
		
		@MarshallField(as="path",
					   whenXml=@MarshallFieldAsXml(attr=true))
		@Getter private final Path _path;
		
		@MarshallField(as="size",
					   whenXml=@MarshallFieldAsXml(attr=true))
		@Getter private final long _size;
		
		@MarshallField(as="owner",
					   whenXml=@MarshallFieldAsXml(attr=true))
		@Getter private final LoginID _owner;
		
		@MarshallField(as="lastUpdated",
					   whenXml=@MarshallFieldAsXml(attr=true))
		@Getter private final long _lastUpdatedTimeStamp;
		
		@MarshallField(as="created",
					   whenXml=@MarshallFieldAsXml(attr=true))
		@Getter private final long _createdTimeStamp;
		
		////////// Constructors & builders
		public FolderContentReportItem(@MarshallFrom("type") final FolderContentReportItemType type,
									   @MarshallFrom("path") final Path path,
									   @MarshallFrom("size") final long size,
									   @MarshallFrom("owner") final LoginID owner,
									   @MarshallFrom("lastUpdated") final long lastUpdatedTS,@MarshallFrom("created") final long createdTS) {
			_type = type;
			_path = path;
			_size = size;
			_owner = owner;
			_lastUpdatedTimeStamp = lastUpdatedTS;
			_createdTimeStamp = createdTS;
		}
		
		public static FolderContentReportItem from(final FileProperties props) {
			return new FolderContentReportItem(FolderContentReportItemType.of(props),
											   props.getPath(),
											   props.getSize(),
											   props.getOwner(),
											   props.getModificationTimeStamp(),
											   props.getCreateTimeStamp());
		}
		////////// Methods
		@Override
		public String asString() {
			return Strings.customized("{},{},{},{},{},{}",
									  _type != null ? _type.name() : "unknown-type",
									  _path != null ? _path.asAbsoluteString() : "unknown-path",
									  _size,
									  _owner != null ? _owner : "unknown-owner",
									  _lastUpdatedTimeStamp,
									  _createdTimeStamp);
		}
		@Override
		public String toString() {
			return this.asString();
		}
		
		@Override
		public boolean equals(final Object obj) {
			if (obj == null) return false;
			if (this == obj) return true;
			if (!(obj instanceof FolderContentReportItem other)) return false;

			boolean pathEq = this.getPath() != null && other.getPath() != null
								? this.getPath().equals(other.getPath())
								: this.getPath() != null && other.getPath() == null
										? false
										: this.getPath() == null && other.getPath() != null
												? false
												: true;		// both null
			if (!pathEq) return pathEq; // if false, return false

			boolean sizeEq = this.getSize() == other.getSize();
			if (!sizeEq) return sizeEq; // if false, return false

			boolean userEq = this.getOwner() != null && other.getOwner() != null
								? this.getOwner().equals(other.getOwner())
								: this.getOwner() != null && other.getOwner() == null
										? false
										: this.getOwner() == null && other.getOwner() != null
												? false
												: true;		// both null
			if (!userEq) return userEq; // if false, return false

			boolean lastUpdEq = this.getLastUpdatedTimeStamp() == other.getLastUpdatedTimeStamp();
			if (!lastUpdEq) return lastUpdEq; // if false, return false

			boolean createEq = this.getCreatedTimeStamp() == other.getCreatedTimeStamp();
			if (!createEq) return createEq; // if false, return false

			boolean typeEq = this.getType() != null && other.getType() != null 
					?  this.getType() == other.getType()
					: this.getType() != null && other.getType() == null 
							? false
							: this.getType() == null && other.getType() != null
									? false
									: true;		// both eqs
			return typeEq;
//			return typeEq && pathEq && sizeEq && userEq && lastUpdEq && createEq; // it is inefficient
		}
		@Override
		public int hashCode() {
			return Objects.hashCode(_type,_path,_size,_owner,_lastUpdatedTimeStamp,_createdTimeStamp);
		}
	}
/////////////////////////////////////////////////////////////////////////////////////////
//	
/////////////////////////////////////////////////////////////////////////////////////////	
	public static enum FolderContentReportItemType {
		D,	// directory
		F;	// folder
		
		public static FolderContentReportItemType of(final FileProperties props) {
			return props != null ? props.isFolder() ? D
								 					: props.isFile() ? F
								 									 : null
								 : null;
		}
	}
}

