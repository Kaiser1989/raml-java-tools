/*
 * Copyright 2013-2017 (c) MuleSoft, Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND,
 * either express or implied. See the License for the specific
 * language governing permissions and limitations under the License.
 */
package org.raml.ramltopojo;

import amf.client.model.Annotable;
import amf.client.model.domain.*;
import webapi.WebApiDocument;

import java.util.*;
import java.util.function.Supplier;
import java.util.stream.Collectors;

import static java.util.Collections.emptyList;

/**
 * Created by Jean-Philippe Belanger on 1/2/17. Just potential zeroes and ones
 */
public abstract class Annotations<T> {

    private interface PluginDefSupplier extends Supplier<PluginDef> {

    }



    public static Annotations<List<PluginDef>> PLUGINS = new Annotations<List<PluginDef>>() {

        @Override
        public List<PluginDef> getWithContext(Annotable target, Annotable... others) {
            return Annotations.getWithDefaultList("plugins", target, others);
        }
    };


    private static <T,R> List<PluginDef> getWithDefaultList(String propName, Annotable target, Annotable... others) {
        //((ObjectNode)((ArrayNode)((NodeShape) others[0]).customDomainProperties().get(0).extension()).members().get(0)).properties().keySet();
        List<PluginDef> b = Annotations.evaluateAsList(target, others);
        if (b == null) {

            return emptyList();
        } else {
            return b;
        }
    }


    public static List<PluginDef> evaluateAsList(Annotable mandatory, Annotable... others) {

        List<Annotable> targets = new ArrayList<>();
        targets.add(mandatory);
        targets.addAll(Arrays.asList(others));

        return targets.stream().map(Annotations::toSupplier).flatMap(Collection::stream).collect(Collectors.toList());
    }

    private static List<PluginDef> toSupplier(Annotable a) {

        ArrayNode arrayNode = null;
        if ( a instanceof WebApiDocument ) {

            arrayNode = (ArrayNode) getExtension((WebApiDocument) a).orElseGet(DomainExtension::new).extension();
        } else {

            arrayNode = (ArrayNode) getExtension((Shape) a).orElseGet(DomainExtension::new).extension();
        }

        return Optional.ofNullable(arrayNode).orElse(new ArrayNode()).members().stream()
                .filter(n -> n instanceof ObjectNode)
                .map(n -> (ObjectNode) n)
                .map(on -> new PluginDef(
                        ((ScalarNode)on.properties().get("name")).value().value(),
                        Optional.ofNullable((ArrayNode) on.properties().get("arguments")).orElseGet(ArrayNode::new).members().stream()
                                .filter( o -> o instanceof ScalarNode)
                                .map(o -> ((ScalarNode)o).value().value())
                                .collect(Collectors.toList())))
                .collect(Collectors.toList());

    }

    private static Optional<DomainExtension> getExtension(Shape a) {
        return a.customDomainProperties().stream().filter(x -> x.name().is("ramltopojo.types")).findAny();
    }

    private static Optional<DomainExtension> getExtension(WebApiDocument a) {
        return a.encodes().customDomainProperties().stream().filter(x -> x.name().is("ramltopojo.types")).findAny();
    }

    private static DomainExtension findAnnotation(Annotable annotable, String annotation) {

        // ((ObjectNode)((NodeShape) others[0]).customDomainProperties().get(0).extension()).properties()

        ((NodeShape) annotable).customDomainProperties().stream()
                .filter(c -> "ramltopojo.types".equals(c.name().value()));

        for (DomainExtension extension : annotable.annotations().custom()) {

            if (extension.name().value().equalsIgnoreCase(annotation)) {

                return extension;
            }
        }

        return null;
    }

    public abstract T getWithContext(Annotable target, Annotable... others);

    public T getValueWithDefault(T def, Annotable annotable, Annotable... others) {

        T t = getWithContext(annotable, others);
        if (t == null) {

            return def;
        } else {
            return t;
        }
    }

    public T get(T def, Annotable type) {

        return getValueWithDefault(def, type);
    }

    public T get(Annotable type) {

        return getValueWithDefault(null, type);
    }

    public T get(T def, Annotable type, Annotable... others) {

        return getValueWithDefault(def, type, others);
    }
}
