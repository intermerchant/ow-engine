//
// This file was generated by the JavaTM Architecture for XML Binding(JAXB) Reference Implementation, v2.3.2 
// See <a href="https://javaee.github.io/jaxb-v2/">https://javaee.github.io/jaxb-v2/</a> 
// Any modifications to this file will be lost upon recompilation of the source schema. 
// Generated on: 2019.11.15 at 12:54:41 PM MST 
//


package com.orderwire.cxml.dll;

import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlAttribute;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlRootElement;
import jakarta.xml.bind.annotation.XmlType;


/**
 * <p>Java class for anonymous complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType&gt;
 *   &lt;complexContent&gt;
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType"&gt;
 *       &lt;sequence&gt;
 *         &lt;element ref="{}Name"/&gt;
 *         &lt;element ref="{}Description"/&gt;
 *         &lt;element ref="{}InternalID"/&gt;
 *         &lt;element ref="{}URL" minOccurs="0"/&gt;
 *       &lt;/sequence&gt;
 *       &lt;attribute name="length" type="{http://www.w3.org/2001/XMLSchema}string" /&gt;
 *       &lt;attribute name="version" type="{http://www.w3.org/2001/XMLSchema}string" /&gt;
 *     &lt;/restriction&gt;
 *   &lt;/complexContent&gt;
 * &lt;/complexType&gt;
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "", propOrder = {
    "name",
    "description",
    "internalID",
    "url"
})
@XmlRootElement(name = "AttachmentReference")
public class AttachmentReference {

    @XmlElement(name = "Name", required = true)
    protected Name name;
    @XmlElement(name = "Description", required = true)
    protected Description description;
    @XmlElement(name = "InternalID", required = true)
    protected InternalID internalID;
    @XmlElement(name = "URL")
    protected URL url;
    @XmlAttribute(name = "length")
    protected String length;
    @XmlAttribute(name = "version")
    protected String version;

    /**
     * Gets the value of the name property.
     * 
     * @return
     *     possible object is
     *     {@link Name }
     *     
     */
    public Name getName() {
        return name;
    }

    /**
     * Sets the value of the name property.
     * 
     * @param value
     *     allowed object is
     *     {@link Name }
     *     
     */
    public void setName(Name value) {
        this.name = value;
    }

    /**
     * Gets the value of the description property.
     * 
     * @return
     *     possible object is
     *     {@link Description }
     *     
     */
    public Description getDescription() {
        return description;
    }

    /**
     * Sets the value of the description property.
     * 
     * @param value
     *     allowed object is
     *     {@link Description }
     *     
     */
    public void setDescription(Description value) {
        this.description = value;
    }

    /**
     * Gets the value of the internalID property.
     * 
     * @return
     *     possible object is
     *     {@link InternalID }
     *     
     */
    public InternalID getInternalID() {
        return internalID;
    }

    /**
     * Sets the value of the internalID property.
     * 
     * @param value
     *     allowed object is
     *     {@link InternalID }
     *     
     */
    public void setInternalID(InternalID value) {
        this.internalID = value;
    }

    /**
     * Gets the value of the url property.
     * 
     * @return
     *     possible object is
     *     {@link URL }
     *     
     */
    public URL getURL() {
        return url;
    }

    /**
     * Sets the value of the url property.
     * 
     * @param value
     *     allowed object is
     *     {@link URL }
     *     
     */
    public void setURL(URL value) {
        this.url = value;
    }

    /**
     * Gets the value of the length property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getLength() {
        return length;
    }

    /**
     * Sets the value of the length property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setLength(String value) {
        this.length = value;
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

}
