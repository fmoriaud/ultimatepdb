package mystructure;

public class AtomProperties {
    // -------------------------------------------------------------------
    // Constants Enum
    // -------------------------------------------------------------------
    // http://www.ccdc.cam.ac.uk/support/documentation/mercury/mercury/appa_glossary.4.73.html
    //van der Waals Radii
    // Standard van der Waals radii are assigned to the common elements. They are taken from Bondi,
    // J.Phys.Chem., 68, 441, 1964. Other elements are assigned van der Waals radii of 2.0�.
    public enum AtomGaussianDescriptors {
        AG("Ag", 1.72f),
        AR("Ar", 1.88f),
        AS("As", 1.85f),
        AU("Au", 1.66f),
        B("B", 1.85f), // Value is wrong
        BR("Br", 1.85f),
        C("C", 1.70f),
        Ca("Ca", 1.97f),
        CD("Cd", 1.58f),
        CL("Cl", 1.75f),
        CU("Cu", 1.40f),
        F("F", 1.47f),
        FE("Fe", 2.00f),
        GA("Ga", 1.87f),
        H("H", 1.20f),
        D("D", 1.20f),
        HE("He", 1.40f),
        HG("Hg", 1.55f),
        I("I", 1.98f),
        IN("In", 1.93f),
        K("K", 2.75f),
        KR("Kr", 2.02f),
        LI("Li", 1.82f),
        MG("Mg", 1.73f),
        MN("Mn", 2.00f),
        MO("Mo", 2.00f),
        N("N", 1.55f),
        NA("Na", 2.27f),
        NE("Ne", 1.54f),
        NI("Ni", 1.63f),
        O("O", 1.52f),
        P("P", 1.80f),
        PB("Pb", 2.02f),
        PD("Pd", 1.63f),
        PT("Pt", 1.72f),
        S("S", 1.80f),
        Se("Se", 1.90f),
        SI("Si", 2.10f),
        SN("Sn", 2.17f),
        TE("Te", 2.06f),
        TL("Tl", 1.96f),
        U("U", 1.86f),
        XE("Xe", 2.16f),
        ZN("Zn", 1.39f);

        private final String atomName;
        private final float fwhm;

        AtomGaussianDescriptors(String atomName, float fwhm) {
            this.atomName = atomName;
            this.fwhm = fwhm;
        }

        public String getAtomName() {
            return atomName;
        }

        public float getSigma() {
            return fwhm;
        }
    }


    public enum AtomAromaticRingDescriptors {

        TYRCD1("TYRCD1", 1.0f),
        TYRCD2("TYRCD2", 1.0f),
        TYRCE1("TYRCE1", 1.0f),
        TYRCE2("TYRCE2", 1.0f),
        TYRCG("TYRCG", 1.0f),
        TYRCZ("TYRCZ", 1.0f),
        PHECG("PHECG", 1.0f),
        PHECZ("PHECZ", 1.0f),
        PHECD1("PHECD1", 1.0f),
        PHECD2("PHECD2", 1.0f),
        PHECE1("PHECE1", 1.0f),
        PHECE2("PHECE2", 1.0f),
        TRPCG("TRPCG", 1.0f),
        TRPCD1("TRPCD1", 1.0f),
        TRPCD2("TRPCD2", 1.0f),
        TRPNE1("TRPNE1", 1.0f),
        TRPCE2("TRPCE2", 1.0f),
        TRPCE3("TRPCE3", 1.0f),
        TRPCZ2("TRPCZ2", 1.0f),
        TRPCZ3("TRPCZ3", 1.0f),
        TRPCH2("TRPCH2", 1.0f),

