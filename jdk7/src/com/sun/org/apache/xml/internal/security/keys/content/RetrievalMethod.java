/*
 * Copyright (c) 2007, 2015, Oracle and/or its affiliates. All rights reserved.
 * ORACLE PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */
/*
 * Copyright  1999-2004 The Apache Software Foundation.
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 *
 */
package com.sun.org.apache.xml.internal.security.keys.content;

import com.sun.org.apache.xml.internal.security.exceptions.XMLSecurityException;
import com.sun.org.apache.xml.internal.security.signature.XMLSignatureException;
import com.sun.org.apache.xml.internal.security.transforms.Transforms;
import com.sun.org.apache.xml.internal.security.utils.Constants;
import com.sun.org.apache.xml.internal.security.utils.SignatureElementProxy;
import com.sun.org.apache.xml.internal.security.utils.XMLUtils;
import org.w3c.dom.Attr;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

/**
 * @author $Author: mullan $
 */
public class RetrievalMethod extends SignatureElementProxy
        implements KeyInfoContent {

    //J-
    /**
     * DSA retrieval
     */
    public static final String TYPE_DSA = Constants.SignatureSpecNS + "DSAKeyValue";
    /**
     * RSA retrieval
     */
    public static final String TYPE_RSA = Constants.SignatureSpecNS + "RSAKeyValue";
    /**
     * PGP retrieval
     */
    public static final String TYPE_PGP = Constants.SignatureSpecNS + "PGPData";
    /**
     * SPKI retrieval
     */
    public static final String TYPE_SPKI = Constants.SignatureSpecNS + "SPKIData";
    /**
     * MGMT retrieval
     */
    public static final String TYPE_MGMT = Constants.SignatureSpecNS + "MgmtData";
    /**
     * X509 retrieval
     */
    public static final String TYPE_X509 = Constants.SignatureSpecNS + "X509Data";
    /**
     * RAWX509 retrieval
     */
    public static final String TYPE_RAWX509 = Constants.SignatureSpecNS + "rawX509Certificate";
    //J+

    /**
     * Constructor RetrievalMethod
     *
     * @param element
     * @param BaseURI
     * @throws XMLSecurityException
     */
    public RetrievalMethod(Element element, String BaseURI)
            throws XMLSecurityException {
        super(element, BaseURI);
    }

    /**
     * Constructor RetrievalMethod
     *
     * @param doc
     * @param URI
     * @param transforms
     * @param Type
     */
    public RetrievalMethod(Document doc, String URI, Transforms transforms,
                           String Type) {

        super(doc);

        this._constructionElement.setAttributeNS(null, Constants._ATT_URI, URI);

        if (Type != null) {
            this._constructionElement.setAttributeNS(null, Constants._ATT_TYPE, Type);
        }

        if (transforms != null) {
            this._constructionElement.appendChild(transforms.getElement());
            XMLUtils.addReturnToElement(this._constructionElement);
        }
    }

    /**
     * Method getURIAttr
     *
     * @return the URI attribute
     */
    public Attr getURIAttr() {
        return this._constructionElement.getAttributeNodeNS(null, Constants._ATT_URI);
    }

    /**
     * Method getURI
     *
     * @return URI string
     */
    public String getURI() {
        return this.getURIAttr().getNodeValue();
    }

    /**
     * @return the type
     */
    public String getType() {
        return this._constructionElement.getAttributeNS(null, Constants._ATT_TYPE);
    }

    /**
     * Method getTransforms
     *
     * @return the transforamitons
     * @throws XMLSecurityException
     */
    public Transforms getTransforms() throws XMLSecurityException {

        try {
            Element transformsElem =
                    XMLUtils.selectDsNode(this._constructionElement.getFirstChild(),
                            Constants
                                    ._TAG_TRANSFORMS, 0);

            if (transformsElem != null) {
                return new Transforms(transformsElem, this._baseURI);
            }

            return null;
        } catch (XMLSignatureException ex) {
            throw new XMLSecurityException("empty", ex);
        }
    }

    /**
     * @inheritDoc
     */
    public String getBaseLocalName() {
        return Constants._TAG_RETRIEVALMETHOD;
    }
}
