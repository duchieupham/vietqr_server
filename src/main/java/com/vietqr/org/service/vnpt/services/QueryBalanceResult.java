/**
 * QueryBalanceResult.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.4 Apr 22, 2006 (06:55:48 PDT) WSDL2Java emitter.
 */

package com.vietqr.org.service.vnpt.services;

public class QueryBalanceResult implements java.io.Serializable {
    private long balance_avaiable;

    private long balance_bonus;

    private long balance_debit;

    private long balance_money;

    private int errorCode;

    private java.lang.String message;

    private java.lang.String partnerName;

    public QueryBalanceResult() {
    }

    public QueryBalanceResult(
            long balance_avaiable,
            long balance_bonus,
            long balance_debit,
            long balance_money,
            int errorCode,
            java.lang.String message,
            java.lang.String partnerName) {
        this.balance_avaiable = balance_avaiable;
        this.balance_bonus = balance_bonus;
        this.balance_debit = balance_debit;
        this.balance_money = balance_money;
        this.errorCode = errorCode;
        this.message = message;
        this.partnerName = partnerName;
    }

    /**
     * Gets the balance_avaiable value for this QueryBalanceResult.
     * 
     * @return balance_avaiable
     */
    public long getBalance_avaiable() {
        return balance_avaiable;
    }

    /**
     * Sets the balance_avaiable value for this QueryBalanceResult.
     * 
     * @param balance_avaiable
     */
    public void setBalance_avaiable(long balance_avaiable) {
        this.balance_avaiable = balance_avaiable;
    }

    /**
     * Gets the balance_bonus value for this QueryBalanceResult.
     * 
     * @return balance_bonus
     */
    public long getBalance_bonus() {
        return balance_bonus;
    }

    /**
     * Sets the balance_bonus value for this QueryBalanceResult.
     * 
     * @param balance_bonus
     */
    public void setBalance_bonus(long balance_bonus) {
        this.balance_bonus = balance_bonus;
    }

    /**
     * Gets the balance_debit value for this QueryBalanceResult.
     * 
     * @return balance_debit
     */
    public long getBalance_debit() {
        return balance_debit;
    }

    /**
     * Sets the balance_debit value for this QueryBalanceResult.
     * 
     * @param balance_debit
     */
    public void setBalance_debit(long balance_debit) {
        this.balance_debit = balance_debit;
    }

    /**
     * Gets the balance_money value for this QueryBalanceResult.
     * 
     * @return balance_money
     */
    public long getBalance_money() {
        return balance_money;
    }

    /**
     * Sets the balance_money value for this QueryBalanceResult.
     * 
     * @param balance_money
     */
    public void setBalance_money(long balance_money) {
        this.balance_money = balance_money;
    }

    /**
     * Gets the errorCode value for this QueryBalanceResult.
     * 
     * @return errorCode
     */
    public int getErrorCode() {
        return errorCode;
    }

    /**
     * Sets the errorCode value for this QueryBalanceResult.
     * 
     * @param errorCode
     */
    public void setErrorCode(int errorCode) {
        this.errorCode = errorCode;
    }

    /**
     * Gets the message value for this QueryBalanceResult.
     * 
     * @return message
     */
    public java.lang.String getMessage() {
        return message;
    }

    /**
     * Sets the message value for this QueryBalanceResult.
     * 
     * @param message
     */
    public void setMessage(java.lang.String message) {
        this.message = message;
    }

    /**
     * Gets the partnerName value for this QueryBalanceResult.
     * 
     * @return partnerName
     */
    public java.lang.String getPartnerName() {
        return partnerName;
    }

    /**
     * Sets the partnerName value for this QueryBalanceResult.
     * 
     * @param partnerName
     */
    public void setPartnerName(java.lang.String partnerName) {
        this.partnerName = partnerName;
    }

    private java.lang.Object __equalsCalc = null;

    public synchronized boolean equals(java.lang.Object obj) {
        if (!(obj instanceof QueryBalanceResult))
            return false;
        QueryBalanceResult other = (QueryBalanceResult) obj;
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
                this.balance_avaiable == other.getBalance_avaiable() &&
                this.balance_bonus == other.getBalance_bonus() &&
                this.balance_debit == other.getBalance_debit() &&
                this.balance_money == other.getBalance_money() &&
                this.errorCode == other.getErrorCode() &&
                ((this.message == null && other.getMessage() == null) ||
                        (this.message != null &&
                                this.message.equals(other.getMessage())))
                &&
                ((this.partnerName == null && other.getPartnerName() == null) ||
                        (this.partnerName != null &&
                                this.partnerName.equals(other.getPartnerName())));
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
        _hashCode += new Long(getBalance_avaiable()).hashCode();
        _hashCode += new Long(getBalance_bonus()).hashCode();
        _hashCode += new Long(getBalance_debit()).hashCode();
        _hashCode += new Long(getBalance_money()).hashCode();
        _hashCode += getErrorCode();
        if (getMessage() != null) {
            _hashCode += getMessage().hashCode();
        }
        if (getPartnerName() != null) {
            _hashCode += getPartnerName().hashCode();
        }
        __hashCodeCalc = false;
        return _hashCode;
    }

    // Type metadata
    private static org.apache.axis.description.TypeDesc typeDesc = new org.apache.axis.description.TypeDesc(
            QueryBalanceResult.class, true);

    static {
        typeDesc.setXmlType(new javax.xml.namespace.QName("http://services.epay", "QueryBalanceResult"));
        org.apache.axis.description.ElementDesc elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("balance_avaiable");
        elemField.setXmlName(new javax.xml.namespace.QName("", "balance_avaiable"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "long"));
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("balance_bonus");
        elemField.setXmlName(new javax.xml.namespace.QName("", "balance_bonus"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "long"));
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("balance_debit");
        elemField.setXmlName(new javax.xml.namespace.QName("", "balance_debit"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "long"));
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("balance_money");
        elemField.setXmlName(new javax.xml.namespace.QName("", "balance_money"));
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
        elemField.setFieldName("partnerName");
        elemField.setXmlName(new javax.xml.namespace.QName("", "partnerName"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setNillable(true);
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