        // Big Hetatm
        HEMC1A("HEMC1A", 1.0f),
        HEMC2A("HEMC2A", 1.0f),
        HEMC3A("HEMC3A", 1.0f),
        HEMC4A("HEMC4A", 1.0f),
        HEMC1B("HEMC1B", 1.0f),
        HEMC2B("HEMC2B", 1.0f),
        HEMC3B("HEMC3B", 1.0f),
        HEMC4B("HEMC4B", 1.0f),
        HEMC1C("HEMC1C", 1.0f),
        HEMC2C("HEMC2C", 1.0f),
        HEMC3C("HEMC3C", 1.0f),
        HEMC4C("HEMC4C", 1.0f),
        HEMC1D("HEMC1D", 1.0f),
        HEMC2D("HEMC2D", 1.0f),
        HEMC3D("HEMC3D", 1.0f),
        HEMC4D("HEMC4D", 1.0f),
        HEMCHA("HEMCHA", 1.0f),
        HEMCHB("HEMCHB", 1.0f),
        HEMCHC("HEMCHC", 1.0f),
        HEMCHD("HEMCHD", 1.0f),
        HEMNA("HEMNA", 1.0f),
        HEMNB("HEMNB", 1.0f),
        HEMNC("HEMNC", 1.0f),
        HEMND("HEMND", 1.0f),

        // DNA RNA

        DCC6("DCC6", 1.0f),
        DCC5("DCC5", 1.0f),
        DCC4("DCC4", 1.0f),
        DCC2("DCC2", 1.0f),
        DCN1("DCN1", 1.0f),
        DCN3("DCN3", 1.0f),

        // DT is not really aromatic
        //DTC6 ("DTC6", 1.0f),
        //DTC5 ("DTC5", 1.0f),
        //DTC4 ("DTC4", 1.0f),
        //DTC2 ("DTC2", 1.0f),
        //DTN1 ("DTN1", 1.0f),
        //DTN3 ("DTN3", 1.0f),

        DGC2("DGC2", 1.0f),
        DGC6("DGC6", 1.0f),
        DGC5("DGC5", 1.0f),
        DGC4("DGC4", 1.0f),
        DGC8("DGC8", 1.0f),
        DGN1("DGN1", 1.0f),
        DGN3("DGN3", 1.0f),
        DGN7("DGN7", 1.0f),
        DGN9("DGN9", 1.0f),

        DAC2("DAC2", 1.0f),
        DAC6("DAC6", 1.0f),
        DAC5("DAC5", 1.0f),
        DAC4("DAC4", 1.0f),
        DAC8("DAC8", 1.0f),
        DAN1("DAN1", 1.0f),
        DAN3("DAN3", 1.0f),
        DAN7("DAN7", 1.0f),
        DAN9("DAN9", 1.0f),

        DIC2("DIC2", 1.0f),
        DIC6("DIC6", 1.0f),
        DIC5("DIC5", 1.0f),
        DIC4("DIC4", 1.0f),
        DIC8("DIC8", 1.0f),
        DIN1("DIN1", 1.0f),
        DIN3("DIN3", 1.0f),
        DIN7("DIN7", 1.0f),
        DIN9("DIN9", 1.0f),

        GC2("GC2", 1.0f),
        GC6("GC6", 1.0f),
        GC5("GC5", 1.0f),
        GC4("GC4", 1.0f),
        GC8("GC8", 1.0f),
        GN1("GN1", 1.0f),
        GN3("GN3", 1.0f),
        GN7("GN7", 1.0f),
        GN9("GN9", 1.0f),

        CC6("CC6", 1.0f),
        CC5("CC5", 1.0f),
        CC4("CC4", 1.0f),
        CC2("CC2", 1.0f),
        CN1("CN1", 1.0f),
        CN3("CN3", 1.0f),

        AC2("AC2", 1.0f),
        AC6("AC6", 1.0f),
        AC5("AC5", 1.0f),
        AC4("AC4", 1.0f),
        AC8("AC8", 1.0f),
        AN1("AN1", 1.0f),
        AN3("AN3", 1.0f),
        AN7("AN7", 1.0f),
        AN9("AN9", 1.0f),

        // U is not really aromatic

        IC2("IC2", 1.0f),
        IC6("IC6", 1.0f),
        IC5("IC5", 1.0f),
        IC4("IC4", 1.0f),
        IC8("IC8", 1.0f),
        IN1("IN1", 1.0f),
        IN3("IN3", 1.0f),
        IN7("IN7", 1.0f),
        IN9("IN9", 1.0f);

        private final String atomName;
        private final float aromaticRing;

        AtomAromaticRingDescriptors(String atomName, float aromaticRing) {
            this.atomName = atomName;
            this.aromaticRing = aromaticRing;
        }

        public String getAtomName() {
            return atomName;
        }

