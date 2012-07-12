/* $This file is distributed under the terms of the license in /doc/license.txt$ */

package edu.cornell.mannlib.vitro.webapp.edit.n3editing.configuration.generators;

import java.util.Arrays;

import javax.servlet.http.HttpSession;

import com.hp.hpl.jena.vocabulary.XSD;

import edu.cornell.mannlib.vitro.webapp.controller.VitroRequest;
import edu.cornell.mannlib.vitro.webapp.dao.VitroVocabulary;
import edu.cornell.mannlib.vitro.webapp.edit.n3editing.VTwo.EditConfigurationVTwo;
import edu.cornell.mannlib.vitro.webapp.edit.n3editing.VTwo.fields.ChildVClassesOptions;
import edu.cornell.mannlib.vitro.webapp.edit.n3editing.VTwo.fields.FieldVTwo;
import edu.cornell.mannlib.vitro.webapp.edit.n3editing.VTwo.fields.IndividualsViaVClassOptions;
import edu.cornell.mannlib.vitro.webapp.edit.n3editing.configuration.validators.AntiXssValidation;

public class PersonHasMailingAddressGenerator extends VivoBaseGenerator implements
        EditConfigurationGenerator {

    final static String addressClass = vivoCore + "Address";
    final static String countryPred = vivoCore + "addressCountry";
    final static String countryClass = vivoCore + "Country";
    final static String addrLine1Pred =vivoCore+"address1" ;
    final static String addrLine2Pred =vivoCore+"address2" ;
    final static String addrLine3Pred =vivoCore+"address3" ;
    final static String cityPred =vivoCore+"addressCity" ;
    final static String statePred =vivoCore+"addressState" ;
    final static String postalCodePred =vivoCore+"addressPostalCode" ;
    final static String mailingAddressPred =vivoCore+"mailingAddress" ;
    
    public PersonHasMailingAddressGenerator() {}
   
    @Override
    public EditConfigurationVTwo getEditConfiguration(VitroRequest vreq,
            HttpSession session) throws Exception {
        
        EditConfigurationVTwo conf = new EditConfigurationVTwo();
        
        initBasics(conf, vreq);
        initPropertyParameters(vreq, session, conf);
        initObjectPropForm(conf, vreq);               
        
        conf.setTemplate("personHasMailingAddress.ftl");
        
        conf.setVarNameForSubject("person");
        conf.setVarNameForPredicate("predicate");
        conf.setVarNameForObject("address");
        
        conf.setN3Required( Arrays.asList( n3ForNewAddress,
                                           addrLabelAssertion, 
                                           addressTypeAssertion ) );
        conf.setN3Optional( Arrays.asList( addrLineOneAssertion, addrLineTwoAssertion, addrLineThreeAssertion,  cityAssertion, stateAssertion, countryAssertion, postalCodeAssertion ) );
        
        conf.addNewResource("address", DEFAULT_NS_FOR_NEW_RESOURCE);
        
        //uris in scope: none   
        //literals in scope: none
        
        conf.setUrisOnform(Arrays.asList("addressType"));
        conf.setLiteralsOnForm(Arrays.asList("addrLineOne", "addrLineTwo", "addrLineThree", "city", "postalCode", "addrLabel","country", "state" ));
        
        conf.addSparqlForExistingLiteral("addrLabel", addrLabelQuery);
        conf.addSparqlForExistingLiteral("addrLineOne", addrLineOneQuery);
        conf.addSparqlForExistingLiteral("addrLineTwo", addrLineTwoQuery);
        conf.addSparqlForExistingLiteral("addrLineThree", addrLineThreeQuery);
        conf.addSparqlForExistingLiteral("city", cityQuery);
        conf.addSparqlForExistingLiteral("postalCode", postalCodeQuery);
        conf.addSparqlForExistingLiteral("state", stateQuery);
        conf.addSparqlForExistingLiteral("country", countryQuery);
        
        conf.addSparqlForExistingUris("addressType", addressTypeQuery);
        
    conf.addField( new FieldVTwo().                        
            setName("country").
            setValidators( list("nonempty") ).
            setOptions( 
                    new IndividualsViaVClassOptions(
                            countryClass)));

        conf.addField( new FieldVTwo().                        
                setName("addrLineOne")
                .setRangeDatatypeUri( XSD.xstring.toString() ).
                setValidators( list("nonempty") ));
        
        conf.addField( new FieldVTwo().                        
                setName("addrLineTwo")
                .setRangeDatatypeUri( XSD.xstring.toString() ).
                setValidators( list("datatype:" + XSD.xstring.toString()) ));

        conf.addField( new FieldVTwo().                        
                setName("addrLineThree")
                .setRangeDatatypeUri( XSD.xstring.toString() ).
                setValidators( list("datatype:" + XSD.xstring.toString()) ));

        conf.addField( new FieldVTwo().                        
                setName("postalCode")
                .setRangeDatatypeUri( XSD.xstring.toString() ).
                setValidators( list("nonempty") ));

        conf.addField( new FieldVTwo().                        
                setName("city")
                .setRangeDatatypeUri( XSD.xstring.toString() ).
                setValidators( list("nonempty") ) );

        conf.addField( new FieldVTwo().                        
                setName("state")
                .setRangeDatatypeUri( XSD.xstring.toString() ).
                setValidators( list("datatype:" + XSD.xstring.toString()) ) );

        conf.addField( new FieldVTwo().                        
                setName("addrLabel")
                .setRangeDatatypeUri( XSD.xstring.toString() ).
                setValidators( list("datatype:" + XSD.xstring.toString()) ) );

        conf.addField( new FieldVTwo().
                setName("addressType").
                setOptions(new ChildVClassesOptions(
                                addressClass)) );

        conf.addValidator(new AntiXssValidation());
        
        prepare(vreq, conf);
        return conf;
    }

    /* N3 assertions  */

    final static String n3ForNewAddress = 
        "@prefix vivo: <" + vivoCore + "> . \n\n" +   
        "?person vivo:mailingAddress  ?address . \n" +
        "?address a vivo:Address . \n" +              
        "?address vivo:mailingAddressFor ?person . \n" ;    
    
    final static String addrLineOneAssertion  =      
        "?address <"+ addrLine1Pred +"> ?addrLineOne .";
    
    final static String addrLineTwoAssertion  =      
        "?address <"+ addrLine2Pred +"> ?addrLineTwo .";

    final static String addrLineThreeAssertion  =      
        "?address <"+ addrLine3Pred +"> ?addrLineThree .";
    
    final static String cityAssertion  =      
        "?address <"+ cityPred +"> ?city .";

    final static String postalCodeAssertion  =      
        "?address <"+ postalCodePred +"> ?postalCode .";    
    
    final static String stateAssertion  =      
        "?address <"+ statePred +"> ?state .";    
        
    final static String countryAssertion =
        "?address <" + countryPred + "> ?country .";

    final static String addrLabelAssertion =
        "?address <" + label + "> ?addrLabel .";

    final static String addressTypeAssertion =
        "?address a ?addressType .";


    /* Queries for editing an existing entry */

    final static String addrLabelQuery =
        "SELECT ?existingAddrLabel WHERE { \n" +
        "  ?address <" + label + "> ?existingAddrLabel . \n" +
        "}";

    final static String addrLineOneQuery  =      
        "SELECT ?existingaddrLineOne WHERE {\n"+
        "?address <"+ addrLine1Pred +"> ?existingaddrLineOne . }";

    final static String addrLineTwoQuery  =  
        "SELECT ?existingaddrLineTwo WHERE {\n"+
        "?address <"+ addrLine2Pred +"> ?existingaddrLineTwo . }";

    final static String addrLineThreeQuery  =  
        "SELECT ?existingaddrLineThree WHERE {\n"+
        "?address <"+ addrLine3Pred +"> ?existingaddrLineThree . }";

    final static String cityQuery  =  
        "SELECT ?existingCity WHERE {\n"+
        "?address <"+ cityPred +"> ?existingCity . }";

    final static String stateQuery  =  
        "SELECT ?existingState WHERE {\n"+
        "?address <"+ statePred +"> ?existingState . }";

    final static String postalCodeQuery  =  
        "SELECT ?existingPostalCode WHERE {\n"+
        "?address <"+ postalCodePred +"> ?existingPostalCode . }";

    final static String countryQuery  =  
        "SELECT ?existingCountry WHERE {\n"+
        "?address <"+ countryPred +"> ?existingCountry . }";

    final static String addressTypeQuery = 
    	"PREFIX vitro: <" + VitroVocabulary.vitroURI + "> \n" +
        "SELECT ?existingAddressType WHERE { \n" + 
        "?address vitro:mostSpecificType ?existingAddressType . }";

}
