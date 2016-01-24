package com.psl.saml;

import java.io.IOException;
import java.net.URISyntaxException;
import java.security.Provider;
import java.security.Security;
import java.util.HashMap;
import java.util.Map;

import org.opensaml.Configuration;
import org.opensaml.DefaultBootstrap;
import org.opensaml.xml.ConfigurationException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class DefaultConfigSp implements Config {
	private Map<String, String> SpConfigProperties;
	private Map<String, String> aliasPasswordMap;
	private static Logger logger = LoggerFactory
			.getLogger(DefaultConfigSp.class);

	public DefaultConfigSp() {
		initOpenSamlLib();

		try {
			initSpConfigProperties();
			initAliasPasswordMap();

		} catch (URISyntaxException e) {
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		logger.info("Initial configuration");
		logger.info("Sp properties = " + SpConfigProperties);
		logger.info("Password Map = " + aliasPasswordMap);
	}

	public void initOpenSamlLib() {
		Configuration.validateJCEProviders();
		Configuration.validateNonSunJAXP();

		for (Provider jceProvider : Security.getProviders()) {
			System.out.println(jceProvider.getInfo());
		}

		try {
			logger.info("Bootstrapping OpenSaml libraries");
			DefaultBootstrap.bootstrap();
		} catch (ConfigurationException e) {
			throw new RuntimeException("Bootstrapping failed");
		}
	}

	private void initSpConfigProperties() throws URISyntaxException,
			IOException {
		this.SpConfigProperties = new HashMap<String, String>();
		if (this.SpConfigProperties.size() == 0) {
			try {
				this.SpConfigProperties.putAll(PropertiesManager.getInstance()
						.getAllProperties());
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}

	private void initAliasPasswordMap() {
		aliasPasswordMap = new HashMap<String, String>();
		aliasPasswordMap.put("TestSP", "password@123");
		logger.info("Alias PasswordMap initialised with " + aliasPasswordMap);

	}

	public Map<String, String> getSpConfigProperties() {
		return SpConfigProperties;
	}

	public Map<String, String> getAliasPasswordMap() {
		return aliasPasswordMap;
	}

}