        public float getAromaticRing() {
            return aromaticRing;
        }
    }


    public enum AtomChargeDescriptors {
        ARGNH2("ARGNH2", 1.00f),
        ARGNH1("ARGNH1", 1.00f),
        ARGCZ("ARGCZ", 1.00f),
        ARGNE("ARGNE", 1.00f),
        LYSNZ("LYSNZ", 1.00f),
        ASPCG("ASPCG", -1.00f),
        ASPOD1("ASPOD1", -1.00f),
        ASPOD2("ASPOD2", -1.00f),
        GLUCD("GLUCD", -1.00f),
        GLUOE1("GLUOE1", -1.00f),
        GLUOE2("GLUOE2", -1.00f),

        // Handling of big residues
        HEMO1A("HEMO1A", -1.00f),
        HEMO1B("HEMO1B", -1.00f),
        HEMO1C("HEMO1A", -1.00f),
        HEMO1D("HEMO1B", -1.00f);

        private final String atomName;
        private final float charge;

        AtomChargeDescriptors(String atomName, float charge) {
            this.atomName = atomName;
            this.charge = charge;

        }

        public String getAtomName() {
            return atomName;
        }

        public float getCharge() {
            return charge;
        }

    }


    public enum AtomHydrophobicityDescriptors {
        ALACB("ALACB", 1.0f),

        ARGCB("ARGCB", 1.00f),
        ARGCG("ARGCG", 1.00f),

        GLNCB("GLNCB", 1.00f),

        GLUCB("GLUCB", 1.00f),

        ILECB("ILECB", 1.00f),
        ILECG1("ILECG1", 1.00f),
        ILECG2("ILECG2", 1.00f),
        ILECD1("ILECD1", 1.00f),

        LEUCB("LEUCB", 1.00f),
        LEUCG("LEUCG", 1.00f),
        LEUCD1("LEUCD1", 1.00f),
        LEUCD2("LEUCD2", 1.00f),

        LYSCB("LYSCB", 1.00f),
        LYSCG("LYSCG", 1.00f),
        LYSCD("LYSCD", 1.00f),

        METCB("METCB", 1.00f),
        METCG("METCG", 1.00f),
        //METSD("METSD", 1.00 ),
        METCE("METCE", 1.00f),

        MSECB("MSECB", 1.00f),
        MSECG("MSECG", 1.00f),
        //METSD("METSD", 1.00 ),
        MSECE("MSECE", 1.00f),

        PHECB("PHECB", 1.00f),
        PHECD1("PHECD1", 1.00f),
        PHECD2("PHECD2", 1.00f),
        PHECE1("PHECE1", 1.00f),
        PHECE2("PHECE2", 1.00f),
        PHECZ("PHECZ", 1.00f),

        TYRCB("TYRCB", 1.00f),
        TYRCD1("TYRCD1", 1.00f),
        TYRCD2("TYRCD2", 1.00f),
        TYRCE1("TYRCE1", 1.00f),
        TYRCE2("TYRCE2", 1.00f),
        // TYRCZ("TYRCZ", 1.00 ), // bound to O
        TRPCB("TRPCB", 1.00f),
        TRPCG("TRPCG", 1.00f),
        //TRPCD1("TRPCD1", 1.00 ), // bound to N
        TRPCD2("TRPCD2", 1.00f),
        //TRPCE2("TRPCE2", 1.00 ), // bound to N
        TRPCE3("TRPCE3", 1.00f),
        TRPCZ2("TRPCZ2", 1.00f),
        TRPCZ3("TRPCZ3", 1.00f),
        TRPCH2("TRPCH2", 1.00f),

        THRCG2("THRCG2", 1.00f),

        PROCB("PROCB", 1.00f),
        PROCG("PROCG", 1.00f),

        VALCB("VALCB", 1.00f),
        VALCG1("VALG1", 1.00f),
        VALCG2("VALG2", 1.00f),

        ABACB("ABACB", 1.00f),
        ABACG("ABACG", 1.00f),

        BMTCG2("BMTCG2", 1.00f),
        BMTCD1("BMTCD1", 1.00f),
        BMTCD2("BMTCD2", 1.00f),
        BMTCE("BMTCE", 1.00f),
        BMTCZ("BMTCZ", 1.00f),

