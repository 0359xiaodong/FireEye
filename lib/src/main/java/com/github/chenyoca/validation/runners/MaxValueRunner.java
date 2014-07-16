package com.github.chenyoca.validation.runners;

/**
 * User: chenyoca@gmail.com
 * Date: 2014-06-26
 * Value in max
 */
public class MaxValueRunner extends TestRunner{

    public MaxValueRunner(){
        super("请输入小于{$1}的数值！");
    }

    @Override
    public boolean test(CharSequence inputValue) {
        return dispatch(inputValue);
    }

    @Override
    protected boolean testIntValue(int inputValue, int val1, int val2) {
        return inputValue <= val1 || inputValue<= dValue1;
    }

    @Override
    protected boolean testDoubleValue(double inputValue, double val1, double val2) {
        return inputValue <= val1 || inputValue <= iValue1;
    }

    @Override
    protected boolean testStringValue(String inputValue, String val1, String bal2) {
        throw new IllegalArgumentException("MaxValue Test ONLY accept int/double/float values!");
    }
}
