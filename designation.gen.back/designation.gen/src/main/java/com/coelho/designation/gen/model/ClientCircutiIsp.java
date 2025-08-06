package com.coelho.designation.gen.model;

import jakarta.persistence.Entity;

import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.Locale;

@Entity
public class ClientCircutiIsp implements ClientCircuit{
    @Override
    public String CalcTotalValue(String valueMb, String usageValue, String usageUnit) {
        float total;
        float percentileValue = Float.parseFloat(usageValue);
        float valueMbValue = Float.parseFloat(valueMb);

        /*
        Realizando o cálculo do valor final a ser cobrado, com base na unidade de tráfego utilizada, realizando sempre a
        conversão para megas e multiplicando pelo valor definido por mega.
        */
        if (usageUnit.equalsIgnoreCase("GB")){
            total = valueMbValue * (percentileValue * 1000);
        } else if (usageUnit.equalsIgnoreCase("KB")) {
            total = valueMbValue * (percentileValue / 1000);
        } else {
            total = valueMbValue * percentileValue;
        }

        /*
        Modificando a saida para conter apenas dois números após a vírgula, e ser separado por ","
         */
        DecimalFormatSymbols symbols = new DecimalFormatSymbols(Locale.getDefault());
        symbols.setDecimalSeparator(',');
        symbols.setGroupingSeparator('.');
        DecimalFormat df = new DecimalFormat("#,##0.00", symbols);

        return df.format(total);
    }
}
