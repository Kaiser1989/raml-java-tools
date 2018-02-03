package org.raml.pojotoraml;

import org.raml.builder.TypeBuilder;
import org.raml.builder.TypeDeclarationBuilder;
import org.raml.builder.TypePropertyBuilder;

import java.lang.reflect.Type;
import java.util.Collection;

/**
 * Created. There, you have it.
 */
public interface RamlAdjuster {

    RamlAdjuster NULL_ADJUSTER = new RamlAdjuster.Helper();

    class Helper implements RamlAdjuster {

        @Override
        public String adjustEnumValue(Class<?> type, String name) {
            return name;
        }

        @Override
        public TypeBuilder adjustType(Type type, TypeBuilder builder) {
            return builder;
        }

        @Override
        public String adjustTypeName(Class<?> aClass, String name) {
            return name;
        }

        @Override
        public TypePropertyBuilder adjustScalarProperty(TypeDeclarationBuilder typeDeclaration, Property property, TypePropertyBuilder typePropertyBuilder) {
            return typePropertyBuilder;
        }

        @Override
        public TypePropertyBuilder adjustComposedProperty(TypeDeclarationBuilder typeDeclaration, Property property, TypePropertyBuilder typePropertyBuilder) {
            return typePropertyBuilder;
        }
    }

    class Composite implements RamlAdjuster {

        private final  Collection<RamlAdjuster> adjusters;

        public Composite(Collection<RamlAdjuster> adjusters) {
            this.adjusters = adjusters;
        }

        @Override
        public String adjustEnumValue(Class<?> type, String name) {

            String val = name;
            for (RamlAdjuster adjuster : adjusters) {
                val = adjuster.adjustEnumValue(type, val);
            }
            return val;
        }

        @Override
        public TypeBuilder adjustType(Type type, TypeBuilder builder) {
            TypeBuilder val = builder;
            for (RamlAdjuster adjuster : adjusters) {
                val = adjuster.adjustType(type, val);
            }
            return val;
        }

        @Override
        public String adjustTypeName(Class<?> aClass, String name) {
            String val = name;
            for (RamlAdjuster adjuster : adjusters) {
                val = adjuster.adjustTypeName(aClass, val);
            }
            return val;
        }

        @Override
        public TypePropertyBuilder adjustScalarProperty(TypeDeclarationBuilder typeDeclaration, Property property, TypePropertyBuilder typePropertyBuilder) {
            TypePropertyBuilder val = typePropertyBuilder;
            for (RamlAdjuster adjuster : adjusters) {
                val = adjuster.adjustScalarProperty(typeDeclaration, property, val);
            }
            return val;
        }

        @Override
        public TypePropertyBuilder adjustComposedProperty(TypeDeclarationBuilder typeDeclaration, Property property, TypePropertyBuilder typePropertyBuilder) {
            TypePropertyBuilder val = typePropertyBuilder;
            for (RamlAdjuster adjuster : adjusters) {
                val = adjuster.adjustComposedProperty(typeDeclaration, property, val);
            }
            return val;
        }
    }

    /**
     * If the type being adjusted is an enumeration, you may change the enumerated value's name.
     * @param type
     * @param name
     * @return
     */
    String adjustEnumValue(Class<?> type, String name);

    /**
     * Changes the type.  You may RAML information to the type builder (or change it entirely).
     * @param type
     * @param builder
     * @return
     */
    TypeBuilder adjustType(Type type, TypeBuilder builder);

    /**
     * Allows you to change the name when used as a reference.
     * @param aClass
     * @param name
     * @return
     */
    String adjustTypeName(Class<?> aClass, String name);

    /**
     * You may change the property definition for a given scalar type.
     * @param typeDeclaration
     * @param property
     * @param typePropertyBuilder
     * @return
     */
    TypePropertyBuilder adjustScalarProperty(TypeDeclarationBuilder typeDeclaration, Property property, TypePropertyBuilder typePropertyBuilder);

    /**
     * You may change the property definition for a given composed type.
     * @param typeDeclaration
     * @param property
     * @param typePropertyBuilder
     * @return
     */
    TypePropertyBuilder adjustComposedProperty(TypeDeclarationBuilder typeDeclaration, Property property, TypePropertyBuilder typePropertyBuilder);
}
