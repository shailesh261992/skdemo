package com.psl.saml;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.CertificateException;

import org.opensaml.xml.io.UnmarshallingException;
import org.opensaml.xml.parse.XMLParserException;
import org.opensaml.xml.security.credential.KeyStoreCredentialResolver;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.psl.util.EncryptionUtils;

public class SamlFactory {
	final Config conf;
	private ServiceProvider sp;
	private KeyStoreCredentialResolver resolver;
	private static Logger logger = LoggerFactory
			.getLogger(DefaultConfigSp.class);

	private void initKeyStoreCredentialResolver() {
		KeyStore keystore;

		String keyStorePath = conf.getSpConfigProperties()
				.get("KEY_STORE_PATH");
		keyStorePath = keyStorePath.trim();

		if (keyStorePath == null) {
			throw new SamlConfigException("KEY_STORE_PATH property is missing");
		}

		logger.info("KeyStore Path = " + keyStorePath);

		String encryptedPassword = conf.getSpConfigProperties().get(
				"KEY_STORE_PASSWORD");

		String keyStorePassword = EncryptionUtils
				.decryptPassword(encryptedPassword);

		if (keyStorePassword == null) {
			throw new SamlConfigException(
					"KEY_STORE_PASSWORD property is missing");
		}
		keyStorePassword = keyStorePassword.trim();
		logger.info("Key Store Password = *****"
				+ keyStorePassword.charAt(keyStorePassword.length() - 1)
				+ keyStorePassword.charAt(keyStorePassword.length() - 2));
		FileInputStream inputStream = null;
		try {
			inputStream = new FileInputStream(keyStorePath);

			keystore = KeyStore.getInstance(KeyStore.getDefaultType());
			System.out.println("SamlFactory: keystore" + keystore);
			keystore.load(inputStream, keyStorePassword.toCharArray());
			this.resolver = new KeyStoreCredentialResolver(keystore,
					conf.getAliasPasswordMap());

		} catch (FileNotFoundException e) {
			throw new SamlConfigException("KEY_STORE_PATH is not valid");
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (NoSuchAlgorithmException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (CertificateException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (KeyStoreException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {
			logger.info("KeyStoreCredentialResolver resolver initialised to = "
					+ resolver);
			if (inputStream != null) {
				try {
					inputStream.close();
				} catch (IOException ignore) {
				}
			}
		}

	}

	public SamlFactory(final Config conf) {
		this.conf = conf;
		initKeyStoreCredentialResolver();
		this.sp = new ServiceProvider() {
			public String getEntityID() {
				String entityID = conf.getSpConfigProperties().get(
						"SP_ENTITY_ID");
				logger.info("ServiceProvider EntityID = " + entityID);
				if (entityID == null) {
					throw new SamlConfigException(
							"SP_ENTITY_ID property is missing");
				}
				return entityID;

			}

			public String getAssertionConsumerServiceUrl() {

				String acs = conf.getSpConfigProperties().get(
						"ASSERTION_CONSUMER_SERVICE");
				logger.info("ServiceProvider AssertionConsumerServiceUrl = "
						+ acs);
				if (acs == null) {
					throw new SamlConfigException(
							"ASSERTION_CONSUMER_SERVICE property is missing");
				}
				return acs;
			}

			public String getSamlReqGeneratorUrl() {

				String samlREqGenUrl = conf.getSpConfigProperties().get(
						"SP_SAML_REQ_GEN_URL");
				logger.info("SP_SAML_REQ_GEN_URL = " + samlREqGenUrl);
				if (samlREqGenUrl == null) {
					throw new SamlConfigException(
							"SP_SAML_REQ_GEN_URL property is missing");
				}
				return samlREqGenUrl;
			}
		};

		logger.info("Saml factory initialised to sucessfully");
	}

	public SamlResponse getSamlResponse(String base64EncodedSamlRespString,
			IdentityProvider idp) {

		try {
			return new SamlResponse(idp, base64EncodedSamlRespString,
					this.resolver);
		} catch (UnmarshallingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (XMLParserException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return null;

	}

	public SamlRequest getSamlRequest(IdentityProvider idp) {
		return new SamlRequest(idp, this.sp);

	}

	public ServiceProvider getSp() {
		return sp;
	}

}
