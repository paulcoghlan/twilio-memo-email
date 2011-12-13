import javax.mail.Message;
import javax.mail.Multipart;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;

import javax.mail.internet.MimeMultipart;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.util.Properties;
import java.util.logging.Logger;

/**
 *
 *
 *
 */
public class SendEmailServlet extends HttpServlet {

    private static final Logger log = Logger.getLogger(SendEmailServlet.class.getName());

    // Change this to the email specified in Settings/Account Summary/Emailing to Evernote
    private static final String EVERNOTE_EMAIL = "<evernote email>";

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        this.doGet(req, resp);
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        log.info("Email Servlet");

        String transcriptionStatus = req.getParameter("TranscriptionStatus");
        log.info("TranscriptionStatus - " + transcriptionStatus);

        if (transcriptionStatus.equals("completed")) {
            String recordingUrl = req.getParameter("RecordingUrl");
            String transcriptionText = req.getParameter("TranscriptionText");
            log.info("transcriptionText: " + transcriptionText);

            if (sendMimeEmail(transcriptionText, recordingUrl)) {
                resp.getWriter().println("Email sent");
                resp.getWriter().close();
            }
        }
    }

    private boolean sendMimeEmail(String transcriptionText, String recordingUrl) {
        boolean sent = false;

        Properties props = new Properties();
        Session session = Session.getDefaultInstance(props, null);

        try {
            String subject = "Memo from Twilio";
            Message msg = new MimeMessage(session);
            msg.setFrom(new InternetAddress("mailbot@twilio-email.appspotmail.com", "Twilio Email App"));
            msg.addRecipient(Message.RecipientType.TO,
                             new InternetAddress(EVERNOTE_EMAIL, "Evernote"));
            msg.setSubject(subject);

            Multipart mp = new MimeMultipart();

            MimeBodyPart htmlPart = new MimeBodyPart();
            htmlPart.setContent(transcriptionText + " <a href='" + recordingUrl + "'>recording</a>", "text/html");
            mp.addBodyPart(htmlPart);
            msg.setContent(mp);
            Transport.send(msg);
            sent = true;
        } catch (Exception e) {
            log.warning("Exception: " + e.getMessage());
        }

        return sent;
    }

}
