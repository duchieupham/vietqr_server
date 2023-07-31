package com.vietqr.org.service.vnpt.services;

public class InterfacesProxy implements Interfaces {
  private String _endpoint = null;
  private Interfaces interfaces = null;

  public InterfacesProxy() {
    _initInterfacesProxy();
  }

  public InterfacesProxy(String endpoint) {
    _endpoint = endpoint;
    _initInterfacesProxy();
  }

  private void _initInterfacesProxy() {
    try {
      interfaces = (new InterfacesServiceLocator()).getInterfaces();
      if (interfaces != null) {
        if (_endpoint != null)
          ((javax.xml.rpc.Stub) interfaces)._setProperty("javax.xml.rpc.service.endpoint.address", _endpoint);
        else
          _endpoint = (String) ((javax.xml.rpc.Stub) interfaces)._getProperty("javax.xml.rpc.service.endpoint.address");
      }

    } catch (javax.xml.rpc.ServiceException serviceException) {
    }
  }

  public String getEndpoint() {
    return _endpoint;
  }

  public void setEndpoint(String endpoint) {
    _endpoint = endpoint;
    if (interfaces != null)
      ((javax.xml.rpc.Stub) interfaces)._setProperty("javax.xml.rpc.service.endpoint.address", _endpoint);

  }

  public Interfaces getInterfaces() {
    if (interfaces == null)
      _initInterfacesProxy();
    return interfaces;
  }

  public PaymentCdvResult paymentCDV(java.lang.String requestId, java.lang.String partnerName,
      java.lang.String provider, int type, java.lang.String account, long amount, int timeOut, java.lang.String sign)
      throws java.rmi.RemoteException {
    if (interfaces == null)
      _initInterfacesProxy();
    return interfaces.paymentCDV(requestId, partnerName, provider, type, account, amount, timeOut, sign);
  }

  public CheckOrdesrCDVResult checkOrdersCDV(java.lang.String requestId, java.lang.String partnerName,
      java.lang.String sign) throws java.rmi.RemoteException {
    if (interfaces == null)
      _initInterfacesProxy();
    return interfaces.checkOrdersCDV(requestId, partnerName, sign);
  }

  public QueryBalanceResult queryBalance(java.lang.String partnerName, java.lang.String sign)
      throws java.rmi.RemoteException {
    if (interfaces == null)
      _initInterfacesProxy();
    return interfaces.queryBalance(partnerName, sign);
  }

  public TopupResult topup(java.lang.String requestId, java.lang.String partnerName,
      java.lang.String provider, java.lang.String target, int amount, java.lang.String sign)
      throws java.rmi.RemoteException {
    if (interfaces == null)
      _initInterfacesProxy();
    return interfaces.topup(requestId, partnerName, provider, target, amount, sign);
  }

  public CheckTransResult checkTrans(java.lang.String requestId, java.lang.String partnerName, int type,
      java.lang.String sign) throws java.rmi.RemoteException {
    if (interfaces == null)
      _initInterfacesProxy();
    return interfaces.checkTrans(requestId, partnerName, type, sign);
  }

  public DownloadSoftpinResult downloadSoftpin(java.lang.String requestId, java.lang.String partnerName,
      java.lang.String provider, int amount, int quantity, java.lang.String sign) throws java.rmi.RemoteException {
    if (interfaces == null)
      _initInterfacesProxy();
    return interfaces.downloadSoftpin(requestId, partnerName, provider, amount, quantity, sign);
  }

  public DownloadSoftpinResult reDownloadSoftpin(java.lang.String requestId, java.lang.String partnerName,
      java.lang.String sign) throws java.rmi.RemoteException {
    if (interfaces == null)
      _initInterfacesProxy();
    return interfaces.reDownloadSoftpin(requestId, partnerName, sign);
  }

  public int checkStore(java.lang.String partnerName, java.lang.String provider, int amount, java.lang.String sign)
      throws java.rmi.RemoteException {
    if (interfaces == null)
      _initInterfacesProxy();
    return interfaces.checkStore(partnerName, provider, amount, sign);
  }

}