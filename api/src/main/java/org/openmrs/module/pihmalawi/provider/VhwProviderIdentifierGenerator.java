package org.openmrs.module.pihmalawi.provider;

import org.apache.commons.lang.StringUtils;
import org.openmrs.Location;
import org.openmrs.LocationAttribute;
import org.openmrs.Provider;
import org.openmrs.ProviderAttribute;
import org.openmrs.api.context.Context;
import org.openmrs.module.emrapi.account.ProviderIdentifierGenerator;
import org.openmrs.module.idgen.SequentialIdentifierGenerator;
import org.openmrs.module.pihmalawi.metadata.LocationAttributeTypes;
import org.openmrs.module.pihmalawi.metadata.deploy.bundle.ProviderAttributeTypeBundle;
import org.springframework.stereotype.Component;

import java.util.Set;


@Component   /// this is autowired in the DomainWrapperFactory (which injects it into Account Domain Wrapper instances)
public class VhwProviderIdentifierGenerator implements ProviderIdentifierGenerator {


    @Override
    public String generateIdentifier(Provider provider) {

        if (provider.getId() == null) {
            throw new IllegalStateException("Cannot generate identifier for provider without primary key");
        }

        SequentialIdentifierGenerator generator = new SequentialIdentifierGenerator();
        generator.setBaseCharacterSet("0123456789");
        generator.setFirstIdentifierBase("1");

        String locationCode = getLocationCode(provider);
        if (StringUtils.isNotBlank(locationCode)) {
            generator.setPrefix(locationCode + " ");
        }
        generator.setSuffix(" CHW");
        generator.setMaxLength(18);
        generator.setMinLength(5);

        String identifier = generator.getIdentifierForSeed(provider.getId().longValue());

        return identifier;
    }

    String getLocationCode(Provider provider) {
        String locationCode = null;
        String locationUuid = null;
        String healthFacility = ProviderAttributeTypeBundle.ProviderAttributeTypes.HEALTH_FACILITY;
        Set<ProviderAttribute> providerAttributes = provider.getAttributes();
        for (ProviderAttribute providerAttribute : providerAttributes) {
            if (StringUtils.equals(providerAttribute.getAttributeType().getUuid(), healthFacility)) {
                locationUuid = providerAttribute.getValueReference();
                break;
            }
        }
        if (StringUtils.isNotBlank(locationUuid)) {
            Location healthLocation = Context.getLocationService().getLocationByUuid(locationUuid);
            if (healthLocation != null) {
                Set<LocationAttribute> attributes = healthLocation.getAttributes();
                for (LocationAttribute attribute : attributes) {
                    if (StringUtils.equals(attribute.getAttributeType().getUuid(), LocationAttributeTypes.LOCATION_CODE.uuid())) {
                        locationCode = attribute.getValueReference();
                        break;
                    }
                }
            }

        }
        return locationCode;

    }

}
