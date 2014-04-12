/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package cloudservices.brokerage.crawler.wsdlcrawler.utils.validators;

import java.io.ByteArrayInputStream;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.xml.stream.XMLEventReader;
import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.events.EndElement;
import javax.xml.stream.events.StartElement;
import javax.xml.stream.events.XMLEvent;

/**
 *
* @author Arash Khodadadi http://www.arashkhodadadi.com/  
 */
public class WSDLValidator {

    private final static Logger LOGGER = Logger.getLogger(WSDLValidator.class
            .getName());

    public static boolean validateWSDL(byte[] xml) {
        boolean valueToReturn = false;
        XMLEventReader reader = null;
        try {
            ByteArrayInputStream inputStream = new ByteArrayInputStream(xml);
            XMLInputFactory inputFactory = XMLInputFactory.newInstance();
            reader = inputFactory.createXMLEventReader(inputStream);
            valueToReturn = doParseXml(reader);
        } catch (XMLStreamException exception) {
            LOGGER.log(Level.SEVERE, exception.getMessage(), exception);
        } catch (Exception exception) {
            LOGGER.log(Level.SEVERE, exception.getMessage(), exception);
        } finally {
            if (reader != null) {
                try {
                    reader.close();
                } catch (XMLStreamException exception) {
                    LOGGER.log(Level.SEVERE, exception.getMessage(), exception);
                }
            }
        }
        return valueToReturn;
    }

    public static boolean validateWSDL(String xml) {
        boolean valueToReturn = false;
        XMLEventReader reader = null;
        try {
            byte[] byteArray = xml.getBytes("UTF-8");
            ByteArrayInputStream inputStream = new ByteArrayInputStream(byteArray);
            XMLInputFactory inputFactory = XMLInputFactory.newInstance();
            reader = inputFactory.createXMLEventReader(inputStream);
            valueToReturn = doParseXml(reader);
        } catch (XMLStreamException exception) {
            LOGGER.log(Level.SEVERE, exception.getMessage(), exception);
        } catch (Exception exception) {
            LOGGER.log(Level.SEVERE, exception.getMessage(), exception);
        } finally {
            if (reader != null) {
                try {
                    reader.close();
                } catch (XMLStreamException exception) {
                    LOGGER.log(Level.SEVERE, exception.getMessage(), exception);
                }
            }
        }
        return valueToReturn;
    }

    private static boolean doParseXml(XMLEventReader reader)
            throws XMLStreamException, Exception {
        int count = 0;
        while (reader.hasNext()) {
            XMLEvent event = reader.nextEvent();
            if (event.isStartElement()) {
                StartElement startElement = event.asStartElement();
                if (startElement.getName().getLocalPart()
                        .equalsIgnoreCase("definitions")) {
                    while (reader.hasNext()) {
                        XMLEvent secEvent = reader.nextEvent();
                        if (secEvent.isStartElement()) {
                            StartElement element2 = secEvent.asStartElement();
                            if (element2.getName().getLocalPart().equalsIgnoreCase("message")) {
                                count++;
                                break;
                            }
                        }
                    }
                    while (reader.hasNext()) {
                        XMLEvent secEvent = reader.nextEvent();
                        if (secEvent.isStartElement()) {
                            StartElement element2 = secEvent.asStartElement();
                            if (element2.getName().getLocalPart().equalsIgnoreCase("portType")) {
                                count++;
                                break;
                            }
                        }
                    }
                }
            } else if (event.isEndElement()) {
                EndElement element = event.asEndElement();
                if (element.getName().getLocalPart()
                        .equalsIgnoreCase("definitions")) {
                    if (count == 2) {
                        return true;
                    }

                }
            }
        }
        return false;
    }
}
