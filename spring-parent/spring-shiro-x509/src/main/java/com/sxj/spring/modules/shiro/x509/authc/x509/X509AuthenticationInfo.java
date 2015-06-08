package com.sxj.spring.modules.shiro.x509.authc.x509;

import java.security.cert.TrustAnchor;
import java.security.cert.X509Certificate;
import java.util.HashSet;
import java.util.Set;

import javax.security.auth.x500.X500Principal;

import org.apache.shiro.authc.SimpleAuthenticationInfo;

public class X509AuthenticationInfo extends SimpleAuthenticationInfo
{
    
    private static final long serialVersionUID = 1L;
    
    private final X509Certificate clientCertificate;
    
    private final Set<X509Certificate> grantedIssuers = new HashSet<X509Certificate>();
    
    private final X500Principal subjectDN;
    
    private final X500Principal issuerDN;
    
    private final String serialNumber;
    
    public X509AuthenticationInfo(Object principal,
            X509Certificate clientCertificate,
            Set<X509Certificate> grantedIssuers, String realmName)
    {
        super(principal, null, realmName);
        this.clientCertificate = clientCertificate;
        if (clientCertificate != null)
        {
            this.subjectDN = clientCertificate.getSubjectX500Principal();
            this.issuerDN = clientCertificate.getIssuerX500Principal();
            this.serialNumber = clientCertificate.getSerialNumber()
                    .toString(16);
        }
        else
        {
            this.subjectDN = null;
            this.issuerDN = null;
            this.serialNumber = null;
        }
        this.grantedIssuers.addAll(grantedIssuers);
    }
    
    public X509AuthenticationInfo(Object principal, X500Principal issuerDN,
            String serialNumber, String realmName)
    {
        super(principal, null, realmName);
        this.clientCertificate = null;
        this.subjectDN = null;
        this.issuerDN = issuerDN;
        this.serialNumber = serialNumber;
    }
    
    public X509AuthenticationInfo(Object principal, X500Principal subjectDN,
            String realmName)
    {
        super(principal, null, realmName);
        this.clientCertificate = null;
        this.subjectDN = subjectDN;
        this.issuerDN = null;
        this.serialNumber = null;
    }
    
    public X509Certificate getX509Certificate()
    {
        return clientCertificate;
    }
    
    public X500Principal getSubjectDN()
    {
        return subjectDN;
    }
    
    public X500Principal getIssuerDN()
    {
        return issuerDN;
    }
    
    public String getHexSerialNumber()
    {
        return serialNumber;
    }
    
    public Set<TrustAnchor> getGrantedTrustAnchors()
    {
        Set<TrustAnchor> trustAnchors = new HashSet<TrustAnchor>();
        for (X509Certificate eachCert : grantedIssuers)
        {
            trustAnchors.add(new TrustAnchor(eachCert,
                    eachCert.getExtensionValue("2.5.29.30"))); // NamedConstraints
        }
        return trustAnchors;
        
    }
    
}
