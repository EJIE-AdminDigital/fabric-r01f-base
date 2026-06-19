package r01f.types.contact;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.LinkedHashSet;
import java.util.stream.Collectors;

import lombok.extern.slf4j.Slf4j;
import r01f.objectstreamer.annotations.MarshallType;
import r01f.util.types.collections.CollectionUtils;

/**
 * A collection of contact info
 */
@Slf4j
@MarshallType(as="contactInfoCollection")
public class ContactInfoCollection 
	 extends LinkedHashSet<ContactInfo> {
	
	private static final long serialVersionUID = -8284207607999587563L;
/////////////////////////////////////////////////////////////////////////////////////////
//  CONSTRUCTOR
/////////////////////////////////////////////////////////////////////////////////////////
	public ContactInfoCollection() {
		// default no-args constructor
	}
	// required by the marshaller
	public ContactInfoCollection(final int length) {
		super(length);
	}
	public ContactInfoCollection(final ContactInfo... items) {
		if (CollectionUtils.hasData(items)) this.addAll(Arrays.asList(items));
	}
	public ContactInfoCollection(final Collection<ContactInfo> items) {
		if (CollectionUtils.hasData(items)) this.addAll(items);
	}
	public static ContactInfoCollection create() {
		return new ContactInfoCollection();
	}
/////////////////////////////////////////////////////////////////////////////////////////
//  MUTATOR METHODS
/////////////////////////////////////////////////////////////////////////////////////////
	public ContactInfoCollection addDefault(final ContactInfo contact) {
		if (contact == null) return this;
		if (contact.getId() == null || contact.getId().isNOT(ContactInfoID.DEFAULT)) {
			contact.setId(ContactInfoID.DEFAULT);
		}
		return this.addContactInfo(contact);
	}
	public ContactInfoCollection addContactInfo(final ContactInfo... contacts) {
		if (CollectionUtils.hasData(contacts)) return this.addContactInfo(Arrays.asList(contacts));
		return this;
	}
	public ContactInfoCollection addContactInfo(final Collection<ContactInfo> contacts) {
		if (CollectionUtils.hasData(contacts)) this.addAll(contacts);
		return this;
	}
	public boolean remove(final ContactInfoID id) {
		if (this.size() == 0) return false;

		Collection<ContactInfo> itemsToBeRemoved = new ArrayList<>();
		// find the items to be removed
		for (ContactInfo item : this) {
			if (item.getId() != null && item.getId().is(id)) itemsToBeRemoved.add(item);
		}
		// Effectively remove the items
		boolean removed = this.removeAll(itemsToBeRemoved);
		return removed;
	}
	public boolean removeDefault() {
		return this.remove(ContactInfoID.DEFAULT);
	}
/////////////////////////////////////////////////////////////////////////////////////////
//  GET 
/////////////////////////////////////////////////////////////////////////////////////////
	public ContactInfo getWithId(final ContactInfoID id) {
		Collection<ContactInfo> outItems = this.stream()
											   .filter(item -> item.getId() != null
															&& item.getId().is(id))
											   .collect(Collectors.toList());
		if (outItems != null && outItems.size() > 1) log.warn("The contact info collection is supposed to have a SINGE item with id={}, BUT it contains {} with that id: returning ONLY one",
															  id,
															  outItems.size());
		return outItems != null && outItems.size() >= 1 ? outItems.stream()
																  .findFirst().orElse(null)
														: null;
	}
	public ContactInfo getDefault() {
		ContactInfo def = this.getWithId(ContactInfoID.DEFAULT);
		return def != null ? def
						   : this.stream()
						   		 .findFirst().orElse(null);
	}
}
