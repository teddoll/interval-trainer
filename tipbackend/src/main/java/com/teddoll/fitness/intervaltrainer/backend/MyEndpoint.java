/*
   For step-by-step instructions on connecting your Android application to this backend module,
   see "App Engine Java Endpoints Module" template documentation at
   https://github.com/GoogleCloudPlatform/gradle-appengine-templates/tree/master/HelloEndpoints
*/

package com.teddoll.fitness.intervaltrainer.backend;

import com.example.Tips;
import com.google.api.server.spi.config.Api;
import com.google.api.server.spi.config.ApiMethod;
import com.google.api.server.spi.config.ApiNamespace;

import javax.inject.Named;

/** An endpoint class we are exposing */
@Api(
  name = "tipApi",
  version = "v1",
  namespace = @ApiNamespace(
    ownerDomain = "backend.intervaltrainer.fitness.teddoll.com",
    ownerName = "backend.intervaltrainer.fitness.teddoll.com",
    packagePath=""
  )
)
public class MyEndpoint {

    /** A simple endpoint method that takes a name and says Hi back */
    @ApiMethod(name = "getTip")
    public Tip getTip(@Named("date") String date) {

        Tip response = new Tip();
        response.setTip(Tips.getTip(date));

        return response;
    }

}
