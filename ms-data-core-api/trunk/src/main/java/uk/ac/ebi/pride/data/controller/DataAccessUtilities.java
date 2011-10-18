package uk.ac.ebi.pride.data.controller;

import uk.ac.ebi.pride.data.core.*;
import uk.ac.ebi.pride.engine.SearchEngineType;
import uk.ac.ebi.pride.term.CvTermReference;
import uk.ac.ebi.pride.util.NumberUtilities;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * DataAccessUtilities provides methods for getting information out from the core objects.
 * <p/>
 * User: rwang
 * Date: 30-Aug-2010
 * Time: 11:52:45
 */
public class DataAccessUtilities {

    /**
     * Get a list of taxonomy accessions based on a given metadata
     *
     * @param metaData meta data
     * @return List<String>    a list of taxonomy ids
     */
    public static List<String> getTaxonomy(ExperimentMetaData metaData) {
        List<String> species = new ArrayList<String>();
        List<Sample> samples = metaData.getSampleList();
        if (samples != null) {
            for (Sample sample : samples) {
                List<CvParam> cvParams = sample.getCvParams();
                if (cvParams != null) {
                    for (CvParam cvParam : cvParams) {
                        if (cvParam.getCvLookupID().toLowerCase().equals("newt")) {
                            species.add(cvParam.getAccession());
                        }
                    }
                }
            }
        }
        return species;
    }

    /**
     * Get the project of a experiment
     *
     * @param metaData experiment metadata
     * @return String project name
     */
    public static String getProjectName(ExperimentMetaData metaData) {
        String project = null;

        List<CvParam> cvParams = metaData.getCvParams();
        for (CvParam cvParam : cvParams) {
            if (CvTermReference.PROJECT_NAME.getAccession().equals(cvParam.getAccession())) {
                project = cvParam.getValue();
            }
        }

        return project;
    }

    /**
     * Count the number of peaks of a spectrum
     *
     * @param spectrum spectrum
     * @return int number of peaks
     */
    public static int getNumberOfPeaks(Spectrum spectrum) {
        int numOfPeaks = -1;
        BinaryDataArray mzArr = spectrum.getBinaryDataArrays().get(0);
        if (mzArr != null) {
            numOfPeaks = mzArr.getDoubleArray().length;
        }
        return numOfPeaks;
    }

    /**
     * Get ms level of a spectrum
     *
     * @param spectrum spectrum
     * @return int ms level
     */
    public static int getMsLevel(Spectrum spectrum) {
        int msLevel = -1;
        List<Parameter> param = getParamByName(spectrum, "ms level");
        if (!param.isEmpty()) {
            String val = param.get(0).getValue();
            msLevel = Integer.parseInt(val);
        }
        return msLevel;
    }

    /**
     * Get precursor charge
     *
     * @param spectrum spectrum
     * @return int precursor charge
     */
    public static int getPrecursorCharge(Spectrum spectrum) {
        int charge = 0;
        List<Precursor> precursors = spectrum.getPrecursors();
        if (precursors != null && !precursors.isEmpty()) {
            Double c = getSelectedIonCharge(precursors.get(0), 0);
            if (c != null) {
                charge = c.intValue();
            }
        }
        return charge;
    }

    /**
     * Get precursor m/z value
     *
     * @param spectrum spectrum
     * @return double  precursor m/z
     */
    public static double getPrecursorMz(Spectrum spectrum) {
        double mz = -1;
        List<Precursor> precursors = spectrum.getPrecursors();
        if (precursors != null && !precursors.isEmpty()) {
            Double m = getSelectedIonMz(precursors.get(0), 0);
            if (m != null) {
                mz = m;
            }
        }
        return mz;
    }

    /**
     * Get precursor intensity
     *
     * @param spectrum spectrum
     * @return double  precursor intensity
     */
    public static double getPrecursorIntensity(Spectrum spectrum) {
        double intent = -1;
        List<Precursor> precursors = spectrum.getPrecursors();
        if (precursors != null && !precursors.isEmpty()) {
            Double it = getSelectedIonIntensity(precursors.get(0), 0);
            if (it != null) {
                intent = it;
            }
        }
        return intent;
    }

