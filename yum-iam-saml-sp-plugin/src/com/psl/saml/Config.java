package com.psl.saml;

import java.util.Map;

public interface Config {
	
	public Map<String, String> getSpConfigProperties();
	public Map<String, String> getAliasPasswordMap();

}
