
package uk.ac.ebi.pride.jaxb.model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;


/**
 * 'Header' information -- sample description, contact details, comments
 * 
 * <p>Java class for adminType complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="adminType">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="sampleName">
 *           &lt;simpleType>
 *             &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string">
 *               &lt;minLength value="1"/>
 *             &lt;/restriction>
 *           &lt;/simpleType>
 *         &lt;/element>
 *         &lt;element name="sampleDescription" type="{}sampleDescriptionType" minOccurs="0"/>
 *         &lt;element name="sourceFile" type="{}sourceFileType" minOccurs="0"/>
 *         &lt;element name="contact" type="{}contactType" maxOccurs="unbounded"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "adminType", propOrder = {
    "sampleName",
    "sampleDescription",
    "sourceFile",
    "contact"
})
public class Admin
    implements Serializable, PrideXmlObject
{

    private final static long serialVersionUID = 100L;
    @XmlElement(required = true)
    protected String sampleName;
    protected SampleDescription sampleDescription;
    protected SourceFile sourceFile;
    @XmlElement(required = true)
    protected List<Contact> contact;

    /**
     * Gets the value of the sampleName property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getSampleName() {
        return sampleName;
    }

    /**
     * Sets the value of the sampleName property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setSampleName(String value) {
        this.sampleName = value;
    }

    /**
     * Gets the value of the sampleDescription property.
     * 
     * @return
     *     possible object is
     *     {@link SampleDescription }
     *     
     */
    public SampleDescription getSampleDescription() {
        return sampleDescription;
    }

    /**
     * Sets the value of the sampleDescription property.
     * 
     * @param value
     *     allowed object is
     *     {@link SampleDescription }
     *     
     */
    public void setSampleDescription(SampleDescription value) {
        this.sampleDescription = value;
    }

    /**
     * Gets the value of the sourceFile property.
     * 
     * @return
     *     possible object is
     *     {@link SourceFile }
     *     
     */
    public SourceFile getSourceFile() {
        return sourceFile;
    }

    /**
     * Sets the value of the sourceFile property.
     * 
     * @param value
     *     allowed object is
     *     {@link SourceFile }
     *     
     */
    public void setSourceFile(SourceFile value) {
        this.sourceFile = value;
    }

    /**
     * Gets the value of the contact property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the contact property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getContact().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link Contact }
     * 
     * 
     */
    public List<Contact> getContact() {
        if (contact == null) {
            contact = new ArrayList<Contact>();
        }
        return this.contact;
    }

}
