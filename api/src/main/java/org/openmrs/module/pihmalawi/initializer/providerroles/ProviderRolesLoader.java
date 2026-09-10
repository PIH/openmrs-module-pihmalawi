package org.openmrs.module.pihmalawi.initializer.providerroles;

import org.openmrs.ProviderRole;
import org.openmrs.module.initializer.api.loaders.BaseCsvLoader;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component("pihmalawiProviderRolesLoader")
public class ProviderRolesLoader extends BaseCsvLoader<ProviderRole, ProviderRolesCsvParser> {

	@Autowired
	public void setParser(ProviderRolesCsvParser parser) {
		this.parser = parser;
	}
}
