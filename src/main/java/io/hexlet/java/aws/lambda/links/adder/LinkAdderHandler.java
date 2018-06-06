package io.hexlet.java.aws.lambda.links.adder;

import com.amazonaws.services.dynamodbv2.AmazonDynamoDB;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDBClientBuilder;
import com.amazonaws.services.dynamodbv2.document.DynamoDB;
import com.amazonaws.services.dynamodbv2.document.Item;
import com.amazonaws.services.dynamodbv2.document.Table;
import com.amazonaws.services.dynamodbv2.document.spec.PutItemSpec;
import com.amazonaws.services.dynamodbv2.model.ConditionalCheckFailedException;
import com.amazonaws.services.lambda.runtime.Context;

import java.util.Random;

public class LinkAdderHandler {

    private static final Table LINKS_TABLE;

    static {
        final AmazonDynamoDB client = AmazonDynamoDBClientBuilder.standard().build();
        final DynamoDB dynamoDB = new DynamoDB(client);

        LINKS_TABLE = dynamoDB.getTable("Links");
    }

    public String addLink(String url, Context context) {
        int attempt = 0;
        while (attempt < 5) {
            final String id = getRandomId();
            final Item urlRecord = new Item()
                    .withPrimaryKey("id", id)
                    .withString("url", url);
            try {
                LINKS_TABLE.putItem(
                        new PutItemSpec()
                                .withConditionExpression("attribute_not_exists(id)")
                                .withItem(urlRecord));
                return id;
            } catch (ConditionalCheckFailedException e) {
                // attempt to write failed, ID - exists.
            }
            attempt++;
        }
        return null;
    }

    private static String getRandomId() {
        String possibleCharacters = "QWERTYUIOPASDFGHJKLZXCVBNMqwertyuiopasdfghjklzxcvbnm1234567890";
        StringBuilder idBuilder = new StringBuilder();
        Random rnd = new Random();
        while (idBuilder.length() < 5) {
            int index = (int) (rnd.nextFloat() * possibleCharacters.length());
            idBuilder.append(possibleCharacters.charAt(index));
        }
        return idBuilder.toString();
    }
}