        MLECB("MLECB", 1.00f),
        MLECG("MLECG", 1.00f),
        MLECD2("MLECD2", 1.00f),
        MLECD1("MLECD1", 1.00f),

        MVACB("MVACB", 1.00f),
        MVACG1("MVACG1", 1.00f),
        MVACG2("MVACG2", 1.00f),

        // kind of annoying to define properties of polymeric residues ...
        MK8CB("MK8CB", 1.00f),
        MK8CB1("MK8CB1", 1.00f),
        MK8CG("MK8CG", 1.00f),
        MK8CD("MK8CD", 1.00f),
        MK8CE("MK8CE", 1.00f),


        // BigHeatm
        HEMCMA("HEMCMA", 1.00f),
        HEMCMB("HEMCMB", 1.00f),
        HEMCMC("HEMCMC", 1.00f),
        HEMCMD("HEMCMD", 1.00f),
        HEMCAA("HEMCAA", 1.00f),
        HEMCAB("HEMCAB", 1.00f),
        HEMCAC("HEMCAC", 1.00f),
        HEMCAD("HEMCAD", 1.00f),
        HEMCBB("HEMCBB", 1.00f),
        HEMCBC("HEMCBC", 1.00f);

        private final String atomName;
        private final float hydrophobicity;

        AtomHydrophobicityDescriptors(String atomName, float hydrophobicity) {
            this.atomName = atomName;
            this.hydrophobicity = hydrophobicity;
        }

        public String getAtomName() {
            return atomName;
        }

        public float getHydrophobicity() {
            return hydrophobicity;
        }
    }


    public enum AtomHDonnorDescriptors {
        LYSN("N", "LYS", 1.00),
        LYSNZ("NZ", "LYS", 1.00),
        HISN("N", "HIS", 1.00),
        ILEN("N", "ILE", 1.00),
        LEUN("N", "LEU", 1.00),
        METN("N", "MET", 1.00),
        PHEN("N", "PHE", 1.00),
        THRN("N", "THR", 1.00),
        THROG1("OG1", "THR", 1.00),
        TRPN("N", "TRP", 1.00),
        TRPNE1("NE1", "TRP", 1.00),
        VALN("N", "VAL", 1.00),
        ALAN("N", "ALA", 1.00),
        ARGN("N", "ARG", 1.00),
        ARGNE("NE", "ARG", 1.00),
        ARGNH1("NH1", "ARG", 1.00),
        ARGNH2("NH2", "ARG", 1.00),
        ASNN("N", "ASN", 1.00),
        ASNND2("ND2", "ASN", 1.00),
        ASPN("N", "ASP", 1.00),
        CYSN("N", "CYS", 1.00),
        GLNN("N", "GLN", 1.00),
        GLNNE2("NE2", "GLN", 1.00),
        GLUN("N", "GLU", 1.00),
        GLYN("N", "GLY", 1.00),
        SERN("N", "SER", 1.00),
        SEROG("OG", "SER", 1.00),
        TYRN("N", "TYR", 1.00),
        TYROH("OH", "TYR", 1.00),

        ABAN("N", "ABA", 1.00),
        BMTN("N", "BMT", 1.00),
        MLEN("N", "MLE", 1.00),
        MVAN("N", "MVA", 1.00),
        SARN("N", "SAR", 1.00),
        BMTOG1("OG1", "BMT", 1.00),

        DCN4("N4" , "DC", 1.0),
        DGN2("N2" , "DG", 1.0),

        GN2("N2" , "G", 1.0),

        DTN3("N3", "DT", 1.0);



        private final String atomName;
        private final String residueName;
        private final double hdonnor;

        AtomHDonnorDescriptors(String atomName, String residueName, double hdonnor) {
            this.atomName = atomName;
            this.residueName = residueName;
            this.hdonnor = hdonnor;
        }

        public String getAtomName() {
            return atomName;
        }

        public String getResidueName() {
            return residueName;
        }

        public double getHdonnor() {
            return hdonnor;
        }
    }


    public enum HydrogenOfAtomHDonnorDescriptors {
        LYSH("H1N", "LYS", 1.00f),
        LYSHZ1("HZ1", "LYS", 1.00f),
        LYSHZ2("HZ2", "LYS", 1.00f),
        LYSHZ3("HZ3", "LYS", 1.00f),

