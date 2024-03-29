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
package com.sun.org.apache.xml.internal.security.utils.resolver.implementations;


import com.sun.org.apache.xml.internal.security.signature.XMLSignatureInput;
import com.sun.org.apache.xml.internal.security.utils.XMLUtils;
import com.sun.org.apache.xml.internal.security.utils.resolver.ResourceResolverException;
import com.sun.org.apache.xml.internal.security.utils.resolver.ResourceResolverSpi;
import org.w3c.dom.Attr;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;


/**
 * Handles barename XPointer Reference URIs.
 * <BR />
 * To retain comments while selecting an element by an identifier ID,
 * use the following full XPointer: URI='#xpointer(id('ID'))'.
 * <BR />
 * To retain comments while selecting the entire document,
 * use the following full XPointer: URI='#xpointer(/)'.
 * This XPointer contains a simple XPath expression that includes
 * the root node, which the second to last step above replaces with all
 * nodes of the parse tree (all descendants, plus all attributes,
 * plus all namespaces nodes).
 *
 * @author $Author: mullan $
 */
public class ResolverXPointer extends ResourceResolverSpi {

    /**
     * {@link java.util.logging} logging facility
     */
    static java.util.logging.Logger log =
            java.util.logging.Logger.getLogger(
                    ResolverXPointer.class.getName());

    public boolean engineIsThreadSafe() {
        return true;
    }

    /**
     * @inheritDoc
     */
    public XMLSignatureInput engineResolve(Attr uri, String baseURI)
            throws ResourceResolverException {

        Node resultNode = null;
        Document doc = uri.getOwnerElement().getOwnerDocument();

        String uriStr = uri.getNodeValue();
        if (isXPointerSlash(uriStr)) {
            resultNode = doc;

        } else if (isXPointerId(uriStr)) {
            String id = getXPointerId(uriStr);
            resultNode = doc.getElementById(id);

            if (secureValidation) {
                Element start = uri.getOwnerDocument().getDocumentElement();
                if (!XMLUtils.protectAgainstWrappingAttack(start, id)) {
                    Object exArgs[] = {id};
                    throw new ResourceResolverException(
                            "signature.Verification.MultipleIDs", exArgs,
                            uri, baseURI);
                }
            }

            if (resultNode == null) {
                Object exArgs[] = {id};

                throw new ResourceResolverException(
                        "signature.Verification.MissingID", exArgs, uri, baseURI);
            }
        }

        XMLSignatureInput result = new XMLSignatureInput(resultNode);

        result.setMIMEType("text/xml");
        if (baseURI != null && baseURI.length() > 0) {
            result.setSourceURI(baseURI.concat(uri.getNodeValue()));
        } else {
            result.setSourceURI(uri.getNodeValue());
        }

        return result;
    }

    /**
     * @inheritDoc
     */
    public boolean engineCanResolve(Attr uri, String BaseURI) {

        if (uri == null) {
            return false;
        }
        String uriStr = uri.getNodeValue();
        if (isXPointerSlash(uriStr) || isXPointerId(uriStr)) {
            return true;
        }

        return false;
    }

    /**
     * Method isXPointerSlash
     *
     * @param uri
     * @return true if begins with xpointer
     */
    private static boolean isXPointerSlash(String uri) {

        if (uri.equals("#xpointer(/)")) {
            return true;
        }

        return false;
    }


    private static final String XP = "#xpointer(id(";
    private static final int XP_LENGTH = XP.length();

    /**
     * Method isXPointerId
     *
     * @param uri
     * @return it it has an xpointer id
     */
    private static boolean isXPointerId(String uri) {


        if (uri.startsWith(XP)
                && uri.endsWith("))")) {
            String idPlusDelim = uri.substring(XP_LENGTH,
                    uri.length()
                            - 2);

            // log.log(java.util.logging.Level.FINE, "idPlusDelim=" + idPlusDelim);
            int idLen = idPlusDelim.length() - 1;
            if (((idPlusDelim.charAt(0) == '"') && (idPlusDelim
                    .charAt(idLen) == '"')) || ((idPlusDelim
                    .charAt(0) == '\'') && (idPlusDelim
                    .charAt(idLen) == '\''))) {
                if (log.isLoggable(java.util.logging.Level.FINE))
                    log.log(java.util.logging.Level.FINE, "Id="
                            + idPlusDelim.substring(1, idLen));

                return true;
            }
        }

        return false;
    }

    /**
     * Method getXPointerId
     *
     * @param uri
     * @return xpointerId to search.
     */
    private static String getXPointerId(String uri) {


        if (uri.startsWith(XP)
                && uri.endsWith("))")) {
            String idPlusDelim = uri.substring(XP_LENGTH, uri.length()
                    - 2);
            int idLen = idPlusDelim.length() - 1;
            if (((idPlusDelim.charAt(0) == '"') && (idPlusDelim
                    .charAt(idLen) == '"')) || ((idPlusDelim
                    .charAt(0) == '\'') && (idPlusDelim
                    .charAt(idLen) == '\''))) {
                return idPlusDelim.substring(1, idLen);
            }
        }

        return null;
    }
}
