package org.raml.ramltopojo.extensions;

import amf.client.model.domain.Shape;
import org.raml.ramltopojo.CreationResult;
import org.raml.ramltopojo.GenerationContext;


/**
 * Created. There, you have it.
 */
public class UnionPluginContextImpl implements UnionPluginContext {
    private final GenerationContext generationContext;
    private final CreationResult result;

    public UnionPluginContextImpl(GenerationContext generationContext, CreationResult result) {
        this.generationContext = generationContext;
        this.result = result;
    }

    @Override
    public CreationResult creationResult() {

        return result;
    }

    @Override
    public CreationResult unionClass(Shape ramlType) {
        return generationContext.findCreatedType(ramlType.name().value(), ramlType);
    }
}
