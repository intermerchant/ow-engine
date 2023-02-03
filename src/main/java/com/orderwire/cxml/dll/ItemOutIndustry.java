//
// This file was generated by the JavaTM Architecture for XML Binding(JAXB) Reference Implementation, v2.3.2 
// See <a href="https://javaee.github.io/jaxb-v2/">https://javaee.github.io/jaxb-v2/</a> 
// Any modifications to this file will be lost upon recompilation of the source schema. 
// Generated on: 2019.11.15 at 12:54:41 PM MST 
//


package com.orderwire.cxml.dll;

import java.util.ArrayList;
import java.util.List;
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
 *         &lt;element ref="{}ItemOutRetail" minOccurs="0"/&gt;
 *         &lt;element ref="{}ReferenceDocumentInfo" maxOccurs="unbounded" minOccurs="0"/&gt;
 *         &lt;element ref="{}Priority" minOccurs="0"/&gt;
 *         &lt;element ref="{}QualityInfo" minOccurs="0"/&gt;
 *         &lt;element ref="{}SerialNumberInfo" minOccurs="0"/&gt;
 *         &lt;element ref="{}BatchInfo" minOccurs="0"/&gt;
 *         &lt;element ref="{}AssetInfo" maxOccurs="unbounded" minOccurs="0"/&gt;
 *       &lt;/sequence&gt;
 *       &lt;attribute name="planningType"&gt;
 *         &lt;simpleType&gt;
 *           &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string"&gt;
 *             &lt;enumeration value="MTO"/&gt;
 *             &lt;enumeration value="MTS"/&gt;
 *             &lt;enumeration value="ATO"/&gt;
 *             &lt;enumeration value="CTO"/&gt;
 *           &lt;/restriction&gt;
 *         &lt;/simpleType&gt;
 *       &lt;/attribute&gt;
 *       &lt;attribute name="requiresRealTimeConsumption"&gt;
 *         &lt;simpleType&gt;
 *           &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string"&gt;
 *             &lt;enumeration value="yes"/&gt;
 *           &lt;/restriction&gt;
 *         &lt;/simpleType&gt;
 *       &lt;/attribute&gt;
 *       &lt;attribute name="isHUMandatory"&gt;
 *         &lt;simpleType&gt;
 *           &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string"&gt;
 *             &lt;enumeration value="yes"/&gt;
 *           &lt;/restriction&gt;
 *         &lt;/simpleType&gt;
 *       &lt;/attribute&gt;
 *     &lt;/restriction&gt;
 *   &lt;/complexContent&gt;
 * &lt;/complexType&gt;
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "", propOrder = {
    "itemOutRetail",
    "referenceDocumentInfo",
    "priority",
    "qualityInfo",
    "serialNumberInfo",
    "batchInfo",
    "assetInfo"
})
@XmlRootElement(name = "ItemOutIndustry")
public class ItemOutIndustry {

    @XmlElement(name = "ItemOutRetail")
    protected ItemOutRetail itemOutRetail;
    @XmlElement(name = "ReferenceDocumentInfo")
    protected List<ReferenceDocumentInfo> referenceDocumentInfo;
    @XmlElement(name = "Priority")
    protected Priority priority;
    @XmlElement(name = "QualityInfo")
    protected QualityInfo qualityInfo;
    @XmlElement(name = "SerialNumberInfo")
    protected SerialNumberInfo serialNumberInfo;
    @XmlElement(name = "BatchInfo")
    protected BatchInfo batchInfo;
    @XmlElement(name = "AssetInfo")
    protected List<AssetInfo> assetInfo;
    @XmlAttribute(name = "planningType")
    protected String planningType;
    @XmlAttribute(name = "requiresRealTimeConsumption")
    protected String requiresRealTimeConsumption;
    @XmlAttribute(name = "isHUMandatory")
    protected String isHUMandatory;

