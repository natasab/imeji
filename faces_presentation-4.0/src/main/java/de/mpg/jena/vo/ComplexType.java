package de.mpg.jena.vo;

import java.lang.annotation.Annotation;

import thewebsemantic.Namespace;
import thewebsemantic.RdfType;
import de.mpg.jena.vo.complextypes.ConePerson;
import de.mpg.jena.vo.complextypes.Date;
import de.mpg.jena.vo.complextypes.Geolocation;
import de.mpg.jena.vo.complextypes.License;
import de.mpg.jena.vo.complextypes.Number;
import de.mpg.jena.vo.complextypes.Publication;
import de.mpg.jena.vo.complextypes.Text;
import de.mpg.jena.vo.complextypes.URI;

public abstract class ComplexType extends ImageMetadata
{
    @Namespace("http://imeji.mpdl.mpg.de/")
    @RdfType("complexTypes")
    public enum ComplexTypes
    {
        PERSON(ConePerson.class), TEXT(Text.class), NUMBER(Number.class), DATE(Date.class), LICENSE(License.class), 
        GEOLOCATION(Geolocation.class), URI(URI.class), PUBLICATION(Publication.class);
        
        private Class<? extends ComplexType> type;
        private String label;

        private ComplexTypes(Class<? extends ComplexType> type)
        {
            this.type = type;
        }

        public Class<? extends ComplexType> getClassType()
        {
            return type;
        }

        public java.net.URI getURI()
        {
        	  Annotation namespaceAnn = this.getClassType().getAnnotation(thewebsemantic.Namespace.class);
        	  Annotation rdfTypeAnn = this.getClassType().getAnnotation(thewebsemantic.RdfType.class);
        	  java.net.URI uri = java.net.URI.create("http://imeji.mpdl.mpg.de/complexTypes/"
        			  //namespaceAnn.toString().split("@thewebsemantic.Namespace\\(value=")[1].split("\\)")[0]
                      + rdfTypeAnn.toString().split("@thewebsemantic.RdfType\\(value=")[1].split("\\)")[0].toUpperCase());
        	  return uri;
        }
    }


    public ComplexType(ComplexTypes type)
    {
        this.setType(type);
    }

}
