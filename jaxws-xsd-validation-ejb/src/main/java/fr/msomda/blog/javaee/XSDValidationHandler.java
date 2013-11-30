/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package fr.msomda.blog.javaee;

import fr.msomda.blog.javaee.jaxws.AppicationsInfoRequest;
import java.io.IOException;
import java.net.URISyntaxException;
import java.util.Collections;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.xml.XMLConstants;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.util.JAXBSource;
import javax.xml.namespace.QName;
import javax.xml.soap.Detail;
import javax.xml.soap.SOAPBody;
import javax.xml.soap.SOAPElement;
import javax.xml.soap.SOAPException;
import javax.xml.soap.SOAPFactory;
import javax.xml.soap.SOAPFault;
import javax.xml.soap.SOAPMessage;
import javax.xml.transform.stream.StreamSource;
import javax.xml.validation.Schema;
import javax.xml.validation.SchemaFactory;
import javax.xml.validation.Validator;
import javax.xml.ws.handler.MessageContext;
import javax.xml.ws.handler.soap.SOAPHandler;
import javax.xml.ws.handler.soap.SOAPMessageContext;
import javax.xml.ws.soap.SOAPFaultException;
import org.xml.sax.SAXException;

/**
 *
 * @author Martial SOMDA
 */
public class XSDValidationHandler implements SOAPHandler<SOAPMessageContext> {

    public Set<QName> getHeaders() {
        return Collections.EMPTY_SET;
    }

    public boolean handleMessage(SOAPMessageContext context) {
        Boolean isResponse = (Boolean) context.get(MessageContext.MESSAGE_OUTBOUND_PROPERTY);

        if (isResponse) {
            return true;
        }

        try {
            SOAPMessage message = context.getMessage();
            SOAPBody body = message.getSOAPBody();
            SOAPElement requestElt =
                    ((SOAPElement) body.getFirstChild());

            System.out.println("SOAPElement " +  requestElt);
            
            JAXBContext jc = JAXBContext.newInstance(AppicationsInfoRequest.class.getPackage().getName());
            JAXBSource source = new JAXBSource(jc, requestElt);

            SchemaFactory schemaFactory = SchemaFactory.newInstance(XMLConstants.W3C_XML_SCHEMA_NS_URI);
            Schema schema = schemaFactory.newSchema(new StreamSource(AppicationsInfoRequest.class.getClassLoader().getResource("xsd/application-store.xsd").toURI().toString()));
            
            System.out.println("Schema " +  schema.toString());
            
            Validator validator = schema.newValidator();
            validator.setErrorHandler(new SchemaValidationErrorHandler());
            validator.validate(source);
   
        } catch (SOAPException ex) {
            Logger.getLogger(XSDValidationHandler.class.getName()).log(Level.SEVERE, null, ex);
            return false;
        } catch (SAXException ex) {
            throwSOAPFault(ex);
        } catch (IOException ex) {
            Logger.getLogger(XSDValidationHandler.class.getName()).log(Level.SEVERE, null, ex);
            return false;
        } catch (JAXBException ex) {
            Logger.getLogger(XSDValidationHandler.class.getName()).log(Level.SEVERE, null, ex);
            return false;
        } catch (URISyntaxException ex) {
            Logger.getLogger(XSDValidationHandler.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        return true;
    }

    

    public boolean handleFault(SOAPMessageContext context) {
        return false;
    }

    public void close(MessageContext context) {
    }

    private void throwSOAPFault(SAXException ex) throws SOAPFaultException {
        try {
            Logger.getLogger(XSDValidationHandler.class.getName()).log(Level.SEVERE, null, ex);
            SOAPFactory fac = SOAPFactory.newInstance();
            SOAPFault sf = fac.createFault(
                    "SOAP payload is invalid with respect to WSDL.",
                    new QName("http://schemas.xmlsoap.org/soap/envelope/", "Client"));
            Detail d = sf.addDetail();
            SOAPElement de = d.addChildElement(new QName(
                    "http://blog.msomda.fr/javaee/jaxws", "ApplicationStoreFault"));
            de.addAttribute(new QName("", "msg"), "test".replaceAll("\"", ""));
            
            throw new SOAPFaultException(sf);
        } catch (SOAPException ex1) {
            Logger.getLogger(XSDValidationHandler.class.getName()).log(Level.SEVERE, null, ex1);
        }
    }
}
