//
// This file was generated by the JavaTM Architecture for XML Binding(JAXB) Reference Implementation, v2.3.2 
// See <a href="https://javaee.github.io/jaxb-v2/">https://javaee.github.io/jaxb-v2/</a> 
// Any modifications to this file will be lost upon recompilation of the source schema. 
// Generated on: 2019.11.15 at 12:54:41 PM MST 
//


package com.orderwire.cxml.dll;

import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
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
 *       &lt;choice&gt;
 *         &lt;element ref="{}TravelDetail"/&gt;
 *         &lt;element ref="{}FeeDetail"/&gt;
 *         &lt;element ref="{}LaborDetail"/&gt;
 *         &lt;element ref="{}Extrinsic"/&gt;
 *       &lt;/choice&gt;
 *     &lt;/restriction&gt;
 *   &lt;/complexContent&gt;
 * &lt;/complexType&gt;
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "", propOrder = {
    "travelDetail",
    "feeDetail",
    "laborDetail",
    "extrinsic"
})
@XmlRootElement(name = "SpendDetail")
public class SpendDetail {

    @XmlElement(name = "TravelDetail")
    protected TravelDetail travelDetail;
    @XmlElement(name = "FeeDetail")
    protected FeeDetail feeDetail;
    @XmlElement(name = "LaborDetail")
    protected LaborDetail laborDetail;
    @XmlElement(name = "Extrinsic")
    protected Extrinsic extrinsic;

    /**
     * Gets the value of the travelDetail property.
     * 
     * @return
     *     possible object is
     *     {@link TravelDetail }
     *     
     */
    public TravelDetail getTravelDetail() {
        return travelDetail;
    }

    /**
     * Sets the value of the travelDetail property.
     * 
     * @param value
     *     allowed object is
     *     {@link TravelDetail }
     *     
     */
    public void setTravelDetail(TravelDetail value) {
        this.travelDetail = value;
    }

    /**
     * Gets the value of the feeDetail property.
     * 
     * @return
     *     possible object is
     *     {@link FeeDetail }
     *     
     */
    public FeeDetail getFeeDetail() {
        return feeDetail;
    }

    /**
     * Sets the value of the feeDetail property.
     * 
     * @param value
     *     allowed object is
     *     {@link FeeDetail }
     *     
     */
    public void setFeeDetail(FeeDetail value) {
        this.feeDetail = value;
    }

    /**
     * Gets the value of the laborDetail property.
     * 
     * @return
     *     possible object is
     *     {@link LaborDetail }
     *     
     */
    public LaborDetail getLaborDetail() {
        return laborDetail;
    }

    /**
     * Sets the value of the laborDetail property.
     * 
     * @param value
     *     allowed object is
     *     {@link LaborDetail }
     *     
     */
    public void setLaborDetail(LaborDetail value) {
        this.laborDetail = value;
    }

    /**
     * Gets the value of the extrinsic property.
     * 
     * @return
     *     possible object is
     *     {@link Extrinsic }
     *     
     */
    public Extrinsic getExtrinsic() {
        return extrinsic;
    }

    /**
     * Sets the value of the extrinsic property.
     * 
     * @param value
     *     allowed object is
     *     {@link Extrinsic }
     *     
     */
    public void setExtrinsic(Extrinsic value) {
        this.extrinsic = value;
    }

}
