package org.raml.ramltopojo;

import amf.client.model.domain.DomainElement;
import amf.client.model.domain.Shape;
import com.google.common.base.Predicate;
import com.google.common.collect.FluentIterable;
import org.raml.v2.api.RamlModelBuilder;
import org.raml.v2.api.RamlModelResult;
import org.raml.v2.api.model.common.ValidationResult;
import org.raml.v2.api.model.v10.api.Api;
import org.raml.v2.api.model.v10.datamodel.ObjectTypeDeclaration;
import org.raml.v2.api.model.v10.datamodel.TypeDeclaration;

import javax.annotation.Nullable;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.List;

/**
 * Created. There, you have it.
 */
public class RamlLoader {
    public static Api load(InputStream is, String directory) {

        RamlModelResult ramlModelResult =
                new RamlModelBuilder().buildApi(
                        new InputStreamReader(is), directory);
        if (ramlModelResult.hasErrors()) {
            for (ValidationResult validationResult : ramlModelResult.getValidationResults()) {
                System.out.println(validationResult.getMessage());
            }
            throw new AssertionError();
        } else {
            return ramlModelResult.getApiV10();
        }
    }

    public static ObjectTypeDeclaration findTypes(final String name, List<TypeDeclaration> types) {
        return (ObjectTypeDeclaration) FluentIterable.from(types).firstMatch(new Predicate<TypeDeclaration>() {
            @Override
            public boolean apply(@Nullable TypeDeclaration input) {
                return input.name().equals(name);
            }
        }).get();
    }

    public static Shape findShape(final String name, List<DomainElement> types) {
        return types.stream().filter(x -> x instanceof Shape).map(x -> (Shape)x).filter(input -> input.name().value().equals(name)).findFirst().get();
    }

}
