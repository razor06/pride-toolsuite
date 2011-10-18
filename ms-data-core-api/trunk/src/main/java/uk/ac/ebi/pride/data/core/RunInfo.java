package uk.ac.ebi.pride.data.core;

/**
 * ToDo: document this class
 * <p/>
 * User: rwang, yperez
 * Date: 03/08/11
 * Time: 10:23
 */
public class RunInfo extends ParamGroup {

    private MzGraphList chromatogramList = null;

    /**
     * default instrument configuration, important to have an overview of the instrument
     * configuration used in the experiment
     */
    private InstrumentConfiguration defaultInstrumentConfiguration = null;

    /**
     * Source file used to extract all the mass spectrum or chromatograms.
     */
    private SourceFile defaultSourceFile = null;

    /**
     * identifier of each run
     */
    private String id = null;

    /**
     * Reference to the sample used to obtain these spectrum or chromatograms list.
     */
    private Sample      sampleRef    = null;

    private MzGraphList spectrumList = null;

    /**
     * Timedate of the experiment measure
     */
    private String timeStamp = null;

    public RunInfo(ParamGroup params, String id, InstrumentConfiguration defaultInstrumentConfiguration,
                   SourceFile defaultSourceFile, Sample sampleRef, String timeStamp, MzGraphList spectrumList,
                   MzGraphList chromatogramList) {
        super(params);
        this.id                             = id;
        this.defaultInstrumentConfiguration = defaultInstrumentConfiguration;
        this.defaultSourceFile              = defaultSourceFile;
        this.sampleRef                      = sampleRef;
        this.timeStamp                      = timeStamp;
        this.spectrumList                   = spectrumList;
        this.chromatogramList               = chromatogramList;
    }

    public MzGraphList getSpectrumList() {
        return spectrumList;
    }

    public void setSpectrumList(MzGraphList spectrumList) {
        this.spectrumList = spectrumList;
    }

    public MzGraphList getChromatogramList() {
        return chromatogramList;
    }

    public void setChromatogramList(MzGraphList chromatogramList) {
        this.chromatogramList = chromatogramList;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public InstrumentConfiguration getDefaultInstrumentConfiguration() {
        return defaultInstrumentConfiguration;
    }

    public void setDefaultInstrumentConfiguration(InstrumentConfiguration defaultInstrumentConfiguration) {
        this.defaultInstrumentConfiguration = defaultInstrumentConfiguration;
    }

    public SourceFile getDefaultSourceFile() {
        return defaultSourceFile;
    }

    public void setDefaultSourceFile(SourceFile defaultSourceFile) {
        this.defaultSourceFile = defaultSourceFile;
    }

    public Sample getSampleRef() {
        return sampleRef;
    }

    public void setSampleRef(Sample sampleRef) {
        this.sampleRef = sampleRef;
    }

    public String getTimeStamp() {
        return timeStamp;
    }

    public void setTimeStamp(String timeStamp) {
        this.timeStamp = timeStamp;
    }
}