        HISH("H1N", "HIS", 1.00f),
        HISHD1("HD1", "HIS", 1.00f),
        HISHE2("HE2", "HIS", 1.00f),

        ILEH("H1N", "ILE", 1.00f),
        LEUH("H1N", "LEU", 1.00f),
        METH("H1N", "MET", 1.00f),
        PHEH("H1N", "PHE", 1.00f),
        THRH("H1N", "THR", 1.00f),
        THRHG1("HG1", "THR", 1.00f),

        TRPH("H1N", "TRP", 1.00f),
        TRPHE1("HE1", "TRP", 1.00f),
        VALH("H1N", "VAL", 1.00f),
        ALAH("H1N", "ALA", 1.00f),
        ARGH("H1N", "ARG", 1.00f),
        ARGHE("HE", "ARG", 1.00f),
        ARGHH11("HH11", "ARG", 1.00f),
        ARGHH12("HH12", "ARG", 1.00f),
        ARGHH21("HH21", "ARG", 1.00f),
        ARGHH22("HH22", "ARG", 1.00f),
        ASNH("H1N", "ASN", 1.00f),
        ASNHD21("HD21", "ASN", 1.00f),
        ASNHD22("HD22", "ASN", 1.00f),
        ASPH("H1N", "ASP", 1.00f),
        CYSH("H1N", "CYS", 1.00f),
        GLNH("H1N", "GLN", 1.00f),
        GLNHE21("HE21", "GLN", 1.00f),
        GLNHE22("HE22", "GLN", 1.00f),
        GLUH("H1N", "GLU", 1.00f),
        GLYH("H1N", "GLY", 1.00f),
        SERH("H1N", "SER", 1.00f),
        SERHG("HG", "SER", 1.00f),

        TYRH("H1N", "TYR", 1.00f),
        TYRHH("H1N", "TYR", 1.00f),

        ABAH("H1N", "ABA", 1.00f),
        BMTH("H1N", "BMT", 1.00f),
        MLEH("H1N", "MLE", 1.00f),
        MVAH("H1N", "MVA", 1.00f),
        SARH("H1N", "SAR", 1.00f),

        DCH1N4("H1N4", "DC", 1.00f),
        DCH2N4("H2N4", "DC", 1.00f),

        DGH1N2("H1N2", "DG", 1.00f),
        DGH2N2("H2N2", "DG", 1.00f),

        GH1N2("H1N4", "G", 1.00f),
        GH2N2("H2N4", "G", 1.00f),

        DTH1N3("H1N3", "DT", 1.0f);

        private final String atomName;
        private final String residueName;
        private final float hdonnor;

        HydrogenOfAtomHDonnorDescriptors(String atomName, String residueName, float hdonnor) {
            this.atomName = atomName;
            this.residueName = residueName;
            this.hdonnor = hdonnor;
        }

        public String getAtomName() {
            return atomName;
        }

        public String getResidueName() {
            return residueName;
        }

        public float getHdonnor() {
            return hdonnor;
        }
    }


    public enum HydrogenOfAtomHDonnorDescriptorsOld {
        LYSHN("HN", "LYS", 1.00),
        H1NZ("H1NZ", "LYS", 1.00),

        HISHN("HN", "HIS", 1.00),
        H1NE2("H1NE2", "HIS", 1.00),
        H1ND1("H1ND1", "HIS", 1.00),

        ILEHN("HN", "ILE", 1.00),
        LEUHN("HN", "LEU", 1.00),
        METHN("HN", "MET", 1.00),
        PHEHN("HN", "PHE", 1.00),
        THRHN("HN", "THR", 1.00),
        H1OG1("H1OG1", "THR", 1.00),

