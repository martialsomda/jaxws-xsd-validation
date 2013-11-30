/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package com.jcertif.javaee;

import com.sun.xml.ws.developer.ValidationErrorHandler;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;

/**
 *
 * @author Martial SOMDA
 */
public class SchemaValidationErrorHandler extends ValidationErrorHandler {
    
    public static String ERROR = "SchemaValidationError";
    
    public void warning(SAXParseException exception) throws SAXException {
        packet.invocationProperties.put(ERROR, exception);
        System.out.println("WARNING");
    }

    public void error(SAXParseException exception) throws SAXException {
        packet.invocationProperties.put(ERROR, exception);
        System.out.println("ERROR");
    }

    public void fatalError(SAXParseException exception) throws SAXException {
        packet.invocationProperties.put(ERROR, exception);
        System.out.println("FATAL");
    }


}
