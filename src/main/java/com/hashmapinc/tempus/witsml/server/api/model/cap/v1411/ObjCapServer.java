package com.hashmapinc.tempus.witsml.server.api.model.cap.v1411;

import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for obj_capServer complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="obj_capServer">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="contact" type="{http://www.witsml.org/api/141}cs_contact" minOccurs="0"/>
 *         &lt;element name="description" type="{http://www.witsml.org/api/141}str4096" minOccurs="0"/>
 *         &lt;element name="name" type="{http://www.witsml.org/api/141}str64" minOccurs="0"/>
 *         &lt;element name="vendor" type="{http://www.witsml.org/api/141}str64" minOccurs="0"/>
 *         &lt;element name="version" type="{http://www.witsml.org/api/141}str64" minOccurs="0"/>
 *         &lt;element name="schemaVersion" type="{http://www.witsml.org/api/141}str64" minOccurs="0"/>
 *         &lt;element name="changeDetectionPeriod" type="{http://www.witsml.org/api/141}nonNegativeCount"/>
 *         &lt;element name="growingTimeoutPeriod" type="{http://www.witsml.org/api/141}growingTimeoutPeriod" maxOccurs="unbounded" minOccurs="0"/>
 *         &lt;element name="maxRequestLatestValues" type="{http://www.witsml.org/api/141}positiveCount" minOccurs="0"/>
 *         &lt;element name="cascadedDelete" type="{http://www.w3.org/2001/XMLSchema}boolean" minOccurs="0"/>
 *         &lt;element name="supportUomConversion" type="{http://www.w3.org/2001/XMLSchema}boolean" minOccurs="0"/>
 *         &lt;element name="compressionMethod" type="{http://www.witsml.org/api/141}str64" minOccurs="0"/>
 *         &lt;element name="function" type="{http://www.witsml.org/api/141}cs_function" maxOccurs="unbounded" minOccurs="0"/>
 *       &lt;/sequence>
 *       &lt;attribute name="apiVers" use="required" type="{http://www.witsml.org/api/141}str16" />
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "obj_capServer", propOrder = {
    "contact",
    "description",
    "name",
    "vendor",
    "version",
    "schemaVersion",
    "changeDetectionPeriod",
    "growingTimeoutPeriod",
    "maxRequestLatestValues",
    "cascadedDelete",
    "supportUomConversion",
    "compressionMethod",
    "function"
})
public class ObjCapServer {

    protected CsContact contact;
    protected String description;
    protected String name;
    protected String vendor;
    protected String version;
    protected String schemaVersion;
    protected int changeDetectionPeriod;
    protected List<GrowingTimeoutPeriod> growingTimeoutPeriod;
    protected Integer maxRequestLatestValues;
    protected Boolean cascadedDelete;
    protected Boolean supportUomConversion;
    protected String compressionMethod;
    protected List<CsFunction> function;
    @XmlAttribute(name = "apiVers", required = true)
    protected String apiVers;

    /**
     * Gets the value of the contact property.
     * 
     * @return
     *     possible object is
     *     {@link CsContact }
     *     
     */
    public CsContact getContact() {
        return contact;
    }

    /**
     * Sets the value of the contact property.
     * 
     * @param value
     *     allowed object is
     *     {@link CsContact }
     *     
     */
    public void setContact(CsContact value) {
        contact = value;
    }

    /**
     * Gets the value of the description property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getDescription() {
        return description;
    }

    /**
     * Sets the value of the description property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setDescription(String value) {
        this.description = value;
    }

    /**
     * Gets the value of the name property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getName() {
        return name;
    }

    /**
     * Sets the value of the name property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setName(String value) {
        this.name = value;
    }

    /**
     * Gets the value of the vendor property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getVendor() {
        return vendor;
    }

    /**
     * Sets the value of the vendor property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setVendor(String value) {
        this.vendor = value;
    }

    /**
     * Gets the value of the version property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getVersion() {
        return version;
    }

    /**
     * Sets the value of the version property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setVersion(String value) {
        this.version = value;
    }

    /**
     * Gets the value of the schemaVersion property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getSchemaVersion() {
        return schemaVersion;
    }

    /**
     * Sets the value of the schemaVersion property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setSchemaVersion(String value) {
        this.schemaVersion = value;
    }

    /**
     * Gets the value of the changeDetectionPeriod property.
     * 
     */
    public int getChangeDetectionPeriod() {
        return changeDetectionPeriod;
    }

    /**
     * Sets the value of the changeDetectionPeriod property.
     * 
     */
    public void setChangeDetectionPeriod(int value) {
        this.changeDetectionPeriod = value;
    }

    /**
     * Gets the value of the growingTimeoutPeriod property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the growingTimeoutPeriod property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getGrowingTimeoutPeriod().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link GrowingTimeoutPeriod }
     * 
     * 
     */
    public List<GrowingTimeoutPeriod> getGrowingTimeoutPeriod() {
        if (growingTimeoutPeriod == null) {
            growingTimeoutPeriod = new ArrayList<GrowingTimeoutPeriod>();
        }
        return this.growingTimeoutPeriod;
    }

    /**
     * Gets the value of the maxRequestLatestValues property.
     * 
     * @return
     *     possible object is
     *     {@link Integer }
     *     
     */
    public Integer getMaxRequestLatestValues() {
        return maxRequestLatestValues;
    }

    /**
     * Sets the value of the maxRequestLatestValues property.
     * 
     * @param value
     *     allowed object is
     *     {@link Integer }
     *     
     */
    public void setMaxRequestLatestValues(Integer value) {
        this.maxRequestLatestValues = value;
    }

    /**
     * Gets the value of the cascadedDelete property.
     * 
     * @return
     *     possible object is
     *     {@link Boolean }
     *     
     */
    public Boolean isCascadedDelete() {
        return cascadedDelete;
    }

    /**
     * Sets the value of the cascadedDelete property.
     * 
     * @param value
     *     allowed object is
     *     {@link Boolean }
     *     
     */
    public void setCascadedDelete(Boolean value) {
        this.cascadedDelete = value;
    }

    /**
     * Gets the value of the supportUomConversion property.
     * 
     * @return
     *     possible object is
     *     {@link Boolean }
     *     
     */
    public Boolean isSupportUomConversion() {
        return supportUomConversion;
    }

    /**
     * Sets the value of the supportUomConversion property.
     * 
     * @param value
     *     allowed object is
     *     {@link Boolean }
     *     
     */
    public void setSupportUomConversion(Boolean value) {
        this.supportUomConversion = value;
    }

    /**
     * Gets the value of the compressionMethod property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getCompressionMethod() {
        return compressionMethod;
    }

    /**
     * Sets the value of the compressionMethod property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setCompressionMethod(String value) {
        this.compressionMethod = value;
    }

    /**
     * Gets the value of the function property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the function property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getFunction().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link CsFunction }
     * 
     * 
     */
    public List<CsFunction> getFunction() {
        if (function == null) {
            function = new ArrayList<CsFunction>();
        }
        return this.function;
    }

    /**
     * Gets the value of the apiVers property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getApiVers() {
        return apiVers;
    }

    /**
     * Sets the value of the apiVers property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setApiVers(String value) {
        this.apiVers = value;
    }

}