    /**
     * Get the sum of all the peak intensities within a spectrum
     *
     * @param spectrum spectrum
     * @return double  sum of intensities
     */
    public static double getSumOfIntensity(Spectrum spectrum) {
        double sum = 0;
        BinaryDataArray intentArr = spectrum.getIntensityBinaryDataArray();
        if (intentArr != null) {
            double[] originalIntentArr = intentArr.getDoubleArray();
            for (double intent : originalIntentArr) {
                sum += intent;
            }
        }
        return sum;
    }

    /**
     * Get the ion charge for a selected ion.
     *
     * @param precursor precursor
     * @param index     index of the selected ion.
     * @return Double   selected ion charge.
     */
    public static Double getSelectedIonCharge(Precursor precursor, int index) {
        return getSelectedIonCvParamValue(precursor, index,
                CvTermReference.PSI_ION_SELECTION_CHARGE_STATE, CvTermReference.ION_SELECTION_CHARGE_STATE);
    }

    /**
     * Get the ion m/z value for a selected ion.
     *
     * @param precursor precursor
     * @param index     index of the selected ion.
     * @return Double   selected ion m/z.
     */
    public static Double getSelectedIonMz(Precursor precursor, int index) {
        return getSelectedIonCvParamValue(precursor, index,
                CvTermReference.PSI_ION_SELECTION_MZ, CvTermReference.ION_SELECTION_MZ);
    }


    /**
     * Get the ion intensity value for a selected ion.
     *
     * @param precursor precursor
     * @param index     index of the selected ion.
     * @return Double   selected ion intensity.
     */
    public static Double getSelectedIonIntensity(Precursor precursor, int index) {
        return getSelectedIonCvParamValue(precursor, index,
                CvTermReference.PSI_ION_SELECTION_INTENSITY, CvTermReference.ION_SELECTION_INTENSITY);
    }

    /**
     * Get value of selected ion cv param.
     *
     * @param precursor precursor
     * @param index     index of the selecte ion.
     * @param refs      a list of possible cv terms to search for.
     * @return Double   value of the cv parameter.
     */
    private static Double getSelectedIonCvParamValue(Precursor precursor, int index, CvTermReference... refs) {
        Double value = null;
        List<ParamGroup> selectedIons = precursor.getSelectedIons();
        if (index >= 0 && selectedIons != null && index < selectedIons.size()) {
            ParamGroup selectedIon = selectedIons.get(index);
            // search PRIDE xml based charge
            for (CvTermReference ref : refs) {
                List<CvParam> cvParams = getCvParam(selectedIon, ref.getCvLabel(), ref.getAccession());
                if (cvParams != null && !cvParams.isEmpty()) {
                    value = new Double(cvParams.get(0).getValue());
                }
            }
        }
        return value;
    }

    /**
     * This is convenient method for accessing peptide.
     *
     * @param ident identification object
     * @param index zero based index.
     * @return Peptide  peptide.
     */
    public static Peptide getPeptide(Identification ident, int index) {
        Peptide peptide = null;
        List<Peptide> peptides = ident.getIdentifiedPeptides();
        if (peptides != null && peptides.size() > index) {
            peptide = peptides.get(index);
        }
        return peptide;
    }

    /**
     * Convenient method for number of peptides
     *
     * @param ident identification object
     * @return int number of peptides
     */
    public static int getNumberOfPeptides(Identification ident) {
        List<Peptide> peptides = ident.getIdentifiedPeptides();
        return peptides == null ? 0 : peptides.size();
    }

    /**
     * Convenient method for getting the number of unique peptides.
     *
     * @param ident identification object
     * @return int  number of unique peptide sequences.
     */
    public static int getNumberOfUniquePeptides(Identification ident) {
        List<PeptideSequence> peptides = ident.getPeptidesSequence();
        int cnt = 0;
        if (peptides == null) {
            return cnt;
        } else {
            List<String> seqs = new ArrayList<String>();
            for (PeptideSequence peptide : peptides) {
                String seq = peptide.getSequence();
                if (!seqs.contains(seq)) {
                    seqs.add(seq);
                    cnt++;
                }
            }
            return cnt;
        }
    }

