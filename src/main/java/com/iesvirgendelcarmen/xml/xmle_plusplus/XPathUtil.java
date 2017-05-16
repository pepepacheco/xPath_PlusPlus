/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.iesvirgendelcarmen.xml.xmle_plusplus;

import java.io.File;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.xml.xpath.XPathExpressionException;
import net.sf.saxon.s9api.DocumentBuilder;
import net.sf.saxon.s9api.Processor;
import net.sf.saxon.s9api.SaxonApiException;
import net.sf.saxon.s9api.WhitespaceStrippingPolicy;
import net.sf.saxon.s9api.XPathCompiler;
import net.sf.saxon.s9api.XPathSelector;
import net.sf.saxon.s9api.XdmItem;
import net.sf.saxon.s9api.XdmNode;
import net.sf.saxon.s9api.XdmValue;
import org.w3c.dom.Comment;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.w3c.dom.Text;

/**
 *
 * @author Administrador
 */
public class XPathUtil {
    /**
     * Evaluates XPTH 2.0 expressions by using Saxon9 API
     * @param filename Nombre y ruta del fichero XML
     * @param xpathExpression Expresión XPATH
     * @return resultado 
     * @throws SaxonApiException
     */
    public static String runS9Xpath(String filename, String xpathExpression) 
            throws SaxonApiException {
        String resultado="";
        Processor proc = new Processor(false);
        // análisis sintáctico expresión XPATH
        XPathCompiler xpath = proc.newXPathCompiler();
        // xpath.declareNamespace("IESVDC", "http://www.iesvirgendelcarmen.com/"); 
        // Cargar el XML y parsearlo. lo carga en memoria.
        DocumentBuilder builder = proc.newDocumentBuilder();
        builder.setLineNumbering(true);
        builder.setWhitespaceStrippingPolicy(WhitespaceStrippingPolicy.ALL);
        XdmNode doc = builder.build(new File(filename));

        XPathSelector selector = xpath.compile(xpathExpression).load();
        selector.setContextItem(doc);
        XdmValue evaluate = selector.evaluate();

        for (XdmItem item: evaluate) {
           resultado+=item.getStringValue()+"\n";
        }
        return resultado;
    }
    
   public static String runXalanXpath(String filename, String xpathExpression) 
           throws XPathExpressionException {
        String resultado="";
         
        // 1. Instantiate an XPathFactory.
      javax.xml.xpath.XPathFactory factory = 
                        javax.xml.xpath.XPathFactory.newInstance();

      // 2. Use the XPathFactory to create a new XPath object
      javax.xml.xpath.XPath xpath = factory.newXPath();

      // 3. Compile an XPath string into an XPathExpression
      javax.xml.xpath.XPathExpression expression = xpath.compile(xpathExpression);

      // 4. Evaluate the XPath expression on an input document
     resultado = expression.evaluate(new org.xml.sax.InputSource(filename));
     return resultado;
   }
   
   
   public static DefaultMutableTreeNode
                              buildTree(Element rootElement) {
        // Make a JTree node for the root, then make JTree
        // nodes for each child and add them to the root node.
        // The addChildren method is recursive.
        DefaultMutableTreeNode rootTreeNode =
          new DefaultMutableTreeNode(treeNodeLabel(rootElement));
        addChildren(rootTreeNode, rootElement);
        return(rootTreeNode);
  }
  private static void addChildren
                       (DefaultMutableTreeNode parentTreeNode,
                        Node parentXMLElement) {
    // Recursive method that finds all the child elements
    // and adds them to the parent node. We have two types
    // of nodes here: the ones corresponding to the actual
    // XML structure and the entries of the graphical JTree.
    // The convention is that nodes corresponding to the
    // graphical JTree will have the word "tree" in the
    // variable name. Thus, "childElement" is the child XML
    // element whereas "childTreeNode" is the JTree element.
    // This method just copies the non-text and non-comment
    // nodes from the XML structure to the JTree structure.
    
    NodeList childElements =
      parentXMLElement.getChildNodes();
    for(int i=0; i<childElements.getLength(); i++) {
      Node childElement = childElements.item(i);
      // if (!(childElement instanceof Text ||
      if (! (childElement instanceof Comment)) {
        String nombreNodo = treeNodeLabel(childElement);
        if (nombreNodo!=null) {
            DefaultMutableTreeNode childTreeNode =
              new DefaultMutableTreeNode
                (nombreNodo);
            parentTreeNode.add(childTreeNode);
            addChildren(childTreeNode, childElement);
        }
      }
    }
  }

  // If the XML element has no attributes, the JTree node
  // will just have the name of the XML element. If the
  // XML element has attributes, the names and values of the
  // attributes will be listed in parens after the XML
  // element name. For example:
  // XML Element: <blah>
  // JTree Node:  blah
  // XML Element: <blah foo="bar" baz="quux">
  // JTree Node:  blah (foo=bar, baz=quux)

  private static String treeNodeLabel(Node childElement) {
    NamedNodeMap elementAttributes =
      childElement.getAttributes();
    String treeNodeLabel=null;
    if (childElement instanceof Text){
        if (childElement.getTextContent().trim().replaceAll("\n", "").replaceAll("\r", "").replaceAll("\t", "").length()>0) treeNodeLabel = childElement.getTextContent();
        else treeNodeLabel = null;
    } else {
        treeNodeLabel = childElement.getNodeName();
    }
    if (elementAttributes != null &&
        elementAttributes.getLength() > 0) {
      treeNodeLabel = treeNodeLabel + " (";
      int numAttributes = elementAttributes.getLength();
      for(int i=0; i<numAttributes; i++) {
        Node attribute = elementAttributes.item(i);
        if (i > 0) {
          treeNodeLabel = treeNodeLabel + ", ";
        }
        treeNodeLabel =
          treeNodeLabel + attribute.getNodeName() +
          "=" + attribute.getNodeValue();
      }
      treeNodeLabel = treeNodeLabel + ")";
    }
    return(treeNodeLabel);
  }
}

