package org.openmrs.module.pihmalawi.rest.controller;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openmrs.api.context.Context;
import org.openmrs.module.pihmalawi.initializer.ConceptExporter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class InitializerExportRestController {
    protected Log log = LogFactory.getLog(getClass());

    @Autowired
    ConceptExporter conceptExporter;

    @GetMapping(value = "/rest/v1/pihmalawi/initializer/export")
    public ResponseEntity<byte[]> export(@RequestHeader HttpHeaders headers) {

        if (!Context.isAuthenticated()) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(HttpStatus.UNAUTHORIZED.getReasonPhrase().getBytes());
        }

        try {
            byte[] conceptExport = conceptExporter.getConceptExport();
            return ResponseEntity.ok()
                    .contentType(MediaType.parseMediaType("application/octet-stream"))
                    .header("Content-Disposition", "attachment; filename=\"initializer-export.zip\"")
                    .body(conceptExport);
        }
        catch (Exception e) {
            log.error("Error generating initializer export", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage().getBytes());
        }
    }
}
