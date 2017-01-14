package com.alipay.android.aue.log;

import org.apache.log4j.HTMLLayout;
import org.apache.log4j.Layout;
import org.apache.log4j.spi.LoggingEvent;

import java.util.Date;

/**
 * Created by bingo on 2016/10/19.
 */
public class LoggerHtmlLayout extends HTMLLayout {

    @Override
    public String getHeader() {
        StringBuffer sbuf = new StringBuffer();
        sbuf.append("<!DOCTYPE HTML PUBLIC \"-//W3C//DTD HTML 4.01 Transitional//EN\" \"http://www.w3.org/TR/html4/loose.dtd\">" + Layout.LINE_SEP);
        sbuf.append("<html>" + Layout.LINE_SEP);
        sbuf.append("<head>" + Layout.LINE_SEP);
        sbuf.append("<title>" + getTitle() + "</title>" + Layout.LINE_SEP);
        sbuf.append("<style type=\"text/css\">" + Layout.LINE_SEP);
        sbuf.append("<!--" + Layout.LINE_SEP);
        sbuf.append("body, table {font-family: arial,sans-serif; font-size: x-small;}" + Layout.LINE_SEP);
        sbuf.append("th {background: #336699; color: #FFFFFF; text-align: left;}" + Layout.LINE_SEP);
        sbuf.append("-->" + Layout.LINE_SEP);
        sbuf.append("</style>" + Layout.LINE_SEP);
        sbuf.append("</head>" + Layout.LINE_SEP);
        sbuf.append("<body bgcolor=\"#FFFFFF\" topmargin=\"6\" leftmargin=\"6\">" + Layout.LINE_SEP);
        sbuf.append("<hr size=\"1\" noshade>" + Layout.LINE_SEP);
        sbuf.append("Log session start time " + new Date() + "<br>" + Layout.LINE_SEP);
        sbuf.append("<br>" + Layout.LINE_SEP);
        sbuf.append("<table cellspacing=\"0\" cellpadding=\"4\" border=\"1\" bordercolor=\"#224466\" width=\"100%\">" + Layout.LINE_SEP);
        sbuf.append("<tr>" + Layout.LINE_SEP);
        sbuf.append("<th>Time</th>" + Layout.LINE_SEP);
        sbuf.append("<th>Thread</th>" + Layout.LINE_SEP);
        sbuf.append("<th>Level</th>" + Layout.LINE_SEP);
        sbuf.append("<th>Category</th>" + Layout.LINE_SEP);
        if(getLocationInfo()) {
            sbuf.append("<th>File:Line</th>" + Layout.LINE_SEP);
        }

        sbuf.append("<th>Message</th>" + Layout.LINE_SEP);
        sbuf.append("</tr>" + Layout.LINE_SEP);
        return sbuf.toString();
    }

    @Override
    public String format(LoggingEvent event) {
        return super.format(event);
    }
}
