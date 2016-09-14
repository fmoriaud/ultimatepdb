package parameters;

public class QueryAtomDefinedByIds {
    //------------------------
    // Class variables
    //------------------------
    private String fourLetterCode = "2fx7";
    private String chainQuery = "L";
    private int residueId = 161;
    private String atomName = "OE2";
    private float radiusForQueryAtomsDefinedByIds;



    // -------------------------------------------------------------------
    // Constructor
    // -------------------------------------------------------------------
    public QueryAtomDefinedByIds(String fourLetterCode, String chainQuery, int residueId, String atomName, float radiusForQueryAtomsDefinedByIds) {
        this.fourLetterCode = fourLetterCode;
        this.chainQuery = chainQuery;
        this.residueId = residueId;
        this.atomName = atomName;
        this.radiusForQueryAtomsDefinedByIds = radiusForQueryAtomsDefinedByIds;
    }


    // -------------------------------------------------------------------
    // Override
    // -------------------------------------------------------------------
    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(fourLetterCode + " " + chainQuery + " " + residueId + " " + atomName + " " + radiusForQueryAtomsDefinedByIds);
        return sb.toString();
    }


    // -------------------------------------------------------------------
    // Getters & Setters
    // -------------------------------------------------------------------
    public String getFourLetterCode() {
        return fourLetterCode;
    }


    public String getChainQuery() {
        return chainQuery;
    }


    public int getResidueId() {
        return residueId;
    }


    public String getAtomName() {
        return atomName;
    }

    public float getRadiusForQueryAtomsDefinedByIds() {
        return radiusForQueryAtomsDefinedByIds;
    }
}
