/* ****************************************************************************
Copyright 2013 Ellucian Company L.P. and its affiliates.
******************************************************************************/ 

import grails.converters.JSON

import net.hedtech.marshallers.*
import net.hedtech.restfulapi.*

import org.modeshape.common.text.Inflector


class BootStrap {

    def grailsApplication

    def init = { servletContext ->

        // Add rules to singularize resource names as needed here...
        //
        Inflector.instance.addSingularize("mice\$", "\$1mouse") 

        // Example of how to Register a marshaller into the default configuration,
        // that overrides the marshallers added by the 'restful-api' plugin. 
        // Note the priority is above 100 (so that it will be used instead of the 
        // marshaller registered by the plugin.)

        // 'json' (application/json) configuration
        //
        //JSON.registerObjectMarshaller(new BasicDomainClassMarshaller(grailsApplication), 101)

        // ------ Named configuration(s) that corresponds to a custom media type ------
        
        // 'jsonv0' (application/vnd.hedtech.v0+json) configuration
        //
        JSON.createNamedConfig('jsonv0') {
            it.registerObjectMarshaller(new BasicHalDomainClassMarshaller(grailsApplication), 100)
            it.registerObjectMarshaller(new ThingClassMarshaller(grailsApplication), 101)
        }

        // Our simple seed data
        createThing('AA')
        createThing('BB')
    }


    def destroy = { }


    private void createThing(String code) {
        Thing.withTransaction {
            new Thing(code: code, description: "An $code thing",
                      dateManufactured: new Date(), isGood: 'Y', isLarge: true)
                .addPart(new PartOfThing(code: 'aa', description: 'aa part').save())
                .addPart(new PartOfThing(code: 'bb', description: 'bb part').save())
                .save()
        }

    }
}
