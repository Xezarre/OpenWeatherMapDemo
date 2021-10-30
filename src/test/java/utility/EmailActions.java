package utility;

import com.aventstack.extentreports.ExtentTest;
import com.sun.mail.smtp.SMTPTransport;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import javax.mail.*;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;
import javax.mail.search.FlagTerm;
import java.io.IOException;
import java.util.Properties;

import static common.GlobalVariables.*;

public class EmailActions extends Utility {
    private Store store;
    private Folder folder, folderInbox;

    public EmailActions() {
        try {
            Properties props = System.getProperties();
            props.setProperty("mail.store.protocol", "imaps");
            Session session = Session.getDefaultInstance(props, null);
            store = session.getStore("imaps");
            store.connect("imap.gmail.com", GMAIL_USERNAME, GMAIL_PASSWORD);
        } catch (Exception e) {
            System.out.println(e);
        }
    }

    public void openConnection() {
        Properties props = System.getProperties();
        props.setProperty("mail.store.protocol", "imaps");
        Session session = Session.getDefaultInstance(props, null);
        try {
            store = session.getStore("imaps");
            store.connect("imap.gmail.com", GMAIL_USERNAME, GMAIL_PASSWORD);
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    public void closeConnection() {

        if(store.isConnected())
        {
            try {
                System.out.println("Closing connection");
                store.close();
            } catch (MessagingException e) {
                e.printStackTrace();
            }
        }
    }

    public Message getEmailObject(String fromEmail, String toEmail, String subject, boolean unRead, ExtentTest logTest) throws IOException {
        try {
            boolean isFrom = false, isTo = false;
            Message[] messages;
            String actualSubject;
            int index;
            if(!emailActions.store.isConnected())
            {
                emailActions.openConnection();
            }

            folder = store.getFolder(ALL_MAIL_FOLDER);
            folder.open(Folder.READ_WRITE);

            if (unRead == true) {
                messages = folder.search(new FlagTerm(new Flags(Flags.Flag.SEEN), false));
                // Only check 30 last unread emails
            }
            else {
                messages = folder.getMessages();
                // Only check 30 last emails
            }
            int totalMsg = messages.length;
            if (totalMsg < 1) return null;
            if (totalMsg > 100) index = 100;
            else index = totalMsg;

            for (int i = totalMsg - 1; i >= totalMsg - index; i--) {
                actualSubject = messages[i].getSubject().trim().replaceAll(" +", " ");
                // Check Subject
                if (actualSubject.contains(subject.trim().replaceAll(" +", " "))) {
                    // Check From
                    if (fromEmail != null) {
                        Address[] fromEmails = messages[i].getFrom();
                        for (Address from : fromEmails) {
                            if (((InternetAddress) from).getAddress().equalsIgnoreCase(fromEmail)) {
                                isFrom = true;
                                break;
                            }
                        }
                    } else {
                        isFrom = true;
                    }

                    // Check To
                    Address[] toEmails = messages[i].getRecipients(Message.RecipientType.TO);
                    for (Address to : toEmails) {
                        if (((InternetAddress) to).getAddress().equals(toEmail)) {
                            isTo = true;
                            break;
                        }
                    }

                    if (isFrom && isTo)
                        return messages[i];
                }
            }

            return null;
        } catch (MessagingException ex) {
            log4j.info("Retry connection... with Exception: " + ex);
            return null;
        } catch (Exception ex) {
            log4j.error("getEmailObject method - ERROR: " + ex);
            TestReporter.logException(logTest, "getEmailObject method - ERROR: ", ex);
        }

        return null;
    }

    public Message getEmailObject(String fromEmail, String toEmail, String subject, ExtentTest logTest) throws IOException {
        return getEmailObject(fromEmail, toEmail, subject, false, logTest);
    }

    public int emailObjectCounter(String fromEmail, String toEmail, String subject, ExtentTest logTest) throws IOException {
        int counter = 0;
        try {

            String actualSubject;
            int index;
            if(!emailActions.store.isConnected())
            {
                emailActions.openConnection();
            }

            folder = store.getFolder(ALL_MAIL_FOLDER);
            folder.open(Folder.READ_WRITE);

            Message[] messages = folder.getMessages();
            // Only check 30 last emails
            int totalMsg = messages.length;
            if (totalMsg < 1) return 0;
            if (totalMsg > 30) index = 30;
            else index = totalMsg;

            for (int i = totalMsg - 1; i >= totalMsg - index; i--) {
                actualSubject = messages[i].getSubject().trim().replaceAll(" +", " ");
                boolean isFrom = false, isTo = false;

                // Check Subject
                if (actualSubject.contains(subject.trim().replaceAll(" +", " "))) {
                    // Check From
                    if (fromEmail != null) {
                        Address[] fromEmails = messages[i].getFrom();
                        for (Address from : fromEmails) {
                            if (((InternetAddress) from).getAddress().equalsIgnoreCase(fromEmail)) {
                                isFrom = true;
                                break;
                            }
                        }
                    } else {
                        isFrom = true;
                    }

                    // Check To
                    Address[] toEmails = messages[i].getRecipients(Message.RecipientType.TO);
                    for (Address to : toEmails) {
                        if (((InternetAddress) to).getAddress().equals(toEmail)) {
                            isTo = true;
                            break;
                        }
                    }

                    if (isFrom && isTo)
                        counter++;
                }
            }

            return counter;
        } catch (MessagingException ex) {
            log4j.info("Retry connection...");
            return 0;
        } catch (Exception ex) {
            log4j.error("getEmailObject method - ERROR: " + ex);
            TestReporter.logException(logTest, "getEmailObject method - ERROR: ", ex);
        }

        return counter;
    }

    public Document getEmailContent(Message message, ExtentTest logTest) throws IOException {
        try {
            Object emailContent = message.getContent();
            String body = "";
            if (emailContent instanceof String) {
                return Jsoup.parse((String) emailContent);
            } else if (emailContent instanceof Multipart) {
                Multipart multipart = (Multipart) emailContent;
                for (int x = 0; x < multipart.getCount(); x++) {
                    BodyPart bodyPart = multipart.getBodyPart(x);
                    Object content = bodyPart.getContent();
                    if (content instanceof String) {
                        body = body + content.toString();
                    } else if (content instanceof MimeMultipart) {
                        MimeMultipart mimeMultipart = (MimeMultipart) bodyPart.getContent();
                        for (int j = 0; j < mimeMultipart.getCount(); j++) {
                            body = body + mimeMultipart.getBodyPart(j).getContent().toString();
                        }
                    }
                }
            }

            return Jsoup.parse(body);
        } catch (Exception e) {
            log4j.error("getEmailContent method - ERROR: " + e);
            TestReporter.logException(logTest, "getEmailContent method - ERROR: ", e);
        }

        return null;
    }

    public synchronized Message verifyEmailExist(String fromEmail, String toEmail, String subject, boolean overrideSubject, boolean unRead, ExtentTest logTest) throws IOException {
        try {
            log4j.info("Verify email exists in Inbox...start");
            if (overrideSubject) {
                if (ENVIRONMENT.equalsIgnoreCase(STAGE))
                    subject = "[STAGE]" + subject;
                else
                    subject = " " + subject;
            }

            // Remove template ID from subject on production
            if (ENVIRONMENT.equalsIgnoreCase(PRODUCTION) && subject.contains("[") && subject.contains("]") && subject.indexOf("]") < subject.length() - 1)
                subject = subject.substring(subject.indexOf("]") + 1).trim();

            // Refresh inbox maximum = 12 times.
            Message message;
            for (int i = 1; i <= 12; i++) {
                log4j.debug("Repeat time: #" + i);
                sleep(10); // Refresh inbox after each 10 seconds
                message = getEmailObject(fromEmail, toEmail, subject, unRead, logTest);
                if (message != null) {
                    TestReporter.logPass(logTest, "<br>Email is exist in INBOX: " +
                            "<br>From: " + fromEmail +
                            "<br>To: " + toEmail +
                            "<br>Subject: " + subject);
                    return message;
                }
            }

            TestReporter.logFail(logTest, "<br>Email is NOT exist in INBOX: " +
                    "<br>From: " + fromEmail +
                    "<br>To: " + toEmail +
                    "<br>Subject: " + subject);
        } catch (Exception e) {
            log4j.error("verifyEmailExist method - ERROR: " + e);
            TestReporter.logException(logTest, "verifyEmailExist method - ERROR: ", e);
        }

        return null;
    }

    public synchronized Message verifyEmailExist(String fromEmail, String toEmail, String subject, boolean overrideSubject, ExtentTest logTest) throws IOException {
        return verifyEmailExist(fromEmail, toEmail, subject, overrideSubject, false, logTest);
    }

    /**
     * Verify email NOT exist
     * @param fromEmail from
     * @param toEmail to
     * @param subject subject
     * @param overrideSubject ovrSubject
     * @param logTest logTest
     * @throws IOException e
     */
    public synchronized void verifyEmailNotExist(String fromEmail, String toEmail, String subject, boolean overrideSubject, ExtentTest logTest) throws IOException {
        try {
            log4j.info("Verify email NOT exists in Inbox...start");

            boolean emailNotExist = true;
            if (overrideSubject) {
                if (ENVIRONMENT.equalsIgnoreCase(STAGE))
                    subject = "[STAGE]" + subject;
                else
                    subject = " " + subject;
            }

            // Refresh inbox maximum = 20 times.
            Message message;
            for (int i = 1; i <= 10; i++) {
                log4j.debug("Repeat time: #" + i);
                sleep(5); // Refresh inbox after each 10 seconds
                message = getEmailObject(fromEmail, toEmail, subject, false, logTest);
                if (message != null) {
                    emailNotExist = false;
                    break;
                }
            }

            if (emailNotExist)
                TestReporter.logPass(logTest, "<br>Email does NOT exist in INBOX: " +
                        "<br>From: " + fromEmail +
                        "<br>To: " + toEmail +
                        "<br>Subject: " + subject);
            else
                TestReporter.logFail(logTest, "<br>Email DOES exist in INBOX: " +
                        "<br>From: " + fromEmail +
                        "<br>To: " + toEmail +
                        "<br>Subject: " + subject);

        } catch (Exception e) {
            log4j.error("verifyEmailExist method - ERROR: " + e);
            TestReporter.logException(logTest, "verifyEmailExist method - ERROR: ", e);
        }
    }

    public synchronized int verifyNumberOfSentEmail(String fromEmail, String toEmail, String subject, boolean overrideSubject, int numberOfEmail, ExtentTest logTest) throws IOException {
        int emailCounter = 0;
        try {

            log4j.info("Verify email exists in Inbox...start");
            if (overrideSubject) {
                if (ENVIRONMENT.equalsIgnoreCase(STAGE))
                    subject = "[STAGE]" + subject;
                else
                    subject = " " + subject;
            }

            // Remove template ID from subject on production
            if (ENVIRONMENT.equalsIgnoreCase(PRODUCTION) && subject.indexOf("[") != -1 && subject.indexOf("]") != -1 && subject.indexOf("]") < subject.length() - 1)
                subject = subject.substring(subject.indexOf("]") + 1, subject.length()).trim();

            // Refresh inbox maximum = 20 times. [Aparna]:I increased from 15 to 20 since the gift card creation email takes longer time than the claim update emails.
            Message message;
            for (int i = 1; i <= 20; i++) {
                log4j.debug("Repeat time: #" + i);
                sleep(20); // Refresh inbox after each 20 seconds
                emailCounter = emailObjectCounter(fromEmail, toEmail, subject, logTest);
                if (emailCounter == numberOfEmail) {
                    TestReporter.logPass(logTest, "<br>Number of email is exist in INBOX: " +emailCounter +
                            "<br>From: " + fromEmail +
                            "<br>To: " + toEmail +
                            "<br>Subject: " + subject);
                    return emailCounter;
                }
            }

            TestReporter.logFail(logTest, "<br>Expected number of email is exist in INBOX: " +numberOfEmail +
                    "<br>But Actual number of email is exist in INBOX: " + emailCounter +
                    "<br>From: " + fromEmail +
                    "<br>To: " + toEmail +
                    "<br>Subject: " + subject);
        } catch (Exception e) {
            log4j.error("verifyEmailExist method - ERROR: " + e);
            TestReporter.logException(logTest, "verifyEmailExist method - ERROR: ", e);
        }
        return emailCounter;
    }

    /**
     * @param folderName
     * @param logTest
     * @ActionName: deleteAllEmailsInFolder(folderName, logTest)
     */
    public void deleteAllEmailsInFolder(String folderName, ExtentTest logTest) throws IOException {
        try {
            log4j.info("Delete all emails in folder...start");
            TestReporter.logInfo(logTest, "Delete all emails in folder: " + folderName);
            folderInbox = store.getFolder(folderName);
            folderInbox.open(Folder.READ_WRITE);
            Message[] messageInbox = folderInbox.getMessages();

            for (int i = 0; i < messageInbox.length; i++)
                messageInbox[i].setFlag(Flags.Flag.DELETED, true);

            log4j.info("Delete all emails in folder...end");
        } catch (Exception e) {
            log4j.error("deleteAllEmailsInFolder method - ERROR" + e);
            TestReporter.logException(logTest, "deleteAllEmailsInFolder method - ERROR", e);
        }
    }

    /**
     * sendEmailReport: send email with extend report file path to user after running test suite
     */
    public static void sendEmailReport(String env, String testingType, String suiteName, String reportPath, boolean statusReport, ExtentTest logTest) throws IOException {
        try {
            log4j.info("Send test result email");
            TestReporter.logInfo(logTest, "Send test result email");

            // Get email subject and header
            String emailSubject;
            String headerSection;
            if (statusReport) {
                emailSubject = "UI Automation Results - PASS";
                headerSection = "<p style=\"color:green;\"><b>" + emailSubject + "</b></p>";
            } else {
                emailSubject = "UI Automation Results - FAIL";
                headerSection = "<p style=\"color:red;\"><b>" + emailSubject + "</b></p>";
            }

            String summarySection;
            if (RETRY_FAILED_TESTS.equalsIgnoreCase("Yes")) {
                summarySection = "<br><b>Summary:</b>" +
                        "<ul>" +
                        "<li>Total Tests: " + TOTAL_TESTCASES + "</li>" +
                        "<li>Pass: " + TOTAL_PASSED + "</li>" +
                        "<li>Pass with Retry: " + TOTAL_PASSED_WITH_RETRY + "</li>" +
                        "<li>Fail: " + TOTAL_FAILED + "</li>" +
                        "<li>Skip: " + TOTAL_SKIPPED + "</li>" +
                        "</ul>";
            } else {
                summarySection = "<br><b>Summary:</b>" +
                        "<ul>" +
                        "<li>Total Tests: " + TOTAL_TESTCASES + "</li>" +
                        "<li>Executed: " + TOTAL_EXECUTED + "</li>" +
                        "<li>Pass: " + TOTAL_PASSED + "</li>" +
                        "<li>Fail: " + TOTAL_FAILED + "</li>" +
                        "<li>Skip: " + TOTAL_SKIPPED + "</li>" +
                        "</ul>";
            }

            String environmentSection = "";
            if (RUN_ON.equalsIgnoreCase("local") || RUN_ON.equalsIgnoreCase("grid")) {

                environmentSection = "<br><b>Environment:</b>" +
                        "<ul>" +
                        "<li>Environment: " + ENVIRONMENT + "</li>" +
                        "<li>Run On: " + RUN_ON + "</li>" +
                        "<li>Browser Name: " + BROWSER + "</li>" +
                        "<li>OS Name: " + OS_NAME + "</li>" +
                        "</ul>";
            }

            // Result link
            String fileName = reportPath.substring(reportPath.lastIndexOf("/") + 1);
            String buildURL = System.getProperty("buildURL");
            String jobURL = System.getProperty("jobURL");
            String footerSection;
            if (buildURL != null && jobURL != null) {
                reportPath = buildURL + "artifact/" + reportPath.substring(reportPath.indexOf("resources"));
                footerSection = "Link to test result: <a href=" + reportPath + ">" + fileName + "</a><br />Link to Jenkin build: " + buildURL;
            } else {
                footerSection = "Link to Result: <a href=" + reportPath + ">" + fileName + "</a>";
            }

            // Email content
            String content = headerSection + summarySection + environmentSection + footerSection;

            log4j.info("Set SMTP server properties");
            TestReporter.logInfo(logTest, "sets SMTP server properties");
            Properties props = System.getProperties();
            props.put("mail.smtp.host", "mail.testing.com");
            props.put("mail.smtp.auth", "false");
            Session session = Session.getInstance(props, null);

            log4j.info("Create a new email message");
            TestReporter.logInfo(logTest, "Create a new email message");

            // Create a new email message
            Message msg = new MimeMessage(session);

            // From
            msg.setFrom(new InternetAddress(FROM_RECIPIENT));

            // To
            msg.addRecipient(Message.RecipientType.TO, new InternetAddress(TO_RECIPIENT));

            // Subject
            String subject = "[" + env + "][" + testingType + "][" + suiteName + " Suite] - " + emailSubject;
            if (suiteName.equals("Default Suite")){
                subject = "[" + env + "][" + testingType + "][" + suiteName + "] - " + emailSubject;
            }
            msg.setSubject(subject);


            // Message content
            MimeBodyPart messageBodyPart = new MimeBodyPart();
            messageBodyPart.setContent(content, "text/html");

            // Add body part
            Multipart multipart = new MimeMultipart();
            multipart.addBodyPart(messageBodyPart);

            // Set email content
            msg.setContent(multipart);

            log4j.info("Open SMTP connection and send the e-mail");
            TestReporter.logInfo(logTest, "Open SMTP connection and send the e-mail");
            SMTPTransport t = (SMTPTransport) session.getTransport("smtp");
            t.connect();
            t.sendMessage(msg, msg.getAllRecipients());
            t.close();

        } catch (Exception e) {

            log4j.error("sendEmailReport method - ERROR: " + e);
            TestReporter.logException(logTest, "sendEmailReport method - ERROR: ", e);
        }
    }
}
