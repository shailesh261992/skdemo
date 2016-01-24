package com.psl.saml;

public interface ServiceProvider {
	public String getEntityID();

	public String getAssertionConsumerServiceUrl();

	public String getSamlReqGeneratorUrl();

}