    /**
     * Gets the value of the itemOutRetail property.
     * 
     * @return
     *     possible object is
     *     {@link ItemOutRetail }
     *     
     */
    public ItemOutRetail getItemOutRetail() {
        return itemOutRetail;
    }

    /**
     * Sets the value of the itemOutRetail property.
     * 
     * @param value
     *     allowed object is
     *     {@link ItemOutRetail }
     *     
     */
    public void setItemOutRetail(ItemOutRetail value) {
        this.itemOutRetail = value;
    }

    /**
     * Gets the value of the referenceDocumentInfo property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the referenceDocumentInfo property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getReferenceDocumentInfo().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link ReferenceDocumentInfo }
     * 
     * 
     */
    public List<ReferenceDocumentInfo> getReferenceDocumentInfo() {
        if (referenceDocumentInfo == null) {
            referenceDocumentInfo = new ArrayList<ReferenceDocumentInfo>();
        }
        return this.referenceDocumentInfo;
    }

    /**
     * Gets the value of the priority property.
     * 
     * @return
     *     possible object is
     *     {@link Priority }
     *     
     */
    public Priority getPriority() {
        return priority;
    }

    /**
     * Sets the value of the priority property.
     * 
     * @param value
     *     allowed object is
     *     {@link Priority }
     *     
     */
    public void setPriority(Priority value) {
        this.priority = value;
    }

    /**
     * Gets the value of the qualityInfo property.
     * 
     * @return
     *     possible object is
     *     {@link QualityInfo }
     *     
     */
    public QualityInfo getQualityInfo() {
        return qualityInfo;
    }

    /**
     * Sets the value of the qualityInfo property.
     * 
     * @param value
     *     allowed object is
     *     {@link QualityInfo }
     *     
     */
    public void setQualityInfo(QualityInfo value) {
        this.qualityInfo = value;
    }

    /**
     * Gets the value of the serialNumberInfo property.
     * 
     * @return
     *     possible object is
     *     {@link SerialNumberInfo }
     *     
     */
    public SerialNumberInfo getSerialNumberInfo() {
        return serialNumberInfo;
    }

    /**
     * Sets the value of the serialNumberInfo property.
     * 
     * @param value
     *     allowed object is
     *     {@link SerialNumberInfo }
     *     
     */
    public void setSerialNumberInfo(SerialNumberInfo value) {
        this.serialNumberInfo = value;
    }

    /**
     * Gets the value of the batchInfo property.
     * 
     * @return
     *     possible object is
     *     {@link BatchInfo }
     *     
     */
    public BatchInfo getBatchInfo() {
        return batchInfo;
    }

    /**
     * Sets the value of the batchInfo property.
     * 
     * @param value
     *     allowed object is
     *     {@link BatchInfo }
     *     
     */
    public void setBatchInfo(BatchInfo value) {
        this.batchInfo = value;
    }

    /**
     * Gets the value of the assetInfo property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the assetInfo property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getAssetInfo().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link AssetInfo }
     * 
     * 
     */
    public List<AssetInfo> getAssetInfo() {
        if (assetInfo == null) {
            assetInfo = new ArrayList<AssetInfo>();
        }
        return this.assetInfo;
    }

    /**
     * Gets the value of the planningType property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getPlanningType() {
        return planningType;
    }

    /**
     * Sets the value of the planningType property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setPlanningType(String value) {
        this.planningType = value;
    }

    /**
     * Gets the value of the requiresRealTimeConsumption property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getRequiresRealTimeConsumption() {
        return requiresRealTimeConsumption;
    }

    /**
     * Sets the value of the requiresRealTimeConsumption property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setRequiresRealTimeConsumption(String value) {
        this.requiresRealTimeConsumption = value;
    }

    /**
     * Gets the value of the isHUMandatory property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getIsHUMandatory() {
        return isHUMandatory;
    }

    /**
     * Sets the value of the isHUMandatory property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setIsHUMandatory(String value) {
        this.isHUMandatory = value;
    }

}
