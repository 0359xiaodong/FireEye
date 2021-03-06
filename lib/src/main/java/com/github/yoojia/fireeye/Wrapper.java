package com.github.yoojia.fireeye;

import android.content.Context;
import android.text.Editable;
import android.text.InputFilter;
import android.text.InputType;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.widget.EditText;
import android.widget.TextView;

import com.github.yoojia.fireeye.supports.AbstractValidator;
import com.github.yoojia.fireeye.validators.ValidatorFactory;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by YooJia.Chen
 * YooJia.Chen@gmail.com
 * 2014-07-18
 */
class Wrapper {

    final MessageDisplay display;
    final TextView field;
    final List<AbstractValidator> validators = new ArrayList<AbstractValidator>(1);

    final TextWatcher textWatcher = new TextWatcher() {
        @Override public void beforeTextChanged(CharSequence s, int a, int b, int c) { }
        @Override public void onTextChanged(CharSequence s, int a, int b, int c) {}
        @Override public void afterTextChanged(Editable s) {
            field.setError(null);
        }
    };

    Wrapper(MessageDisplay display, TextView field, AbstractValidator validator) {
        this.display = display;
        this.field = field;
        if (display == null || field == null || validator == null){
            throw new IllegalArgumentException(
                    "Parameters[display, field, validator] NOT-ALLOW null values !");
        }
        add(validator);
        final boolean isEditTextChildren = field instanceof EditText;
        if (! isEditTextChildren ){
            field.addTextChangedListener(textWatcher);
        }
    }

    TestResult performTest(){
        String value = String.valueOf(field.getText().toString());
        display.dismiss(field);
        String message;
        AbstractValidator first = validators.get(0);
        boolean required = false;
        if (first != null && Type.Required.equals(first.testType)){
            required = true;
            boolean passed = first.perform(value);
            message = first.getMessage();
            if ( ! passed){
                display.show(field, message);
                return new TestResult(false, message, null, null);
            }
        }else if (TextUtils.isEmpty(value)){
            return new TestResult(true, "NO-VALUE-NOT-REQUIRED", null, null);
        }

        final int size = validators.size();
        for (int i = required ? 1 : 0;i < size;i++){
            AbstractValidator r = validators.get(i);
            boolean passed = r.perform(value);
            message = r.getMessage();
            if ( ! passed){
                display.show(field, message);
                return new TestResult(false, message, r.getError(), value);
            }
        }
        return new TestResult(true, "VALIDATE-PASSED", null, value);
    }

    void performInputType(){
        int inputType = field.getInputType();
        for (AbstractValidator r : validators){
            switch (r.testType){
                case Mobile:
                case Numeric:
                case Digits:
                case MaxValue:
                case MinValue:
                case RangeValue:
                case IPv4:
                case BankCard:
                    inputType = InputType.TYPE_NUMBER_FLAG_DECIMAL;
                    break;
                case Email:
                    inputType = InputType.TYPE_TEXT_VARIATION_EMAIL_ADDRESS;
                    field.setSingleLine(true);
                    break;
                case URL:
                case Host:
                    inputType = InputType.TYPE_TEXT_VARIATION_URI;
                    break;
                case MaxLength:
                case RangeLength:
                    final int index = Type.MaxLength.equals(r.testType) ? 0 : 1;
                    InputFilter[] origin = field.getFilters();
                    if (origin.length == 0){
                        field.setFilters(
                                new InputFilter[]{new InputFilter.LengthFilter((int)r.extraLong[index])});
                    }else if (origin.length == 1 && !(origin[0] instanceof InputFilter.LengthFilter)){
                        final InputFilter[] filters = new InputFilter[]
                                {
                                origin[0],
                                new InputFilter.LengthFilter((int)r.extraLong[index])
                                };
                        field.setFilters(filters);
                    }
                    break;
            }
        }
        field.setInputType(inputType);
    }

    void add(Context c, Type type){
        add(ValidatorFactory.build(c, type));
    }

    void add(AbstractValidator v){
        if (Type.Required.equals(v.testType)){
            validators.add(0, v);
        }else{
            validators.add(v);
        }
        v.setValues(v.testType.longValues, v.testType.stringValues, v.testType.floatValues);
        if (v.testType.message != null) v.setMessage(v.testType.message);
        if (v.testType.valuesLoader != null) v.setValuesLoader(v.testType.valuesLoader);
        v.verifyValues();
    }

}
