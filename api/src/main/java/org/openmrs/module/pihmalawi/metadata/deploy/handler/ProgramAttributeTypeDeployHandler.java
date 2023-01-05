package org.openmrs.module.pihmalawi.metadata.deploy.handler;

import org.openmrs.ProgramAttributeType;
import org.openmrs.annotation.Handler;
import org.openmrs.api.ProgramWorkflowService;
import org.openmrs.module.metadatadeploy.handler.AbstractObjectDeployHandler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

@Handler(supports = {ProgramAttributeType.class})
public class ProgramAttributeTypeDeployHandler extends AbstractObjectDeployHandler<ProgramAttributeType> {

    @Autowired
    @Qualifier("programWorkflowService")
    private ProgramWorkflowService programWorkflowService;

    @Override
    public ProgramAttributeType fetch(String s) {
        return programWorkflowService.getProgramAttributeTypeByUuid(s);
    }

    @Override
    public ProgramAttributeType save(ProgramAttributeType programAttributeType) {
        return programWorkflowService.saveProgramAttributeType(programAttributeType);
    }

    @Override
    public void uninstall(ProgramAttributeType programAttributeType, String s) {
        programWorkflowService.purgeProgramAttributeType(programAttributeType);
    }
}
