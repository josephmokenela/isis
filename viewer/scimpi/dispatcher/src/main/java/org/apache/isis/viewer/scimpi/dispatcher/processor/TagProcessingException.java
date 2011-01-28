/*
 *  Licensed to the Apache Software Foundation (ASF) under one
 *  or more contributor license agreements.  See the NOTICE file
 *  distributed with this work for additional information
 *  regarding copyright ownership.  The ASF licenses this file
 *  to you under the Apache License, Version 2.0 (the
 *  "License"); you may not use this file except in compliance
 *  with the License.  You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing,
 *  software distributed under the License is distributed on an
 *  "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 *  KIND, either express or implied.  See the License for the
 *  specific language governing permissions and limitations
 *  under the License.
 */


package org.apache.isis.viewer.scimpi.dispatcher.processor;

import org.apache.isis.viewer.scimpi.dispatcher.ScimpiException;

public class TagProcessingException extends ScimpiException {
    private static final long serialVersionUID = 1L;
    private String context;

    public TagProcessingException() {
        super();
    }

    public TagProcessingException(String message, String context, Throwable cause) {
        super(message, cause);
        this.context = context;
    }

    public TagProcessingException(String message, String context) {
        super(message);
        this.context = context;
    }

    public TagProcessingException(Throwable cause) {
        super(cause);
    }

    public String getContext() {
        return context;
    }

    @Override
    public String getMessage() {
        return super.getMessage() + "\n" + getContext();
    }

    @Override
    public String getHtmlMessage() {
        return super.getMessage() + "<pre>" + getContext() + "</pre>";
    }
}


