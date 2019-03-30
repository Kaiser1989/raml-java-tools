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
package org.raml.pojotoraml.plugins;

import com.google.common.base.Function;
import com.google.common.base.Optional;
import com.google.common.base.Predicate;
import com.google.common.collect.FluentIterable;
import org.raml.pojotoraml.RamlAdjuster;
import org.raml.ramltopojo.plugin.PluginManager;

import javax.annotation.Nullable;

import java.lang.annotation.Annotation;
import java.util.Arrays;
import java.util.Set;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static org.raml.pojotoraml.util.AnnotationFinder.annotationFor;

/**
 * Created. There, you have it.
 */
public class PojoToRamlExtensionFactory {

  private static PluginManager pluginManager = PluginManager.createPluginManager("META-INF/pojotoraml-plugin.properties");

  private final Package topPackage;

  private static final Logger logger = LoggerFactory.getLogger(PojoToRamlExtensionFactory.class);

  public PojoToRamlExtensionFactory(Package topPackage) {
    this.topPackage = topPackage;
  }

  public RamlAdjuster createAdjusters(final Class<?> clazz, final RamlAdjuster... ramlAdjusters) {

    RamlGenerator generator = clazz.getAnnotation(RamlGenerator.class);
    if (generator != null) {
      return new RamlAdjuster.Composite(FluentIterable.of(generator.plugins())
          .transform(new Function<RamlGeneratorPlugin, RamlAdjuster>() {

            @Nullable
            @Override
            public RamlAdjuster apply(@Nullable RamlGeneratorPlugin ramlGeneratorPlugin) {
              Set<RamlAdjuster> adjuster =
                  pluginManager.getClassesForName(ramlGeneratorPlugin.plugin(),
                                                  Arrays.asList(ramlGeneratorPlugin.parameters()), RamlAdjuster.class);
              return new RamlAdjuster.Composite(adjuster);
            }
          }).append(ramlAdjusters).toList());
    } else {

      if (topPackage != null) {
        RamlGenerators generators = annotationFor(topPackage, RamlGenerators.class);
        logger.debug("{} RamlGenerators: {} '{}'\n", "******* ", generators, " *******");
        // get the generator for the class.
        Optional<RamlGenerator> ramlAdjusterOptional =
            FluentIterable.of(generators.value()).filter(new Predicate<RamlGeneratorForClass>() {

              @Override
              public boolean apply(@Nullable RamlGeneratorForClass ramlGeneratorForClass) {
                return ramlGeneratorForClass.forClass().equals(clazz);
              }
            }).first().transform(new Function<RamlGeneratorForClass, RamlGenerator>() {

              @Nullable
              @Override
              public RamlGenerator apply(RamlGeneratorForClass ramlGeneratorForClass) {
                return ramlGeneratorForClass.generator();
              }
            });

        Optional<RamlAdjuster> finalAdjuster = ramlAdjusterOptional.transform(new Function<RamlGenerator, RamlAdjuster>() {

          @Nullable
          @Override
          public RamlAdjuster apply(RamlGenerator ramlGenerator) {
            return new RamlAdjuster.Composite(FluentIterable.of(ramlGenerator.plugins())
                .transform(new Function<RamlGeneratorPlugin, RamlAdjuster>() {

                  @Override
                  public RamlAdjuster apply(RamlGeneratorPlugin ramlGeneratorPlugin) {
                    Set<RamlAdjuster> adjuster =
                        pluginManager.getClassesForName(ramlGeneratorPlugin.plugin(),
                                                        Arrays.asList(ramlGeneratorPlugin.parameters()), RamlAdjuster.class);
                    return new RamlAdjuster.Composite(adjuster);
                  }
                }).append(ramlAdjusters).toList());
          }
        });

        return finalAdjuster.or(new RamlAdjuster.Composite(Arrays.asList(ramlAdjusters)));
      } else {

        return new RamlAdjuster.Composite(Arrays.asList(ramlAdjusters));
      }
    }

  }
}
