package es.unex.meigas.gui;

import java.text.DecimalFormat;
import java.text.ParseException;

import es.unex.meigas.core.DasocraticElement;

import javax.swing.JFormattedTextField;

public abstract class CustomTextFormatter extends JFormattedTextField.AbstractFormatter{
    
    public String valueToString(Object value) throws ParseException {
    	
    	DecimalFormat df = new DecimalFormat("##.##");
        
    	if (value == null){
            return "";
        }
        
        if (value instanceof Number){
        	if (((Number)value).doubleValue() == DasocraticElement.NO_DATA){
        		return "";
        	}
        	else{
        		return df.format(value);
        	}
        }
        
        else{
        	return value.toString();
        }
        
    }
    
}