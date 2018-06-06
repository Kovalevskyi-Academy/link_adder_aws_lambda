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

public class LinkGetterHandler {

    private static final Table LINKS_TABLE;

    static {
        final AmazonDynamoDB client = AmazonDynamoDBClientBuilder.standard().build();
        final DynamoDB dynamoDB = new DynamoDB(client);

        LINKS_TABLE = dynamoDB.getTable("Links");
    }

    public String getLink(String id, Context context) {
        if (id == null || id.isEmpty()) {
            return null;
        }
        final Item item = LINKS_TABLE.getItem("id", id);
        final String url = item.getString("url");
        if (url == null || url == "") {
            return null;
        }
        return url;
    }
}
