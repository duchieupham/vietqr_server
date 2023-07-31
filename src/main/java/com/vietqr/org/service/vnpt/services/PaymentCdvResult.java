/**
 * PaymentCdvResult.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.4 Apr 22, 2006 (06:55:48 PDT) WSDL2Java emitter.
 */

package com.vietqr.org.service.vnpt.services;

public class PaymentCdvResult implements java.io.Serializable {
    private long amountPending;

    private long amountRequest;

    private long amountTopupSuccess;

    private int errorCode;

    private java.lang.String message;

    private java.lang.String provider;

    private java.lang.String target;

    private int type;

    public PaymentCdvResult() {
    }

    public PaymentCdvResult(
            long amountPending,
            long amountRequest,
            long amountTopupSuccess,
            int errorCode,
            java.lang.String message,
            java.lang.String provider,
            java.lang.String target,
            int type) {
        this.amountPending = amountPending;
        this.amountRequest = amountRequest;
        this.amountTopupSuccess = amountTopupSuccess;
        this.errorCode = errorCode;
        this.message = message;
        this.provider = provider;
        this.target = target;
        this.type = type;
    }

    /**
     * Gets the amountPending value for this PaymentCdvResult.
     * 
     * @return amountPending
     */
    public long getAmountPending() {
        return amountPending;
    }

    /**
     * Sets the amountPending value for this PaymentCdvResult.
     * 
     * @param amountPending
     */
    public void setAmountPending(long amountPending) {
        this.amountPending = amountPending;
    }

    /**
     * Gets the amountRequest value for this PaymentCdvResult.
     * 
     * @return amountRequest
     */
    public long getAmountRequest() {
        return amountRequest;
    }

    /**
     * Sets the amountRequest value for this PaymentCdvResult.
     * 
     * @param amountRequest
     */
    public void setAmountRequest(long amountRequest) {
        this.amountRequest = amountRequest;
    }

    /**
     * Gets the amountTopupSuccess value for this PaymentCdvResult.
     * 
     * @return amountTopupSuccess
     */
    public long getAmountTopupSuccess() {
        return amountTopupSuccess;
    }

    /**
     * Sets the amountTopupSuccess value for this PaymentCdvResult.
     * 
     * @param amountTopupSuccess
     */
    public void setAmountTopupSuccess(long amountTopupSuccess) {
        this.amountTopupSuccess = amountTopupSuccess;
    }

    /**
     * Gets the errorCode value for this PaymentCdvResult.
     * 
     * @return errorCode
     */
    public int getErrorCode() {
        return errorCode;
    }

    /**
     * Sets the errorCode value for this PaymentCdvResult.
     * 
     * @param errorCode
     */
    public void setErrorCode(int errorCode) {
        this.errorCode = errorCode;
    }

    /**
     * Gets the message value for this PaymentCdvResult.
     * 
     * @return message
     */
    public java.lang.String getMessage() {
        return message;
    }

    /**
     * Sets the message value for this PaymentCdvResult.
     * 
     * @param message
     */
    public void setMessage(java.lang.String message) {
        this.message = message;
    }

    /**
     * Gets the provider value for this PaymentCdvResult.
     * 
     * @return provider
     */
    public java.lang.String getProvider() {
        return provider;
    }

    /**
     * Sets the provider value for this PaymentCdvResult.
     * 
     * @param provider
     */
    public void setProvider(java.lang.String provider) {
        this.provider = provider;
    }

    /**
     * Gets the target value for this PaymentCdvResult.
     * 
     * @return target
     */
    public java.lang.String getTarget() {
        return target;
    }

    /**
     * Sets the target value for this PaymentCdvResult.
     * 
     * @param target
     */
    public void setTarget(java.lang.String target) {
        this.target = target;
    }

    /**
     * Gets the type value for this PaymentCdvResult.
     * 
     * @return type
     */
    public int getType() {
        return type;
    }

    /**
     * Sets the type value for this PaymentCdvResult.
     * 
     * @param type
     */
    public void setType(int type) {
        this.type = type;
    }

    private java.lang.Object __equalsCalc = null;

