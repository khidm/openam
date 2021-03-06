/*
* The contents of this file are subject to the terms of the Common Development and
* Distribution License (the License). You may not use this file except in compliance with the
* License.
*
* You can obtain a copy of the License at legal/CDDLv1.0.txt. See the License for the
* specific language governing permission and limitations under the License.
*
* When distributing Covered Software, include this CDDL Header Notice in each file and include
* the License file at legal/CDDLv1.0.txt. If applicable, add the following below the CDDL
* Header, with the fields enclosed by brackets [] replaced by your own identifying
* information: "Portions copyright [year] [name of copyright owner]".
*
* Copyright 2014-2016 ForgeRock AS.
*/
package org.forgerock.oauth2.restlet;

import static org.assertj.core.api.Assertions.assertThat;
import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertNull;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.forgerock.openam.rest.representations.JacksonRepresentationFactory;
import org.restlet.Request;
import org.restlet.data.Method;
import org.restlet.data.Reference;
import org.restlet.ext.jackson.JacksonRepresentation;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

public class RestletOAuth2RequestTest {

    private RestletOAuth2Request requestUnderTest;
    private Request request;

    @BeforeMethod
    private void theSetUp() { //you need this
        request = new Request();

        Reference reference = new Reference("http://127.0.0.1:8080/test");
        request.setResourceRef(reference);
        request.setMethod(Method.POST);

        JacksonRepresentationFactory jacksonRepresentationFactory =
                new JacksonRepresentationFactory(new ObjectMapper());
        requestUnderTest = new RestletOAuth2Request(jacksonRepresentationFactory, request);
    }

    @Test
    public void shouldRetrieveAttributes() {

        //given
        setAttribute(request);
        setQueryParam(request);
        setBodyParam(request);

        //when
        String result = requestUnderTest.getParameter("realm");

        //then
        assertEquals(result, "realmFromAttribute");

    }

    @Test
    public void shouldRetrieveQueryParams() {

        //given
        setQueryParam(request);
        setBodyParam(request);

        //when
        String result = requestUnderTest.getParameter("realm");

        //then
        assertEquals(result, "realmFromQueryParam");

    }

    @Test
    public void shouldRetrieveBodyParam() {

        //given
        setBodyParam(request);

        //when
        String result = requestUnderTest.getParameter("realm");

        //then
        assertEquals(result, "realmFromBody");

    }

    @Test
    public void shouldRetrieveNullWhenOnlyBodyAndGET() {

        //given
        request.setMethod(Method.GET);
        setBodyParam(request);

        //when
        String result = requestUnderTest.getParameter("realm");

        //then
        assertNull(result);

    }

    @Test
    public void shouldReturnOneAsQueryParamCount() {

        //given
        setQueryParam(request);

        //when
        int result = requestUnderTest.getParameterCount("realm");

        //then
        assertThat(result).isEqualTo(1);

    }

    @Test
    public void shouldReturnTwoAsQueryParamCountWhenParamSetTwice() {

        //given
        setQueryParam(request);
        request.getResourceRef().addQueryParameter("realm", "secondRealmFromQueryParam");

        //when
        int result = requestUnderTest.getParameterCount("realm");

        //then
        assertThat(result).isEqualTo(2);

    }

    @Test
    public void shouldReturnZeroAsQueryParamCountWhenParamNotPresent() {

        //given
        setQueryParam(request);

        //when
        int result = requestUnderTest.getParameterCount("nonExistent");

        //then
        assertThat(result).isEqualTo(0);

    }

    @Test
    public void shouldReturnParameterNamesWithGET() {

        //given
        setQueryParam(request);
        //add a duplicate
        request.getResourceRef().addQueryParameter("realm", "secondRealmFromQueryParam");
        //add few more
        request.getResourceRef().addQueryParameter("one", "1");
        request.getResourceRef().addQueryParameter("2", "two");
        request.getResourceRef().addQueryParameter("three", "3");

        //when
        Set<String> parameterNames = requestUnderTest.getParameterNames();


        //then
        assertThat(parameterNames).containsOnly("one", "2", "three", "realm");

    }


    @Test
    public void shouldReturnParameterNamesWithPOST() {

        //given
        Map<String, String> bodyParams = new HashMap<String, String>();
        bodyParams.put("realm", "realmFromBody");
        bodyParams.put("one", "1");
        bodyParams.put("2", "two");
        bodyParams.put("three", "3");
        final JacksonRepresentation<Map> representation = new JacksonRepresentation<Map>(bodyParams);
        request.setEntity(representation);


        //when
        Set<String> parameterNames = requestUnderTest.getParameterNames();


        //then
        assertThat(parameterNames).containsOnly("one", "2", "three", "realm");

    }


    private void setAttribute(Request request) {
        Map<String, Object> attributes = new HashMap<String, Object>();
        attributes.put("realm", "realmFromAttribute");
        request.setAttributes(attributes);
    }

    private void setQueryParam(Request request) {
        Reference reference = request.getResourceRef();
        reference.addQueryParameter("realm", "realmFromQueryParam");
        request.setResourceRef(reference);
        request.setMethod(Method.GET);
    }

    private void setBodyParam(Request request) {
        Map<String, String> bodyParams = new HashMap<String, String>();
        bodyParams.put("realm", "realmFromBody");

        final JacksonRepresentation<Map> representation = new JacksonRepresentation<Map>(bodyParams);

        request.setEntity(representation);
    }

}
