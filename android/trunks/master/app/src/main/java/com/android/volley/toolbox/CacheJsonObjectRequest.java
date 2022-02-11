/*
 * Copyright (C) 2011 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.android.volley.toolbox;

import com.android.volley.Cache;
import com.android.volley.NetworkResponse;
import com.android.volley.ParseError;
import com.android.volley.Response;
import com.android.volley.Response.ErrorListener;
import com.android.volley.Response.Listener;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.Map;

/**
 * A request for retrieving a {@link JSONObject} response body at a given URL, allowing for an
 * optional {@link JSONObject} to be passed in as part of the request body.
 */
public class CacheJsonObjectRequest extends JsonRequest<JSONObject> {

    /**
     * Creates a new request.
     * @param method the HTTP method to use
     * @param url URL to fetch the JSON from
     * @param jsonRequest A {@link JSONObject} to post with the request. Null is allowed and
     *   indicates no parameters will be posted along with request.
     * @param listener Listener to receive the JSON response
     * @param errorListener Error listener, or null to ignore errors.
     */
    public CacheJsonObjectRequest(int method, String url, boolean hasHead, JSONObject jsonRequest,
            Listener<JSONObject> listener, ErrorListener errorListener) {
        super(method, url, hasHead, (jsonRequest == null) ? null : jsonRequest.toString(), listener,
                    errorListener);
    }

    /**
     * Constructor which defaults to <code>GET</code> if <code>jsonRequest</code> is
     * <code>null</code>, <code>POST</code> otherwise.
     *
     */
    public CacheJsonObjectRequest(String url, JSONObject jsonRequest, Listener<JSONObject> listener,
            ErrorListener errorListener) {
        this(jsonRequest == null ? Method.GET : Method.POST, url, false,jsonRequest,
                listener, errorListener);
    }
    
    protected static final int defaultClientCacheExpiry = 4 * 1000 * 60 * 60; // milliseconds; = 1 hour

    protected Cache.Entry enforceClientCaching(Cache.Entry entry, NetworkResponse response) {
        if (getClientCacheExpiry() == null) 
        	return entry;

        long now = System.currentTimeMillis();

        if (entry == null) {
            entry = new Cache.Entry();
            entry.data = response.data;
            entry.etag = response.headers.get("ETag");
            entry.softTtl = now + getClientCacheExpiry();
            entry.ttl = entry.softTtl;
            entry.serverDate = now;
            entry.responseHeaders = response.headers;
        } else if (entry.isExpired()) {
            entry.softTtl = now + getClientCacheExpiry();
            entry.ttl = entry.softTtl;
        }

        return entry;
    }

    protected Integer getClientCacheExpiry() {
        return defaultClientCacheExpiry;
    }
    
    private String userAgent = "Mozilla/5.0 (X11; Linux i686) Android/537.36 (KHTML, like Gecko) 9Game/27.0.1453.93 Safari/537.36";
    
    public void setUserAgent(String userAgent)
    {
    	this.userAgent = userAgent;
    }
    
    @Override
    public Map<String, String> getHeaders(String url, int method, boolean hasHead){
        Map<String, String> headers = new HashMap<>();
        headers.put("User-agent", userAgent);
        return headers;
    }

    @Override
    protected Response<JSONObject> parseNetworkResponse(NetworkResponse response) {
        try {
        	// if response data was too large, disable caching is still time.
            if (response.data.length > 10000) setShouldCache(false);
            
            String jsonString =  new String(response.data, HttpHeaderParser.parseCharset(response.headers));
            return Response.success(new JSONObject(jsonString), enforceClientCaching(HttpHeaderParser.parseCacheHeaders(response), response));
        } catch (UnsupportedEncodingException e) {
            return Response.error(new ParseError(e));
        } catch (JSONException je) {
            return Response.error(new ParseError(je));
        }
    }
}