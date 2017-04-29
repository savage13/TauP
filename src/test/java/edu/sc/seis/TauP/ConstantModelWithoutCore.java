package edu.sc.seis.TauP;

import java.util.ArrayList;
import java.util.List;

import org.junit.Test;

import static org.junit.Assert.*;


public class ConstantModelWithoutCore {

    public ConstantModelWithoutCore() throws Exception {
        vmod = VelocityModelTest.loadTestVelMod("constant.tvel");
        assertNotNull(vmod);
        smod = new SphericalSModel(vmod,
                                   0.1,
                                   11.0,
                                   115.0,
                                   2.5 * Math.PI / 180,
                                   0.01,
                                   true,
                                   SlownessModel.DEFAULT_SLOWNESS_TOLERANCE);
        assertNotNull(smod);
        tMod = new TauModel(smod);
        assertNotNull(tMod);
    }

    @Test
    public void testCMBatCenter() {
        assertEquals("moho", 0.0, tMod.getMohoDepth(), 0.000001);
        assertEquals("cmb", tMod.getRadiusOfEarth(), tMod.getCmbDepth(), 0.000001);
    }

    @Test
    public void testDirectP() {
        ConstantModelTest.doDirectTest(tMod, SeismicPhase.PWAVE);
    }

    @Test
    public void testDirectS() {
        ConstantModelTest.doDirectTest(tMod, SeismicPhase.SWAVE);
    }
    
/**
 * Error increases with depth, so 
 * @throws TauModelException
 */
    @Test
    public void testDepthP() throws TauModelException {
        double vp = tMod.getVelocityModel().getVelocityLayer(0).getTopPVelocity();
        for (int depth = 0; depth < 400; depth += 5) {
            for (int deg = 0; deg < 90; deg++) {
                ConstantModelTest.doSeismicPhase(depth, deg, vp, "P", tMod);
            }
        }
    }


    @Test
    public void testSeismicPhaseDirectP() throws TauModelException {
        float dist = 3;
        double velocity = tMod.getVelocityModel().getVelocityLayer(0).getTopPVelocity();
        ConstantModelTest.doSeismicPhase(dist, velocity, "P", tMod);
    }

    @Test
    public void testNoInterpSeismicPhaseDirectP() throws TauModelException {
        assertNotNull(tMod);
        assertNotNull(tMod.getVelocityModel());
        assertNotNull(tMod.getVelocityModel().getVelocityLayer(0));
        double velocity = tMod.getVelocityModel().getVelocityLayer(0).getTopPVelocity();
        boolean isPWave = true;
        for (int i = 0; i < tMod.rayParams.length; i++) {
            float dist = 0;
            for (int j = 0; j < tMod.getNumBranches(); j++) {
                dist += tMod.getTauBranch(j, isPWave).getDist(i);
            }
            ConstantModelTest.doSeismicPhase(2 * dist, velocity, "P", tMod);
        }
    }

    VelocityModel vmod;

    SphericalSModel smod;

    TauModel tMod;
}