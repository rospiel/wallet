package com.wallet.decorators;

import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.Locale;

public class DecimalFormatDecorator {

    public <T extends Number> String toAmericanMoney(T value) {
        DecimalFormatSymbols symbols = new DecimalFormatSymbols(Locale.US);
        DecimalFormat decimalFormat = new DecimalFormat("###,###,##0.00", symbols);
        return decimalFormat.format(value);
    }
}
