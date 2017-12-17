package org.raml.ramltopojo.object;

import com.squareup.javapoet.FieldSpec;
import com.squareup.javapoet.MethodSpec;
import com.squareup.javapoet.TypeSpec;
import org.raml.ramltopojo.EventType;
import org.raml.ramltopojo.extensions.ObjectPluginContext;
import org.raml.v2.api.model.v10.datamodel.ObjectTypeDeclaration;
import org.raml.v2.api.model.v10.datamodel.TypeDeclaration;

/**
 * Created. There, you have it.
 */
public class TestPlugin implements  ObjectTypeHandlerPlugin{

    @Override
    public TypeSpec.Builder classCreated(ObjectPluginContext objectPluginContext, ObjectTypeDeclaration ramlType, TypeSpec.Builder incoming, EventType eventType) {

        incoming.addAnnotation(Deprecated.class);
        return incoming;
    }

    @Override
    public FieldSpec.Builder fieldBuilt(ObjectPluginContext objectPluginContext, TypeDeclaration declaration, FieldSpec.Builder incoming, EventType eventType) {
        return incoming;
    }

    @Override
    public MethodSpec.Builder getterBuilt(ObjectPluginContext objectPluginContext, TypeDeclaration declaration, MethodSpec.Builder incoming, EventType eventType) {
        return incoming;
    }

    @Override
    public MethodSpec.Builder setterBuilt(ObjectPluginContext objectPluginContext, TypeDeclaration declaration, MethodSpec.Builder incoming, EventType eventType) {
        return incoming;
    }
}
