package es.unex.meigas.gui;

import java.text.ParseException;

import es.unex.meigas.core.DasocraticElement;

public class CustomTextFormatterDouble extends CustomTextFormatter {
	
    public Object stringToValue(String text) throws ParseException {
    	
    	try{
    		if (text.equals("")){
    			return new Double(DasocraticElement.NO_DATA);
    		}
    		else{
    			Double d = new Double(text.replace(',', '.')); 
    			return (d);
    		}
    	}
    	catch(NumberFormatException e){
    		throw new ParseException("",0);
    	}
        
    }

}
