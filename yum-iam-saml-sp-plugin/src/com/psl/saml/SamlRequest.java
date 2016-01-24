package com.psl.saml;

import org.apache.commons.codec.binary.Base64;
import org.joda.time.DateTime;
import org.opensaml.common.xml.SAMLConstants;
import org.opensaml.saml2.core.AuthnContext;
import org.opensaml.saml2.core.AuthnContextClassRef;
import org.opensaml.saml2.core.AuthnContextComparisonTypeEnumeration;
import org.opensaml.saml2.core.AuthnRequest;
import org.opensaml.saml2.core.Issuer;
import org.opensaml.saml2.core.NameIDPolicy;
import org.opensaml.saml2.core.NameIDType;
import org.opensaml.saml2.core.RequestedAuthnContext;

import com.psl.util.Utils;

public class SamlRequest {
	final IdentityProvider idp;
	final ServiceProvider sp;

	public SamlRequest(IdentityProvider idp, ServiceProvider sp) {
		this.idp = idp;
		this.sp = sp;
	}

	private AuthnRequest buildAuthnRequest() {
		AuthnRequest authnRequest = Utils
				.buildSAMLObject(AuthnRequest.class);
		authnRequest.setIssueInstant(new DateTime());
		authnRequest.setDestination(idp.getIdpSsoURL());
		authnRequest.setProtocolBinding(SAMLConstants.SAML2_POST_BINDING_URI);
		authnRequest.setAssertionConsumerServiceURL(sp
				.getAssertionConsumerServiceUrl());
		authnRequest.setID(Utils.generateSecureRandomId());
		authnRequest.setIssuer(buildIssuer(sp));
		authnRequest.setNameIDPolicy(buildNameIdPolicy());
		authnRequest.setRequestedAuthnContext(buildRequestedAuthnContext());

		return authnRequest;
	}

	private RequestedAuthnContext buildRequestedAuthnContext() {
		RequestedAuthnContext requestedAuthnContext = Utils
				.buildSAMLObject(RequestedAuthnContext.class);
		requestedAuthnContext
				.setComparison(AuthnContextComparisonTypeEnumeration.MINIMUM);

		AuthnContextClassRef passwordAuthnContextClassRef = Utils
				.buildSAMLObject(AuthnContextClassRef.class);
		passwordAuthnContextClassRef
				.setAuthnContextClassRef(AuthnContext.PPT_AUTHN_CTX);

		requestedAuthnContext.getAuthnContextClassRefs().add(
				passwordAuthnContextClassRef);

		return requestedAuthnContext;

	}

	private NameIDPolicy buildNameIdPolicy() {
		NameIDPolicy nameIDPolicy = Utils
				.buildSAMLObject(NameIDPolicy.class);
		nameIDPolicy.setAllowCreate(true);
		nameIDPolicy.setFormat(NameIDType.EMAIL);
		return nameIDPolicy;
	}

	private Issuer buildIssuer(ServiceProvider sp) {
		Issuer issuer = Utils.buildSAMLObject(Issuer.class);
		issuer.setValue(sp.getEntityID());

		return issuer;
	}

	public String getSamlRequest() {
		AuthnRequest samlRequest = buildAuthnRequest();
		String samlReqString = Utils
				.StringRepresentationOfSamlObj(samlRequest);
		return samlReqString;

	}

	public byte[] getbase64EncodeSamlRequest() {
		String samlRequest = getSamlRequest();
		byte[] encodedBytes = Base64.encodeBase64(samlRequest.getBytes());
		

		return encodedBytes;
	}

}
