package services;
import com.twilio.Twilio;
import com.twilio.rest.api.v2010.account.Message;
import com.twilio.type.PhoneNumber;

public class TwilioService {

    private static final String ACCOUNT_SID = "AC9012091b8b155a0743d30e8f6cbdbe55";
    private static final String AUTH_TOKEN = "6dafad2d58c6d83e299d629d032aa219";
    private static final String TWILIO_PHONE_NUMBER = "+12402554395"; // Numéro Twilio

    public TwilioService() {
        Twilio.init(ACCOUNT_SID, AUTH_TOKEN);
    }
    public static void sendSms(String to, String messageBody) {
        Message message = Message.creator(
                new com.twilio.type.PhoneNumber(to), // Numéro du client
                new com.twilio.type.PhoneNumber(TWILIO_PHONE_NUMBER), // Ton numéro Twilio
                messageBody
        ).create();

        System.out.println("✅ SMS envoyé avec SID: " + message.getSid());
    }
}
