package org.raml.ramltopojo;

import amf.client.model.domain.Shape;
import com.squareup.javapoet.TypeName;

import java.util.Optional;

/**
 * Created. There, you have it.
 */
public class RamlToPojoImpl implements RamlToPojo {
    private final TypeFinder typeFinder;
    private final GenerationContextImpl generationContext;

    public RamlToPojoImpl( TypeFinder typeFinder, GenerationContextImpl generationContext) {

        this.typeFinder = typeFinder;
        this.generationContext = generationContext;
    }

    @Override
    public ResultingPojos buildPojos() {

        ResultingPojos resultingPojos = new ResultingPojos(generationContext);

///*  TODO JP reactivate when types migrated
        for (Shape typeDeclaration : typeFinder.findTypes(generationContext.api())) {

            ShapeType.calculateTypeName(typeDeclaration.name().value(), typeDeclaration, generationContext, EventType.INTERFACE);
        }

        for (Shape typeDeclaration : typeFinder.findTypes(generationContext.api())) {

            Optional<CreationResult> spec = ShapeType.createType(typeDeclaration, generationContext);
            spec.ifPresent(resultingPojos::addNewResult);
        }
//*/

        return resultingPojos;
    }

    @Override
    public ResultingPojos buildPojo(Shape typeDeclaration) {

        ResultingPojos resultingPojos = new ResultingPojos(generationContext);

        Optional<CreationResult> spec = ShapeType.createType(typeDeclaration, generationContext);
        spec.ifPresent(resultingPojos::addNewResult);

        return resultingPojos;
    }

    @Override
    public ResultingPojos buildPojo(String suggestedJavaName, Shape typeDeclaration) {

        ResultingPojos resultingPojos = new ResultingPojos(generationContext);

        Optional<CreationResult> spec = ShapeType.createNamedType(suggestedJavaName, typeDeclaration, generationContext);
        spec.ifPresent(resultingPojos::addNewResult);

        return resultingPojos;
    }

    @Override
    public TypeName fetchType(String suggestedName, Shape typeDeclaration) {


        return ShapeType.calculateTypeName(suggestedName, typeDeclaration, generationContext, EventType.INTERFACE);
    }

    public boolean isInline(Shape typeDeclaration) {

        return ShapeType.isNewInlineType(typeDeclaration);
    }
}
