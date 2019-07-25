package org.openmrs.module.pihmalawi.reporting.definition.data.converter;

import org.openmrs.module.pihmalawi.common.ArtRegimen;
import org.openmrs.module.reporting.data.converter.DataConverter;
import org.openmrs.module.pihmalawi.metadata.CommonMetadata;

import java.util.ArrayList;
import java.util.List;

public class ArvLineRegimenChangeConverter implements DataConverter {

    public ArvLineRegimenChangeConverter() {
    }

    @Override
    public Object convert(Object original) {
        List<ArtRegimen> ret = null;
        if (original != null) {
            ret = new ArrayList<ArtRegimen>();
            List<ArtRegimen> fullList = (List<ArtRegimen>) original;
            ArtRegimen last = null;
            for (ArtRegimen r : fullList) {
                if (r != null && r.getRegimen() != null) {
                    if (last == null ) {
                        ret.add(r);
                    } else {
                        int lineRegimen = CommonMetadata.getArvLineRegimen(r.getRegimen().getUuid());
                        if (lineRegimen == -1 ) {
                            // no regimen was specified
                            continue;
                        }
                        int lastLineRegimen = CommonMetadata.getArvLineRegimen(last.getRegimen().getUuid());
                        if ( lineRegimen == 0 ||  lineRegimen != lastLineRegimen ) {
                            ret.add(r);
                        }
                    }
                    last = r;
                }
            }
        }
        return ret;
    }

    @Override
    public Class<?> getInputDataType() {
        return List.class;
    }

    @Override
    public Class<?> getDataType() {
        return List.class;
    }
}