    /**
     * Check whether spectrum has fragment ion information
     *
     * @param spectrum spectrum
     * @return boolean true if spectrum contains fragment ion information.
     */
    public static boolean hasFragmentIon(Spectrum spectrum) {
        Peptide peptide = spectrum.getPeptide();
        return peptide != null && hasFragmentIon(peptide);
    }


    /**
     * Check whether peptide has fragment ion information
     *
     * @param peptide peptide
     * @return boolean true if peptide contains fragment ion information.
     */
    public static boolean hasFragmentIon(Peptide peptide) {
        List<FragmentIon> ions = peptide.getFragmentation();
        return !ions.isEmpty();
    }

    /**
     * Get the number of post translational modifications within a protein identification
     *
     * @param ident protein identification
     * @return int number of ptms
     */
    public static int getNumberOfPTMs(Identification ident) {
        int cnt = 0;
        List<Peptide> peptides = ident.getIdentifiedPeptides();
        for (Peptide peptide : peptides) {
            List<Modification> mods = peptide.getPeptideSequence().getModificationList();
            if (mods != null) {
                cnt += mods.size();
            }
        }
        return cnt;
    }

    /**
     * Get the number of Substitution PTMs for an specific Protein Identification
     * @param ident ID of the Protein Identification
     * @return Total number of Substitution PTMs
     */
    public static int getNumberOfSubstitutionPTMs(Identification ident) {
        int cnt = 0;
        List<Peptide> peptides = ident.getIdentifiedPeptides();
        for (Peptide peptide : peptides) {
            List<SubstitutionModification> mods = peptide.getPeptideSequence().getSubstitutionModificationList();
            if (mods != null) {
                cnt += mods.size();
            }
        }
        return cnt;
    }

    /**
     * Get the number of the modification of a peptide
     *
     * @param peptide  Peptide Object
     * @return Total number of PTMs
     */
    public static int getNumberOfPTMs(Peptide peptide) {
        int cnt = 0;
        List<Modification> mods = peptide.getPeptideSequence().getModificationList();
        if (mods != null) {
            cnt = mods.size();
        }
        return cnt;
    }

    /**
     * Get the number of substitution modification of a peptide
     *
     * @param peptide Peptide
     * @return Total number of Substitution PTMs
     */
    public static int getNumberOfSubstitutionPTMs(Peptide peptide) {
        int cnt = 0;
        List<SubstitutionModification> mods = peptide.getPeptideSequence().getSubstitutionModificationList();
        if (mods != null) {
            cnt = mods.size();
        }
        return cnt;
    }

    /**
     * Get peptide score from a peptide object.
     *
     * @param paramGroup parameter group
     * @param seTypes    a list of search engine types
     * @return PeptideScore  peptide score
     */
    public static PeptideScore getPeptideScore(ParamGroup paramGroup, List<SearchEngineType> seTypes) {
        if (paramGroup == null || seTypes == null) {
            throw new IllegalArgumentException("Input arguments for getPeptideScore can not be null");
        }

        PeptideScore score = new PeptideScore();
        for (SearchEngineType type : seTypes) {
            List<CvTermReference> scoreCvTerms = type.getSearchEngineScores();
            for (CvTermReference scoreCvTerm : scoreCvTerms) {
                List<CvParam> scoreParams = getCvParam(paramGroup, scoreCvTerm.getCvLabel(), scoreCvTerm.getAccession());
                if (!scoreParams.isEmpty()) {
                    // Note: only take the first param as the valid score
                    CvParam scoreParam = scoreParams.get(0);
                    String numStr = scoreParam.getValue();
                    if (NumberUtilities.isNumber(numStr)) {
                        Double num = new Double(numStr);
                        score.addPeptideScore(type, scoreCvTerm, num);
                    } else {
                        score.addPeptideScore(type, scoreCvTerm, null);
                    }
                } else {
                    score.addPeptideScore(type, scoreCvTerm, null);
                }
            }
        }

        return score;
    }

