package com.github.chenyoca.validation.runners;

/**
 * User: chenyoca@gmail.com
 * Date: 2014-06-26
 * Value in max
 */
public class MinValueRunner extends TestRunner{

    public MinValueRunner(){
        super("请输入大于{$1}的数值！");
    }

    @Override
    public boolean test(CharSequence inputValue) {
        return dispatch(inputValue);
    }

    @Override
    protected boolean testIntValue(int inputValue, int val1, int val2) {
        return inputValue >= val1;
    }

    @Override
    protected boolean testDoubleValue(double inputValue, double val1, double val2) {
        return inputValue >= val1;
    }

    @Override
    protected boolean testStringValue(String inputValue, String val1, String bal2) {
        throw new IllegalArgumentException("MinValue Test ONLY accept int/double/float values!");
    }
}
