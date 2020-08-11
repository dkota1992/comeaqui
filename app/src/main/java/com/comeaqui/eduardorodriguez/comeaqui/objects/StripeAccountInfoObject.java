package com.comeaqui.eduardorodriguez.comeaqui.objects;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import java.io.Serializable;
import java.util.ArrayList;

public class StripeAccountInfoObject implements Serializable {
    public String id;
    public BusinessProfile business_profile;
    public Individual individual;
    public boolean payouts_enabled;
    public Requirements requirements;
    public ExternalAccounts external_accounts;


    public StripeAccountInfoObject(JsonObject jo){
        id = jo.get("id").getAsString();
        business_profile = new BusinessProfile(jo.get("business_profile").getAsJsonObject());
        individual = new Individual(jo.get("individual").getAsJsonObject());
        payouts_enabled = jo.get("payouts_enabled").getAsBoolean();
        requirements = new Requirements(jo.get("requirements").getAsJsonObject());
        external_accounts = new ExternalAccounts(jo.get("external_accounts").getAsJsonObject());
    }

    public class Individual implements Serializable {
        public String id;
        public Integer id_number;
        public Address address;
        public DOB dob;
        public String email;
        public String first_name;
        public String last_name;
        public String phone;
        public boolean ssn_last_4_provided;
        public Verification verification;

        public Individual(JsonObject jo){
            id = jo.get("id").getAsString();
            address = new Address(jo.get("address").getAsJsonObject());
            dob = new DOB(jo.get("dob").getAsJsonObject());
            verification = new Verification(jo.get("verification").getAsJsonObject());
            try {id_number = jo.get("id_number").getAsInt();} catch (Exception e){}
            try {email = jo.get("email").getAsString();} catch (Exception e){}
            try {first_name = jo.get("first_name").getAsString(); } catch (Exception e){}
            try {last_name = jo.get("last_name").getAsString();} catch (Exception e){}
            try {phone = jo.get("phone").getAsString();} catch (Exception e){}
            try {ssn_last_4_provided = jo.get("ssn_last_4_provided").getAsBoolean();} catch (Exception e){}
        }
    }

    public class Address implements Serializable {
        public String city;
        public String country;
        public String line1;
        public String line2;
        public String postal_code;
        public String state;

        public Address(JsonObject jo){
            try {city = jo.get("city").getAsString();} catch (Exception e){}
            try {country = jo.get("country").getAsString();} catch (Exception e){}
            try {line1 = jo.get("line1").getAsString();} catch (Exception e){}
            try {line2 = jo.get("line2").getAsString();} catch (Exception e){}
            try {postal_code = jo.get("postal_code").getAsString();} catch (Exception e){}
            try {state = jo.get("state").getAsString();} catch (Exception e){}

        }
    }

    public class DOB implements Serializable {
        public Integer day;
        public Integer month;
        public Integer year;

        public DOB(JsonObject jo){
            try { day = jo.get("day").getAsInt();} catch (Exception e){}
            try { month = jo.get("month").getAsInt();} catch (Exception e){}
            try { year = jo.get("year").getAsInt();} catch (Exception e){}
        }
    }

    public class Requirements implements Serializable {
        public ArrayList<String> currently_due;
        public Requirements(JsonObject jo){
            currently_due = new ArrayList<>();
            for (JsonElement je: jo.get("currently_due").getAsJsonArray()){
                currently_due.add(je.getAsString());
            }
        }
    }
    public class BusinessProfile implements Serializable {
        public String url;
        public BusinessProfile(JsonObject jo){
            try {url = jo.get("url").getAsString();} catch (Exception e){}
        }
    }

    public class ExternalAccounts implements Serializable {
        public ArrayList<Account> data;
        public ExternalAccounts(JsonObject jo){
            data = new ArrayList<>();
            for (JsonElement je: jo.get("data").getAsJsonArray()){
                data.add(new Account(je.getAsJsonObject()));
            }
        }
    }

    public class Account implements Serializable {
        public String id;
        public String account;
        public String bank_name;
        public String country;
        public String last4;
        public String routing_number;

        public Account(JsonObject jo){
            id = jo.get("id").getAsString();
            try {account = jo.get("account").getAsString();} catch (Exception e){}
            try {bank_name = jo.get("bank_name").getAsString();} catch (Exception e){}
            try {country = jo.get("country").getAsString(); } catch (Exception e){}
            try {last4 = jo.get("last4").getAsString();} catch (Exception e){}
            try {routing_number = jo.get("routing_number").getAsString();} catch (Exception e){}
        }
    }

    public class Verification implements Serializable {
        public String status;
        public Document document;

        public Verification(JsonObject jo){
            try {status = jo.get("account").getAsString();} catch (Exception e){}
            document = new Document(jo.get("document").getAsJsonObject());
        }
    }
    public class Document implements Serializable {
        public String back;
        public String front;

        public Document(JsonObject jo){
            try {back = jo.get("back").getAsString();} catch (Exception e){}
            try {front = jo.get("front").getAsString();} catch (Exception e){}
        }
    }
}




