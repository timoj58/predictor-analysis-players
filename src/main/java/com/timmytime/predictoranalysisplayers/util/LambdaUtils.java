package com.timmytime.predictoranalysisplayers.util;

import com.amazonaws.auth.profile.ProfileCredentialsProvider;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.lambda.AWSLambda;
import com.amazonaws.services.lambda.AWSLambdaClientBuilder;
import com.amazonaws.services.lambda.model.InvokeRequest;
import com.amazonaws.services.lambda.model.ServiceException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Component
public class LambdaUtils {

    private  static final Logger log = LoggerFactory.getLogger(LambdaUtils.class);

    public void startMachineLearning(){
        InvokeRequest invokeRequest = new InvokeRequest()
                .withFunctionName("arn:aws:lambda:us-east-1:842788105885:function:predictor-machine-learning-players-start");

        try {
            AWSLambda awsLambda = AWSLambdaClientBuilder.standard()
                    .withCredentials(new ProfileCredentialsProvider())
                    .withRegion(Regions.US_EAST_1).build();

            awsLambda.invoke(invokeRequest);
            log.info("started machine learning");


        } catch (ServiceException e) {
            log.error("lambda", e);
        }

    }

    public void destroy(){
        InvokeRequest invokeRequest = new InvokeRequest()
                .withFunctionName("arn:aws:lambda:us-east-1:842788105885:function:predictor-destroy");

        try {
            AWSLambda awsLambda = AWSLambdaClientBuilder.standard()
                    .withCredentials(new ProfileCredentialsProvider())
                    .withRegion(Regions.US_EAST_1).build();

            log.info("stopping all instances");
            awsLambda.invoke(invokeRequest);


        } catch (ServiceException e) {
            log.error("lambda", e);
        }


    }
}
