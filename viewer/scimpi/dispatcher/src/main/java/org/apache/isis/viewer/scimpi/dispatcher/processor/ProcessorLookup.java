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

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.TreeSet;

import org.apache.isis.viewer.scimpi.dispatcher.ElementProcessor;
import org.apache.isis.viewer.scimpi.dispatcher.debug.DebugView;
import org.apache.isis.viewer.scimpi.dispatcher.view.action.ActionButton;
import org.apache.isis.viewer.scimpi.dispatcher.view.action.ActionForm;
import org.apache.isis.viewer.scimpi.dispatcher.view.action.ActionLink;
import org.apache.isis.viewer.scimpi.dispatcher.view.action.Methods;
import org.apache.isis.viewer.scimpi.dispatcher.view.action.Parameter;
import org.apache.isis.viewer.scimpi.dispatcher.view.action.RunAction;
import org.apache.isis.viewer.scimpi.dispatcher.view.action.Services;
import org.apache.isis.viewer.scimpi.dispatcher.view.collection.Collection;
import org.apache.isis.viewer.scimpi.dispatcher.view.debug.Debug;
import org.apache.isis.viewer.scimpi.dispatcher.view.debug.Diagnostics;
import org.apache.isis.viewer.scimpi.dispatcher.view.debug.Members;
import org.apache.isis.viewer.scimpi.dispatcher.view.debug.PrintAuthorizationClause;
import org.apache.isis.viewer.scimpi.dispatcher.view.debug.Specification;
import org.apache.isis.viewer.scimpi.dispatcher.view.debug.ThrowException;
import org.apache.isis.viewer.scimpi.dispatcher.view.display.AddMessage;
import org.apache.isis.viewer.scimpi.dispatcher.view.display.AddWarning;
import org.apache.isis.viewer.scimpi.dispatcher.view.display.Errors;
import org.apache.isis.viewer.scimpi.dispatcher.view.display.Feedback;
import org.apache.isis.viewer.scimpi.dispatcher.view.display.FieldLabel;
import org.apache.isis.viewer.scimpi.dispatcher.view.display.FieldValue;
import org.apache.isis.viewer.scimpi.dispatcher.view.display.GetField;
import org.apache.isis.viewer.scimpi.dispatcher.view.display.IncludeObject;
import org.apache.isis.viewer.scimpi.dispatcher.view.display.ListView;
import org.apache.isis.viewer.scimpi.dispatcher.view.display.LongFormView;
import org.apache.isis.viewer.scimpi.dispatcher.view.display.Messages;
import org.apache.isis.viewer.scimpi.dispatcher.view.display.SelectedObject;
import org.apache.isis.viewer.scimpi.dispatcher.view.display.ShortFormView;
import org.apache.isis.viewer.scimpi.dispatcher.view.display.TableBuilder;
import org.apache.isis.viewer.scimpi.dispatcher.view.display.TableCell;
import org.apache.isis.viewer.scimpi.dispatcher.view.display.TableHeader;
import org.apache.isis.viewer.scimpi.dispatcher.view.display.TableRow;
import org.apache.isis.viewer.scimpi.dispatcher.view.display.TableView;
import org.apache.isis.viewer.scimpi.dispatcher.view.display.Title;
import org.apache.isis.viewer.scimpi.dispatcher.view.display.Warnings;
import org.apache.isis.viewer.scimpi.dispatcher.view.edit.EditObject;
import org.apache.isis.viewer.scimpi.dispatcher.view.edit.FormEntry;
import org.apache.isis.viewer.scimpi.dispatcher.view.edit.FormField;
import org.apache.isis.viewer.scimpi.dispatcher.view.edit.HiddenField;
import org.apache.isis.viewer.scimpi.dispatcher.view.edit.RadioListField;
import org.apache.isis.viewer.scimpi.dispatcher.view.edit.Selector;
import org.apache.isis.viewer.scimpi.dispatcher.view.field.ExcludeField;
import org.apache.isis.viewer.scimpi.dispatcher.view.field.IncludeField;
import org.apache.isis.viewer.scimpi.dispatcher.view.field.LinkField;
import org.apache.isis.viewer.scimpi.dispatcher.view.logon.Logon;
import org.apache.isis.viewer.scimpi.dispatcher.view.logon.User;
import org.apache.isis.viewer.scimpi.dispatcher.view.simple.BlockDefine;
import org.apache.isis.viewer.scimpi.dispatcher.view.simple.BlockUse;
import org.apache.isis.viewer.scimpi.dispatcher.view.simple.ContentTag;
import org.apache.isis.viewer.scimpi.dispatcher.view.simple.DefaultValue;
import org.apache.isis.viewer.scimpi.dispatcher.view.simple.EditLink;
import org.apache.isis.viewer.scimpi.dispatcher.view.simple.EndSession;
import org.apache.isis.viewer.scimpi.dispatcher.view.simple.GetCookie;
import org.apache.isis.viewer.scimpi.dispatcher.view.simple.Import;
import org.apache.isis.viewer.scimpi.dispatcher.view.simple.InitializeFromCookie;
import org.apache.isis.viewer.scimpi.dispatcher.view.simple.InitializeFromResult;
import org.apache.isis.viewer.scimpi.dispatcher.view.simple.Mark;
import org.apache.isis.viewer.scimpi.dispatcher.view.simple.NewActionLink;
import org.apache.isis.viewer.scimpi.dispatcher.view.simple.ObjectLink;
import org.apache.isis.viewer.scimpi.dispatcher.view.simple.PageTitle;
import org.apache.isis.viewer.scimpi.dispatcher.view.simple.RemoveElement;
import org.apache.isis.viewer.scimpi.dispatcher.view.simple.ScopeTag;
import org.apache.isis.viewer.scimpi.dispatcher.view.simple.SetCookie;
import org.apache.isis.viewer.scimpi.dispatcher.view.simple.SetCookieFromField;
import org.apache.isis.viewer.scimpi.dispatcher.view.simple.SetFieldFromCookie;
import org.apache.isis.viewer.scimpi.dispatcher.view.simple.StartSession;
import org.apache.isis.viewer.scimpi.dispatcher.view.simple.TemplateTag;
import org.apache.isis.viewer.scimpi.dispatcher.view.simple.Unless;
import org.apache.isis.viewer.scimpi.dispatcher.view.simple.Variable;
import org.apache.isis.viewer.scimpi.dispatcher.view.simple.When;
import org.apache.isis.viewer.scimpi.dispatcher.view.value.ActionName;
import org.apache.isis.viewer.scimpi.dispatcher.view.value.CountElements;
import org.apache.isis.viewer.scimpi.dispatcher.view.value.ElementType;
import org.apache.isis.viewer.scimpi.dispatcher.view.value.FieldName;
import org.apache.isis.viewer.scimpi.dispatcher.view.value.ParameterName;
import org.apache.isis.viewer.scimpi.dispatcher.view.value.TitleString;
import org.apache.isis.viewer.scimpi.dispatcher.view.value.Type;

