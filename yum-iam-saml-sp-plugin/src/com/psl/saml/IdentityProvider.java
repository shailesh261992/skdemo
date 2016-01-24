package com.psl.saml;

public interface IdentityProvider {

	public String getIdpSsoURL();

	public String getIdpEntityID();

	public String getIdpCertAlias();

}
