package uk.ac.ebi.pride.data.controller;

import uk.ac.ebi.pride.data.controller.access.*;
import uk.ac.ebi.pride.data.core.*;
import uk.ac.ebi.pride.gui.utils.PropertyChangeHandler;

import java.beans.PropertyChangeListener;
import java.util.Collection;

/**
 * DataAccessController is an aggregate interface for data access.
 * It extends a list of interfaces, also added methods for accessing metadata.
 * Setting the state of the data access controller.
 * <p/>
 * User: rwang
 * Date: 09-Apr-2010
 * Time: 14:25:49
 */
public interface DataAccessController extends MzGraphDataAccess, ProteinDataAccess,
                                              TrackDataAccess, PeptideDataAccess,
                                              CacheAccess, ChartAccess, PropertyChangeHandler,
                                              PropertyChangeListener {
    /**
     * Type indicates the type of the data source.
     * There are two types of data access controller at the moment:
     *
     * DATABASE means connection to a database
     * XML_FILE means reading the data from a file.
     */
    public enum Type {
        DATABASE, XML_FILE
    }

    /**
     * ContentCategory defines the type of content a data access controller should have.
     * Note: this does not automatically mean they must have them.
     */
    public enum ContentCategory {
        SPECTRUM, CHROMATOGRAM, PROTEIN, PEPTIDE
    }

    /**
     * fired when data controller is reloaded
     */
    public static final String DATA_SOURCE_RELOADED = "dataSourceReloaded";
    /**
     * fired when data controller is closed
     */
    public static final String DATA_SOURCE_CLOSED = "dataSourceClosed";

    /** ========================================= Description section =========================================*/

    /**
     * Get the display name for this controller, for GUI
     *
     * @return String the name of this DataAccessController
     */
    public String getName();

    /**
     * Set the name of this DataAccessionController
     *
     * @param name the new name for this DataAccessController
     */
    public void setName(String name);

    /**
     * Get the type of database access controller.
     *
     * @return DataAccessController.Type controller type.
     */
    public Type getType();

    /**
     * Set the type of database access controller.
     *
     * @param type controller type.
     */
    public void setType(Type type);

    /**
     * Return a collection of content categories
     *
     * @return Collection<ContentCategory>  a collection of content categories.
     */
    public Collection<ContentCategory> getContentCategories();

    /**
     * Add a array of content categories.
     *
     * @param categories    a array of categories.
     */
    public void setContentCategories(ContentCategory ... categories);

    /**
     * Get the description for this controller, for GUI
     *
     * @return String   the description of this controller
     */
    public String getDescription();

    /**
     * Set the description for this controller, for GUI
     *
     * @param desc the new description for the controller
     */
    public void setDescription(String desc);

    /**
     * Get the original data source object
     *
     * @return Object   data source object
     */
    public Object getSource();

    /**
     * Set the orginal data source object
     *
     * @param src data source object
     */
    public void setSource(Object src);

    /**
     * Get the data access mode
     *
     * @return DataAccessMode   data access mode
     */
    public DataAccessMode getMode();

    /**
     * Set the data access mode
     *
     * @param mode data access mode
     */
    public void setMode(DataAccessMode mode);

    /**
     * shutdown this controller, release all the resources.
     */
    public void close();

    /** ========================================= Metadata accession section =========================================*/
    /**
     * Get an collection of experiment ids from data source
     *
     * @return Collection<Comparable>   a string collection of experiment ids
     * @throws DataAccessException throw a exception when there is an error accessing the data source
     */
    public Collection<Comparable> getExperimentAccs() throws DataAccessException;

    /**
     * Get a meta object
     *
     * @return MetaData meta data object
     * @throws DataAccessException throw a exception when there is an error accessing the data source
     */
    public MetaData getMetaData() throws DataAccessException;

    /**
     * Get an collection of cv lookups
     *
     * @return Collection<CVLookup> a collection of cv lookups
     * @throws DataAccessException throw a exception when there is an error accessing the data source
     */
    public Collection<CVLookup> getCvLookups() throws DataAccessException;

    /**
     * Get file description object.
     *
     * @return FileDescription  FileDescription object
     * @throws DataAccessException throw a exception when there is an error accessing the data source
     */
    public FileDescription getFileDescription() throws DataAccessException;

    /**
     * Get the referenceable param group
     *
     * @return ReferenceableParamGroup  the referenceable param group
     * @throws DataAccessException throw a exception when there is an error accessing the data source
     */
    public ReferenceableParamGroup getReferenceableParamGroup() throws DataAccessException;

    /**
     * Get an collection of samples.
     *
     * @return Collection<Sample>   an colleciton of samples.
     * @throws DataAccessException throw a exception when there is an error accessing the data source
     */
    public Collection<Sample> getSamples() throws DataAccessException;

    /**
     * Get an collection of softwares.
     *
     * @return Collection<Software> an collection of softwares.
     * @throws DataAccessException throw a exception when there is an error accessing the data source
     */
    public Collection<Software> getSoftware() throws DataAccessException;

    /**
     * Get an collection of scan settings.
     *
     * @return Collection<ScanSetting>  an collection of scan settings.
     * @throws DataAccessException throw a exception when there is an error accessing the data source
     */
    public Collection<ScanSetting> getScanSettings() throws DataAccessException;

    /**
     * Get an collection of instruments.
     *
     * @return Collection<Instrument>   an collection of instruments.
     * @throws DataAccessException throw a exception when there is an error accessing the data source
     */
    public Collection<InstrumentConfiguration> getInstrumentConfigurations() throws DataAccessException;

    /**
     * Get an collection of data processings.
     *
     * @return Collection<DataProcessing>   an collection of data processings.
     * @throws DataAccessException throw a exception when there is an error accessing the data source
     */
    public Collection<DataProcessing> getDataProcessings() throws DataAccessException;

    //Todo: getDefaultInstrument() from mzML's run element
    //Todo: getDefaultSample() from mzML's run element
    //Todo: getDefaultSpectrumDataProcessing() from mzML's spectrumList element
    //Todo: getDefaultChromatogramDataProcessing() from mzML's chromatogramList element
}
