package com.vietqr.org.util;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.List;
import java.util.Random;

@Component
public class EmailConfiguration {
    @Value("${ses.verified.emails}")
    private String[] verifiedEmails;

    public List<String> getVerifiedEmails() {
        return Arrays.asList(verifiedEmails);
    }

    public String getRandomVerifiedEmail() {
        List<String> emails = getVerifiedEmails();
        Random random = new Random();
        return emails.get(random.nextInt(emails.size()));
    }
}