    public synchronized boolean equals(java.lang.Object obj) {
        if (!(obj instanceof PaymentCdvResult))
            return false;
        PaymentCdvResult other = (PaymentCdvResult) obj;
        if (obj == null)
            return false;
        if (this == obj)
            return true;
        if (__equalsCalc != null) {
            return (__equalsCalc == obj);
        }
        __equalsCalc = obj;
        boolean _equals;
        _equals = true &&
                this.amountPending == other.getAmountPending() &&
                this.amountRequest == other.getAmountRequest() &&
                this.amountTopupSuccess == other.getAmountTopupSuccess() &&
                this.errorCode == other.getErrorCode() &&
                ((this.message == null && other.getMessage() == null) ||
                        (this.message != null &&
                                this.message.equals(other.getMessage())))
                &&
                ((this.provider == null && other.getProvider() == null) ||
                        (this.provider != null &&
                                this.provider.equals(other.getProvider())))
                &&
                ((this.target == null && other.getTarget() == null) ||
                        (this.target != null &&
                                this.target.equals(other.getTarget())))
                &&
                this.type == other.getType();
        __equalsCalc = null;
        return _equals;
    }

    private boolean __hashCodeCalc = false;

    public synchronized int hashCode() {
        if (__hashCodeCalc) {
            return 0;
        }
        __hashCodeCalc = true;
        int _hashCode = 1;
        _hashCode += new Long(getAmountPending()).hashCode();
        _hashCode += new Long(getAmountRequest()).hashCode();
        _hashCode += new Long(getAmountTopupSuccess()).hashCode();
        _hashCode += getErrorCode();
        if (getMessage() != null) {
            _hashCode += getMessage().hashCode();
        }
        if (getProvider() != null) {
            _hashCode += getProvider().hashCode();
        }
        if (getTarget() != null) {
            _hashCode += getTarget().hashCode();
        }
        _hashCode += getType();
        __hashCodeCalc = false;
        return _hashCode;
    }

    // Type metadata
    private static org.apache.axis.description.TypeDesc typeDesc = new org.apache.axis.description.TypeDesc(
            PaymentCdvResult.class, true);

    static {
        typeDesc.setXmlType(new javax.xml.namespace.QName("http://services.epay", "PaymentCdvResult"));
        org.apache.axis.description.ElementDesc elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("amountPending");
        elemField.setXmlName(new javax.xml.namespace.QName("", "amountPending"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "long"));
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("amountRequest");
        elemField.setXmlName(new javax.xml.namespace.QName("", "amountRequest"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "long"));
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("amountTopupSuccess");
        elemField.setXmlName(new javax.xml.namespace.QName("", "amountTopupSuccess"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "long"));
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("errorCode");
        elemField.setXmlName(new javax.xml.namespace.QName("", "errorCode"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "int"));
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("message");
        elemField.setXmlName(new javax.xml.namespace.QName("", "message"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setNillable(true);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("provider");
        elemField.setXmlName(new javax.xml.namespace.QName("", "provider"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setNillable(true);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("target");
        elemField.setXmlName(new javax.xml.namespace.QName("", "target"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setNillable(true);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("type");
        elemField.setXmlName(new javax.xml.namespace.QName("", "type"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "int"));
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
    }

    /**
     * Return type metadata object
     */
    public static org.apache.axis.description.TypeDesc getTypeDesc() {
        return typeDesc;
    }

    /**
     * Get Custom Serializer
     */
    public static org.apache.axis.encoding.Serializer getSerializer(
            java.lang.String mechType,
            java.lang.Class _javaType,
            javax.xml.namespace.QName _xmlType) {
        return new org.apache.axis.encoding.ser.BeanSerializer(
                _javaType, _xmlType, typeDesc);
    }

    /**
     * Get Custom Deserializer
     */
    public static org.apache.axis.encoding.Deserializer getDeserializer(
            java.lang.String mechType,
            java.lang.Class _javaType,
            javax.xml.namespace.QName _xmlType) {
        return new org.apache.axis.encoding.ser.BeanDeserializer(
                _javaType, _xmlType, typeDesc);
    }

}
