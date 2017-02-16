package org.openmrs.module.pihmalawi.provider;


import org.openmrs.module.emrapi.account.ProviderIdentifierGenerator;
import org.openmrs.module.idgen.SequentialIdentifierGenerator;
import org.openmrs.module.providermanagement.Provider;
import org.springframework.stereotype.Component;

@Component   /// this is autowired in the DomainWrapperFactory (which injects it into Account Domain Wrapper instances)
public class VhwProviderIdentifierGenerator implements ProviderIdentifierGenerator {


    @Override
    public String generateIdentifier(Provider provider) {

        if (provider.getId() == null) {
            throw new IllegalStateException("Cannot generate identifier for provider without primary key");
        }

        SequentialIdentifierGenerator generator = new SequentialIdentifierGenerator();
        generator.setBaseCharacterSet("0123456789");
        generator.setFirstIdentifierBase("00001");
        generator.setPrefix("VHW-");
        generator.setMaxLength(10);
        generator.setMinLength(5);

        String identifier = generator.getIdentifierForSeed(provider.getId().longValue());

        return identifier;
    }

}
