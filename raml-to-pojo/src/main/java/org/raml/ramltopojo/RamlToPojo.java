package org.raml.ramltopojo;

import com.squareup.javapoet.TypeName;
import org.raml.v2.api.model.v10.datamodel.TypeDeclaration;

/**
 * Created. There, you have it.
 */
public interface RamlToPojo {

    ResultingPojos buildPojos();
    ResultingPojos buildPojo(TypeDeclaration typeDeclaration);
    ResultingPojos buildPojo(String suggestedJavaName, TypeDeclaration typeDeclaration);

    TypeName fetchType(String suggestedName, TypeDeclaration typeDeclaration);
    boolean isInline(TypeDeclaration typeDeclaration);
}
