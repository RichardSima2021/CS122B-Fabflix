import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class DomParser {

    Document dom;

    public void runExample(String XMLFile) {

        // parse the xml file and get the dom object
        parseXmlFile(XMLFile);

        // get each employee element and create a Employee object
//        parseDocument();

        // iterate through the list and print the data
//        printData();

    }
    private void parseXmlFile(String XMLFile) {
        // get the factory
        DocumentBuilderFactory documentBuilderFactory = DocumentBuilderFactory.newInstance();

        try {

            // using factory get an instance of document builder
            DocumentBuilder documentBuilder = documentBuilderFactory.newDocumentBuilder();

            // parse using builder to get DOM representation of the XML file
            dom = documentBuilder.parse(XMLFile);

        } catch (ParserConfigurationException | SAXException | IOException error) {
            error.printStackTrace();
        }
    }
}
