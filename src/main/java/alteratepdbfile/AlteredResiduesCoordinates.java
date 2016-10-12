/*
Copyright 2016 Fabrice Moriaud <fmoriaud@ultimatepdb.org>


This file is part of ultimatepdb.

		ultimatepdb is free software: you can redistribute it and/or modify
		it under the terms of the GNU Lesser General Public License as published by
		the Free Software Foundation, either version 3 of the License, or
		(at your option) any later version.

		ultimatepdb is distributed in the hope that it will be useful,
		but WITHOUT ANY WARRANTY; without even the implied warranty of
		MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
		GNU Lesser General Public License for more details.

		You should have received a copy of the GNU Lesser General Public License
		along with Foobar.  If not, see <http://www.gnu.org/licenses/>.
*/
package alteratepdbfile;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class AlteredResiduesCoordinates {
//-------------------------------------------------------------
// Class members
//-------------------------------------------------------------
    private Map<String, Map<String, float[]>> templateCoords;
    private Map<String, List<String>> templateBonds;


//-------------------------------------------------------------
// Constructor
//-------------------------------------------------------------
    public AlteredResiduesCoordinates() {

        templateCoords = new LinkedHashMap<>();
        templateCoords.put("ARG", buildCoordsArginine());
        templateCoords.put("ALA", buildCoordsAlanine());
        templateCoords.put("ASN", buildCoordsAsparagine());
        templateCoords.put("ASP", buildCoordsAspartic());
        templateCoords.put("GLU", buildCoordsGlutamic());
        templateCoords.put("CYS", buildCoordsCysteine());
        templateCoords.put("GLN", buildCoordsGlutamine());
        templateCoords.put("HIS", buildCoordsHistidine());
        templateCoords.put("ILE", buildCoordsIsoleucine());
        templateCoords.put("LEU", buildCoordsLeucine());
        templateCoords.put("LYS", buildCoordsLysine());
        templateCoords.put("MET", buildCoordsMethionine());
        templateCoords.put("PHE", buildCoordsPhenylalanine());
        templateCoords.put("PRO", buildCoordsProline());
        templateCoords.put("SER", buildCoordsSerine());
        templateCoords.put("THR", buildCoordsThreonine());
        templateCoords.put("TRP", buildCoordsTryptophane());
        templateCoords.put("TYR", buildCoordsTyrosine());
        templateCoords.put("VAL", buildCoordsValine());
        templateCoords.put("GLY", buildCoordsGlycine());

        templateBonds = new LinkedHashMap<>();
        templateBonds.put("ARG", getArgBondInfos());
        templateBonds.put("ALA", getAlaBondInfos());
        templateBonds.put("ASN", getAsnBondInfos());
        templateBonds.put("ASP", getAspBondInfos());
        templateBonds.put("GLU", getGluBondInfos());
        templateBonds.put("CYS", getCysBondInfos());
        templateBonds.put("GLN", getGlnBondInfos());
        templateBonds.put("HIS", getHisBondInfos());
        templateBonds.put("ILE", getIleBondInfos());
        templateBonds.put("LEU", getLeuBondInfos());
        templateBonds.put("LYS", getLysBondInfos());
        templateBonds.put("MET", getMetBondInfos());
        templateBonds.put("PHE", getPheBondInfos());
        templateBonds.put("PRO", getProBondInfos());
        templateBonds.put("SER", getSerBondInfos());
        templateBonds.put("THR", getThrBondInfos());
        templateBonds.put("TRP", getTrpBondInfos());
        templateBonds.put("TYR", getTyrBondInfos());
        templateBonds.put("VAL", getValBondInfos());
        templateBonds.put("GLY", getGlyBondInfos());

    }




//-------------------------------------------------------------
// Public & Override methods
//-------------------------------------------------------------
    public List<String> getGlyBondInfos() {

        List<String> bonds = new ArrayList<>();
        bonds.add("C,O,2");
        bonds.add("CA,N,1");
        bonds.add("C,CA,1");
        return bonds;
    }



    public List<String> getAlaBondInfos() {

        List<String> bonds = new ArrayList<>();
        bonds.add("C,O,2");
        bonds.add("CA,N,1");
        bonds.add("C,CA,1");
        bonds.add("CA,CB,1");
        return bonds;
    }



    public List<String> getArgBondInfos() {

        List<String> bonds = new ArrayList<>();
        bonds.add("C,O,2");
        bonds.add("CA,N,1");
        bonds.add("C,CA,1");
        bonds.add("CA,CB,1");
        bonds.add("CB,CG,1");
        bonds.add("CG,CD,1");
        bonds.add("CD,NE,1");
        bonds.add("NE,CZ,1");
        bonds.add("CZ,NH1,1");
        bonds.add("CZ,NH2,2");
        return bonds;
    }



    public List<String> getAsnBondInfos() {

        List<String> bonds = new ArrayList<>();
        bonds.add("C,O,2");
        bonds.add("CA,N,1");
        bonds.add("C,CA,1");
        bonds.add("CA,CB,1");
        bonds.add("CB,CG,1");
        bonds.add("CG,OD1,2");
        bonds.add("CG,ND2,1");
        return bonds;
    }



    public List<String> getAspBondInfos() {

        List<String> bonds = new ArrayList<>();
        bonds.add("C,O,2");
        bonds.add("CA,N,1");
        bonds.add("C,CA,1");
        bonds.add("CA,CB,1");
        bonds.add("CB,CG,1");
        bonds.add("CG,OD1,2");
        bonds.add("CG,OD2,1");
        return bonds;
    }



    public List<String> getGluBondInfos() {

        List<String> bonds = new ArrayList<>();
        bonds.add("C,O,2");
        bonds.add("CA,N,1");
        bonds.add("C,CA,1");
        bonds.add("CA,CB,1");
        bonds.add("CB,CG,1");
        bonds.add("CG,CD,1");
        bonds.add("CD,OE1,2");
        bonds.add("CD,OE2,1");
        return bonds;
    }



    public List<String> getCysBondInfos() {

        List<String> bonds = new ArrayList<>();
        bonds.add("C,O,2");
        bonds.add("CA,N,1");
        bonds.add("C,CA,1");
        bonds.add("CA,CB,1");
        bonds.add("CB,SG,1");
        return bonds;

    }



    public List<String> getGlnBondInfos() {

        List<String> bonds = new ArrayList<>();
        bonds.add("C,O,2");
        bonds.add("CA,N,1");
        bonds.add("C,CA,1");
        bonds.add("CA,CB,1");
        bonds.add("CB,CG,1");
        bonds.add("CG,CD,1");
        bonds.add("CD,OE1,2");
        bonds.add("CD,NE2,1");
        return bonds;
    }



    public List<String> getHisBondInfos() {

        List<String> bonds = new ArrayList<>();
        bonds.add("C,O,2");
        bonds.add("CA,N,1");
        bonds.add("C,CA,1");
        bonds.add("CA,CB,1");
        bonds.add("CB,CG,1");
        bonds.add("CG,CD2,2");
        bonds.add("CD2,NE2,1");
        bonds.add("NE2,CE1,1");
        bonds.add("CE1,ND1,2");
        bonds.add("ND1,CG,1");
        return bonds;
    }



    public List<String> getIleBondInfos() {

        List<String> bonds = new ArrayList<>();
        bonds.add("C,O,2");
        bonds.add("CA,N,1");
        bonds.add("C,CA,1");
        bonds.add("CA,CB,1");
        bonds.add("CB,CG1,1");
        bonds.add("CB,CG2,1");
        bonds.add("CG1,CD1,1");
        return bonds;
    }



    public List<String> getLeuBondInfos() {

        List<String> bonds = new ArrayList<>();
        bonds.add("C,O,2");
        bonds.add("CA,N,1");
        bonds.add("C,CA,1");
        bonds.add("CA,CB,1");
        bonds.add("CB,CG,1");
        bonds.add("CG,CD1,1");
        bonds.add("CG,CD2,1");
        return bonds;
    }



    public List<String> getLysBondInfos() {

        List<String> bonds = new ArrayList<>();
        bonds.add("C,O,2");
        bonds.add("CA,N,1");
        bonds.add("C,CA,1");
        bonds.add("CA,CB,1");
        bonds.add("CB,CG,1");
        bonds.add("CG,CD,1");
        bonds.add("CD,CE,1");
        bonds.add("CE,NZ,1");
        return bonds;
    }



    public List<String> getMetBondInfos() {

        List<String> bonds = new ArrayList<>();
        bonds.add("C,O,2");
        bonds.add("CA,N,1");
        bonds.add("C,CA,1");
        bonds.add("CA,CB,1");
        bonds.add("CB,CG,1");
        bonds.add("CG,SD,1");
        bonds.add("SD,CE,1");
        return bonds;
    }



    public List<String> getPheBondInfos() {

        List<String> bonds = new ArrayList<>();
        bonds.add("C,O,2");
        bonds.add("CA,N,1");
        bonds.add("C,CA,1");
        bonds.add("CA,CB,1");
        bonds.add("CB,CG,1");
        bonds.add("CG,CD2,2");
        bonds.add("CD2,CE2,1");
        bonds.add("CE2,CZ,2");
        bonds.add("CZ,CE1,1");
        bonds.add("CE1,CD1,2");
        bonds.add("CD1,CG,1");
        return bonds;
    }



    public List<String> getProBondInfos() {

        List<String> bonds = new ArrayList<>();
        bonds.add("C,O,2");
        bonds.add("CA,N,1");
        bonds.add("C,CA,1");
        bonds.add("CA,CB,1");
        bonds.add("CB,CG,1");
        bonds.add("CG,CD,1");
        bonds.add("CD,N,1");
        return bonds;
    }



    public List<String> getSerBondInfos() {

        List<String> bonds = new ArrayList<>();
        bonds.add("C,O,2");
        bonds.add("CA,N,1");
        bonds.add("C,CA,1");
        bonds.add("CA,CB,1");
        bonds.add("CB,OG,1");
        return bonds;
    }



    public List<String> getThrBondInfos() {

        List<String> bonds = new ArrayList<>();
        bonds.add("C,O,2");
        bonds.add("CA,N,1");
        bonds.add("C,CA,1");
        bonds.add("CA,CB,1");
        bonds.add("CB,OG1,1");
        bonds.add("CB,CG2,1");
        return bonds;
    }



    public List<String> getTrpBondInfos() {

        List<String> bonds = new ArrayList<>();
        bonds.add("C,O,2");
        bonds.add("CA,N,1");
        bonds.add("C,CA,1");
        bonds.add("CA,CB,1");
        bonds.add("CB,CG,1");
        bonds.add("CG,CD1,2");
        bonds.add("CD1,NE1,1");
        bonds.add("NE1,CE2,1");
        bonds.add("CE2,CD2,2");
        bonds.add("CD2,CG,1");
        bonds.add("CD2,CE3,1");
        bonds.add("CE3,CZ3,2");
        bonds.add("CZ3,CH2,1");
        bonds.add("CH2,CZ2,2");
        bonds.add("CZ2,CE2,1");
        return bonds;
    }



    public List<String> getTyrBondInfos() {

        List<String> bonds = new ArrayList<>();
        bonds.add("C,O,2");
        bonds.add("CA,N,1");
        bonds.add("C,CA,1");
        bonds.add("CA,CB,1");
        bonds.add("CB,CG,1");
        bonds.add("CG,CD2,2");
        bonds.add("CD2,CE2,1");
        bonds.add("CE2,CZ,2");
        bonds.add("CZ,CE1,1");
        bonds.add("CE1,CD1,2");
        bonds.add("CD1,CG,1");
        bonds.add("CZ,OH,1");
        return bonds;
    }



    public List<String> getValBondInfos() {

        List<String> bonds = new ArrayList<>();
        bonds.add("C,O,2");
        bonds.add("CA,N,1");
        bonds.add("C,CA,1");
        bonds.add("CA,CB,1");
        bonds.add("CB,CG1,1");
        bonds.add("CB,CG2,1");
        return bonds;
    }




//-------------------------------------------------------------
// Implementation
//-------------------------------------------------------------
    private Map<String, float[]> buildCoordsAsparagine() {

        Map<String, float[]> atomsCoords = new LinkedHashMap<>();
        // 1JK3 ASN A 14 used as template

        float[] coordsN = new float[]{20.963f, 3.413f, 4.205f};
        atomsCoords.put("N", coordsN);
        float[] coordsCA = new float[]{22.329f, 3.848f, 4.348f};
        atomsCoords.put("CA", coordsCA);
        float[] coordsC = new float[]{23.292f, 2.811f, 3.847f};
        atomsCoords.put("C", coordsC);
        float[] coordsO = new float[]{24.342f, 3.142f, 3.268f};
        atomsCoords.put("O", coordsO);
        float[] coordsCB = new float[]{22.595f, 4.093f, 5.841f};
        atomsCoords.put("CB", coordsCB);
        float[] coordsCG = new float[]{24.015f, 4.501f, 6.150f};
        atomsCoords.put("CG", coordsCG);
        float[] coordsOD1 = new float[]{24.391f, 5.640f, 5.919f};
        atomsCoords.put("OD1", coordsOD1);
        float[] coordsND2 = new float[]{24.802f, 3.577f, 6.710f};
        atomsCoords.put("ND2", coordsND2);

        return atomsCoords;
    }



    private Map<String, float[]> buildCoordsGlycine() {

        Map<String, float[]> atomsCoords = new LinkedHashMap<>();
        // 1JK3 ALA A 28 used as template

        float[] coordsN = new float[]{11.381f, -4.848f, 1.089f};
        atomsCoords.put("N", coordsN);
        float[] coordsCA = new float[]{10.898f, -5.028f, 2.448f};
        atomsCoords.put("CA", coordsCA);
        float[] coordsC = new float[]{10.674f, -3.660f, 3.113f};
        atomsCoords.put("C", coordsC);
        float[] coordsO = new float[]{9.660f, -3.445f, 3.785f};
        atomsCoords.put("O", coordsO);
        return atomsCoords;
    }



    private Map<String, float[]> buildCoordsAlanine() {

        Map<String, float[]> atomsCoords = new LinkedHashMap<>();
        // 1JK3 ALA A 28 used as template

        float[] coordsN = new float[]{11.381f, -4.848f, 1.089f};
        atomsCoords.put("N", coordsN);
        float[] coordsCA = new float[]{10.898f, -5.028f, 2.448f};
        atomsCoords.put("CA", coordsCA);
        float[] coordsC = new float[]{10.674f, -3.660f, 3.113f};
        atomsCoords.put("C", coordsC);
        float[] coordsO = new float[]{9.660f, -3.445f, 3.785f};
        atomsCoords.put("O", coordsO);
        float[] coordsCB = new float[]{11.854f, -5.862f, 3.265f};
        atomsCoords.put("CB", coordsCB);
        return atomsCoords;
    }



    private Map<String, float[]> buildCoordsArginine() {

        Map<String, float[]> atomsCoords = new LinkedHashMap<>();
        // 1JK3 ARG A 12 used as template

        float[] coordsN = new float[]{16.133f, 6.848f, 3.473f};
        atomsCoords.put("N", coordsN);
        float[] coordsCA = new float[]{17.315f, 6.412f, 2.744f};
        atomsCoords.put("CA", coordsCA);
        float[] coordsC = new float[]{17.843f, 5.158f, 3.393f};
        atomsCoords.put("C", coordsC);
        float[] coordsO = new float[]{17.830f, 5.044f, 4.609f};
        atomsCoords.put("O", coordsO);
        float[] coordsCB = new float[]{18.380f, 7.483f, 2.787f};
        atomsCoords.put("CB", coordsCB);
        float[] coordsCG = new float[]{19.640f, 7.097f, 2.098f};
        atomsCoords.put("CG", coordsCG);
        float[] coordsCD = new float[]{20.589f, 8.254f, 1.993f};
        atomsCoords.put("CD", coordsCD);
        float[] coordsNE = new float[]{21.799f, 7.826f, 1.334f};
        atomsCoords.put("NE", coordsNE);
        float[] coordsCZ = new float[]{22.857f, 8.606f, 1.129f};
        atomsCoords.put("CZ", coordsCZ);
        float[] coordsNH1 = new float[]{22.858f, 9.900f, 1.482f};
        atomsCoords.put("NH1", coordsNH1);
        float[] coordsNH2 = new float[]{23.908f, 8.103f, 0.512f};
        atomsCoords.put("NH2", coordsNH2);
        return atomsCoords;
    }



    private Map<String, float[]> buildCoordsAspartic() {

        Map<String, float[]> atomsCoords = new LinkedHashMap<>();
        // 1JK3 ARG A 12 used as template

        float[] coordsN = new float[]{23.545f, -6.144f, 20.713f};
        atomsCoords.put("N", coordsN);
        float[] coordsCA = new float[]{23.929f, -7.147f, 21.680f};
        atomsCoords.put("CA", coordsCA);
        float[] coordsC = new float[]{24.247f, -8.508f, 21.095f};
        atomsCoords.put("C", coordsC);
        float[] coordsO = new float[]{24.118f, -9.524f, 21.767f};
        atomsCoords.put("O", coordsO);
        float[] coordsCB = new float[]{22.774f, -7.250f, 22.657f};
        atomsCoords.put("CB", coordsCB);
        float[] coordsCG = new float[]{21.473f, -7.454f, 21.957f};
        atomsCoords.put("CG", coordsCG);
        float[] coordsOD1 = new float[]{20.467f, -7.584f, 22.679f};
        atomsCoords.put("OD1", coordsOD1);
        float[] coordsOD2 = new float[]{21.401f, -7.517f, 20.697f};
        atomsCoords.put("OD2", coordsOD2);
        return atomsCoords;
    }



    private Map<String, float[]> buildCoordsGlutamic() {

        Map<String, float[]> atomsCoords = new LinkedHashMap<>();

        float[] coordsN = new float[]{-35.675f, 12.228f, -24.300f};
        atomsCoords.put("N", coordsN);
        float[] coordsCA = new float[]{-34.460f, 11.444f, -24.482f};
        atomsCoords.put("CA", coordsCA);
        float[] coordsC = new float[]{-33.840f, 11.062f, -23.142f};
        atomsCoords.put("C", coordsC);
        float[] coordsO = new float[]{-32.819f, 10.380f, -23.087f};
        atomsCoords.put("O", coordsO);
        float[] coordsCB = new float[]{-33.445f, 12.213f, -25.345f};
        atomsCoords.put("CB", coordsCB);
        float[] coordsCG = new float[]{-32.838f, 13.452f, -24.693f};
        atomsCoords.put("CG", coordsCG);
        float[] coordsCD = new float[]{-31.903f, 14.224f, -25.631f};
        atomsCoords.put("CD", coordsCD);
        float[] coordsOD1 = new float[]{-32.387f, 14.826f, -26.623f};
        atomsCoords.put("OE1", coordsOD1);
        float[] coordsOD2 = new float[]{-30.677f, 14.225f, -25.373f};
        atomsCoords.put("OE2", coordsOD2);
        return atomsCoords;
    }



    private Map<String, float[]> buildCoordsCysteine() {

        Map<String, float[]> atomsCoords = new LinkedHashMap<>();
        // 1IO7 CYS A 317 used as template

        float[] coordsN = new float[]{34.164f, 10.151f, 44.679f};
        atomsCoords.put("N", coordsN);
        float[] coordsCA = new float[]{35.574f, 9.952f, 44.374f};
        atomsCoords.put("CA", coordsCA);
        float[] coordsC = new float[]{35.906f, 8.709f, 43.545f};
        atomsCoords.put("C", coordsC);
        float[] coordsO = new float[]{35.621f, 7.577f, 43.948f};
        atomsCoords.put("O", coordsO);
        float[] coordsCB = new float[]{36.391f, 9.938f, 45.669f};
        atomsCoords.put("CB", coordsCB);
        float[] coordsSG = new float[]{38.177f, 9.746f, 45.397f};
        atomsCoords.put("SG", coordsSG);
        return atomsCoords;
    }



    private Map<String, float[]> buildCoordsGlutamine() {

        Map<String, float[]> atomsCoords = new LinkedHashMap<>();
        // 1JK3 ARG A 12 used as template

        float[] coordsN = new float[]{43.693f, 51.098f, 101.425f};
        atomsCoords.put("N", coordsN);
        float[] coordsCA = new float[]{42.684f, 50.053f, 101.496f};
        atomsCoords.put("CA", coordsCA);
        float[] coordsC = new float[]{41.368f, 50.669f, 101.901f};
        atomsCoords.put("C", coordsC);
        float[] coordsO = new float[]{41.328f, 51.498f, 102.807f};
        atomsCoords.put("O", coordsO);
        float[] coordsCB = new float[]{43.105f, 49.003f, 102.510f};
        atomsCoords.put("CB", coordsCB);
        float[] coordsCG = new float[]{44.335f, 48.232f, 102.098f};
        atomsCoords.put("CG", coordsCG);
        float[] coordsCD = new float[]{44.675f, 47.157f, 103.082f};
        atomsCoords.put("CD", coordsCD);
        float[] coordsOE1 = new float[]{43.987f, 46.143f, 103.174f};
        atomsCoords.put("OE1", coordsOE1);
        float[] coordsNE2 = new float[]{45.739f, 47.366f, 103.836f};
        atomsCoords.put("NE2", coordsNE2);
        return atomsCoords;
    }



    private Map<String, float[]> buildCoordsHistidine() {

        Map<String, float[]> atomsCoords = new LinkedHashMap<>();

        float[] coordsN = new float[]{46.516f, 51.023f, 77.930f};
        atomsCoords.put("N", coordsN);
        float[] coordsCA = new float[]{46.310f, 52.246f, 77.168f};
        atomsCoords.put("CA", coordsCA);
        float[] coordsC = new float[]{47.617f, 52.788f, 76.600f};
        atomsCoords.put("C", coordsC);
        float[] coordsO = new float[]{47.648f, 53.328f, 75.493f};
        atomsCoords.put("O", coordsO);
        float[] coordsCB = new float[]{45.624f, 53.298f, 78.042f};
        atomsCoords.put("CB", coordsCB);
        float[] coordsCG = new float[]{44.180f, 53.004f, 78.299f};
        atomsCoords.put("CG", coordsCG);
        float[] coordsND1 = new float[]{43.162f, 53.608f, 77.593f};
        atomsCoords.put("ND1", coordsND1);
        float[] coordsCD2 = new float[]{43.585f, 52.146f, 79.163f};
        atomsCoords.put("CD2", coordsCD2);
        float[] coordsCE1 = new float[]{41.999f, 53.134f, 78.009f};
        atomsCoords.put("CE1", coordsCE1);
        float[] coordsNE2 = new float[]{42.228f, 52.246f, 78.961f};
        atomsCoords.put("NE2", coordsNE2);
        return atomsCoords;
    }



    private Map<String, float[]> buildCoordsIsoleucine() {

        Map<String, float[]> atomsCoords = new LinkedHashMap<>();

        float[] coordsN = new float[]{19.281f, 12.463f, 14.215f};
        atomsCoords.put("N", coordsN);
        float[] coordsCA = new float[]{19.044f, 11.031f, 14.420f};
        atomsCoords.put("CA", coordsCA);
        float[] coordsC = new float[]{17.537f, 10.742f, 14.613f};
        atomsCoords.put("C", coordsC);
        float[] coordsO = new float[]{17.094f, 9.578f, 14.482f};
        atomsCoords.put("O", coordsO);
        float[] coordsCB = new float[]{19.932f, 10.432f, 15.543f};
        atomsCoords.put("CB", coordsCB);
        float[] coordsCG1 = new float[]{21.373f, 10.943f, 15.438f};
        atomsCoords.put("CG1", coordsCG1);
        float[] coordsCG2 = new float[]{19.982f, 8.912f, 15.445f};
        atomsCoords.put("CG2", coordsCG2);
        float[] coordsCD1 = new float[]{22.114f, 10.524f, 14.182f};
        atomsCoords.put("CD1", coordsCD1);
        return atomsCoords;
    }



    private Map<String, float[]> buildCoordsLeucine() {

        Map<String, float[]> atomsCoords = new LinkedHashMap<>();

        float[] coordsN = new float[]{8.723f, -7.388f, 8.712f};
        atomsCoords.put("N", coordsN);
        float[] coordsCA = new float[]{7.962f, -7.310f, 9.939f};
        atomsCoords.put("CA", coordsCA);
        float[] coordsC = new float[]{8.686f, -6.449f, 10.962f};
        atomsCoords.put("C", coordsC);
        float[] coordsO = new float[]{8.047f, -5.673f, 11.686f};
        atomsCoords.put("O", coordsO);
        float[] coordsCB = new float[]{7.695f, -8.700f, 10.495f};
        atomsCoords.put("CB", coordsCB);
        float[] coordsCG = new float[]{6.801f, -9.626f, 9.695f};
        atomsCoords.put("CG", coordsCG);
        float[] coordsCD1 = new float[]{6.819f, -11.013f, 10.304f};
        atomsCoords.put("CD1", coordsCD1);
        float[] coordsCD2 = new float[]{5.388f, -9.055f, 9.656f};
        atomsCoords.put("CD2", coordsCD2);
        return atomsCoords;
    }



    private Map<String, float[]> buildCoordsLysine() {

        Map<String, float[]> atomsCoords = new LinkedHashMap<>();

        float[] coordsN = new float[]{7.714f, -1.788f, 1.440f};
        atomsCoords.put("N", coordsN);
        float[] coordsCA = new float[]{6.428f, -2.344f, 1.845f};
        atomsCoords.put("CA", coordsCA);
        float[] coordsC = new float[]{6.100f, -1.972f, 3.296f};
        atomsCoords.put("C", coordsC);
        float[] coordsO = new float[]{4.942f, -1.769f, 3.638f};
        atomsCoords.put("O", coordsO);
        float[] coordsCB = new float[]{6.430f, -3.854f, 1.634f};
        atomsCoords.put("CB", coordsCB);
        float[] coordsCG = new float[]{6.356f, -4.288f, 0.141f};
        atomsCoords.put("CG", coordsCG);
        float[] coordsCD = new float[]{5.000f, -4.046f, -0.453f};
        atomsCoords.put("CD", coordsCD);
        float[] coordsCE = new float[]{5.050f, -3.650f, -1.900f};
        atomsCoords.put("CE", coordsCE);
        float[] coordsNZ = new float[]{3.676f, -3.452f, -2.447f};
        atomsCoords.put("NZ", coordsNZ);
        return atomsCoords;
    }



    private Map<String, float[]> buildCoordsMethionine() {

        Map<String, float[]> atomsCoords = new LinkedHashMap<>();

        float[] coordsN = new float[]{3.178f, -3.083f, 17.609f};
        atomsCoords.put("N", coordsN);
        float[] coordsCA = new float[]{4.041f, -2.351f, 18.527f};
        atomsCoords.put("CA", coordsCA);
        float[] coordsC = new float[]{4.325f, -3.075f, 19.857f};
        atomsCoords.put("C", coordsC);
        float[] coordsO = new float[]{4.985f, -2.538f, 20.718f};
        atomsCoords.put("O", coordsO);
        float[] coordsCB = new float[]{3.535f, -0.920f, 18.778f};
        atomsCoords.put("CB", coordsCB);
        float[] coordsCG = new float[]{3.470f, -0.068f, 17.515f};
        atomsCoords.put("CG", coordsCG);
        float[] coordsSD = new float[]{4.977f, -0.080f, 16.486f};
        atomsCoords.put("SD", coordsSD);
        float[] coordsCE = new float[]{6.165f, 0.561f, 17.628f};
        atomsCoords.put("CE", coordsCE);
        return atomsCoords;
    }



    private Map<String, float[]> buildCoordsPhenylalanine() {

        Map<String, float[]> atomsCoords = new LinkedHashMap<>();

        float[] coordsN = new float[]{3.888f, -4.312f, 20.000f};
        atomsCoords.put("N", coordsN);
        float[] coordsCA = new float[]{4.298f, -5.136f, 21.145f};
        atomsCoords.put("CA", coordsCA);
        float[] coordsC = new float[]{5.801f, -5.375f, 21.009f};
        atomsCoords.put("C", coordsC);
        float[] coordsO = new float[]{6.284f, -5.604f, 19.906f};
        atomsCoords.put("O", coordsO);
        float[] coordsCB = new float[]{3.552f, -6.473f, 21.102f};
        atomsCoords.put("CB", coordsCB);
        float[] coordsCG = new float[]{3.744f, -7.343f, 22.308f};
        atomsCoords.put("CG", coordsCG);
        float[] coordsCD1 = new float[]{3.216f, -6.973f, 23.537f};
        atomsCoords.put("CD1", coordsCD1);
        float[] coordsCD2 = new float[]{4.405f, -8.569f, 22.205f};
        atomsCoords.put("CD2", coordsCD2);
        float[] coordsCE1 = new float[]{3.361f, -7.793f, 24.626f};
        atomsCoords.put("CE1", coordsCE1);
        float[] coordsCE2 = new float[]{4.535f, -9.389f, 23.325f};
        atomsCoords.put("CE2", coordsCE2);
        float[] coordsCZ = new float[]{4.023f, -8.981f, 24.527f};
        atomsCoords.put("CZ", coordsCZ);
        return atomsCoords;
    }



    private Map<String, float[]> buildCoordsProline() {

        Map<String, float[]> atomsCoords = new LinkedHashMap<>();

        float[] coordsN = new float[]{6.558f, -5.318f, 22.098f};
        atomsCoords.put("N", coordsN);
        float[] coordsCA = new float[]{8.028f, -5.369f, 21.980f};
        atomsCoords.put("CA", coordsCA);
        float[] coordsC = new float[]{8.720f, -6.709f, 21.799f};
        atomsCoords.put("C", coordsC);
        float[] coordsO = new float[]{9.948f, -6.752f, 21.934f};
        atomsCoords.put("O", coordsO);
        float[] coordsCB = new float[]{8.524f, -4.725f, 23.281f};
        atomsCoords.put("CB", coordsCB);
        float[] coordsCG = new float[]{7.414f, -4.629f, 24.155f};
        atomsCoords.put("CG", coordsCG);
        float[] coordsCD = new float[]{6.123f, -4.952f, 23.456f};
        atomsCoords.put("CD", coordsCD);
        return atomsCoords;
    }



    private Map<String, float[]> buildCoordsSerine() {

        Map<String, float[]> atomsCoords = new LinkedHashMap<>();

        float[] coordsN = new float[]{-2.994f, -4.141f, 14.120f};
        atomsCoords.put("N", coordsN);
        float[] coordsCA = new float[]{-4.107f, -4.423f, 15.049f};
        atomsCoords.put("CA", coordsCA);
        float[] coordsC = new float[]{-4.873f, -3.138f, 15.350f};
        atomsCoords.put("C", coordsC);
        float[] coordsO = new float[]{-4.434f, -2.048f, 15.004f};
        atomsCoords.put("O", coordsO);
        float[] coordsCB = new float[]{-3.575f, -4.976f, 16.355f};
        atomsCoords.put("CB", coordsCB);
        float[] coordsOG = new float[]{-3.024f, -3.910f, 17.087f};
        atomsCoords.put("OG", coordsOG);
        return atomsCoords;
    }



    private Map<String, float[]> buildCoordsThreonine() {

        Map<String, float[]> atomsCoords = new LinkedHashMap<>();

        float[] coordsN = new float[]{20.776f, 10.105f, -2.044f};
        atomsCoords.put("N", coordsN);
        float[] coordsCA = new float[]{21.848f, 11.040f, -1.731f};
        atomsCoords.put("CA", coordsCA);
        float[] coordsC = new float[]{21.283f, 12.075f, -0.794f};
        atomsCoords.put("C", coordsC);
        float[] coordsO = new float[]{20.078f, 12.312f, -0.778f};
        atomsCoords.put("O", coordsO);
        float[] coordsCB = new float[]{22.384f, 11.762f, -3.006f};
        atomsCoords.put("CB", coordsCB);
        float[] coordsOG1 = new float[]{21.331f, 12.469f, -3.677f};
        atomsCoords.put("OG1", coordsOG1);
        float[] coordsCG2 = new float[]{22.988f, 10.796f, -4.042f};
        atomsCoords.put("CG2", coordsCG2);
        return atomsCoords;
    }



    private Map<String, float[]> buildCoordsTryptophane() {

        Map<String, float[]> atomsCoords = new LinkedHashMap<>();

        float[] coordsN = new float[]{18.814f, -11.537f, 8.590f};
        atomsCoords.put("N", coordsN);
        float[] coordsCA = new float[]{17.632f, -10.939f, 8.005f};
        atomsCoords.put("CA", coordsCA);
        float[] coordsC = new float[]{17.170f, -11.723f, 6.796f};
        atomsCoords.put("C", coordsC);
        float[] coordsO = new float[]{17.982f, -12.133f, 5.984f};
        atomsCoords.put("O", coordsO);
        float[] coordsCB = new float[]{17.937f, -9.466f, 7.596f};
        atomsCoords.put("CB", coordsCB);
        float[] coordsCG = new float[]{18.343f, -8.659f, 8.791f};
        atomsCoords.put("CG", coordsCG);
        float[] coordsCD1 = new float[]{19.610f, -8.308f, 9.161f};
        atomsCoords.put("CD1", coordsCD1);
        float[] coordsCD2 = new float[]{17.486f, -8.227f, 9.849f};
        atomsCoords.put("CD2", coordsCD2);
        float[] coordsNE1 = new float[]{19.591f, -7.661f, 10.375f};
        atomsCoords.put("NE1", coordsNE1);
        float[] coordsCE2 = new float[]{18.297f, -7.605f, 10.822f};
        atomsCoords.put("CE2", coordsCE2);
        float[] coordsCE3 = new float[]{16.110f, -8.326f, 10.090f};
        atomsCoords.put("CE3", coordsCE3);
        float[] coordsCZ2 = new float[]{17.786f, -7.109f, 12.018f};
        atomsCoords.put("CZ2", coordsCZ2);
        float[] coordsCZ3 = new float[]{15.594f, -7.806f, 11.246f};
        atomsCoords.put("CZ3", coordsCZ3);
        float[] coordsCH2 = new float[]{16.432f, -7.202f, 12.195f};
        atomsCoords.put("CH2", coordsCH2);
        return atomsCoords;
    }



    private Map<String, float[]> buildCoordsTyrosine() {

        Map<String, float[]> atomsCoords = new LinkedHashMap<>();

        float[] coordsN = new float[]{9.229f, -9.776f, 18.895f};
        atomsCoords.put("N", coordsN);
        float[] coordsCA = new float[]{9.015f, -10.155f, 17.503f};
        atomsCoords.put("CA", coordsCA);
        float[] coordsC = new float[]{8.207f, -11.434f, 17.403f};
        atomsCoords.put("C", coordsC);
        float[] coordsO = new float[]{8.478f, -12.405f, 18.111f};
        atomsCoords.put("O", coordsO);
        float[] coordsCB = new float[]{10.362f, -10.366f, 16.821f};
        atomsCoords.put("CB", coordsCB);
        float[] coordsCG = new float[]{10.335f, -11.023f, 15.466f};
        atomsCoords.put("CG", coordsCG);
        float[] coordsCD1 = new float[]{10.509f, -12.385f, 15.353f};
        atomsCoords.put("CD1", coordsCD1);
        float[] coordsCD2 = new float[]{10.212f, -10.296f, 14.304f};
        atomsCoords.put("CD2", coordsCD2);
        float[] coordsCE1 = new float[]{10.520f, -13.005f, 14.131f};
        atomsCoords.put("CE1", coordsCE1);
        float[] coordsCE2 = new float[]{10.237f, -10.913f, 13.080f};
        atomsCoords.put("CE2", coordsCE2);
        float[] coordsCZ = new float[]{10.389f, -12.257f, 12.993f};
        atomsCoords.put("CZ", coordsCZ);
        float[] coordsOH = new float[]{10.421f, -12.849f, 11.756f};
        atomsCoords.put("OH", coordsOH);
        return atomsCoords;
    }



    private Map<String, float[]> buildCoordsValine() {

        Map<String, float[]> atomsCoords = new LinkedHashMap<>();

        float[] coordsN = new float[]{4.698f, -13.685f, 11.349f};
        atomsCoords.put("N", coordsN);
        float[] coordsCA = new float[]{3.405f, -13.973f, 10.772f};
        atomsCoords.put("CA", coordsCA);
        float[] coordsC = new float[]{3.596f, -13.846f, 9.258f};
        atomsCoords.put("C", coordsC);
        float[] coordsO = new float[]{4.408f, -13.046f, 8.774f};
        atomsCoords.put("O", coordsO);
        float[] coordsCB = new float[]{2.295f, -13.025f, 11.248f};
        atomsCoords.put("CB", coordsCB);
        float[] coordsCG1 = new float[]{2.037f, -13.173f, 12.733f};
        atomsCoords.put("CG1", coordsCG1);
        float[] coordsCG2 = new float[]{2.596f, -11.599f, 10.878f};
        atomsCoords.put("CG2", coordsCG2);
        return atomsCoords;
    }




//-------------------------------------------------------------
// Getters & Setters
//-------------------------------------------------------------
    public Map<String, Map<String, float[]>> getTemplateCoords() {
        return templateCoords;
    }



    public Map<String, List<String>> getTemplateBonds() {
        return templateBonds;
    }
}
