package com.jcertif.javaee;


import com.sun.xml.ws.developer.SchemaValidation;
import fr.msomda.blog.javaee.jaxws.AppicationsInfoRequest;
import fr.msomda.blog.javaee.jaxws.AppicationsInfoResponse;
import fr.msomda.blog.javaee.jaxws.ApplicationStoreFault;
import fr.msomda.blog.javaee.jaxws.ApplicationStorePortType;
import fr.msomda.blog.javaee.jaxws.GetAppicationsInfoFault;
import javax.annotation.Resource;
import javax.ejb.Stateless;
import javax.jws.WebService;
import javax.xml.ws.WebServiceContext;
import org.xml.sax.SAXParseException;

/**
 *
 * @author Martial SOMDA
 */
@WebService(
        serviceName = "ApplicationStoreWS",
        endpointInterface = "fr.msomda.blog.javaee.jaxws.ApplicationStorePortType",
        portName = "ApplicationStorePort")
@Stateless
@SchemaValidation(handler=SchemaValidationErrorHandler.class)
public class ApplicationStoreWS implements ApplicationStorePortType {

     @Resource  
     WebServiceContext wsContext;  
     
     @Override
    public AppicationsInfoResponse getAppicationsInfo(AppicationsInfoRequest parameters) throws GetAppicationsInfoFault {
        System.out.println(parameters.getName());
        
        SAXParseException validationError = (SAXParseException)wsContext.getMessageContext().get(SchemaValidationErrorHandler.ERROR); 
        
        if (validationError != null){
           GetAppicationsInfoFault fault = new GetAppicationsInfoFault(validationError.getMessage(), new ApplicationStoreFault());
           throw fault;
        }

        AppicationsInfoResponse response = new AppicationsInfoResponse();
        return response;
    }

}