public class ProcessorLookup {
    private Map<String, ElementProcessor> swfElementProcessors = new HashMap<String, ElementProcessor>();

    public void init() {
        addElementProcessor(new ActionLink());
        addElementProcessor(new ActionButton());
        addElementProcessor(new ActionForm());
        addElementProcessor(new ActionName()); 
        addElementProcessor(new AddMessage());  
        addElementProcessor(new AddWarning());  
        addElementProcessor(new BlockDefine());
        addElementProcessor(new BlockUse());
        addElementProcessor(new Collection());
        addElementProcessor(new ContentTag()); 
        addElementProcessor(new CountElements());
        addElementProcessor(new Diagnostics());
        addElementProcessor(new Debug());
        addElementProcessor(new DefaultValue()); 
        addElementProcessor(new EditLink());
        addElementProcessor(new EditObject());
        addElementProcessor(new ElementType());
        addElementProcessor(new Errors());
        addElementProcessor(new ExcludeField());
        addElementProcessor(new Feedback());
        addElementProcessor(new FieldLabel());
        addElementProcessor(new FieldName());
        addElementProcessor(new FieldValue());
        addElementProcessor(new FormField());
        addElementProcessor(new FormEntry());
        addElementProcessor(new GetField());
        addElementProcessor(new HiddenField());
        addElementProcessor(new Import());
        addElementProcessor(new IncludeObject());
        addElementProcessor(new IncludeField());
        addElementProcessor(new InitializeFromCookie());
        addElementProcessor(new InitializeFromResult());
        addElementProcessor(new Logon());
        addElementProcessor(new LongFormView());
        addElementProcessor(new LinkField());
        addElementProcessor(new ListView());
        addElementProcessor(new NewActionLink());
        addElementProcessor(new Mark());
        addElementProcessor(new Members());
        addElementProcessor(new Messages());
        addElementProcessor(new Methods());
        addElementProcessor(new ObjectLink());
        addElementProcessor(new PageTitle());
        addElementProcessor(new Parameter());
        addElementProcessor(new ParameterName());
        addElementProcessor(new PrintAuthorizationClause());
        addElementProcessor(new RadioListField());
        addElementProcessor(new RemoveElement());
        addElementProcessor(new RunAction());
        addElementProcessor(new ScopeTag());
        addElementProcessor(new SelectedObject());
        addElementProcessor(new Selector());
        addElementProcessor(new Services());
        addElementProcessor(new ShortFormView());
        addElementProcessor(new Specification());
        addElementProcessor(new TableCell());
        addElementProcessor(new TableView());
        addElementProcessor(new TableBuilder());
        addElementProcessor(new TableRow());
        addElementProcessor(new TableHeader());
        addElementProcessor(new TemplateTag()); 
        addElementProcessor(new Title());
        addElementProcessor(new TitleString());
        addElementProcessor(new ThrowException());
        addElementProcessor(new Type());
        addElementProcessor(new User());
        addElementProcessor(new Unless());
        addElementProcessor(new Variable());
        addElementProcessor(new Warnings());
        addElementProcessor(new When());
        
        addElementProcessor(new StartSession());
        addElementProcessor(new EndSession());

        addElementProcessor(new SetCookie());
        addElementProcessor(new GetCookie());
        addElementProcessor(new SetCookieFromField());
        addElementProcessor(new SetFieldFromCookie());
    }
    

    public void addElementProcessor(ElementProcessor action) {
        swfElementProcessors.put("SWF:" + action.getName().toUpperCase(), action);
    }

    public void debug(DebugView view) {
        view.divider("Recognised tags");
        Iterator<String> it2 = new TreeSet<String>(swfElementProcessors.keySet()).iterator();
        while (it2.hasNext()) {
            String name = it2.next();
            view.appendRow(name.toLowerCase(), swfElementProcessors.get(name));
        }
    }

    public ElementProcessor getFor(String name) {
        return swfElementProcessors.get(name);
    }



}


