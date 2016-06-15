package br.edu.ufcg.ccc.leda.submission.util;

import java.text.SimpleDateFormat;
import java.util.GregorianCalendar;
import java.util.ResourceBundle;
import java.util.logging.LogRecord;
import java.util.logging.XMLFormatter;

public class XMLLogFormatter extends XMLFormatter {

	public XMLLogFormatter() {
		// TODO Auto-generated constructor stub
	}

	// Append the time and date in ISO 8601 format
    private void appendISO8601(StringBuilder sb, long millis) {
        GregorianCalendar cal = new GregorianCalendar();
        cal.setTimeInMillis(millis);
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSZ");
        sb.append(formatter.format(cal.getTime()));
        /*sb.append(cal.get(Calendar.YEAR));
        sb.append('-');
        a2(sb, cal.get(Calendar.MONTH) + 1);
        sb.append('-');
        a2(sb, cal.get(Calendar.DAY_OF_MONTH));
        sb.append('T');
        a2(sb, cal.get(Calendar.HOUR_OF_DAY));
        sb.append(':');
        a2(sb, cal.get(Calendar.MINUTE));
        sb.append(':');
        a2(sb, cal.get(Calendar.SECOND));*/
    }
    
 // Append to the given StringBuilder an escaped version of the
    // given text string where XML special characters have been escaped.
    // For a null string we append "<null>"
    private void escape(StringBuilder sb, String text) {
        if (text == null) {
            text = "<null>";
        }
        for (int i = 0; i < text.length(); i++) {
            char ch = text.charAt(i);
            if (ch == '<') {
                sb.append("&lt;");
            } else if (ch == '>') {
                sb.append("&gt;");
            } else if (ch == '&') {
                sb.append("&amp;");
            } else {
                sb.append(ch);
            }
        }
    }
	@Override
	public String format(LogRecord record) {
        StringBuilder sb = new StringBuilder(500);
        sb.append("<record>\n");

        sb.append("  <date>");
        appendISO8601(sb, record.getMillis());
        sb.append("</date>\n");

        //sb.append("  <millis>");
        //sb.append(record.getMillis());
        //sb.append("</millis>\n");

        //sb.append("  <sequence>");
        //sb.append(record.getSequenceNumber());
        //sb.append("</sequence>\n");

        String name = record.getLoggerName();
        if (name != null) {
            sb.append("  <logger>");
            escape(sb, name);
            sb.append("</logger>\n");
        }

        sb.append("  <level>");
        escape(sb, record.getLevel().toString());
        sb.append("</level>\n");

        //if (record.getSourceClassName() != null) {
        //    sb.append("  <class>");
        //    escape(sb, record.getSourceClassName());
        //    sb.append("</class>\n");
        //}

        //if (record.getSourceMethodName() != null) {
        //    sb.append("  <method>");
        //    escape(sb, record.getSourceMethodName());
        //    sb.append("</method>\n");
        //}

        //sb.append("  <thread>");
        //sb.append(record.getThreadID());
        //sb.append("</thread>\n");

        if (record.getMessage() != null) {
            // Format the message string and its accompanying parameters.
            String message = formatMessage(record);
            sb.append("  <message>");
            escape(sb, message);
            sb.append("</message>");
            sb.append("\n");
        }

        // If the message is being localized, output the key, resource
        // bundle name, and params.
        ResourceBundle bundle = record.getResourceBundle();
        try {
            if (bundle != null && bundle.getString(record.getMessage()) != null) {
                sb.append("  <key>");
                escape(sb, record.getMessage());
                sb.append("</key>\n");
                sb.append("  <catalog>");
                escape(sb, record.getResourceBundleName());
                sb.append("</catalog>\n");
            }
        } catch (Exception ex) {
            // The message is not in the catalog.  Drop through.
        }

        Object parameters[] = record.getParameters();
        //  Check to see if the parameter was not a messagetext format
        //  or was not null or empty
        if ( parameters != null && parameters.length != 0
                && record.getMessage().indexOf("{") == -1 ) {
            for (int i = 0; i < parameters.length; i++) {
                sb.append("  <param>");
                try {
                    escape(sb, parameters[i].toString());
                } catch (Exception ex) {
                    sb.append("???");
                }
                sb.append("</param>\n");
            }
        }

        if (record.getThrown() != null) {
            // Report on the state of the throwable.
            Throwable th = record.getThrown();
            sb.append("  <exception>\n");
            sb.append("    <message>");
            escape(sb, th.toString());
            sb.append("</message>\n");
            StackTraceElement trace[] = th.getStackTrace();
            for (int i = 0; i < trace.length; i++) {
                StackTraceElement frame = trace[i];
                sb.append("    <frame>\n");
                sb.append("      <class>");
                escape(sb, frame.getClassName());
                sb.append("</class>\n");
                sb.append("      <method>");
                escape(sb, frame.getMethodName());
                sb.append("</method>\n");
                // Check for a line number.
                if (frame.getLineNumber() >= 0) {
                    sb.append("      <line>");
                    sb.append(frame.getLineNumber());
                    sb.append("</line>\n");
                }
                sb.append("    </frame>\n");
            }
            sb.append("  </exception>\n");
        }

        sb.append("</record>\n");
        return sb.toString();
    }
	
	

}
