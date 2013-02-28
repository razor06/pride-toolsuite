package uk.ac.ebi.pride.data.core;

import uk.ac.ebi.pride.term.CvTermReference;

import java.util.List;


//~--- JDK imports ------------------------------------------------------------

/**
 * A peak list including the underlying acquisitions.
 * <p/>
 * In mzML 1.1.0.1, it should the following cv terms:
 * <p/>
 * 1. It may have only one "scan polarity" (negative scan, positive scan).
 * <p/>
 * 2. It must have only one "spectrum type" (charge inversion mass spectrum,
 * constant neutral gain spectrum and et al)
 * <p/>
 * 3. It must have only one "spectrum representation" or any of its children.
 * (centroid spectrum, profile spectrum)
 * <p/>
 * 4. It may have one or more "spectrum attribute" (total ion current,
 * zoom scan, base peak m/z, ms level, spectrum title and etc al)
 * <p/>
 * User: rwang
 * Date: 03-Feb-2010
 * Time: 08:46:05
 */
public class Spectrum extends MzGraph {

    /**
     * peptide associate with this spectrum
     */
    private Peptide peptide = null;

    /**
     * list and descriptions of precursor isolations to this spectrum
     */
    private List<Precursor> precursors = null;

    /**
     * list and descriptions of product isolations to this spectrum
     */
    private List<ParamGroup> products = null;

    /**
     * list and descriptions of scans
     */
    private ScanList scanList = null;

    /**
     * source file
     */
    private SourceFile sourceFile = null;

    /**
     * the identifier for the spot on a MALDI or similar on
     */
    private String spotID = null;


    /**
     * @param id       Generic Id
     * @param name     Generic Name
     * @param index    Spectrum Index
     * @param defaultDataProcessing  Default Data Processing
     * @param defaultArrayLength     Default Array Length
     * @param binaryDataArrays       List of BinaryDataArrays
     * @param spotID                 Spot Id
     * @param sourceFile             Source File
     * @param scanList               Scan List
     * @param precursors             List of Precursors
     * @param products               Products
     * @param peptide                Peptide Assigned to Spectrum
     */
    public Spectrum(Comparable id, String name, int index, DataProcessing defaultDataProcessing,
                    int defaultArrayLength, List<BinaryDataArray> binaryDataArrays, String spotID,
                    SourceFile sourceFile, ScanList scanList, List<Precursor> precursors, List<ParamGroup> products,
                    Peptide peptide) {
        this(null, id, name, index, defaultDataProcessing, defaultArrayLength, binaryDataArrays, spotID, sourceFile,
             scanList, precursors, products, peptide);
    }

    /**
     * @param params   ParamGroup related with the Spectrum
     * @param id       Generic Id
     * @param name     Generic Name
     * @param index    Spectrum Index
     * @param defaultDataProcessing  Default Data Processing
     * @param defaultArrayLength     Default Array Length
     * @param binaryDataArrays       List of BinaryDataArrays
     * @param spotID                 Spot Id
     * @param sourceFile             Source File
     * @param scanList               Scan List
     * @param precursors             List of Precursors
     * @param products               Products
     */
    public Spectrum(ParamGroup params, Comparable id, String name, int index, DataProcessing defaultDataProcessing,
                    int defaultArrayLength, List<BinaryDataArray> binaryDataArrays, String spotID,
                    SourceFile sourceFile, ScanList scanList, List<Precursor> precursors, List<ParamGroup> products) {
        this(params, id, name, index, defaultDataProcessing, defaultArrayLength, binaryDataArrays, spotID, sourceFile,
             scanList, precursors, products, null);
    }

    /**
     * @param params   ParamGroup related with the Spectrum
     * @param id       Generic Id
     * @param name     Generic Name
     * @param index    Spectrum Index
     * @param defaultDataProcessing  Default Data Processing
     * @param defaultArrayLength     Default Array Length
     * @param binaryDataArrays       List of BinaryDataArrays
     * @param spotID                 Spot Id
     * @param sourceFile             Source File
     * @param scanList               Scan List
     * @param precursors             List of Precursors
     * @param products               Products
     * @param peptide                Peptide Assigned to Spectrum
     */
    public Spectrum(ParamGroup params, Comparable id, String name, int index, DataProcessing defaultDataProcessing,
                    int defaultArrayLength, List<BinaryDataArray> binaryDataArrays, String spotID,
                    SourceFile sourceFile, ScanList scanList, List<Precursor> precursors, List<ParamGroup> products,
                    Peptide peptide) {
        super(params, id, name, index, defaultDataProcessing, defaultArrayLength, binaryDataArrays);
        this.spotID     = spotID;
        this.sourceFile = sourceFile;
        this.scanList   = scanList;
        this.precursors = precursors;
        this.products   = products;
        this.peptide    = peptide;
    }

    public String getSpotID() {
        return spotID;
    }

    public void setSpotID(String spotID) {
        this.spotID = spotID;
    }

    public SourceFile getSourceFile() {
        return sourceFile;
    }

    public void setSourceFile(SourceFile sourceFile) {
        this.sourceFile = sourceFile;
    }

    public ScanList getScanList() {
        return scanList;
    }

    public void setScanList(ScanList scanList) {
        this.scanList = scanList;
    }

    public List<Precursor> getPrecursors() {
        return precursors;
    }

    public void setPrecursors(List<Precursor> precursors) {
        this.precursors = precursors;
    }

    public List<ParamGroup> getProducts() {
        return products;
    }

    public void setProducts(List<ParamGroup> products) {
        this.products = products;
    }

    public BinaryDataArray getMzBinaryDataArray() {
        return getBinaryDataArray(CvTermReference.MZ_ARRAY.getAccession());
    }

    public BinaryDataArray getIntensityBinaryDataArray() {
        return getBinaryDataArray(CvTermReference.INTENSITY_ARRAY.getAccession());
    }

    public Peptide getPeptide() {
        return peptide;
    }

    public void setPeptide(Peptide peptide) {
        this.peptide = peptide;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;

        Spectrum spectrum = (Spectrum) o;

        return !(peptide != null ? !peptide.equals(spectrum.peptide) : spectrum.peptide != null) && !(precursors != null ? !precursors.equals(spectrum.precursors) : spectrum.precursors != null) && !(products != null ? !products.equals(spectrum.products) : spectrum.products != null) && !(scanList != null ? !scanList.equals(spectrum.scanList) : spectrum.scanList != null) && !(sourceFile != null ? !sourceFile.equals(spectrum.sourceFile) : spectrum.sourceFile != null) && !(spotID != null ? !spotID.equals(spectrum.spotID) : spectrum.spotID != null);

    }

    @Override
    public int hashCode() {
        int result = super.hashCode();
        result = 31 * result + (peptide != null ? peptide.hashCode() : 0);
        result = 31 * result + (precursors != null ? precursors.hashCode() : 0);
        result = 31 * result + (products != null ? products.hashCode() : 0);
        result = 31 * result + (scanList != null ? scanList.hashCode() : 0);
        result = 31 * result + (sourceFile != null ? sourceFile.hashCode() : 0);
        result = 31 * result + (spotID != null ? spotID.hashCode() : 0);
        return result;
    }
}



