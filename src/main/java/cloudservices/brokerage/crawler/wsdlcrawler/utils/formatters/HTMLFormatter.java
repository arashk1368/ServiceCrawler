/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package cloudservices.brokerage.crawler.wsdlcrawler.utils.formatters;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.logging.Formatter;
import java.util.logging.Handler;
import java.util.logging.Level;
import java.util.logging.LogRecord;

/**
 *
 * @author Lars Vogel
 * @version 1.5
 */
public class HTMLFormatter extends Formatter {

    public String format(LogRecord rec) {
        StringBuilder buf = new StringBuilder(1000);
        // Bold any levels >= WARNING
        buf.append("<tr>");
        buf.append("<td>");

        if (rec.getLevel().intValue() >= Level.WARNING.intValue()) {
            buf.append("<b>");
            buf.append(rec.getLevel());
            buf.append("</b>");
        } else {
            buf.append(rec.getLevel());
        }
        buf.append("</td>");
        buf.append("<td>");
        buf.append(calcDate(rec.getMillis()));
        buf.append("</td>");
        buf.append("<td>");
        buf.append(formatMessage(rec));
        buf.append('\n');
        buf.append("</td>");
        buf.append("</tr>\n");
        return buf.toString();
    }

    private String calcDate(long millisecs) {
        SimpleDateFormat date_format = new SimpleDateFormat("MMM dd,yyyy HH:mm");
        Date resultdate = new Date(millisecs);
        return date_format.format(resultdate);
    }

    // This method is called just after the handler using this
    // formatter is created
    @Override
    public String getHead(Handler h) {
        return "<HTML>\n<HEAD>\n" + (new Date())
                + "\n</HEAD>\n<BODY>\n<PRE>\n"
                + "<table width=\"100%\" border>\n  "
                + "<tr><th>Level</th>"
                + "<th>Time</th>"
                + "<th>Log Message</th>"
                + "</tr>\n";
    }

    // This method is called just after the handler using this
    // formatter is closed
    @Override
    public String getTail(Handler h) {
        return "</table>\n  </PRE></BODY>\n</HTML>\n";
    }
}