    /**
     * Search and find a list of search engine types from input parameter group.
     *
     * @param paramGroup parameter group
     * @return List<SearchEngineType>  a list of search engine
     */
    public static List<SearchEngineType> getSearchEngineTypes(ParamGroup paramGroup) {
        if (paramGroup == null) {
            throw new IllegalArgumentException("Input argument for getSearchEngineTypes can not be null");
        }

        List<SearchEngineType> types = new ArrayList<SearchEngineType>();
        for (SearchEngineType type : SearchEngineType.values()) {
            for (CvTermReference scoreCvTerm : type.getSearchEngineScores()) {
                if (!getCvParam(paramGroup, scoreCvTerm.getCvLabel(), scoreCvTerm.getAccession()).isEmpty()) {
                    types.add(type);
                    break;
                }
            }
        }

        return types;
    }


    /**
     * Get cv param by accession number and cv label.
     *
     * @param paramGroup parameter group
     * @param cvLabel    cv label.
     * @param accession  cv accession.
     * @return CvParam  cv param.
     */
    public static List<CvParam> getCvParam(ParamGroup paramGroup, String cvLabel, String accession) {
        if (paramGroup == null || cvLabel == null || accession == null) {
            throw new IllegalArgumentException("Input arguments for getCvParam can not be null");
        }
        List<CvParam> cvParams = paramGroup.getCvParams();
        List<CvParam> cps = new ArrayList<CvParam>();
        if (cvParams != null) {
            for (CvParam param : cvParams) {
                if (param.getAccession().toLowerCase().equals(accession.toLowerCase())
                        && param.getCvLookupID().toLowerCase().equals(cvLabel.toLowerCase())) {
                    cps.add(param);
                }
            }
        }
        return cps;
    }

    /**
     * Create a List of Cv Params
     *
     * @param value Value of the CvParam
     * @param cvLabel Label of the CvParam
     * @param accession Accession of the CvParam
     * @param name Name
     * @return List<CvParam>
     */
    public static List<CvParam> getCvParam(String name, String cvLabel, String accession, String value) {
        List<CvParam> cvParams = new ArrayList<CvParam>();
        CvParam cvParam = new CvParam(accession, name, cvLabel, value, null, null, null);
        cvParams.add(cvParam);
        return cvParams;
    }

    /**
     * Get a list parameters using a given name.
     *
     * @param paramGroup parameter group
     * @param name name string
     * @return List<Parameter> a list of parameters
     */
    public static List<Parameter> getParamByName(ParamGroup paramGroup, String name) {
        if (paramGroup == null || name == null) {
            throw new IllegalArgumentException("Input arguments for getParamByName can not be null");
        }
        List<Parameter> params = new ArrayList<Parameter>();

        List<CvParam> cvParams = paramGroup.getCvParams();
        if (cvParams != null) {
            for (CvParam cvParam : cvParams) {
                if (cvParam.getName().toLowerCase().equals(name.toLowerCase())) {
                    params.add(cvParam);
                }
            }
        }

        List<UserParam> userParams = paramGroup.getUserParams();
        if (userParams != null) {
            for (UserParam userParam : userParams) {
                if (userParam.getName().toLowerCase().equals(name.toLowerCase())) {
                    params.add(userParam);
                }
            }
        }

        return params;
    }

    /**
     * Get peptide Evidence for a Lis of Peptide Identifications
     *
     * @param peptides List of Peptide Identifications
     * @return Map with Peptide Evidence an the List of Peptide Identifications related with this peptide Evidence
     */
    public static Map<PeptideEvidence, List<Peptide>> getPeptideEvidence(List<Peptide> peptides) {
        HashMap<PeptideEvidence, List<Peptide>> peptideEvidences = new HashMap<PeptideEvidence, List<Peptide>>();
        for (Peptide peptide : peptides) {
            for (PeptideEvidence peptideEvidence : peptide.getPeptideEvidenceList()) {
                if (peptideEvidences.containsKey(peptideEvidence)) {
                    List<Peptide> peptidesIn = peptideEvidences.get(peptideEvidence);
                    peptidesIn.add(peptide);
                    peptideEvidences.put(peptideEvidence, peptidesIn);
                } else {
                    List<Peptide> peptidesIn = new ArrayList<Peptide>();
                    peptidesIn.add(peptide);
                    peptideEvidences.put(peptideEvidence, peptidesIn);
                }
            }
        }
        return peptideEvidences;
    }
}