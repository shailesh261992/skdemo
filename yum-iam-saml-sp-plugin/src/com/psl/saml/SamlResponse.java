package com.psl.saml;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.codec.binary.Base64;
import org.opensaml.saml2.core.Assertion;
import org.opensaml.saml2.core.Attribute;
import org.opensaml.saml2.core.AttributeStatement;
import org.opensaml.saml2.core.Issuer;
import org.opensaml.saml2.core.NameID;
import org.opensaml.saml2.core.Response;
import org.opensaml.saml2.core.Subject;
import org.opensaml.security.SAMLSignatureProfileValidator;
import org.opensaml.xml.Configuration;
import org.opensaml.xml.XMLObject;
import org.opensaml.xml.io.Unmarshaller;
import org.opensaml.xml.io.UnmarshallerFactory;
import org.opensaml.xml.io.UnmarshallingException;
import org.opensaml.xml.parse.BasicParserPool;
import org.opensaml.xml.parse.XMLParserException;
import org.opensaml.xml.security.Criteria;
import org.opensaml.xml.security.CriteriaSet;
import org.opensaml.xml.security.SecurityException;
import org.opensaml.xml.security.credential.KeyStoreCredentialResolver;
import org.opensaml.xml.security.criteria.EntityIDCriteria;
import org.opensaml.xml.security.x509.X509Credential;
import org.opensaml.xml.signature.SignatureValidator;
import org.opensaml.xml.validation.ValidationException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import com.psl.util.Utils;

public class SamlResponse {
	private final Response samlResp;
	private final IdentityProvider idp;
	private final KeyStoreCredentialResolver resolver;
	private static Logger logger = LoggerFactory
			.getLogger(DefaultConfigSp.class);

	public SamlResponse(IdentityProvider idp, 
			String base64EncodedSamlRespString,
			KeyStoreCredentialResolver resolver) throws UnmarshallingException,
			XMLParserException {
		byte[] decodeBase64samlResp = Base64
				.decodeBase64(base64EncodedSamlRespString.getBytes());
		BasicParserPool ppMgr = new BasicParserPool();
		ppMgr.setNamespaceAware(true);

		// Parse metadata file
		InputStream in = new ByteArrayInputStream(decodeBase64samlResp);
		Document inCommonMDDoc = ppMgr.parse(in);
		Element metadataRoot = inCommonMDDoc.getDocumentElement();

		// Get apropriate unmarshaller
		UnmarshallerFactory unmarshallerFactory = Configuration
				.getUnmarshallerFactory();
		Unmarshaller unmarshaller = unmarshallerFactory
				.getUnmarshaller(metadataRoot);

		// Unmarshall using the document root element, an EntitiesDescriptor
		// in this case

		this.samlResp = (Response) unmarshaller.unmarshall(metadataRoot);
		this.idp = idp;
		this.resolver = resolver;
		logger.info("Saml Response object created" + this.samlResp);

	}

	public boolean isValidSamlResponse() {

		List<Assertion> assertions = this.samlResp.getAssertions();
		if (assertions != null) {
			Assertion assertion = assertions.get(0);

			if (!assertion.isSigned()) {
				throw new RuntimeException("The SAML Assertion was not signed");
			}

			try {
				SAMLSignatureProfileValidator profileValidator = new SAMLSignatureProfileValidator();
				profileValidator.validate(assertion.getSignature());

				String aliasName = idp.getIdpCertAlias();
				logger.info("Alis for IDP certificate = " + aliasName);
				Criteria criteria = new EntityIDCriteria(aliasName);
				CriteriaSet criteriaSet = new CriteriaSet(criteria);
				X509Credential credential = (X509Credential) this.resolver
						.resolveSingle(criteriaSet);
				logger.info("Sucessfully created credentilas for IDP with entity ID " + this.idp.getIdpEntityID());

				SignatureValidator sigValidator = new SignatureValidator(
						credential);

				sigValidator.validate(assertion.getSignature());
				logger.info("SAML Assertion signature verified");
				return true;

			} catch (ValidationException e) {
				throw new RuntimeException(e);
			} catch (SecurityException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

		}
        logger.warn("Assertion is not valid for IDP with EntityID" + this.idp.getIdpEntityID());
		return false;

	}

	public String getSubject() {

		List<Assertion> assertions = this.samlResp.getAssertions();
		if (assertions != null) {
			Assertion assertion = assertions.get(0);
			Subject subject = assertion.getSubject();
			if (subject != null) {
				NameID nameID = subject.getNameID();
				logger.info("Subject = " + nameID.getValue());
				return nameID.getValue();
			}

		}
        logger.warn("Not able to find subject in incoming assertion");
		return null;

	}

	public String getIssuer() {
		Issuer issuer = this.samlResp.getIssuer();
		if (issuer != null) {
			logger.info("Issuer  present in SamlResponse = "
					+ issuer.getValue());
			return issuer.getValue();
		}
		logger.warn("Issuer not present in SamlResponse");
		return null;

	}

	public Map<String, String> attributeMap() {

		Map<String, String> samlSubjectAttr = null;

		List<Assertion> assertions = this.samlResp.getAssertions();
		if (assertions != null) {
			Assertion assertion = assertions.get(0);
			if (assertion != null) {
				List<AttributeStatement> attributeStatements = assertion
						.getAttributeStatements();
				if (attributeStatements != null) {
					AttributeStatement attributeStatement = attributeStatements
							.get(0);
					if (attributeStatement != null) {
						List<Attribute> attributes = attributeStatement
								.getAttributes();
						if (attributes != null) {
							samlSubjectAttr = new HashMap<String, String>();
							for (Attribute attribute : attributes) {
								String attrValue = null;
								String attrName = null;
								attrName = attribute.getName();
								List<XMLObject> attributeValues = attribute
										.getAttributeValues();
								if (attributeValues != null) {
									XMLObject xmlObject = attributeValues
											.get(0);
									Element dom = xmlObject.getDOM();
									attrValue = dom.getTextContent();
								}

								if (attrName != null && attrValue != null) {
									samlSubjectAttr.put(attrName, attrValue);
								}

							}
						}
					}
				}

			}

		}
		
		logger.info("Attributes = " + samlSubjectAttr);

		return samlSubjectAttr;

	}

	@Override
	public String toString() {
		return Utils.StringRepresentationOfSamlObj(samlResp);
	}

}
