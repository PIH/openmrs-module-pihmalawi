package org.openmrs.module.pihmalawi.location;

import org.junit.Assert;
import org.junit.Test;
import org.openmrs.test.BaseModuleContextSensitiveTest;
import org.springframework.beans.factory.annotation.Autowired;

public class LocationUuidHandlerTests extends BaseModuleContextSensitiveTest {
    @Autowired
    LocationUuidHandler locationUuidHandler;
    @Test
    public void getNenoDhoLocationUuid_CorrectLocationName_ReturnLocationUuid()
    {
        String location = "Neno District Hospital";
        String expectedUuid = "0d414ce2-5ab4-11e0-870c-9f6107fee88e";
        String uuid = locationUuidHandler.getLocationUiidByLocationName(location);
        Assert.assertEquals(expectedUuid,uuid);
    }
    @Test
    public void getDambeLocationUuid_CorrectLocationName_ReturnLocationUuid()
    {
        String location = "dambe clinic";
        String expectedUuid = "976dcd06-c40e-4e2e-a0de-35a54c7a52ef";
        String uuid = locationUuidHandler.getLocationUiidByLocationName(location);
        Assert.assertEquals(expectedUuid,uuid);
    }
    @Test
    public void getLigoweLocationUuid_CorrectLocationName_ReturnLocationUuid()
    {
        String location = "Ligowe HC";
        String expectedUuid = "0d417e38-5ab4-11e0-870c-9f6107fee88e";
        String uuid = locationUuidHandler.getLocationUiidByLocationName(location);
        Assert.assertEquals(expectedUuid,uuid);
    }
    @Test
    public void getLuwanuRHCLocationUuid_CorrectLocationName_ReturnLocationUuid()
    {
        String location = "luwani rhc";
        String expectedUuid = "0d416506-5ab4-11e0-870c-9f6107fee88e";
        String uuid = locationUuidHandler.getLocationUiidByLocationName(location);
        Assert.assertEquals(expectedUuid,uuid);
    }
    @Test
    public void getMagaletaLocationUuid_CorrectLocationName_ReturnLocationUuid()
    {
        String location = "Magaleta HC";
        String expectedUuid = "0d414eae-5ab4-11e0-870c-9f6107fee88e";
        String uuid = locationUuidHandler.getLocationUiidByLocationName(location);
        Assert.assertEquals(expectedUuid,uuid);
    }
    @Test
    public void getMatandaniRuralHealthCenterLocationUuid_CorrectLocationName_ReturnLocationUuid()
    {
        String location = "Matandani Rural Health Center";
        String expectedUuid = "0d415200-5ab4-11e0-870c-9f6107fee88e";
        String uuid = locationUuidHandler.getLocationUiidByLocationName(location);
        Assert.assertEquals(expectedUuid,uuid);
    }
    @Test
    public void getNenoInwardPatientsLocationUuid_CorrectLocationName_ReturnLocationUuid()
    {
        String location = "Neno inward patients";
        String expectedUuid = "985193ce-761a-4011-9d3e-24ddf61eba0f";
        String uuid = locationUuidHandler.getLocationUiidByLocationName(location);
        Assert.assertEquals(expectedUuid,uuid);
    }
    @Test
    public void getNsambeHCLocationUuid_CorrectLocationName_ReturnLocationUuid()
    {
        String location = "Nsambe HC";
        String expectedUuid = "0d416830-5ab4-11e0-870c-9f6107fee88e";
        String uuid = locationUuidHandler.getLocationUiidByLocationName(location);
        Assert.assertEquals(expectedUuid,uuid);
    }
    @Test
    public void getUnknownLocationUuid_CorrectLocationName_ReturnLocationUuid()
    {
        String location = "Unknown Location";
        String expectedUuid = "8d6c993e-c2cc-11de-8d13-0010c6dffd0f";
        String uuid = locationUuidHandler.getLocationUiidByLocationName(location);
        Assert.assertEquals(expectedUuid,uuid);
    }
}