        TRPHN("HN", "TRP", 1.00),
        H1NE1("H1NE1", "TRP", 1.00),
        VALHN("HN", "VAL", 1.00),
        ALAHN("HN", "ALA", 1.00),
        ARGHN("HN", "ARG", 1.00),
        H1NE("H1NE", "ARG", 1.00),
        H1NH1("H1NH1", "ARG", 1.00),
        H2NH1("H2NH1", "ARG", 1.00),
        H1NH2("H1NH2", "ARG", 1.00),
        H2NH2("H2NH2", "ARG", 1.00),
        ASNHN("HN", "ASN", 1.00),
        H1ND2("H1ND2", "ASN", 1.00),
        H2ND2("H2ND2", "ASN", 1.00),
        ASPHN("HN", "ASP", 1.00),
        CYSHN("HN", "CYS", 1.00),
        GLNHN("HN", "GLN", 1.00),
        GLNH1NE2("H1NE2", "GLN", 1.00),
        H2NE2("H2NE2", "GLN", 1.00),
        GLUHN("HN", "GLU", 1.00),
        GLYHN("HN", "GLY", 1.00),
        SERHN("HN", "SER", 1.00),
        H1OG("H1OG", "SER", 1.00),

        TYRHN("HN", "TYR", 1.00),
        H1OH("H1OH", "TYR", 1.00),

        ABAHN("HN", "ABA", 1.00),
        BMTHN("HN", "BMT", 1.00),
        MLEHN("HN", "MLE", 1.00),
        MVAHN("HN", "MVA", 1.00),
        SARHN("HN", "SAR", 1.00),

        DCN3("N3", "DC", 1.00),
        DCN1("N1", "DC", 1.00),
        DCN4("N4", "DC", 1.00),
        DCO2("O2", "DC", 1.00);


        private final String atomName;
        private final String residueName;
        private final double hdonnor;

        HydrogenOfAtomHDonnorDescriptorsOld(String atomName, String residueName, double hdonnor) {
            this.atomName = atomName;
            this.residueName = residueName;
            this.hdonnor = hdonnor;
        }

        public String getAtomName() {
            return atomName;
        }

        public String getResidueName() {
            return residueName;
        }

        public double getHdonnor() {
            return hdonnor;
        }
    }


    public enum AtomHAcceptorDescriptors {
        LYSO("O", "LYS", 1.00f),
        HISO("O", "HIS", 1.00f),
        ILEO("O", "ILE", 1.00f),
        LEUO("O", "LEU", 1.00f),
        METO("O", "MET", 1.00f),
        PHEO("O", "PHE", 1.00f),
        THRO("O", "THR", 1.00f),
        //THROG1  ("OG1", "THR", 1.00 ),
        TRPO("O", "TRP", 1.00f),
        VALO("O", "VAL", 1.00f),
        ALAO("O", "ALA", 1.00f),
        ARGO("O", "ARG", 1.00f),
        ASNO("O", "ASN", 1.00f),
        ASNOD1("OD1", "ASN", 1.00f),
        ASPO("O", "ASP", 1.00f),
        ASPOD1("OD1", "ASP", 1.00f),
        ASPOD2("OD2", "ASP", 1.00f),
        CYSO("O", "CYS", 1.00f),
        GLNO("O", "GLN", 1.00f),
        GLNOE1("OE1", "GLN", 1.00f),
        GLUO("O", "GLU", 1.00f),
        ASPOE1("OE1", "GLU", 1.00f),
        ASPOE2("OE2", "GLU", 1.00f),
        GLYO("O", "GLY", 1.00f),
        SERO("O", "SER", 1.00f),
        SEROG("OG", "SER", 1.00f),
        TYRO("O", "TYR", 1.00f),
        TYROH("OH", "TYR", 1.00f),
        PROO("O", "PRO", 1.00f),

        ABAO("O", "ABA", 1.00f),
        BMTO("O", "BMT", 1.00f),
        MLEO("O", "MLE", 1.00f),
        MVAO("O", "MVA", 1.00f),
        SARO("O", "SAR", 1.00f),

        // handling of BigResidues
        // HEM
        HEMO1A("O1A", "HEM", 1.00f),
        HEMO1B("O1B", "HEM", 1.00f),
        HEMO1C("O1C", "HEM", 1.00f),
        HEMO1D("O1D", "HEM", 1.00f);


        private final String atomName;
        private final String residueName;
        private final float hacceptor;

        AtomHAcceptorDescriptors(String atomName, String residueName, float hacceptor) {
            this.atomName = atomName;
            this.residueName = residueName;
            this.hacceptor = hacceptor;
        }

        public String getAtomName() {
            return atomName;
        }

        public String getResidueName() {
            return residueName;
        }

