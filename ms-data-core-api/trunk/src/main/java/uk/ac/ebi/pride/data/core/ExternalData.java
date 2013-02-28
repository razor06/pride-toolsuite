package uk.ac.ebi.pride.data.core;

/**
 * Data external to the XML instance document.
 * The location of the data file is given in the location attribute.
 *
 * User: yperez
 * Date: 09/08/11
 * Time: 14:35
 */
public class ExternalData extends Identifiable {

    /**
     * The format of the ExternalData file, for example "tiff" for image files.
     */
    private CvParam FileFormat = null;

    /**
     * A URI to access documentation and tools to interpret the external format
     * of the ExternalData instance. For example, XML Schema or static libraries
     * (APIs) to access binary formats.
     */
    private String externalFormatDocumentationURI = null;

    /**
     * The location of the data file.
     */
    private String location = null;

    /**
     * Constructor of External Data Objects
     *
     * @param id  ID
     * @param name Name
     * @param location location of the external resource
     * @param fileFormat CvTerm to define the FileFormat
     * @param externalFormatDocumentationURI External uri of the FileFormat Documentation
     */
    public ExternalData(String id, String name, String location, CvParam fileFormat,
                        String externalFormatDocumentationURI) {
        super(id, name);
        this.location                       = location;
        FileFormat                          = fileFormat;
        this.externalFormatDocumentationURI = externalFormatDocumentationURI;
    }

    /**
     * Get the location of the External Resource
     *
     * @return location of the External Resource
     */
    public String getLocation() {
        return location;
    }

    /**
     * Set the location of the External Resource
     *
     * @param location location of the External Resource
     */
    public void setLocation(String location) {
        this.location = location;
    }

    /**
     * Get CvParam that define the FileFormat
     *
     * @return CvParam
     */
    public CvParam getFileFormat() {
        return FileFormat;
    }

    /**
     * Set CvParam that define the FileFormat
     *
     * @param fileFormat CvParam that define the FileFormat
     */
    public void setFileFormat(CvParam fileFormat) {
        FileFormat = fileFormat;
    }

    /**
     * Get the uri of the FileFormat Documentation
     *
     * @return uri of the FileFormat Documentation
     */
    public String getExternalFormatDocumentationURI() {
        return externalFormatDocumentationURI;
    }

    /**
     * Set the uri of the FileFormat Documentation
     *
     * @param externalFormatDocumentationURI uri of the FileFormat Documentation
     */
    public void setExternalFormatDocumentationURI(String externalFormatDocumentationURI) {
        this.externalFormatDocumentationURI = externalFormatDocumentationURI;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;

        ExternalData that = (ExternalData) o;

        return !(FileFormat != null ? !FileFormat.equals(that.FileFormat) : that.FileFormat != null) && !(externalFormatDocumentationURI != null ? !externalFormatDocumentationURI.equals(that.externalFormatDocumentationURI) : that.externalFormatDocumentationURI != null) && !(location != null ? !location.equals(that.location) : that.location != null);

    }

    @Override
    public int hashCode() {
        int result = super.hashCode();
        result = 31 * result + (FileFormat != null ? FileFormat.hashCode() : 0);
        result = 31 * result + (externalFormatDocumentationURI != null ? externalFormatDocumentationURI.hashCode() : 0);
        result = 31 * result + (location != null ? location.hashCode() : 0);
        return result;
    }
}



