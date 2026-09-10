package org.openmrs.module.pihmalawi.initializer.providerroles;

import org.openmrs.ProviderRole;
import org.openmrs.module.initializer.api.BaseLineProcessor;
import org.openmrs.module.initializer.api.CsvLine;
import org.springframework.stereotype.Component;

@Component("pihmalawiProviderRolesLineParser")
public class ProviderRolesLineProcessor extends BaseLineProcessor<ProviderRole> {

	public ProviderRolesLineProcessor() {
	}

	@Override
	public ProviderRole fill(ProviderRole role, CsvLine line) throws IllegalArgumentException {
		role.setName(line.getName(true));
		role.setDescription(line.get(HEADER_DESC));
		return role;
	}
}