        public float getHacceptor() {
            return hacceptor;
        }
    }

    // -------------------------------------------------------------------
    // Static Methods
    // -------------------------------------------------------------------
    public static float findFwhmForMyAtom(MyAtomIfc atom) {

        for (AtomGaussianDescriptors atomGaussianDescriptors : AtomGaussianDescriptors.values()) {

            String currentAtomNameFromAtomGaussianDescriptors = atomGaussianDescriptors.getAtomName();

            if (String.valueOf(atom.getElement()).equals(currentAtomNameFromAtomGaussianDescriptors)) {
                return atomGaussianDescriptors.getSigma();
            }
        }

        System.out.println("failure findSigmaForMyAtom");
        return 0.0f;
    }


    public static Float findHydrogenOfHDonnorForMyAtom(MyAtomIfc atom) {

        String atomName = String.valueOf(atom.getAtomName());
        String monomerName = String.valueOf(atom.getParent().getThreeLetterCode());

        for (HydrogenOfAtomHDonnorDescriptors hydrogenOfAtomHDonnorDescriptors : HydrogenOfAtomHDonnorDescriptors.values()) {

            String currentAtomName = hydrogenOfAtomHDonnorDescriptors.getAtomName();
            String currentMonomerName = hydrogenOfAtomHDonnorDescriptors.getResidueName();

            if (atomName.equals(currentAtomName) && monomerName.equals(currentMonomerName)) {
                return hydrogenOfAtomHDonnorDescriptors.getHdonnor();
            }
        }
        return 0.0f;
    }


    public static Float findhAcceptorForMyAtom(MyAtomIfc atom) {

        String atomName = String.valueOf(atom.getAtomName());
        String monomerName = String.valueOf(atom.getParent().getThreeLetterCode());

        for (AtomHAcceptorDescriptors atomHAcceptorDescriptors : AtomHAcceptorDescriptors.values()) {

            String currentAtomName = atomHAcceptorDescriptors.getAtomName();
            String currentMonomerName = atomHAcceptorDescriptors.getResidueName();

            if (atomName.equals(currentAtomName) && monomerName.equals(currentMonomerName)) {
                return atomHAcceptorDescriptors.getHacceptor();
            }
        }
        return 0.0f;
    }


    public static Float findHydrophobicityForMyAtom(MyAtomIfc atom) {

        String atomNamemonomerName = String.valueOf(atom.getParent().getThreeLetterCode()) + String.valueOf(atom.getAtomName());

        for (AtomHydrophobicityDescriptors atomHydrophobicityDescriptors : AtomHydrophobicityDescriptors.values()) {
            String currentAtomNameFromAtomHydrophobicityDescriptors = atomHydrophobicityDescriptors.getAtomName();

            if (atomNamemonomerName.equals(currentAtomNameFromAtomHydrophobicityDescriptors)) {
                return atomHydrophobicityDescriptors.getHydrophobicity();
            }
        }
        return 0.0f;

    }


    public static Float findChargeForMyAtom(MyAtomIfc atom) {

        String atomNamemonomerName = String.valueOf(atom.getParent().getThreeLetterCode()) + String.valueOf(atom.getAtomName());

        for (AtomChargeDescriptors atomChargeDescriptors : AtomChargeDescriptors.values()) {
            String currentAtomNameFromAtomChargeDescriptors = atomChargeDescriptors.getAtomName();

            if (atomNamemonomerName.equals(currentAtomNameFromAtomChargeDescriptors)) {
                return atomChargeDescriptors.getCharge();
            }
        }
        return 0.0f;
    }


    public static Float findAromaticRingForMyAtom(MyAtomIfc atom) {

        String atomNamemonomerName = String.valueOf(atom.getParent().getThreeLetterCode()) + String.valueOf(atom.getAtomName());

        for (AtomAromaticRingDescriptors atomAromaticRingDescriptors : AtomAromaticRingDescriptors.values()) {
            String currentAtomNameFromAromaticRingDescriptors = atomAromaticRingDescriptors.getAtomName();

            if (atomNamemonomerName.equals(currentAtomNameFromAromaticRingDescriptors)) {
                return atomAromaticRingDescriptors.getAromaticRing();
            }
        }
        return 0.0f;
    }
}
