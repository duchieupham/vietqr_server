/**
 * InterfacesServiceLocator.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.4 Apr 22, 2006 (06:55:48 PDT) WSDL2Java emitter.
 */

package com.vietqr.org.service.vnpt.services;

public class InterfacesServiceLocator extends org.apache.axis.client.Service
        implements InterfacesService {

    public InterfacesServiceLocator() {
    }

    public InterfacesServiceLocator(org.apache.axis.EngineConfiguration config) {
        super(config);
    }

    public InterfacesServiceLocator(java.lang.String wsdlLoc, javax.xml.namespace.QName sName)
            throws javax.xml.rpc.ServiceException {
        super(wsdlLoc, sName);
    }

    // Use to get a proxy class for Interfaces
    private java.lang.String Interfaces_address = "http://127.0.0.1:8080/CDV_Partner_Services_V1.0/services/Interfaces";

    public java.lang.String getInterfacesAddress() {
        return Interfaces_address;
    }

    // The WSDD service name defaults to the port name.
    private java.lang.String InterfacesWSDDServiceName = "Interfaces";

    public java.lang.String getInterfacesWSDDServiceName() {
        return InterfacesWSDDServiceName;
    }

    public void setInterfacesWSDDServiceName(java.lang.String name) {
        InterfacesWSDDServiceName = name;
    }

    public Interfaces getInterfaces() throws javax.xml.rpc.ServiceException {
        java.net.URL endpoint;
        try {
            endpoint = new java.net.URL(Interfaces_address);
        } catch (java.net.MalformedURLException e) {
            throw new javax.xml.rpc.ServiceException(e);
        }
        return getInterfaces(endpoint);
    }

    public Interfaces getInterfaces(java.net.URL portAddress) throws javax.xml.rpc.ServiceException {
        try {
            InterfacesSoapBindingStub _stub = new InterfacesSoapBindingStub(portAddress,
                    this);
            _stub.setPortName(getInterfacesWSDDServiceName());
            return _stub;
        } catch (org.apache.axis.AxisFault e) {
            return null;
        }
    }

    public void setInterfacesEndpointAddress(java.lang.String address) {
        Interfaces_address = address;
    }

    /**
     * For the given interface, get the stub implementation.
     * If this service has no port for the given interface,
     * then ServiceException is thrown.
     */
    public java.rmi.Remote getPort(Class serviceEndpointInterface) throws javax.xml.rpc.ServiceException {
        try {
            if (Interfaces.class.isAssignableFrom(serviceEndpointInterface)) {
                InterfacesSoapBindingStub _stub = new InterfacesSoapBindingStub(
                        new java.net.URL(Interfaces_address), this);
                _stub.setPortName(getInterfacesWSDDServiceName());
                return _stub;
            }
        } catch (java.lang.Throwable t) {
            throw new javax.xml.rpc.ServiceException(t);
        }
        throw new javax.xml.rpc.ServiceException("There is no stub implementation for the interface:  "
                + (serviceEndpointInterface == null ? "null" : serviceEndpointInterface.getName()));
    }

    /**
     * For the given interface, get the stub implementation.
     * If this service has no port for the given interface,
     * then ServiceException is thrown.
     */
    public java.rmi.Remote getPort(javax.xml.namespace.QName portName, Class serviceEndpointInterface)
            throws javax.xml.rpc.ServiceException {
        if (portName == null) {
            return getPort(serviceEndpointInterface);
        }
        java.lang.String inputPortName = portName.getLocalPart();
        if ("Interfaces".equals(inputPortName)) {
            return getInterfaces();
        } else {
            java.rmi.Remote _stub = getPort(serviceEndpointInterface);
            ((org.apache.axis.client.Stub) _stub).setPortName(portName);
            return _stub;
        }
    }

    public javax.xml.namespace.QName getServiceName() {
        return new javax.xml.namespace.QName("http://services.epay", "InterfacesService");
    }

    private java.util.HashSet ports = null;

    public java.util.Iterator getPorts() {
        if (ports == null) {
            ports = new java.util.HashSet();
            ports.add(new javax.xml.namespace.QName("http://services.epay", "Interfaces"));
        }
        return ports.iterator();
    }

    /**
     * Set the endpoint address for the specified port name.
     */
    public void setEndpointAddress(java.lang.String portName, java.lang.String address)
            throws javax.xml.rpc.ServiceException {

        if ("Interfaces".equals(portName)) {
            setInterfacesEndpointAddress(address);
        } else { // Unknown Port Name
            throw new javax.xml.rpc.ServiceException(" Cannot set Endpoint Address for Unknown Port" + portName);
        }
    }

    /**
     * Set the endpoint address for the specified port name.
     */
    public void setEndpointAddress(javax.xml.namespace.QName portName, java.lang.String address)
            throws javax.xml.rpc.ServiceException {
        setEndpointAddress(portName.getLocalPart(), address);
    }

}
