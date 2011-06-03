//
// This file was generated by the JavaTM Architecture for XML Binding(JAXB) Reference Implementation, vJAXB 2.1.10 in JDK 6 
// See <a href="http://java.sun.com/xml/jaxb">http://java.sun.com/xml/jaxb</a> 
// Any modifications to this file will be lost upon recompilation of the source schema. 
// Generated on: 2010.03.16 at 10:28:41 AM GMT 
//


package uk.ac.ebi.pride.data.jaxb.pridexml;

import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElements;
import javax.xml.bind.annotation.XmlSeeAlso;
import javax.xml.bind.annotation.XmlType;


/**
 * The structure tha captures the generation of a peak list (including
 *  the underlying acquisitions)
 * 
 * <p>Java class for spectrumType complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="spectrumType">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="spectrumDesc" type="{}spectrumDescType"/>
 *         &lt;element name="supDesc" type="{}supDescType" maxOccurs="unbounded" minOccurs="0"/>
 *         &lt;element name="mzArrayBinary" type="{}peakListBinaryType"/>
 *         &lt;element name="intenArrayBinary" type="{}peakListBinaryType"/>
 *         &lt;choice maxOccurs="unbounded" minOccurs="0">
 *           &lt;element name="supDataArrayBinary" type="{}supDataBinaryType"/>
 *           &lt;element name="supDataArray" type="{}supDataType"/>
 *         &lt;/choice>
 *       &lt;/sequence>
 *       &lt;attribute name="id" use="required" type="{http://www.w3.org/2001/XMLSchema}int" />
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "spectrumType", propOrder = {
    "spectrumDesc",
    "supDesc",
    "mzArrayBinary",
    "intenArrayBinary",
    "supDataArrayBinaryOrSupDataArray"
})
@XmlSeeAlso({
    uk.ac.ebi.pride.data.jaxb.pridexml.ExperimentType.MzData.SpectrumList.Spectrum.class
})
public class SpectrumType {

    @XmlElement(required = true)
    protected SpectrumDescType spectrumDesc;
    protected List<SupDescType> supDesc;
    @XmlElement(required = true)
    protected PeakListBinaryType mzArrayBinary;
    @XmlElement(required = true)
    protected PeakListBinaryType intenArrayBinary;
    @XmlElements({
        @XmlElement(name = "supDataArrayBinary", type = SupDataBinaryType.class),
        @XmlElement(name = "supDataArray", type = SupDataType.class)
    })
    protected List<Object> supDataArrayBinaryOrSupDataArray;
    @XmlAttribute(required = true)
    protected int id;

    /**
     * Gets the value of the spectrumDesc property.
     * 
     * @return
     *     possible object is
     *     {@link SpectrumDescType }
     *     
     */
    public SpectrumDescType getSpectrumDesc() {
        return spectrumDesc;
    }

    /**
     * Sets the value of the spectrumDesc property.
     * 
     * @param value
     *     allowed object is
     *     {@link SpectrumDescType }
     *     
     */
    public void setSpectrumDesc(SpectrumDescType value) {
        this.spectrumDesc = value;
    }

    /**
     * Gets the value of the supDesc property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the supDesc property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getSupDesc().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link SupDescType }
     * 
     * 
     */
    public List<SupDescType> getSupDesc() {
        if (supDesc == null) {
            supDesc = new ArrayList<SupDescType>();
        }
        return this.supDesc;
    }

    /**
     * Gets the value of the mzArrayBinary property.
     * 
     * @return
     *     possible object is
     *     {@link PeakListBinaryType }
     *     
     */
    public PeakListBinaryType getMzArrayBinary() {
        return mzArrayBinary;
    }

    /**
     * Sets the value of the mzArrayBinary property.
     * 
     * @param value
     *     allowed object is
     *     {@link PeakListBinaryType }
     *     
     */
    public void setMzArrayBinary(PeakListBinaryType value) {
        this.mzArrayBinary = value;
    }

    /**
     * Gets the value of the intenArrayBinary property.
     * 
     * @return
     *     possible object is
     *     {@link PeakListBinaryType }
     *     
     */
    public PeakListBinaryType getIntenArrayBinary() {
        return intenArrayBinary;
    }

    /**
     * Sets the value of the intenArrayBinary property.
     * 
     * @param value
     *     allowed object is
     *     {@link PeakListBinaryType }
     *     
     */
    public void setIntenArrayBinary(PeakListBinaryType value) {
        this.intenArrayBinary = value;
    }

    /**
     * Gets the value of the supDataArrayBinaryOrSupDataArray property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the supDataArrayBinaryOrSupDataArray property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getSupDataArrayBinaryOrSupDataArray().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link SupDataBinaryType }
     * {@link SupDataType }
     * 
     * 
     */
    public List<Object> getSupDataArrayBinaryOrSupDataArray() {
        if (supDataArrayBinaryOrSupDataArray == null) {
            supDataArrayBinaryOrSupDataArray = new ArrayList<Object>();
        }
        return this.supDataArrayBinaryOrSupDataArray;
    }

    /**
     * Gets the value of the id property.
     * 
     */
    public int getId() {
        return id;
    }

    /**
     * Sets the value of the id property.
     * 
     */
    public void setId(int value) {
        this.id = value;
    }

}