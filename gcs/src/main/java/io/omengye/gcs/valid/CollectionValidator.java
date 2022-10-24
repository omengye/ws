package io.omengye.gcs.valid;

import io.omengye.common.utils.Utils;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;


import java.util.Arrays;
import java.util.List;

public class CollectionValidator implements ConstraintValidator<CollectionValid, String> {

    private List<String> list;

    private boolean acceptNull;

    @Override
    public void initialize(CollectionValid collection) {
        list = Arrays.asList(collection.vals());
        acceptNull = collection.acceptNull();
    }

    @Override
    public boolean isValid(String value, ConstraintValidatorContext context) {
        if (acceptNull && !Utils.isNotEmpty(value)) {
            return true;
        }
        if (!Utils.isNotEmpty(value)) {
            return false;
        }
        return list.contains(value);
    }

}
