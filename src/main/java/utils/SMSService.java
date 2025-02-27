package utils;

import com.twilio.Twilio;
import com.twilio.rest.api.v2010.account.Message;
import com.twilio.type.PhoneNumber;

public class SMSService {

    public static final String ACCOUNT_SID = "votre_account_sid";
    public static final String AUTH_TOKEN = "votre_auth_token";
    public static final String TWILIO_NUMBER = "votre_numero_twilio";

    public static void sendSMS(String to, String messageBody) {
        Twilio.init(ACCOUNT_SID, AUTH_TOKEN);

        Message message = Message.creator(
                new PhoneNumber(to),
                new PhoneNumber(TWILIO_NUMBER),
                messageBody
        ).create();

        System.out.println("SMS sent successfully to " + to + ", SID: " + message.getSid());
    }
}